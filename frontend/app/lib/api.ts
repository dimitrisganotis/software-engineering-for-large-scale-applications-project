// API service for communicating with the backend
// Uses native fetch() - no external dependencies needed

// Determine API base URL based on environment
// - Development: Use Vite proxy (/api)
// - Production/Docker: Use full backend URL to bypass React Router
function getApiBaseUrl(): string {
  // Check for explicit environment variable (set in docker-compose.yml)
  // @ts-ignore - Vite env variables
  const envApiUrl = typeof import.meta !== 'undefined' && import.meta.env?.VITE_API_BASE_URL;
  if (envApiUrl) {
    return envApiUrl;
  }
  
  // In browser (client-side)
  if (typeof window !== 'undefined') {
    // Check if we're in development (Vite dev server on port 5173)
    const isDev = window.location.port === '5173' || window.location.port === '';
    if (isDev) {
      return '/api'; // Use Vite proxy in development
    }
    // Production: use full backend URL (backend is exposed on port 8080)
    return 'http://localhost:8080/api';
  }
  
  // Server-side (SSR): use Docker service name for internal communication
  return 'http://app:8080/api';
}

const API_BASE_URL = getApiBaseUrl();

// Types matching backend Recipe model exactly
export interface RecipeStep {
  id?: number;
  stepOrder: number;
  title: string;
  description: string;
  durationMinutes: number;
  imageUrl?: string;
}

export interface Ingredient {
  id?: number;
  name: string;
  quantity?: number; // Double in backend
  unit?: string; // e.g., "gr", "ml", "κουτάλι"
}

export type DifficultyLevel = 'EASY' | 'MEDIUM' | 'HARD';
export type RecipeCategory = 'PASTA' | 'MEAT' | 'VEGETARIAN' | 'DESSERT' | 'SOUP' | 'SALAD';

export interface Recipe {
  id?: number;
  title: string;
  difficulty: DifficultyLevel;
  category: RecipeCategory;
  prepTimeMinutes?: number;
  totalTimeMinutes: number;
  dateCreated?: string;
  ingredients: Ingredient[];
  steps: RecipeStep[];
  imageUrls?: string[];
}

// Helper function to handle API responses
async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`API Error: ${response.status} ${response.statusText} - ${errorText}`);
  }
  
  // Handle empty responses (e.g., DELETE 204)
  if (response.status === 204 || response.status === 201) {
    const contentType = response.headers.get('content-type');
    if (!contentType || !contentType.includes('application/json')) {
      return {} as T;
    }
  }
  
  return response.json();
}

// GET all recipes
export async function getAllRecipes(): Promise<Recipe[]> {
  const response = await fetch(`${API_BASE_URL}/recipes`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });
  return handleResponse<Recipe[]>(response);
}

// GET recipe by ID
export async function getRecipeById(id: number): Promise<Recipe> {
  const response = await fetch(`${API_BASE_URL}/recipes/${id}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });
  return handleResponse<Recipe>(response);
}

// POST create new recipe
export async function createRecipe(recipe: Omit<Recipe, 'id' | 'dateCreated'>): Promise<Recipe> {
  const response = await fetch(`${API_BASE_URL}/recipes`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(recipe),
  });
  return handleResponse<Recipe>(response);
}

// PUT update recipe
export async function updateRecipe(id: number, recipe: Omit<Recipe, 'id' | 'dateCreated'>): Promise<Recipe> {
  const response = await fetch(`${API_BASE_URL}/recipes/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(recipe),
  });
  return handleResponse<Recipe>(response);
}

// DELETE recipe
export async function deleteRecipe(id: number): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/recipes/${id}`, {
    method: 'DELETE',
  });
  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`API Error: ${response.status} ${response.statusText} - ${errorText}`);
  }
}

// GET recipes by category
export async function getRecipesByCategory(category: RecipeCategory): Promise<Recipe[]> {
  const response = await fetch(`${API_BASE_URL}/recipes/category/${category}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });
  return handleResponse<Recipe[]>(response);
}

// GET recipes by search term
export async function searchRecipes(title: string): Promise<Recipe[]> {
  const response = await fetch(`${API_BASE_URL}/recipes/search?title=${encodeURIComponent(title)}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });
  return handleResponse<Recipe[]>(response);
}

// GET execution progress
export async function getExecutionProgress(id: number, completedStepOrder: number): Promise<number> {
  const response = await fetch(`${API_BASE_URL}/recipes/${id}/progress?completedStepOrder=${completedStepOrder}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });
  return handleResponse<number>(response);
}
