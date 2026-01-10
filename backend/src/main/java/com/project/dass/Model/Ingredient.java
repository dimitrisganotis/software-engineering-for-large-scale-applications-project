package com.project.dass.Model;

import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "ingredients")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double quantity;
    private String unit; // π.χ. "gr", "ml", "κουτάλι"
    // Σύνδεση µε τη Συνταγή (Many Ingredients -> One Recipe)
    // Χρησιµοποιούµε JsonIgnore για να µην έχουµε ατέρµονους βρόχους
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
}