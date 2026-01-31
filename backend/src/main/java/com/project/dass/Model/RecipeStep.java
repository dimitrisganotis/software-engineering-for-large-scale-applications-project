package com.project.dass.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.ArrayList;

@Data
@Entity
@Table(name = "recipe_steps")
public class RecipeStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer stepOrder;
    private String title;
    @Column(length = 1000)
    private String description;
    private Integer durationMinutes;
    private String imageUrl;
    // image -> recipe
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    @JsonIgnore
    private Recipe recipe;

    // many ingredients <-> many steps
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "step_ingredients", joinColumns = @JoinColumn(name = "step_id"), inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    private List<Ingredient> ingredients = new ArrayList<>();

}
