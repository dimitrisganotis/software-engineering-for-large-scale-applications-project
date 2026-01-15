package com.project.dass.Service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PhotoService {
    
    /**
     * Upload a photo for a recipe or recipe step
     * @param ids Map containing "recipeId" (and optionally "stepId" for step photos)
     * @param file The file to upload
     * @return The filename of the saved photo, or empty if upload failed
     */
    Optional<String> uploadPhoto(Map<String, Long> ids, MultipartFile file);
    
    /**
     * Get a photo file as a resource
     * @param recipeId The ID of the recipe (to determine the folder)
     * @param filename The name of the photo file
     * @return The resource if found, empty otherwise
     */
    Optional<Resource> getPhoto(Long recipeId, String filename);
    
    /**
     * Get all photo filenames for a recipe or recipe step
     * @param ids Map containing "recipeId" (and optionally "stepId" for step photos)
     * @return List of photo filenames
     */
    List<String> getPhotoFilenames(Map<String, Long> ids);
    
    /**
     * Delete a photo
     * @param recipeId The ID of the recipe (to determine the folder)
     * @param filename The name of the photo file to delete
     * @return true if deleted successfully, false otherwise
     */
    boolean deletePhoto(Long recipeId, String filename);
    
    /**
     * Delete all photos for a recipe or recipe step
     * @param ids Map containing "recipeId" (and optionally "stepId" for step photos)
     * @return true if all photos deleted successfully, false otherwise
     */
    boolean deleteAllPhotos(Map<String, Long> ids);
    
    /**
     * Get content type for a file based on its extension
     * @param filename The filename
     * @return The MediaType for the file
     */
    org.springframework.http.MediaType getContentType(String filename);
}
