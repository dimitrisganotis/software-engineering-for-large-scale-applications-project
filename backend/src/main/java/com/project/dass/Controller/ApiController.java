package com.project.dass.Controller;

import com.project.dass.Model.Recipe;
import com.project.dass.Model.RecipeCategory;
import com.project.dass.Repos.RecipeRepository;
import com.project.dass.Service.PhotoService;
import com.project.dass.Service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Επιτρέπει στο React να μιλάει με το Spring
public class ApiController {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    @Qualifier("photoService")
    private PhotoService photoService;

    @Autowired
    @Qualifier("stepPhotoService")
    private PhotoService stepPhotoService;

    @GetMapping(value = "/", produces = "application/json;charset=UTF-8")
    public String helloWorld(){
        return "Hello World";
    }

    // GET all recipes
    @GetMapping(value = "/recipes", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    // GET recipe by ID
    @GetMapping(value = "/recipes/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        Optional<Recipe> recipe = recipeService.getRecipeById(id);
        return recipe.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET recipes by category
        @GetMapping(value = "/recipes/category/{category}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<Recipe>> getRecipesByCategory(@PathVariable RecipeCategory category) {
        return ResponseEntity.ok(recipeService.getRecipesByCategory(category));
    }

    // GET recipes by search term
    @GetMapping(value = "/recipes/search", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<Recipe>> searchRecipes(@RequestParam String title) {
        return ResponseEntity.ok(recipeService.searchRecipes(title));
    }

    // POST create new recipe
    @PostMapping(value = "/recipes", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe) {
        Recipe savedRecipe = recipeService.saveRecipe(recipe);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRecipe);
    }

    // PUT update recipe
    @PutMapping(value = "/recipes/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @RequestBody Recipe recipeDetails) {
        // Καλούμε το Service να κάνει όλη τη δουλειά
        Optional<Recipe> updatedRecipe = recipeService.updateRecipe(id, recipeDetails);
        // Αν γυρίσει αποτέλεσμα -> 200 OK, αλλιώς -> 404 Not Found
        return updatedRecipe
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE recipe
    @DeleteMapping(value = "/recipes/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        if (recipeService.getRecipeById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    // --- NEW ENDPOINT: PROGRESS CALCULATION ---
    // Αυτό λείπει και είναι απαραίτητο για το παραδοτέο Π2.2
    @GetMapping(value = "/recipes/{id}/progress", produces = "application/json")
    public ResponseEntity<Double> getExecutionProgress(
            @PathVariable Long id,
            @RequestParam int completedStepOrder) {

        Optional<Recipe> recipeOpt = recipeService.getRecipeById(id);
        if (recipeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        double progress = recipeService.calculateProgress(recipeOpt.get(), completedStepOrder);
        return ResponseEntity.ok(progress);
    }

    // --- PHOTO CRUD ENDPOINTS ---

    // POST - Upload photo
    @PostMapping(value = "/recipes/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        Map<String, Long> ids = new HashMap<>();
        ids.put("recipeId", id);
        Optional<String> imageUrl = photoService.uploadPhoto(ids, file);

        if (imageUrl.isEmpty()) {
            // Check if recipe exists to provide better error message
            if (recipeService.getRecipeById(id).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Recipe not found with id: " + id);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to upload photo. File may be empty or invalid.");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body("Photo uploaded successfully: " + imageUrl.get());
    }

    // GET - Get photo file
    @GetMapping(value = "/recipes/{id}/photo/{filename}")
    public ResponseEntity<Resource> getPhoto(
            @PathVariable Long id,
            @PathVariable String filename) {

        Optional<Resource> resource = photoService.getPhoto(id, filename);

        if (resource.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Determine content type based on file extension
        MediaType mediaType = photoService.getContentType(filename);
        
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource.get());
    }

    // GET - List all photos for a recipe
    @GetMapping(value = "/recipes/{id}/photos", produces = "application/json")
    public ResponseEntity<List<String>> getPhotoFilenames(@PathVariable Long id) {
        Map<String, Long> ids = new HashMap<>();
        ids.put("recipeId", id);
        List<String> filenames = photoService.getPhotoFilenames(ids);
        return ResponseEntity.ok(filenames);
    }

    // DELETE - Delete a specific photo
    @DeleteMapping(value = "/recipes/{id}/photo/{filename}")
    public ResponseEntity<Void> deletePhoto(
            @PathVariable Long id,
            @PathVariable String filename) {

        boolean deleted = photoService.deletePhoto(id, filename);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    // DELETE - Delete all photos for a recipe
    @DeleteMapping(value = "/recipes/{id}/photos")
    public ResponseEntity<Void> deleteAllPhotos(@PathVariable Long id) {
        Map<String, Long> ids = new HashMap<>();
        ids.put("recipeId", id);
        boolean deleted = photoService.deleteAllPhotos(ids);

        if (!deleted) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.noContent().build();
    }

    // --- STEP PHOTO CRUD ENDPOINTS ---

    // POST - Upload photo for a recipe step
    @PostMapping(value = "/recipes/{recipeId}/steps/{stepId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadStepPhoto(
            @PathVariable Long recipeId,
            @PathVariable Long stepId,
            @RequestParam("file") MultipartFile file) {

        Map<String, Long> ids = new HashMap<>();
        ids.put("recipeId", recipeId);
        ids.put("stepId", stepId);
        Optional<String> imageUrl = stepPhotoService.uploadPhoto(ids, file);

        if (imageUrl.isEmpty()) {
            // Check if recipe exists to provide better error message
            Optional<Recipe> recipeOpt = recipeService.getRecipeById(recipeId);
            if (recipeOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Recipe not found with id: " + recipeId);
            }
            // Check if step exists
            boolean stepExists = recipeOpt.get().getSteps().stream()
                    .anyMatch(step -> step.getId().equals(stepId));
            if (!stepExists) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Step not found with id: " + stepId + " for recipe id: " + recipeId);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to upload photo. File may be empty or invalid.");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body("Photo uploaded successfully: " + imageUrl.get());
    }

    // GET - Get photo file for a recipe step
    @GetMapping(value = "/recipes/{recipeId}/steps/{stepId}/photo/{filename}")
    public ResponseEntity<Resource> getStepPhoto(
            @PathVariable Long recipeId,
            @PathVariable Long stepId,
            @PathVariable String filename) {

        Optional<Resource> resource = stepPhotoService.getPhoto(recipeId, filename);

        if (resource.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Determine content type based on file extension
        MediaType mediaType = stepPhotoService.getContentType(filename);
        
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource.get());
    }

    // DELETE - Delete a specific photo for a recipe step
    @DeleteMapping(value = "/recipes/{recipeId}/steps/{stepId}/photo/{filename}")
    public ResponseEntity<Void> deleteStepPhoto(
            @PathVariable Long recipeId,
            @PathVariable Long stepId,
            @PathVariable String filename) {

        boolean deleted = stepPhotoService.deletePhoto(recipeId, filename);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    // DELETE - Delete all photos for a recipe step
    @DeleteMapping(value = "/recipes/{recipeId}/steps/{stepId}/photos")
    public ResponseEntity<Void> deleteAllStepPhotos(
            @PathVariable Long recipeId,
            @PathVariable Long stepId) {
        Map<String, Long> ids = new HashMap<>();
        ids.put("recipeId", recipeId);
        ids.put("stepId", stepId);
        boolean deleted = stepPhotoService.deleteAllPhotos(ids);

        if (!deleted) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.noContent().build();
    }

}