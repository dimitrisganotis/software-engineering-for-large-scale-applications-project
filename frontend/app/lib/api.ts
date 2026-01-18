const API_URL = "http://localhost:8080/api";

export interface Ingredient {
  id?: number;
  name: string;
  quantity: number;
  unit: string;
}

export interface RecipeStep {
  id?: number;
  stepOrder: number;
  title: string;
  description: string;
  durationMinutes: number;
  imageUrl?: string;
  ingredients?: Ingredient[]; // Linked ingredients
}

export type Difficulty = "EASY" | "MEDIUM" | "HARD";
export type Category = "PASTA" | "MEAT" | "VEGETARIAN" | "DESSERT" | "SOUP";

export interface Recipe {
  id?: number;
  title: string;
  description?: string; // Backend might not have this, be careful
  category: Category;
  difficulty: Difficulty;
  prepTimeMinutes: number;
  totalTimeMinutes: number;
  ingredients: Ingredient[];
  steps: RecipeStep[];
  imageUrls: string[];
  dateCreated?: string;
}

export const api = {
  getRecipes: async (): Promise<Recipe[]> => {
    const res = await fetch(`${API_URL}/recipes`);
    if (!res.ok) throw new Error("Failed to fetch recipes");
    return res.json();
  },

  getRecipe: async (id: string | number): Promise<Recipe> => {
    const res = await fetch(`${API_URL}/recipes/${id}`);
    if (!res.ok) throw new Error("Failed to fetch recipe");
    return res.json();
  },

  createRecipe: async (recipe: Omit<Recipe, "id">): Promise<Recipe> => {
    const res = await fetch(`${API_URL}/recipes`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(recipe),
    });
    if (!res.ok) throw new Error("Failed to create recipe");
    return res.json();
  },

  updateRecipe: async (
    id: string | number,
    recipe: Partial<Recipe>,
  ): Promise<Recipe> => {
    const res = await fetch(`${API_URL}/recipes/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(recipe),
    });
    if (!res.ok) throw new Error("Failed to update recipe");
    return res.json();
  },

  deleteRecipe: async (id: string | number): Promise<void> => {
    const res = await fetch(`${API_URL}/recipes/${id}`, {
      method: "DELETE",
    });
    if (!res.ok) throw new Error("Failed to delete recipe");
  },

  uploadPhoto: async (recipeId: number, file: File): Promise<string> => {
    const formData = new FormData();
    formData.append("file", file);
    const res = await fetch(`${API_URL}/recipes/${recipeId}/photo`, {
      method: "POST",
      body: formData,
    });
    if (!res.ok) throw new Error("Failed to upload photo");
    return res.text();
  },

  uploadStepPhoto: async (
    recipeId: number,
    stepId: number,
    file: File,
  ): Promise<string> => {
    const formData = new FormData();
    formData.append("file", file);
    const res = await fetch(
      `${API_URL}/recipes/${recipeId}/steps/${stepId}/photo`,
      {
        method: "POST",
        body: formData,
      },
    );
    if (!res.ok) throw new Error("Failed to upload step photo");
    return res.text();
  },

  getExecutionProgress: async (
    id: number,
    completedStepOrder: number,
  ): Promise<number> => {
    const res = await fetch(
      `${API_URL}/recipes/${id}/progress?completedStepOrder=${completedStepOrder}`,
    );
    if (!res.ok) throw new Error("Failed to get progress");
    return res.json();
  },
};
