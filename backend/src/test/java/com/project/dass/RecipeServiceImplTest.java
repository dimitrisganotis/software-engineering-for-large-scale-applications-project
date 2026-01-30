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

@ExtendWith(MockitoExtension.class) // Ενεργοποιεί το Mockito
class RecipeServiceImplTest {

    @Mock
    private RecipeRepository recipeRepository; // Ψεύτικο Repository

    @InjectMocks
    private RecipeServiceImpl recipeService; // Το Service που τεστάρουμε

    // --- ΤΕΣΤ 1: Υπολογισμός Συνολικού Χρόνου κατά το Save ---
    @Test
    void saveRecipe_ShouldCalculateTotalTimeFromSteps() {
        // 1. Προετοιμασία δεδομένων (Arrange)
        Recipe recipe = new Recipe();
        recipe.setTitle("Test Pasta");

        RecipeStep step1 = new RecipeStep();
        step1.setDurationMinutes(10); // Βράσιμο
        RecipeStep step2 = new RecipeStep();
        step2.setDurationMinutes(20); // Σάλτσα

        recipe.setSteps(Arrays.asList(step1, step2));

        // Όταν καλέσουμε το save, να επιστρέψει το ίδιο recipe
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        // 2. Εκτέλεση (Act)
        Recipe savedRecipe = recipeService.saveRecipe(recipe);

        // 3. Έλεγχος (Assert)
        // Περιμένουμε 10 + 20 = 30 λεπτά
        assertEquals(30, savedRecipe.getTotalTimeMinutes(), "Ο συνολικός χρόνος πρέπει να είναι το άθροισμα των βημάτων");

        // Επιβεβαίωση ότι καλέστηκε η save του repository
        verify(recipeRepository, times(1)).save(recipe);
    }

    // --- ΤΕΣΤ 2: Υπολογισμός Προόδου (Business Logic) ---
    @Test
    void calculateProgress_ShouldReturnCorrectPercentage() {
        // Arrange
        Recipe recipe = new Recipe();

        RecipeStep step1 = new RecipeStep();
        step1.setStepOrder(1);
        step1.setDurationMinutes(10);

        RecipeStep step2 = new RecipeStep();
        step2.setStepOrder(2);
        step2.setDurationMinutes(30); // Μεγάλο βήμα

        RecipeStep step3 = new RecipeStep();
        step3.setStepOrder(3);
        step3.setDurationMinutes(10);

        recipe.setSteps(Arrays.asList(step1, step2, step3));
        // Total time θα υπολογιστεί 50 λεπτά (10+30+10)
        // Αν το saveRecipe δουλεύει σωστά, θα το έβαζε. Εδώ το βάζουμε χειροκίνητα για το τεστ της μεθόδου calculateProgress
        recipe.setTotalTimeMinutes(50);

        // Act
        // Έχουμε ολοκληρώσει μέχρι και το βήμα 2.
        // Ολοκληρωμένος χρόνος: 10 + 30 = 40.
        // Πρόοδος: 40 / 50 = 0.8 -> 80%
        double progress = recipeService.calculateProgress(recipe, 2);

        // Assert
        assertEquals(80.0, progress, 0.01);
    }

    // --- ΤΕΣΤ 3: Update Recipe (Έλεγχος ότι ξανα-υπολογίζει το χρόνο) ---
    @Test
    void updateRecipe_ShouldRecalculateTime() {
        // Arrange
        Long recipeId = 1L;

        // Η παλιά συνταγή στη βάση
        Recipe existingRecipe = new Recipe();
        existingRecipe.setId(recipeId);
        existingRecipe.setTitle("Old Title");
        existingRecipe.setTotalTimeMinutes(100); // Παλιός χρόνος

        // Τα νέα δεδομένα από το Frontend
        Recipe newDetails = new Recipe();
        newDetails.setTitle("New Title");
        RecipeStep newStep = new RecipeStep();
        newStep.setDurationMinutes(15); // Νέος χρόνος μόνο 15 λεπτά
        newDetails.setSteps(Arrays.asList(newStep));

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(existingRecipe));
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Recipe> result = recipeService.updateRecipe(recipeId, newDetails);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("New Title", result.get().getTitle());
        // Ο χρόνος πρέπει να έγινε 15 (από το νέο βήμα) και όχι να έμεινε 100
        assertEquals(15, result.get().getTotalTimeMinutes());
    }

    // --- ΤΕΣΤ 4: Διαχείριση Σχέσεων (Ingredients Parent Link) ---
    @Test
    void saveRecipe_ShouldSetParentOnIngredients() {
        // Arrange
        Recipe recipe = new Recipe();
        Ingredient ing1 = new Ingredient(); // Δεν έχει parent ακόμα
        recipe.setIngredients(List.of(ing1));

        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        // Act
        recipeService.saveRecipe(recipe);

        // Assert
        // Ελέγχουμε αν το service έκανε ing1.setRecipe(recipe)
        assertEquals(recipe, ing1.getRecipe(), "Το συστατικό πρέπει να δείχνει πίσω στη συνταγή");
    }

    // --- ΤΕΣΤ 5: Διαίρεση με το μηδέν (Edge Case) ---
    @Test
    void calculateProgress_ZeroTotalTime_ShouldReturnZero() {
        Recipe recipe = new Recipe();
        recipe.setTotalTimeMinutes(0); // Κενή συνταγή

        double progress = recipeService.calculateProgress(recipe, 1);

        assertEquals(0.0, progress);
    }
}