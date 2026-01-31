package com.project.dass.Repos;

import com.project.dass.Model.Recipe;
import com.project.dass.Model.RecipeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>
{
    // Find recipe by category
    List<Recipe> findByCategory(RecipeCategory category);
    // FInd recipe by title containing
    List<Recipe> findByTitleContainingIgnoreCase(String title);
}
