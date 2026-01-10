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
export const mockRecipes: Recipe[] = [
  {
    id: '1',
    name: 'Σπαγγέτι Καρμπονάρα',
    category: 'Ιταλική Κουζίνα',
    difficulty: 'easy',
    totalTimeMinutes: 25,
    ingredients: [
      '400g σπαγγέτι',
      '200g πανσέτα',
      '4 αυγά',
      '100g παρμεζάνα',
      'Αλάτι και πιπέρι'
    ],
    steps: [
      {
        id: 's1-1',
        order: 1,
        title: 'Βράσιμο ζυμαρικών',
        description: 'Βράστε τα σπαγγέτι σε αλατισμένο νερό σύμφωνα με τις οδηγίες της συσκευασίας.',
        durationMinutes: 10
      },
      {
        id: 's1-2',
        order: 2,
        title: 'Τηγάνισμα πανσέτας',
        description: 'Κόψτε την πανσέτα σε κυβάκια και τηγανίστε την μέχρι να γίνει τραγανή.',
        durationMinutes: 8
      },
      {
        id: 's1-3',
        order: 3,
        title: 'Ανακάτεμα σάλτσας',
        description: 'Χτυπήστε τα αυγά με την παρμεζάνα. Αναμείξτε με τα ζυμαρικά και την πανσέτα.',
        durationMinutes: 7
      }
    ]
  },
  {
    id: '2',
    name: 'Ελληνική Σαλάτα',
    category: 'Σαλάτες',
    difficulty: 'easy',
    totalTimeMinutes: 15,
    ingredients: [
      '3 ντομάτες',
      '1 αγγούρι',
      '1 πράσινη πιπεριά',
      '200g φέτα',
      'Ελιές',
      'Ελαιόλαδο και ρίγανη'
    ],
    steps: [
      {
        id: 's2-1',
        order: 1,
        title: 'Κόψιμο λαχανικών',
        description: 'Κόψτε τις ντομάτες, το αγγούρι και την πιπεριά σε χοντρά κομμάτια.',
        durationMinutes: 8
      },
      {
        id: 's2-2',
        order: 2,
        title: 'Σερβίρισμα',
        description: 'Τοποθετήστε τα λαχανικά σε πιάτο, προσθέστε φέτα, ελιές, λάδι και ρίγανη.',
        durationMinutes: 7
      }
    ]
  },
  {
    id: '3',
    name: 'Μοσχαράκι Λεμονάτο',
    category: 'Κρέατα',
    difficulty: 'medium',
    totalTimeMinutes: 90,
    ingredients: [
      '1kg μοσχάρι',
      '2 λεμόνια',
      '4 πατάτες',
      'Ελαιόλαδο',
      'Αλάτι, πιπέρι, ρίγανη'
    ],
    steps: [
      {
        id: 's3-1',
        order: 1,
        title: 'Μαρινάρισμα κρέατος',
        description: 'Μαρινάρετε το κρέας με λεμόνι, λάδι και μπαχαρικά για 15 λεπτά.',
        durationMinutes: 15
      },
      {
        id: 's3-2',
        order: 2,
        title: 'Ψήσιμο',
        description: 'Ψήστε το κρέας με τις πατάτες στο φούρνο στους 180°C.',
        durationMinutes: 70
      },
      {
        id: 's3-3',
        order: 3,
        title: 'Ξεκούραση',
        description: 'Αφήστε το κρέας να ξεκουραστεί 5 λεπτά πριν το σερβίρετε.',
        durationMinutes: 5
      }
    ]
  }
];

// Συνάρτηση για να πάρουμε όλες τις συνταγές
// Στο μέλλον: GET /api/recipes
export const getAllRecipes = (): Promise<Recipe[]> => {
  return Promise.resolve(mockRecipes);
};

// Συνάρτηση για να πάρουμε μία συνταγή με βάση το ID
// Στο μέλλον: GET /api/recipes/{id}
export const getRecipeById = (id: string): Promise<Recipe | undefined> => {
  return Promise.resolve(mockRecipes.find(r => r.id === id));
};

// Συνάρτηση για να δημιουργήσουμε νέα συνταγή
// Στο μέλλον: POST /api/recipes
export const createRecipe = (recipe: Omit<Recipe, 'id'>): Promise<Recipe> => {
  const newRecipe: Recipe = {
    ...recipe,
    id: Date.now().toString()
  };
  mockRecipes.push(newRecipe);
  return Promise.resolve(newRecipe);
};

// Συνάρτηση για να ενημερώσουμε μία συνταγή
// Στο μέλλον: PUT /api/recipes/{id}
export const updateRecipe = (id: string, recipe: Omit<Recipe, 'id'>): Promise<Recipe> => {
  const index = mockRecipes.findIndex(r => r.id === id);
  if (index !== -1) {
    mockRecipes[index] = { ...recipe, id };
    return Promise.resolve(mockRecipes[index]);
  }
  throw new Error('Recipe not found');
};

// Συνάρτηση για να διαγράψουμε μία συνταγή
// Στο μέλλον: DELETE /api/recipes/{id}
export const deleteRecipe = (id: string): Promise<void> => {
  const index = mockRecipes.findIndex(r => r.id === id);
  if (index !== -1) {
    mockRecipes.splice(index, 1);
  }
  return Promise.resolve();
};

