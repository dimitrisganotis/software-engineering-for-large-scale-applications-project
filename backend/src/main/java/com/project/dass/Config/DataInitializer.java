package com.project.dass.Config;

import com.project.dass.Model.*;
import com.project.dass.Repos.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RecipeRepository recipeRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if database is empty
        if (recipeRepository.count() == 0) {
//            initializeDummyData();
        }
    }

    private void initializeDummyData() {
        // Recipe 1: Spaghetti Carbonara
        Recipe carbonara = new Recipe();
        carbonara.setTitle("Spaghetti Carbonara");
        carbonara.setDifficulty(DifficultyLevel.MEDIUM);
        carbonara.setCategory(RecipeCategory.PASTA);
        carbonara.setPrepTimeMinutes(15);
        carbonara.setTotalTimeMinutes(30);
        carbonara.setDateCreated(new Date());
        carbonara.setImageUrls(Arrays.asList("https://example.com/carbonara1.jpg", "https://example.com/carbonara2.jpg"));

        Ingredient ing1 = new Ingredient();
        ing1.setName("Spaghetti");
        ing1.setQuantity(400.0);
        ing1.setUnit("gr");
        carbonara.addIngredient(ing1);

        Ingredient ing2 = new Ingredient();
        ing2.setName("Eggs");
        ing2.setQuantity(4.0);
        ing2.setUnit("pieces");
        carbonara.addIngredient(ing2);

        Ingredient ing3 = new Ingredient();
        ing3.setName("Pancetta");
        ing3.setQuantity(200.0);
        ing3.setUnit("gr");
        carbonara.addIngredient(ing3);

        Ingredient ing4 = new Ingredient();
        ing4.setName("Parmesan Cheese");
        ing4.setQuantity(100.0);
        ing4.setUnit("gr");
        carbonara.addIngredient(ing4);

        RecipeStep step1 = new RecipeStep();
        step1.setStepOrder(1);
        step1.setTitle("Boil the pasta");
        step1.setDescription("Bring a large pot of salted water to a boil. Add spaghetti and cook until al dente.");
        step1.setDurationMinutes(10);
        carbonara.addStep(step1);

        RecipeStep step2 = new RecipeStep();
        step2.setStepOrder(2);
        step2.setTitle("Cook the pancetta");
        step2.setDescription("While pasta is cooking, fry pancetta in a large pan until crispy.");
        step2.setDurationMinutes(5);
        carbonara.addStep(step2);

        RecipeStep step3 = new RecipeStep();
        step3.setStepOrder(3);
        step3.setTitle("Mix everything");
        step3.setDescription("Drain pasta, reserving some pasta water. Mix pasta with pancetta, eggs, and cheese. Add pasta water if needed.");
        step3.setDurationMinutes(5);
        carbonara.addStep(step3);

        recipeRepository.save(carbonara);

        // Recipe 2: Greek Salad
        Recipe greekSalad = new Recipe();
        greekSalad.setTitle("Greek Salad");
        greekSalad.setDifficulty(DifficultyLevel.EASY);
        greekSalad.setCategory(RecipeCategory.SALAD);
        greekSalad.setPrepTimeMinutes(10);
        greekSalad.setTotalTimeMinutes(10);
        greekSalad.setDateCreated(new Date());
        greekSalad.setImageUrls(Arrays.asList("https://example.com/greeksalad.jpg"));

        Ingredient sal1 = new Ingredient();
        sal1.setName("Tomatoes");
        sal1.setQuantity(4.0);
        sal1.setUnit("pieces");
        greekSalad.addIngredient(sal1);

        Ingredient sal2 = new Ingredient();
        sal2.setName("Cucumber");
        sal2.setQuantity(1.0);
        sal2.setUnit("piece");
        greekSalad.addIngredient(sal2);

        Ingredient sal3 = new Ingredient();
        sal3.setName("Feta Cheese");
        sal3.setQuantity(200.0);
        sal3.setUnit("gr");
        greekSalad.addIngredient(sal3);

        Ingredient sal4 = new Ingredient();
        sal4.setName("Olives");
        sal4.setQuantity(100.0);
        sal4.setUnit("gr");
        greekSalad.addIngredient(sal4);

        Ingredient sal5 = new Ingredient();
        sal5.setName("Olive Oil");
        sal5.setQuantity(3.0);
        sal5.setUnit("tablespoons");
        greekSalad.addIngredient(sal5);

        RecipeStep salStep1 = new RecipeStep();
        salStep1.setStepOrder(1);
        salStep1.setTitle("Chop vegetables");
        salStep1.setDescription("Chop tomatoes, cucumber, and onion into bite-sized pieces.");
        salStep1.setDurationMinutes(5);
        greekSalad.addStep(salStep1);

        RecipeStep salStep2 = new RecipeStep();
        salStep2.setStepOrder(2);
        salStep2.setTitle("Add cheese and olives");
        salStep2.setDescription("Add feta cheese and olives. Drizzle with olive oil and season with salt and oregano.");
        salStep2.setDurationMinutes(5);
        greekSalad.addStep(salStep2);

        recipeRepository.save(greekSalad);

        // Recipe 3: Chocolate Cake
        Recipe chocolateCake = new Recipe();
        chocolateCake.setTitle("Chocolate Cake");
        chocolateCake.setDifficulty(DifficultyLevel.MEDIUM);
        chocolateCake.setCategory(RecipeCategory.DESSERT);
        chocolateCake.setPrepTimeMinutes(20);
        chocolateCake.setTotalTimeMinutes(60);
        chocolateCake.setDateCreated(new Date());
        chocolateCake.setImageUrls(Arrays.asList("https://example.com/chocolatecake.jpg"));

        Ingredient cake1 = new Ingredient();
        cake1.setName("Flour");
        cake1.setQuantity(200.0);
        cake1.setUnit("gr");
        chocolateCake.addIngredient(cake1);

        Ingredient cake2 = new Ingredient();
        cake2.setName("Sugar");
        cake2.setQuantity(200.0);
        cake2.setUnit("gr");
        chocolateCake.addIngredient(cake2);

        Ingredient cake3 = new Ingredient();
        cake3.setName("Cocoa Powder");
        cake3.setQuantity(50.0);
        cake3.setUnit("gr");
        chocolateCake.addIngredient(cake3);

        Ingredient cake4 = new Ingredient();
        cake4.setName("Eggs");
        cake4.setQuantity(3.0);
        cake4.setUnit("pieces");
        chocolateCake.addIngredient(cake4);

        Ingredient cake5 = new Ingredient();
        cake5.setName("Butter");
        cake5.setQuantity(150.0);
        cake5.setUnit("gr");
        chocolateCake.addIngredient(cake5);

        RecipeStep cakeStep1 = new RecipeStep();
        cakeStep1.setStepOrder(1);
        cakeStep1.setTitle("Mix dry ingredients");
        cakeStep1.setDescription("Sift flour, sugar, and cocoa powder together in a large bowl.");
        cakeStep1.setDurationMinutes(5);
        chocolateCake.addStep(cakeStep1);

        RecipeStep cakeStep2 = new RecipeStep();
        cakeStep2.setStepOrder(2);
        cakeStep2.setTitle("Mix wet ingredients");
        cakeStep2.setDescription("Melt butter and mix with eggs. Combine with dry ingredients.");
        cakeStep2.setDurationMinutes(10);
        chocolateCake.addStep(cakeStep2);

        RecipeStep cakeStep3 = new RecipeStep();
        cakeStep3.setStepOrder(3);
        cakeStep3.setTitle("Bake");
        cakeStep3.setDescription("Pour into greased pan and bake at 180°C for 40 minutes.");
        cakeStep3.setDurationMinutes(40);
        chocolateCake.addStep(cakeStep3);

        recipeRepository.save(chocolateCake);

        // Recipe 4: Grilled Chicken
        Recipe grilledChicken = new Recipe();
        grilledChicken.setTitle("Grilled Chicken Breast");
        grilledChicken.setDifficulty(DifficultyLevel.EASY);
        grilledChicken.setCategory(RecipeCategory.MEAT);
        grilledChicken.setPrepTimeMinutes(10);
        grilledChicken.setTotalTimeMinutes(25);
        grilledChicken.setDateCreated(new Date());
        grilledChicken.setImageUrls(Arrays.asList("https://example.com/grilledchicken.jpg"));

        Ingredient ch1 = new Ingredient();
        ch1.setName("Chicken Breast");
        ch1.setQuantity(500.0);
        ch1.setUnit("gr");
        grilledChicken.addIngredient(ch1);

        Ingredient ch2 = new Ingredient();
        ch2.setName("Olive Oil");
        ch2.setQuantity(2.0);
        ch2.setUnit("tablespoons");
        grilledChicken.addIngredient(ch2);

        Ingredient ch3 = new Ingredient();
        ch3.setName("Lemon");
        ch3.setQuantity(1.0);
        ch3.setUnit("piece");
        grilledChicken.addIngredient(ch3);

        Ingredient ch4 = new Ingredient();
        ch4.setName("Garlic");
        ch4.setQuantity(2.0);
        ch4.setUnit("cloves");
        grilledChicken.addIngredient(ch4);

        RecipeStep chStep1 = new RecipeStep();
        chStep1.setStepOrder(1);
        chStep1.setTitle("Marinate");
        chStep1.setDescription("Mix olive oil, lemon juice, and minced garlic. Marinate chicken for at least 30 minutes.");
        chStep1.setDurationMinutes(5);
        grilledChicken.addStep(chStep1);

        RecipeStep chStep2 = new RecipeStep();
        chStep2.setStepOrder(2);
        chStep2.setTitle("Grill");
        chStep2.setDescription("Grill chicken on medium-high heat for 6-7 minutes per side until cooked through.");
        chStep2.setDurationMinutes(15);
        grilledChicken.addStep(chStep2);

        recipeRepository.save(grilledChicken);

        // Recipe 5: Vegetable Soup
        Recipe vegSoup = new Recipe();
        vegSoup.setTitle("Vegetable Soup");
        vegSoup.setDifficulty(DifficultyLevel.EASY);
        vegSoup.setCategory(RecipeCategory.SOUP);
        vegSoup.setPrepTimeMinutes(15);
        vegSoup.setTotalTimeMinutes(45);
        vegSoup.setDateCreated(new Date());
        vegSoup.setImageUrls(Arrays.asList("https://example.com/vegsoup.jpg"));

        Ingredient vs1 = new Ingredient();
        vs1.setName("Carrots");
        vs1.setQuantity(2.0);
        vs1.setUnit("pieces");
        vegSoup.addIngredient(vs1);

        Ingredient vs2 = new Ingredient();
        vs2.setName("Potatoes");
        vs2.setQuantity(3.0);
        vs2.setUnit("pieces");
        vegSoup.addIngredient(vs2);

        Ingredient vs3 = new Ingredient();
        vs3.setName("Onion");
        vs3.setQuantity(1.0);
        vs3.setUnit("piece");
        vegSoup.addIngredient(vs3);

        Ingredient vs4 = new Ingredient();
        vs4.setName("Vegetable Broth");
        vs4.setQuantity(1.0);
        vs4.setUnit("liter");
        vegSoup.addIngredient(vs4);

        RecipeStep vsStep1 = new RecipeStep();
        vsStep1.setStepOrder(1);
        vsStep1.setTitle("Chop vegetables");
        vsStep1.setDescription("Chop all vegetables into small cubes.");
        vsStep1.setDurationMinutes(10);
        vegSoup.addStep(vsStep1);

        RecipeStep vsStep2 = new RecipeStep();
        vsStep2.setStepOrder(2);
        vsStep2.setTitle("Cook");
        vsStep2.setDescription("Sauté onions, add vegetables and broth. Simmer for 30 minutes until vegetables are tender.");
        vsStep2.setDurationMinutes(30);
        vegSoup.addStep(vsStep2);

        recipeRepository.save(vegSoup);

        System.out.println("Dummy data initialized successfully!");
    }
}

