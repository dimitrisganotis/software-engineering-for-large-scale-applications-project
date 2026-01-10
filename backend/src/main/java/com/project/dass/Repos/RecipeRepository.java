package com.project.dass.Repos;

import com.project.dass.Model.Recipe;
import com.project.dass.Model.RecipeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>
{
    // Custom query: Βρες συνταγές ανά κατηγορία
    List<Recipe> findByCategory(RecipeCategory category);
    // Custom query: Αναζήτηση µε βάση τον τίτλο
    List<Recipe> findByTitleContainingIgnoreCase(String title);
}
