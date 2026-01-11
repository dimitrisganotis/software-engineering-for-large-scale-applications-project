package com.project.dass.Service;

import com.project.dass.Model.Recipe;
import com.project.dass.Model.RecipeCategory;
import java.util.List;
import java.util.Optional;

// Εδώ ορίζουμε ΜΟΝΟ τα συμβόλαια (τις μεθόδους), όχι τον κώδικα.
public interface RecipeService {

    List<Recipe> getAllRecipes();

    Optional<Recipe> getRecipeById(Long id);

    Recipe saveRecipe(Recipe recipe);

    void deleteRecipe(Long id);

    List<Recipe> searchRecipes(String keyword);

    Optional<Recipe> updateRecipe(Long id, Recipe recipeDetails);

    List<Recipe> getRecipesByCategory(RecipeCategory category);

    double calculateProgress(Recipe recipe, int lastCompletedStepOrder);
}