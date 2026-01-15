package com.project.dass.ServiceImpl;

import com.project.dass.Model.Recipe;
import com.project.dass.Service.PhotoService;
import com.project.dass.Service.RecipeService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service("photoService")
@Transactional // Ensures database operations are atomic
public class PhotoServiceImpl implements PhotoService {

    private static final Logger logger = LoggerFactory.getLogger(PhotoServiceImpl.class);
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp"
    );

    @Value("${photos.directory:photos}")
    private String photosDirectory;

    private final RecipeService recipeService;

    public PhotoServiceImpl(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    /**
     * Extract recipeId from the map
     */
    private Optional<Long> extractRecipeId(Map<String, Long> ids) {
        if (ids == null || !ids.containsKey("recipeId")) {
            return Optional.empty();
        }
        Long recipeId = ids.get("recipeId");
        if (recipeId == null) {
            return Optional.empty();
        }
        return Optional.of(recipeId);
    }

    @Override
    public Optional<String> uploadPhoto(Map<String, Long> ids, MultipartFile file) {
        // Extract recipeId from map
        Optional<Long> recipeIdOpt = extractRecipeId(ids);
        if (recipeIdOpt.isEmpty()) {
            logger.warn("Invalid ids map provided - must contain 'recipeId' key");
            return Optional.empty();
        }
        
        Long recipeId = recipeIdOpt.get();
        
        // Check if recipe exists
        Optional<Recipe> recipeOpt = recipeService.getRecipeById(recipeId);
        if (recipeOpt.isEmpty()) {
            return Optional.empty();
        }

        // Check if file is empty
        if (file.isEmpty()) {
            logger.warn("Attempted to upload empty file for recipe ID: {}", recipeId);
            return Optional.empty();
        }

        // Validate file type
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            logger.warn("File has no original filename for recipe ID: {}", recipeId);
            return Optional.empty();
        }

        String fileExtension = "";
        if (originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        if (!ALLOWED_IMAGE_EXTENSIONS.contains(fileExtension)) {
            logger.warn("Invalid file type attempted for recipe ID {}: {}", recipeId, fileExtension);
            return Optional.empty();
        }

        try {
            // Get the backend folder (current working directory)
            String backendRoot = System.getProperty("user.dir");
            Path photosDir = Paths.get(backendRoot, photosDirectory, String.valueOf(recipeId));

            // Create directory if it doesn't exist
            if (!Files.exists(photosDir)) {
                Files.createDirectories(photosDir);
                logger.debug("Created photos directory for recipe ID: {}", recipeId);
            }

            // Generate unique filename to avoid overwriting
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Save the file
            Path filePath = photosDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Update recipe with the filename (stored in DB for retrieval via /recipes/{id}/photo/{filename})
            // The recipe ID in the endpoint specifies the folder (photos/{id}/), filename specifies the file
            Recipe recipe = recipeOpt.get();
            
            // Store only the filename in the database - the recipe ID is already known from the recipe context
            // This allows easy construction of the endpoint URL: /recipes/{id}/photo/{filename}
            String imageUrl = uniqueFilename;
            
            // Ensure imageUrls list is initialized
            if (recipe.getImageUrls() == null) {
                recipe.setImageUrls(new ArrayList<>());
            }
            
            // Add the filename to the database
            recipe.getImageUrls().add(imageUrl);
            
            // Save to database - this will update the recipe_images table
            recipeService.saveRecipe(recipe);

            logger.info("Successfully uploaded photo for recipe ID {}: {} (saved to DB as filename)", recipeId, uniqueFilename);
            // Return only what is saved in the database (the filename)
            return Optional.of(imageUrl);

        } catch (IOException e) {
            logger.error("Failed to upload photo for recipe ID {}: {}", recipeId, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Resource> getPhoto(Long recipeId, String filename) {
        try {
            String backendRoot = System.getProperty("user.dir");
            Path photoPath = Paths.get(backendRoot, photosDirectory, String.valueOf(recipeId), filename);

            if (!Files.exists(photoPath) || !Files.isRegularFile(photoPath)) {
                return Optional.empty();
            }

            Resource resource = new UrlResource(photoPath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return Optional.of(resource);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Failed to get photo for recipe ID {} and filename {}: {}", recipeId, filename, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public List<String> getPhotoFilenames(Map<String, Long> ids) {
        // Extract recipeId from map
        Optional<Long> recipeIdOpt = extractRecipeId(ids);
        if (recipeIdOpt.isEmpty()) {
            logger.warn("Invalid ids map provided - must contain 'recipeId' key");
            return new ArrayList<>();
        }
        
        Long recipeId = recipeIdOpt.get();
        
        // Return filenames from the database (stored in recipe.imageUrls)
        // This ensures consistency - we return what's actually stored in the DB
        Optional<Recipe> recipeOpt = recipeService.getRecipeById(recipeId);
        if (recipeOpt.isEmpty()) {
            return new ArrayList<>();
        }

        Recipe recipe = recipeOpt.get();
        if (recipe.getImageUrls() == null || recipe.getImageUrls().isEmpty()) {
            return new ArrayList<>();
        }

        // Return the filenames stored in the database
        // These can be used to construct URLs: /recipes/{id}/photo/{filename}
        return new ArrayList<>(recipe.getImageUrls());
    }

    @Override
    public boolean deletePhoto(Long recipeId, String filename) {
        try {
            String backendRoot = System.getProperty("user.dir");
            Path photoPath = Paths.get(backendRoot, photosDirectory, String.valueOf(recipeId), filename);

            if (!Files.exists(photoPath)) {
                return false;
            }

            // Delete the file
            Files.delete(photoPath);

            // Remove from recipe's imageUrls and update database
            // Since we store only the filename in the database, remove by filename
            Optional<Recipe> recipeOpt = recipeService.getRecipeById(recipeId);
            if (recipeOpt.isPresent()) {
                Recipe recipe = recipeOpt.get();
                
                // Ensure imageUrls list is initialized
                if (recipe.getImageUrls() != null) {
                    // Remove the filename from the database (stored as just the filename)
                    recipe.getImageUrls().remove(filename);
                    // Save to database - this will update the recipe_images table
                    recipeService.saveRecipe(recipe);
                }
            }

            logger.info("Successfully deleted photo for recipe ID {}: {}", recipeId, filename);
            return true;
        } catch (IOException e) {
            logger.error("Failed to delete photo for recipe ID {} and filename {}: {}", recipeId, filename, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteAllPhotos(Map<String, Long> ids) {
        // Extract recipeId from map
        Optional<Long> recipeIdOpt = extractRecipeId(ids);
        if (recipeIdOpt.isEmpty()) {
            logger.warn("Invalid ids map provided - must contain 'recipeId' key");
            return false;
        }
        
        Long recipeId = recipeIdOpt.get();
        
        try {
            String backendRoot = System.getProperty("user.dir");
            Path photosDir = Paths.get(backendRoot, photosDirectory, String.valueOf(recipeId));

            if (!Files.exists(photosDir)) {
                return true; // Directory doesn't exist, consider it successful
            }

            // Delete all files in the directory
            try (Stream<Path> paths = Files.list(photosDir)) {
                paths.filter(Files::isRegularFile)
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                // Log error but continue
                            }
                        });
            }

            // Delete the directory itself
            Files.deleteIfExists(photosDir);

            // Clear recipe's imageUrls and update database
            Optional<Recipe> recipeOpt = recipeService.getRecipeById(recipeId);
            if (recipeOpt.isPresent()) {
                Recipe recipe = recipeOpt.get();
                
                // Ensure imageUrls list is initialized
                if (recipe.getImageUrls() != null) {
                    recipe.getImageUrls().clear();
                    // Save to database - this will clear the recipe_images table entries
                    recipeService.saveRecipe(recipe);
                }
            }

            logger.info("Successfully deleted all photos for recipe ID: {}", recipeId);
            return true;
        } catch (IOException e) {
            logger.error("Failed to delete all photos for recipe ID {}: {}", recipeId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public MediaType getContentType(String filename) {
        String lowerFilename = filename.toLowerCase();
        if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        } else if (lowerFilename.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        } else if (lowerFilename.endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        } else if (lowerFilename.endsWith(".webp")) {
            return MediaType.parseMediaType("image/webp");
        } else if (lowerFilename.endsWith(".bmp")) {
            return MediaType.parseMediaType("image/bmp");
        } else {
            // Default to octet stream if unknown
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
