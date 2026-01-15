package com.project.dass.ServiceImpl;

import com.project.dass.Model.Recipe;
import com.project.dass.Model.RecipeStep;
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

@Service("stepPhotoService")
@Transactional // Ensures database operations are atomic
public class StepPhotoServiceImpl implements PhotoService {

    private static final Logger logger = LoggerFactory.getLogger(StepPhotoServiceImpl.class);
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp"
    );

    @Value("${photos.directory:photos}")
    private String photosDirectory;

    private final RecipeService recipeService;

    public StepPhotoServiceImpl(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    /**
     * Extract recipeId and stepId from the map
     */
    private Optional<Long[]> extractIds(Map<String, Long> ids) {
        if (ids == null || !ids.containsKey("recipeId") || !ids.containsKey("stepId")) {
            return Optional.empty();
        }
        Long recipeId = ids.get("recipeId");
        Long stepId = ids.get("stepId");
        if (recipeId == null || stepId == null) {
            return Optional.empty();
        }
        return Optional.of(new Long[]{recipeId, stepId});
    }

    @Override
    public Optional<String> uploadPhoto(Map<String, Long> ids, MultipartFile file) {
        // Extract recipeId and stepId from map
        Optional<Long[]> idsOpt = extractIds(ids);
        if (idsOpt.isEmpty()) {
            logger.warn("Invalid ids map provided - must contain 'recipeId' and 'stepId' keys");
            return Optional.empty();
        }
        
        Long recipeId = idsOpt.get()[0];
        Long stepId = idsOpt.get()[1];
        
        // Check if recipe exists
        Optional<Recipe> recipeOpt = recipeService.getRecipeById(recipeId);
        if (recipeOpt.isEmpty()) {
            logger.warn("Recipe not found with ID: {}", recipeId);
            return Optional.empty();
        }

        Recipe recipe = recipeOpt.get();
        
        // Check if step exists and belongs to this recipe
        Optional<RecipeStep> stepOpt = recipe.getSteps().stream()
                .filter(step -> step.getId().equals(stepId))
                .findFirst();
        
        if (stepOpt.isEmpty()) {
            logger.warn("Step not found with ID {} for recipe ID: {}", stepId, recipeId);
            return Optional.empty();
        }

        // Check if file is empty
        if (file.isEmpty()) {
            logger.warn("Attempted to upload empty file for recipe ID: {}, step ID: {}", recipeId, stepId);
            return Optional.empty();
        }

        // Validate file type
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            logger.warn("File has no original filename for recipe ID: {}, step ID: {}", recipeId, stepId);
            return Optional.empty();
        }

        String fileExtension = "";
        if (originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        if (!ALLOWED_IMAGE_EXTENSIONS.contains(fileExtension)) {
            logger.warn("Invalid file type attempted for recipe ID {}, step ID {}: {}", recipeId, stepId, fileExtension);
            return Optional.empty();
        }

        try {
            // Get the backend folder (current working directory)
            String backendRoot = System.getProperty("user.dir");
            // Save to the same folder as recipe photos: photos/{recipeId}/
            Path photosDir = Paths.get(backendRoot, photosDirectory, String.valueOf(recipeId));

            // Create directory if it doesn't exist
            if (!Files.exists(photosDir)) {
                Files.createDirectories(photosDir);
                logger.debug("Created photos directory for recipe ID: {}", recipeId);
            }

            // Generate unique filename with step ID prefix: stepId_uuid.extension
            String uniqueFilename = stepId + "_" + UUID.randomUUID().toString() + fileExtension;

            // Save the file
            Path filePath = photosDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Update step with the filename (stored in DB as imageUrl)
            RecipeStep step = stepOpt.get();
            // Store only the filename in the database (format: stepId_uuid.extension)
            String imageUrl = uniqueFilename;
            step.setImageUrl(imageUrl);
            
            // Save to database - this will update the recipe_step's imageUrl field
            recipeService.saveRecipe(recipe);

            logger.info("Successfully uploaded photo for recipe ID {}, step ID {}: {} (saved to DB as filename)", 
                    recipeId, stepId, uniqueFilename);
            // Return only what is saved in the database (the filename)
            return Optional.of(imageUrl);

        } catch (IOException e) {
            logger.error("Failed to upload photo for recipe ID {}, step ID {}: {}", 
                    recipeId, stepId, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Resource> getPhoto(Long recipeId, String filename) {
        try {
            String backendRoot = System.getProperty("user.dir");
            // Photos are stored in the same folder as recipe photos: photos/{recipeId}/
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
            logger.error("Failed to get photo for recipe ID {} and filename {}: {}", 
                    recipeId, filename, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public List<String> getPhotoFilenames(Map<String, Long> ids) {
        // Extract recipeId and stepId from map
        Optional<Long[]> idsOpt = extractIds(ids);
        if (idsOpt.isEmpty()) {
            logger.warn("Invalid ids map provided - must contain 'recipeId' and 'stepId' keys");
            return new ArrayList<>();
        }
        
        Long recipeId = idsOpt.get()[0];
        Long stepId = idsOpt.get()[1];
        
        // Return filename from the database (stored in recipeStep.imageUrl)
        // This ensures consistency - we return what's actually stored in the DB
        Optional<Recipe> recipeOpt = recipeService.getRecipeById(recipeId);
        if (recipeOpt.isEmpty()) {
            return new ArrayList<>();
        }

        Recipe recipe = recipeOpt.get();
        Optional<RecipeStep> stepOpt = recipe.getSteps().stream()
                .filter(step -> step.getId().equals(stepId))
                .findFirst();

        if (stepOpt.isEmpty()) {
            return new ArrayList<>();
        }

        RecipeStep step = stepOpt.get();
        if (step.getImageUrl() == null || step.getImageUrl().isEmpty()) {
            return new ArrayList<>();
        }

        // Return the filename stored in the database
        // Format: stepId_uuid.extension
        List<String> filenames = new ArrayList<>();
        filenames.add(step.getImageUrl());
        return filenames;
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

            // Remove from step's imageUrl and update database
            Optional<Recipe> recipeOpt = recipeService.getRecipeById(recipeId);
            if (recipeOpt.isPresent()) {
                Recipe recipe = recipeOpt.get();
                
                // Find the step that has this filename
                recipe.getSteps().stream()
                        .filter(step -> filename.equals(step.getImageUrl()))
                        .findFirst()
                        .ifPresent(step -> {
                            step.setImageUrl(null);
                            // Save to database - this will update the recipe_step's imageUrl field
                            recipeService.saveRecipe(recipe);
                        });
            }

            logger.info("Successfully deleted photo for recipe ID {} and filename {}", recipeId, filename);
            return true;
        } catch (IOException e) {
            logger.error("Failed to delete photo for recipe ID {} and filename {}: {}", 
                    recipeId, filename, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteAllPhotos(Map<String, Long> ids) {
        // Extract recipeId and stepId from map
        Optional<Long[]> idsOpt = extractIds(ids);
        if (idsOpt.isEmpty()) {
            logger.warn("Invalid ids map provided - must contain 'recipeId' and 'stepId' keys");
            return false;
        }
        
        Long recipeId = idsOpt.get()[0];
        Long stepId = idsOpt.get()[1];
        
        try {
            // Get the step and its imageUrl
            Optional<Recipe> recipeOpt = recipeService.getRecipeById(recipeId);
            if (recipeOpt.isEmpty()) {
                return false;
            }

            Recipe recipe = recipeOpt.get();
            Optional<RecipeStep> stepOpt = recipe.getSteps().stream()
                    .filter(step -> step.getId().equals(stepId))
                    .findFirst();

            if (stepOpt.isEmpty()) {
                return false;
            }

            RecipeStep step = stepOpt.get();
            String imageUrl = step.getImageUrl();

            if (imageUrl == null || imageUrl.isEmpty()) {
                return true; // No photo to delete, consider it successful
            }

            // Delete the file
            String backendRoot = System.getProperty("user.dir");
            Path photoPath = Paths.get(backendRoot, photosDirectory, String.valueOf(recipeId), imageUrl);

            if (Files.exists(photoPath)) {
                Files.delete(photoPath);
            }

            // Clear step's imageUrl and update database
            step.setImageUrl(null);
            recipeService.saveRecipe(recipe);

            logger.info("Successfully deleted all photos for recipe ID {}, step ID: {}", recipeId, stepId);
            return true;
        } catch (IOException e) {
            logger.error("Failed to delete all photos for recipe ID {}, step ID {}: {}", 
                    recipeId, stepId, e.getMessage(), e);
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
