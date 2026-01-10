package com.project.dass.Controller;

import com.project.dass.Model.Recipe;
import com.project.dass.Model.RecipeCategory;
import com.project.dass.Repos.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private RecipeRepository recipeRepository;

    @GetMapping(value = "/", produces = "application/json;charset=UTF-8")
    public String helloWorld(){
        return "Hello World";
    }

    // GET all recipes
    @GetMapping(value = "/recipes", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        List<Recipe> recipes = recipeRepository.findAll();
        return ResponseEntity.ok(recipes);
    }

    // GET recipe by ID
    @GetMapping(value = "/recipes/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        Optional<Recipe> recipe = recipeRepository.findById(id);
        return recipe.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET recipes by category
    @GetMapping(value = "/recipes/category/{category}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<Recipe>> getRecipesByCategory(@PathVariable RecipeCategory category) {
        List<Recipe> recipes = recipeRepository.findByCategory(category);
        return ResponseEntity.ok(recipes);
    }

    // GET recipes by search term (title)
    @GetMapping(value = "/recipes/search", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<Recipe>> searchRecipes(@RequestParam String title) {
        List<Recipe> recipes = recipeRepository.findByTitleContainingIgnoreCase(title);
        return ResponseEntity.ok(recipes);
    }

    // POST create new recipe
    @PostMapping(value = "/recipes", produces = "application/json;charset=UTF-8")
    @Transactional
    public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe) {
        Recipe savedRecipe = recipeRepository.save(recipe);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRecipe);
    }

    // PUT update recipe
    @PutMapping(value = "/recipes/{id}", produces = "application/json;charset=UTF-8")
    @Transactional
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @RequestBody Recipe recipeDetails) {
        Optional<Recipe> optionalRecipe = recipeRepository.findById(id);
        
        if (optionalRecipe.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Recipe recipe = optionalRecipe.get();
        recipe.setTitle(recipeDetails.getTitle());
        recipe.setDifficulty(recipeDetails.getDifficulty());
        recipe.setCategory(recipeDetails.getCategory());
        recipe.setPrepTimeMinutes(recipeDetails.getPrepTimeMinutes());
        recipe.setTotalTimeMinutes(recipeDetails.getTotalTimeMinutes());
        recipe.setImageUrls(recipeDetails.getImageUrls());

        // Update ingredients
        if (recipeDetails.getIngredients() != null) {
            recipe.getIngredients().clear();
            recipeDetails.getIngredients().forEach(ingredient -> {
                ingredient.setRecipe(recipe);
                recipe.getIngredients().add(ingredient);
            });
        }

        // Update steps
        if (recipeDetails.getSteps() != null) {
            recipe.getSteps().clear();
            recipeDetails.getSteps().forEach(step -> {
                step.setRecipe(recipe);
                recipe.getSteps().add(step);
            });
        }

        Recipe updatedRecipe = recipeRepository.save(recipe);
        return ResponseEntity.ok(updatedRecipe);
    }

    // DELETE recipe
    @DeleteMapping(value = "/recipes/{id}")
    @Transactional
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        if (!recipeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        recipeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
