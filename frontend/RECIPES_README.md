# Cooking Recipes Management System - Frontend

Σύστημα Διαχείρισης Συνταγών Μαγειρικής με React, React Router, Tailwind CSS και shadcn/ui.

## Δομή Εφαρμογής

### Routes
- `/` - Λίστα συνταγών
- `/recipes/new` - Δημιουργία νέας συνταγής
- `/recipes/:id` - Προβολή συνταγής
- `/recipes/:id/edit` - Επεξεργασία συνταγής
- `/recipes/:id/execute` - Εκτέλεση συνταγής με progress tracking

### Δομή Φακέλων

```
app/
├── components/
│   └── ui/                    # shadcn/ui components
│       ├── button.tsx
│       ├── card.tsx
│       ├── input.tsx
│       ├── label.tsx
│       ├── progress.tsx
│       ├── select.tsx
│       └── textarea.tsx
├── context/
│   └── RecipesContext.tsx     # React Context για διαχείριση state
├── data/
│   └── mockRecipes.ts         # Mock data και API functions
├── routes/
│   ├── recipes.tsx            # Λίστα συνταγών (/)
│   ├── recipes.new.tsx        # Φόρμα δημιουργίας/επεξεργασίας
│   ├── recipes.$id.tsx        # Προβολή συνταγής
│   ├── recipes.$id.edit.tsx   # Επεξεργασία συνταγής
│   └── recipes.$id.execute.tsx # Εκτέλεση συνταγής
├── lib/
│   └── utils.ts               # Utility functions
├── app.css                    # Tailwind CSS
├── root.tsx                   # Root component με RecipesProvider
└── routes.ts                  # Route configuration
```

## Mock Data

Τα mock δεδομένα βρίσκονται στο `app/data/mockRecipes.ts`.

### Δομή Δεδομένων

```typescript
interface Recipe {
  id: string;
  name: string;
  category: string;
  difficulty: 'easy' | 'medium' | 'hard';
  totalTimeMinutes: number;
  ingredients: string[];
  steps: RecipeStep[];
}

interface RecipeStep {
  id: string;
  order: number;
  title: string;
  description: string;
  durationMinutes: number;
}
```

### API Functions (Mock)

Οι παρακάτω functions χρησιμοποιούν προς το παρόν mock data, αλλά είναι δομημένες έτσι ώστε να μπορούν εύκολα να αντικατασταθούν με πραγματικά API calls:

- `getAllRecipes()` → `GET /api/recipes`
- `getRecipeById(id)` → `GET /api/recipes/{id}`
- `createRecipe(recipe)` → `POST /api/recipes`
- `updateRecipe(id, recipe)` → `PUT /api/recipes/{id}`
- `deleteRecipe(id)` → `DELETE /api/recipes/{id}`

## State Management

Χρησιμοποιείται React Context (`RecipesContext`) για τη διαχείριση του global state.

### RecipesContext

Παρέχει:
- `recipes`: Λίστα συνταγών
- `loading`: Loading state
- `getRecipe(id)`: Ανάκτηση συνταγής με βάση το ID
- `createRecipe(recipe)`: Δημιουργία νέας συνταγής
- `updateRecipe(id, recipe)`: Ενημέρωση συνταγής
- `deleteRecipe(id)`: Διαγραφή συνταγής
- `refreshRecipes()`: Ανανέωση λίστας συνταγών

## Χαρακτηριστικά

### 1. Λίστα Συνταγών
- Προβολή όλων των συνταγών σε card layout
- Εμφάνιση: όνομα, κατηγορία, δυσκολία, χρόνος
- Actions: Προβολή, Επεξεργασία, Διαγραφή

### 2. Προβολή Συνταγής
- Εμφάνιση πλήρων στοιχείων συνταγής
- Λίστα υλικών
- Βήματα με σειρά και διάρκεια
- Κουμπί "Έναρξη Μαγειρέματος"

### 3. Δημιουργία/Επεξεργασία Συνταγής
- Φόρμα με όλα τα πεδία
- Δυναμική προσθήκη/αφαίρεση βημάτων
- Αυτόματη αρίθμηση βημάτων
- Validation

### 4. Εκτέλεση Συνταγής
- Step-by-step οδηγίες
- Progress bar με βάση τον χρόνο (όχι τα βήματα)
- Προεπισκόπηση επόμενου βήματος
- Οθόνη ολοκλήρωσης

## Εκτέλεση

```bash
npm run dev
```

Η εφαρμογή θα είναι διαθέσιμη στο `http://localhost:5173`

## Technologies

- React 19
- React Router 7
- Tailwind CSS 4
- shadcn/ui (New York style, neutral colors)
- TypeScript
- Vite

## Σημειώσεις

- Όλα τα σχόλια κώδικα είναι στα ελληνικά
- Το UI είναι καθαρό, minimal και φιλικό
- Δεν χρησιμοποιούνται global state libraries (Redux, Zustand)
- Δεν υπάρχει authentication
- Όλα τα δεδομένα είναι mock - εύκολα αντικαθίσταται με REST API

