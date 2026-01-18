import { createContext, useContext, useState, useEffect } from 'react';
import { api, type Recipe } from '~/lib/api';

interface RecipesContextType {
  recipes: Recipe[];
  loading: boolean;
  getRecipe: (id: string) => Recipe | undefined;
  createRecipe: (recipe: Omit<Recipe, 'id'>) => Promise<void>;
  updateRecipe: (id: string, recipe: Partial<Recipe>) => Promise<void>;
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
      const data = await api.getRecipes();
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
    // We convert ID to string for comparison, as URL params are strings
    return recipes.find(r => String(r.id) === id);
  };

  const createRecipe = async (recipe: Omit<Recipe, 'id'>) => {
    await api.createRecipe(recipe);
    await refreshRecipes();
  };

  const updateRecipe = async (id: string, recipe: Partial<Recipe>) => {
    await api.updateRecipe(id, recipe);
    await refreshRecipes();
  };

  const deleteRecipe = async (id: string) => {
    await api.deleteRecipe(id);
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


