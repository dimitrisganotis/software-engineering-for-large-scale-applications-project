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
├── context/
│   └── RecipesContext.tsx     # React Context για διαχείριση state
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

## Προετοιμασία Παρουσίασης

### Παράδειγμα JSON Συνταγής

**Τίτλος:** Λουκουμάδες
**Κατηγορία:** DESSERT (Γλυκό)
**Δυσκολία:** MEDIUM (Μέτρια)
**Χρόνος:** 45 λεπτά

**Υλικά:**

1. Αλεύρι: 200 g
2. Μέλι: 100 g

**Βήματα:**

1. **Τίτλος:** Αλεύρι και νερό
   **Περιγραφή:** Ανακατεύουμε το αλεύρι με νερό μέχρι να γίνει χυλός.
   **Διάρκεια:** 5 λεπτά
   **Υλικά:** Αλεύρι

2. **Τίτλος:** Τηγάνισμα
   **Περιγραφή:** Ρίχνουμε κουταλιές στο καυτό λάδι.
   **Διάρκεια:** 10 λεπτά

3. **Τίτλος:** Σερβίρισμα
   **Περιγραφή:** Περιχύνουμε με μέλι και κανέλα.
   **Διάρκεια:** 2 λεπτά
   **Υλικά:** Μέλι

### JSON Payload (για Postman)

```json
{
  "title": "Λουκουμάδες",
  "category": "DESSERT",
  "difficulty": "MEDIUM",
  "prepTimeMinutes": 15,
  "totalTimeMinutes": 45,
  "ingredients": [
    { "name": "Αλεύρι", "quantity": 200, "unit": "g" },
    { "name": "Μέλι", "quantity": 100, "unit": "g" }
  ],
  "steps": [
    {
      "stepOrder": 1,
      "title": "Αλεύρι και νερό",
      "description": "Ανακατεύουμε το αλεύρι με νερό.",
      "durationMinutes": 5,
      "ingredients": [{ "name": "Αλεύρι", "quantity": 200, "unit": "g" }]
    },
    {
      "stepOrder": 2,
      "title": "Σερβίρισμα",
      "description": "Περιχύνουμε με μέλι.",
      "durationMinutes": 2,
      "ingredients": [{ "name": "Μέλι", "quantity": 100, "unit": "g" }]
    }
  ]
}
```

## Σημειώσεις Υλοποίησης

- Το Backend είναι σε **Java Spring Boot**.
- Η Βάση Δεδομένων είναι **MySQL**.
- Το Frontend είναι **React/TypeScript**.
- Οι φωτογραφίες αποθηκεύονται τοπικά στον φάκελο `backend/photos`.
