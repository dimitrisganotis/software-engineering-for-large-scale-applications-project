package com.project.dass.Model;

import jakarta.persistence.*;
import lombok.Data;

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
    @Column(length = 1000) // Μεγαλύτερο κείµενο για περιγραφή
    private String description;
    // Η διάρκεια σε λεπτά (για τον υπολογισµό προόδου)
    private Integer durationMinutes;
    // Φωτογραφία βήµατος (απλοποιηµένο ως URL για αρχή)
    private String imageUrl;
    // Σύνδεση µε τη Συνταγή
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;


}
