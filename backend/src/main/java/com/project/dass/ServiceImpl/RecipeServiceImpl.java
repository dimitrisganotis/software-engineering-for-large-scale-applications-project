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
@Transactional // Εξασφαλίζει ότι οι αλλαγές στη βάση γίνονται ατομικά (ACID)
public class RecipeServiceImpl implements RecipeService{

    private final RecipeRepository recipeRepository;

    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    // --- BASIC CRUD OPERATIONS ---

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
        // Αν είναι update, πρέπει να ξανα-συνδέσουμε τα παιδιά με τον γονέα
        // για σιγουριά, αν και οι helper methods το κάνουν ήδη.
        if (recipe.getSteps() != null) {
            recipe.getSteps().forEach(step -> step.setRecipe(recipe));
        }
        if (recipe.getIngredients() != null) {
            recipe.getIngredients().forEach(ing -> ing.setRecipe(recipe));
        }
        return recipeRepository.save(recipe);
    }

    @Override
    public Optional<Recipe> updateRecipe(Long id, Recipe recipeDetails) {
        return recipeRepository.findById(id).map(existingRecipe -> {

            // 1. Ενημέρωση απλών πεδίων
            existingRecipe.setTitle(recipeDetails.getTitle());
            existingRecipe.setDifficulty(recipeDetails.getDifficulty());
            existingRecipe.setCategory(recipeDetails.getCategory());
            existingRecipe.setPrepTimeMinutes(recipeDetails.getPrepTimeMinutes());
            existingRecipe.setTotalTimeMinutes(recipeDetails.getTotalTimeMinutes());
            existingRecipe.setImageUrls(recipeDetails.getImageUrls());

            // 2. Ενημέρωση Ingredients (Καθαρισμός & Προσθήκη)
            if (recipeDetails.getIngredients() != null) {
                existingRecipe.getIngredients().clear(); // Σβήνουμε τα παλιά
                recipeDetails.getIngredients().forEach(ingredient -> {
                    ingredient.setRecipe(existingRecipe); // Συνδέουμε με τον γονέα
                    existingRecipe.getIngredients().add(ingredient); // Προσθέτουμε τα νέα
                });
            }

            // 3. Ενημέρωση Steps
            if (recipeDetails.getSteps() != null) {
                existingRecipe.getSteps().clear();
                recipeDetails.getSteps().forEach(step -> {
                    step.setRecipe(existingRecipe);
                    existingRecipe.getSteps().add(step);
                });
            }

            // 4. Αποθήκευση
            return recipeRepository.save(existingRecipe);
        });
    }

    @Override
    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }

    @Override
    public List<Recipe> searchRecipes(String keyword) {
        // Αναζήτηση στον τίτλο (case insensitive)
        return recipeRepository.findByTitleContainingIgnoreCase(keyword);
    }

    @Override
    public List<Recipe> getRecipesByCategory(RecipeCategory category) {
        return recipeRepository.findByCategory(category);
    }

    // --- BUSINESS LOGIC: EXECUTION & PROGRESS BAR ---

    /**
     * Υπολογίζει την πρόοδο (0.0 έως 100.0) με βάση το χρόνο των ολοκληρωμένων βημάτων.
     * @param recipe Η συνταγή
     * @param lastCompletedStepOrder Το νούμερο του βήματος που μόλις τελείωσε (π.χ. 2)
     */
    @Override
    public double calculateProgress(Recipe recipe, int lastCompletedStepOrder) {
        if (recipe.getSteps() == null || recipe.getSteps().isEmpty()) {
            return 0.0;
        }

        double totalDuration = recipe.getTotalTimeMinutes();
        if (totalDuration == 0) return 0.0; // Αποφυγή διαίρεσης με το μηδέν

        double completedTime = 0.0;

        for (RecipeStep step : recipe.getSteps()) {
            if (step.getStepOrder() <= lastCompletedStepOrder) {
                completedTime += step.getDurationMinutes();
            }
        }

        // Υπολογισμός ποσοστού
        double progress = (completedTime / totalDuration) * 100.0;

        // Επιστρέφουμε το μέγιστο 100% (σε περίπτωση λάθους στους χρόνους)
        return Math.min(progress, 100.0);
    }
}