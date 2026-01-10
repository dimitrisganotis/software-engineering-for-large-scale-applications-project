import { createContext, useContext, useState, useEffect } from 'react';
import type { Recipe } from '~/data/mockRecipes';
import { getAllRecipes, createRecipe as createRecipeAPI, updateRecipe as updateRecipeAPI, deleteRecipe as deleteRecipeAPI } from '~/data/mockRecipes';

interface RecipesContextType {
  recipes: Recipe[];
  loading: boolean;
  getRecipe: (id: string) => Recipe | undefined;
  createRecipe: (recipe: Omit<Recipe, 'id'>) => Promise<void>;
  updateRecipe: (id: string, recipe: Omit<Recipe, 'id'>) => Promise<void>;
  deleteRecipe: (id: string) => Promise<void>;
  refreshRecipes: () => Promise<void>;
}

const RecipesContext = createContext<RecipesContextType | undefined>(undefined);

export function RecipesProvider({ children }: { children: React.ReactNode }) {
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  const [loading, setLoading] = useState(true);

  const refreshRecipes = async () => {
    setLoading(true);
    try {
      // Στο μέλλον: API call
      const data = await getAllRecipes();
      setRecipes(data);
    } catch (error) {
      console.error('Failed to fetch recipes:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    refreshRecipes();
  }, []);

  const getRecipe = (id: string) => {
    return recipes.find(r => r.id === id);
  };

  const createRecipe = async (recipe: Omit<Recipe, 'id'>) => {
    // Στο μέλλον: API call
    await createRecipeAPI(recipe);
    await refreshRecipes();
  };

  const updateRecipe = async (id: string, recipe: Omit<Recipe, 'id'>) => {
    // Στο μέλλον: API call
    await updateRecipeAPI(id, recipe);
    await refreshRecipes();
  };

  const deleteRecipe = async (id: string) => {
    // Στο μέλλον: API call
    await deleteRecipeAPI(id);
    await refreshRecipes();
  };

  return (
    <RecipesContext.Provider
      value={{
        recipes,
        loading,
        getRecipe,
        createRecipe,
        updateRecipe,
        deleteRecipe,
        refreshRecipes
      }}
    >
      {children}
    </RecipesContext.Provider>
  );
}

export function useRecipes() {
  const context = useContext(RecipesContext);
  if (context === undefined) {
    throw new Error('useRecipes must be used within a RecipesProvider');
  }
  return context;
}

