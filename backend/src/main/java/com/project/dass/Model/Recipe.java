package com.project.dass.Model;

import jakarta.persistence.*; //JPA?/MySQL
import lombok.Getter;
import lombok.Setter;
//import org.springframework.data.annotation.Id; // MongoDB/JDBC

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "recipes")
@Getter
@Setter
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficulty;
    @Enumerated(EnumType.STRING)
    private RecipeCategory category;
    //private Integer prepTimeMinutes;
    private Integer totalTimeMinutes;
    @Column(name = "date_created", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date dateCreated = new Date();
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Ingredient> ingredients = new ArrayList<>();
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<RecipeStep> steps = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "recipe_images", joinColumns =
    @JoinColumn(name = "recipe_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    public void addStep(RecipeStep step) {
        steps.add(step);
        step.setRecipe(this);
    }

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
        ingredient.setRecipe(this);
    }
}