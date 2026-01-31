package com.project.dass.ServiceImpl;

import com.project.dass.Model.*;
import com.project.dass.Repos.RecipeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.dass.Service.RecipeService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;

    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

   //calculate total time based on steps only
    private void calculateAndSetTotalTime(Recipe recipe) {
        int stepsDuration = 0;

        // sum durations of all steps
        if (recipe.getSteps() != null) {
            stepsDuration = recipe.getSteps().stream()
                    .mapToInt(step -> step.getDurationMinutes() != null ? step.getDurationMinutes() : 0)
                    .sum();
        }
        // Total time is the sum of step durations now
        recipe.setTotalTimeMinutes(stepsDuration);
    }

    // --- BASIC  OPERATIONS ---

    @Override
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    @Override
    public Optional<Recipe> getRecipeById(Long id) {
        return recipeRepository.findById(id);
    }

    @Override
    public Recipe saveRecipe(Recipe recipe) {
        // Calculate and set total time based on steps
        calculateAndSetTotalTime(recipe);

        // Link main ingredients to recipe
        if (recipe.getIngredients() != null) {
            recipe.getIngredients().forEach(ing -> ing.setRecipe(recipe));
        }

        // Link steps to recipe and unify ingredients
        if (recipe.getSteps() != null) {
            recipe.getSteps().forEach(step -> {
                step.setRecipe(recipe);

                if (step.getIngredients() != null && recipe.getIngredients() != null) {
                    java.util.List<Ingredient> unifiedList = new java.util.ArrayList<>();

                    for (Ingredient stepIng : step.getIngredients()) {
                        recipe.getIngredients().stream()
                                .filter(mainIng -> mainIng.getName().equals(stepIng.getName()) &&
                                        mainIng.getQuantity().equals(stepIng.getQuantity()) &&
                                        mainIng.getUnit().equals(stepIng.getUnit()))
                                .findFirst()
                                .ifPresent(unifiedList::add);
                    }
                    step.setIngredients(unifiedList);
                }
            });
        }
        return recipeRepository.save(recipe);
    }

    @Override
    public Optional<Recipe> updateRecipe(Long id, Recipe recipeDetails) {
        return recipeRepository.findById(id).map(existingRecipe -> {

            // Update basic fields
            existingRecipe.setTitle(recipeDetails.getTitle());
            existingRecipe.setDifficulty(recipeDetails.getDifficulty());
            existingRecipe.setCategory(recipeDetails.getCategory());

            existingRecipe.setImageUrls(recipeDetails.getImageUrls());

            // Update ingredients
            if (recipeDetails.getIngredients() != null) {
                existingRecipe.getIngredients().clear();
                recipeDetails.getIngredients().forEach(ingredient -> {
                    ingredient.setRecipe(existingRecipe);
                    existingRecipe.getIngredients().add(ingredient);
                });
            }

            // Update steps
            if (recipeDetails.getSteps() != null) {
                existingRecipe.getSteps().clear();
                recipeDetails.getSteps().forEach(step -> {
                    step.setRecipe(existingRecipe);
                    existingRecipe.getSteps().add(step);
                });
            }

            // Calculate and set total time
            calculateAndSetTotalTime(existingRecipe);

            // Save updated recipe
            return recipeRepository.save(existingRecipe);
        });
    }

    @Override
    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }

    @Override
    public List<Recipe> searchRecipes(String keyword) {
        return recipeRepository.findByTitleContainingIgnoreCase(keyword);
    }

    @Override
    public List<Recipe> getRecipesByCategory(RecipeCategory category) {
        return recipeRepository.findByCategory(category);
    }

    // --- EXECUTION & PROGRESS BAR ---

    @Override
    public double calculateProgress(Recipe recipe, int lastCompletedStepOrder) {
        if (recipe.getSteps() == null || recipe.getSteps().isEmpty()) {
            return 0.0;
        }

        double totalDuration = recipe.getTotalTimeMinutes();
        if (totalDuration == 0) return 0.0;

        double completedTime = 0.0;

        for (RecipeStep step : recipe.getSteps()) {
            if (step.getStepOrder() <= lastCompletedStepOrder) {
                completedTime += step.getDurationMinutes();
            }
        }

        double progress = (completedTime / totalDuration) * 100.0;
        return Math.min(progress, 100.0);
    }
}