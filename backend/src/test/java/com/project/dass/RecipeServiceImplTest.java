package com.project.dass;

import com.project.dass.Model.*;
import com.project.dass.Repos.RecipeRepository;
import com.project.dass.ServiceImpl.RecipeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceImplTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeServiceImpl recipeService;


    @Test
    void saveRecipe_ShouldCalculateTotalTimeFromSteps() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Test Pasta");

        RecipeStep step1 = new RecipeStep();
        step1.setDurationMinutes(10);
        RecipeStep step2 = new RecipeStep();
        step2.setDurationMinutes(20);

        recipe.setSteps(Arrays.asList(step1, step2));

        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        Recipe savedRecipe = recipeService.saveRecipe(recipe);

        assertEquals(30, savedRecipe.getTotalTimeMinutes(), "Total time should be the sum of step durations");

        verify(recipeRepository, times(1)).save(recipe);
    }

    // Test Calculate Progress
    @Test
    void calculateProgress_ShouldReturnCorrectPercentage() {
        // Arrange
        Recipe recipe = new Recipe();

        RecipeStep step1 = new RecipeStep();
        step1.setStepOrder(1);
        step1.setDurationMinutes(10);

        RecipeStep step2 = new RecipeStep();
        step2.setStepOrder(2);
        step2.setDurationMinutes(30);

        RecipeStep step3 = new RecipeStep();
        step3.setStepOrder(3);
        step3.setDurationMinutes(10);

        recipe.setSteps(Arrays.asList(step1, step2, step3));
        // Total time (10+30+10)
        recipe.setTotalTimeMinutes(50);

        // Act
        // Total time: 10 + 30 = 40.
        // Progress: 40 / 50 = 0.8 -> 80%
        double progress = recipeService.calculateProgress(recipe, 2);

        // Assert
        assertEquals(80.0, progress, 0.01);
    }

    // Update Recipe (also check time recalculation)
    @Test
    void updateRecipe_ShouldRecalculateTime() {
        // Arrange
        Long recipeId = 1L;

        // Old recipe data
        Recipe existingRecipe = new Recipe();
        existingRecipe.setId(recipeId);
        existingRecipe.setTitle("Old Title");
        existingRecipe.setTotalTimeMinutes(100);

        // New data for update
        Recipe newDetails = new Recipe();
        newDetails.setTitle("New Title");
        RecipeStep newStep = new RecipeStep();
        newStep.setDurationMinutes(15);
        newDetails.setSteps(Arrays.asList(newStep));

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(existingRecipe));
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Recipe> result = recipeService.updateRecipe(recipeId, newDetails);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("New Title", result.get().getTitle());
        assertEquals(15, result.get().getTotalTimeMinutes());
    }

    //  Ingredients Parent Link
    @Test
    void saveRecipe_ShouldSetParentOnIngredients() {
        // Arrange
        Recipe recipe = new Recipe();
        Ingredient ing1 = new Ingredient(); // No parent yet
        recipe.setIngredients(List.of(ing1));

        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        // Act
        recipeService.saveRecipe(recipe);

        // Assert
        //  ing1.setRecipe(recipe)
        assertEquals(recipe, ing1.getRecipe(), "The step should show bck in the recipe");
    }

    //  Edge Case - Devision by Zero in Progress Calculation
    @Test
    void calculateProgress_ZeroTotalTime_ShouldReturnZero() {
        Recipe recipe = new Recipe();
        recipe.setTotalTimeMinutes(0);

        double progress = recipeService.calculateProgress(recipe, 1);

        assertEquals(0.0, progress);
    }
}