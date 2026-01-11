package com.project.dass.Controller;

import com.project.dass.Model.Recipe;
import com.project.dass.Model.RecipeCategory;
import com.project.dass.Service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // Επιτρέπει στο React να μιλάει με το Spring
public class ApiController {

    @Autowired
    private RecipeService recipeService; // Αλλαγή από Repository σε Service

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
}