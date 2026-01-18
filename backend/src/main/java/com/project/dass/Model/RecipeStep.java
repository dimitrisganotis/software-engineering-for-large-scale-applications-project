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
    @Column(length = 1000) // Μεγαλύτερο κείµενο για περιγραφή
    private String description;
    // Η διάρκεια σε λεπτά (για τον υπολογισµό προόδου)
    private Integer durationMinutes;
    // Φωτογραφία βήµατος (απλοποιηµένο ως URL για αρχή)
    private String imageUrl;
    // Σύνδεση µε τη Συνταγή
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    @JsonIgnore
    private Recipe recipe;

    // Υλικά που χρησιµοποιούνται σε αυτό το βήµα
    // Χρησιμοποιούμε ManyToMany γιατί ένα υλικό (instance στη DB) ανήκει στη
    // συνταγή,
    // και μπορεί να αναφέρεται σε πολλά βήματα (αν το βάζουμε σταδιακά)
    // ή απλά να συνδεθεί με το βήμα.
    // Σημείωση: Αν θέλουμε να πούμε "200g από τα 500g", θέλει πιο πολύπλοκο
    // μοντέλο.
    // Για την εργασία, απλή σύνδεση αρκεί.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "step_ingredients", joinColumns = @JoinColumn(name = "step_id"), inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    private List<Ingredient> ingredients = new ArrayList<>(); // Προσθήκη λίστας υλικών

}
