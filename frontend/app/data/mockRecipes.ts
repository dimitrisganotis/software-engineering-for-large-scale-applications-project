// Τύποι δεδομένων για τις συνταγές
export type Difficulty = 'easy' | 'medium' | 'hard';

export interface RecipeStep {
  id: string;
  order: number;
  title: string;
  description: string;
  durationMinutes: number;
}

export interface Recipe {
  id: string;
  name: string;
  category: string;
  difficulty: Difficulty;
  totalTimeMinutes: number;
  ingredients: string[];
  steps: RecipeStep[];
}

// Mock δεδομένα - Αργότερα θα αντικατασταθούν από API calls
// export const mockRecipes: Recipe[] = [
//   {
//     id: '1',
//     name: 'Σπαγγέτι Καρμπονάρα',
//     category: 'Ιταλική Κουζίνα',
//     difficulty: 'easy',
//     totalTimeMinutes: 25,
//     ingredients: [
//       '400g σπαγγέτι',
//       '200g πανσέτα',
//       '4 αυγά',
//       '100g παρμεζάνα',
//       'Αλάτι και πιπέρι'
//     ],
//     steps: [
//       {
//         id: 's1-1',
//         order: 1,
//         title: 'Βράσιμο ζυμαρικών',
//         description: 'Βράστε τα σπαγγέτι σε αλατισμένο νερό σύμφωνα με τις οδηγίες της συσκευασίας.',
//         durationMinutes: 10
//       },
//       {
//         id: 's1-2',
//         order: 2,
//         title: 'Τηγάνισμα πανσέτας',
//         description: 'Κόψτε την πανσέτα σε κυβάκια και τηγανίστε την μέχρι να γίνει τραγανή.',
//         durationMinutes: 8
//       },
//       {
//         id: 's1-3',
//         order: 3,
//         title: 'Ανακάτεμα σάλτσας',
//         description: 'Χτυπήστε τα αυγά με την παρμεζάνα. Αναμείξτε με τα ζυμαρικά και την πανσέτα.',
//         durationMinutes: 7
//       }
//     ]
//   },
//   {
//     id: '2',
//     name: 'Ελληνική Σαλάτα',
//     category: 'Σαλάτες',
//     difficulty: 'easy',
//     totalTimeMinutes: 15,
//     ingredients: [
//       '3 ντομάτες',
//       '1 αγγούρι',
//       '1 πράσινη πιπεριά',
//       '200g φέτα',
//       'Ελιές',
//       'Ελαιόλαδο και ρίγανη'
//     ],
//     steps: [
//       {
//         id: 's2-1',
//         order: 1,
//         title: 'Κόψιμο λαχανικών',
//         description: 'Κόψτε τις ντομάτες, το αγγούρι και την πιπεριά σε χοντρά κομμάτια.',
//         durationMinutes: 8
//       },
//       {
//         id: 's2-2',
//         order: 2,
//         title: 'Σερβίρισμα',
//         description: 'Τοποθετήστε τα λαχανικά σε πιάτο, προσθέστε φέτα, ελιές, λάδι και ρίγανη.',
//         durationMinutes: 7
//       }
//     ]
//   },
//   {
//     id: '3',
//     name: 'Μοσχαράκι Λεμονάτο',
//     category: 'Κρέατα',
//     difficulty: 'medium',
//     totalTimeMinutes: 90,
//     ingredients: [
//       '1kg μοσχάρι',
//       '2 λεμόνια',
//       '4 πατάτες',
//       'Ελαιόλαδο',
//       'Αλάτι, πιπέρι, ρίγανη'
//     ],
//     steps: [
//       {
//         id: 's3-1',
//         order: 1,
//         title: 'Μαρινάρισμα κρέατος',
//         description: 'Μαρινάρετε το κρέας με λεμόνι, λάδι και μπαχαρικά για 15 λεπτά.',
//         durationMinutes: 15
//       },
//       {
//         id: 's3-2',
//         order: 2,
//         title: 'Ψήσιμο',
//         description: 'Ψήστε το κρέας με τις πατάτες στο φούρνο στους 180°C.',
//         durationMinutes: 70
//       },
//       {
//         id: 's3-3',
//         order: 3,
//         title: 'Ξεκούραση',
//         description: 'Αφήστε το κρέας να ξεκουραστεί 5 λεπτά πριν το σερβίρετε.',
//         durationMinutes: 5
//       }
//     ]
//   }
// ];

// Import API functions
import * as api from '~/lib/api';

// Helper functions to transform between API format and mock format
function transformApiToMock(apiRecipe: api.Recipe): Recipe {
  // Convert ingredients from Ingredient[] to string[]
  const ingredientsStrings = apiRecipe.ingredients.map(ing => {
    if (ing.quantity && ing.unit) {
      return `${ing.quantity}${ing.unit} ${ing.name}`;
    }
    return ing.name;
  });

  // Convert steps: stepOrder -> order, number id -> string id
  const steps: RecipeStep[] = apiRecipe.steps.map((step, index) => ({
    id: step.id?.toString() || `step-${apiRecipe.id}-${index}`,
    order: step.stepOrder,
    title: step.title,
    description: step.description,
    durationMinutes: step.durationMinutes
  }));

  // Convert difficulty: EASY -> easy, etc.
  const difficultyMap: Record<api.DifficultyLevel, Difficulty> = {
    'EASY': 'easy',
    'MEDIUM': 'medium',
    'HARD': 'hard'
  };

  return {
    id: apiRecipe.id?.toString() || '',
    name: apiRecipe.title,
    category: apiRecipe.category,
    difficulty: difficultyMap[apiRecipe.difficulty],
    totalTimeMinutes: apiRecipe.totalTimeMinutes,
    ingredients: ingredientsStrings,
    steps
  };
}

function transformMockToApi(mockRecipe: Omit<Recipe, 'id'>): Omit<api.Recipe, 'id' | 'dateCreated'> {
  // Convert ingredients from string[] to Ingredient[]
  const ingredients: api.Ingredient[] = mockRecipe.ingredients.map(ingStr => {
    // Try to parse "quantityunit name" or "quantity unit name" format
    const parts = ingStr.trim().split(/\s+/);
    if (parts.length >= 2) {
      // Try to extract quantity and unit from first part
      const firstPart = parts[0];
      const match = firstPart.match(/^(\d+(?:\.\d+)?)([a-zA-Zα-ωΑ-Ω]+)$/);
      if (match) {
        return {
          name: parts.slice(1).join(' '),
          quantity: parseFloat(match[1]),
          unit: match[2]
        };
      }
      // Try "quantity unit" format
      const quantity = parseFloat(parts[0]);
      if (!isNaN(quantity)) {
        return {
          name: parts.slice(2).join(' '),
          quantity: quantity,
          unit: parts[1]
        };
      }
    }
    // Fallback: just name
    return { name: ingStr };
  });

  // Convert steps: order -> stepOrder
  const steps: api.RecipeStep[] = mockRecipe.steps.map(step => ({
    stepOrder: step.order,
    title: step.title,
    description: step.description,
    durationMinutes: step.durationMinutes
  }));

  // Convert difficulty: easy -> EASY, etc.
  const difficultyMap: Record<Difficulty, api.DifficultyLevel> = {
    'easy': 'EASY',
    'medium': 'MEDIUM',
    'hard': 'HARD'
  };

  return {
    title: mockRecipe.name,
    category: mockRecipe.category as api.RecipeCategory,
    difficulty: difficultyMap[mockRecipe.difficulty],
    totalTimeMinutes: mockRecipe.totalTimeMinutes,
    ingredients,
    steps
  };
}

// Συνάρτηση για να πάρουμε όλες τις συνταγές
// GET /api/recipes
export const getAllRecipes = async (): Promise<Recipe[]> => {
  const apiRecipes = await api.getAllRecipes();
  return apiRecipes.map(transformApiToMock);
};

// Συνάρτηση για να πάρουμε μία συνταγή με βάση το ID
// GET /api/recipes/{id}
export const getRecipeById = async (id: string): Promise<Recipe | undefined> => {
  try {
    const recipeId = parseInt(id, 10);
    if (isNaN(recipeId)) {
      return undefined;
    }
    const apiRecipe = await api.getRecipeById(recipeId);
    return transformApiToMock(apiRecipe);
  } catch (error) {
    console.error('Failed to fetch recipe:', error);
    return undefined;
  }
};

// Συνάρτηση για να δημιουργήσουμε νέα συνταγή
// POST /api/recipes
export const createRecipe = async (recipe: Omit<Recipe, 'id'>): Promise<Recipe> => {
  const apiRecipeData = transformMockToApi(recipe);
  const apiRecipe = await api.createRecipe(apiRecipeData);
  return transformApiToMock(apiRecipe);
};

// Συνάρτηση για να ενημερώσουμε μία συνταγή
// PUT /api/recipes/{id}
export const updateRecipe = async (id: string, recipe: Omit<Recipe, 'id'>): Promise<Recipe> => {
  const recipeId = parseInt(id, 10);
  if (isNaN(recipeId)) {
    throw new Error('Invalid recipe ID');
  }
  const apiRecipeData = transformMockToApi(recipe);
  const apiRecipe = await api.updateRecipe(recipeId, apiRecipeData);
  return transformApiToMock(apiRecipe);
};

// Συνάρτηση για να διαγράψουμε μία συνταγή
// DELETE /api/recipes/{id}
export const deleteRecipe = async (id: string): Promise<void> => {
  const recipeId = parseInt(id, 10);
  if (isNaN(recipeId)) {
    throw new Error('Invalid recipe ID');
  }
  await api.deleteRecipe(recipeId);
};

