import { Link } from "react-router";
import { useRecipes } from "~/context/RecipesContext";
import { Button } from "~/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "~/components/ui/card";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "~/components/ui/alert-dialog"; // <--- 1. ΝΕΑ IMPORTS
import type { Difficulty } from "~/lib/api";

const difficultyLabels: Record<Difficulty, string> = {
  EASY: "Εύκολη",
  MEDIUM: "Μέτρια",
  HARD: "Δύσκολη",
};

export default function RecipesList() {
  const { recipes, loading, deleteRecipe } = useRecipes();

  if (loading) {
    return (
      <div className="container mx-auto py-8 px-4">
        <p className="text-center text-neutral-500">Φόρτωση...</p>
      </div>
    );
  }

  return (
    <div className="container mx-auto py-8 px-4 max-w-6xl">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold">Συνταγές</h1>
        <Button asChild>
          <Link to="/recipes/new">Νέα Συνταγή</Link>
        </Button>
      </div>

      {recipes.length === 0 ? (
        <Card>
          <CardContent className="py-8">
            <p className="text-center text-neutral-500">
              Δεν υπάρχουν συνταγές. Δημιουργήστε την πρώτη σας!
            </p>
          </CardContent>
        </Card>
      ) : (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {recipes.map((recipe) => (
            <Card key={recipe.id} className="flex flex-col">
              <CardHeader>
                <CardTitle className="text-xl">{recipe.title}</CardTitle>
                <div className="text-sm text-neutral-500 space-y-1">
                  <p>Κατηγορία: {recipe.category}</p>
                  <p>Δυσκολία: {difficultyLabels[recipe.difficulty]}</p>
                  <p>Χρόνος: {recipe.totalTimeMinutes} λεπτά</p>
                  {recipe.dateCreated && (
                    <p>
                      Δημ:{" "}
                      {new Date(recipe.dateCreated).toLocaleDateString("el-GR")}
                    </p>
                  )}
                </div>
              </CardHeader>
              <CardContent className="flex-1 flex flex-col justify-end">
                <div className="flex gap-2">
                  <Button asChild variant="outline" className="flex-1">
                    <Link to={`/recipes/${recipe.id}`}>Προβολή</Link>
                  </Button>
                  <Button asChild variant="outline" className="flex-1">
                    <Link to={`/recipes/${recipe.id}/edit`}>Επεξεργασία</Link>
                  </Button>
                  {/* --- 2. ΕΔΩ ΕΙΝΑΙ Η ΑΛΛΑΓΗ ΓΙΑ ΤΗ ΔΙΑΓΡΑΦΗ --- */}
                                    <AlertDialog>
                                      <AlertDialogTrigger asChild>
                                        <Button variant="destructive" size="icon">
                                          ✕
                                        </Button>
                                      </AlertDialogTrigger>
                                      <AlertDialogContent>
                                        <AlertDialogHeader>
                                          <AlertDialogTitle>Είστε σίγουροι;</AlertDialogTitle>
                                          <AlertDialogDescription>
                                            Αυτή η ενέργεια δεν μπορεί να αναιρεθεί. Η συνταγή
                                            <strong> "{recipe.title}" </strong>
                                            θα διαγραφεί μόνιμα.
                                          </AlertDialogDescription>
                                        </AlertDialogHeader>
                                        <AlertDialogFooter>
                                          <AlertDialogCancel>Ακύρωση</AlertDialogCancel>
                                          <AlertDialogAction
                                            className="bg-red-600 hover:bg-red-700"
                                            onClick={() => deleteRecipe(String(recipe.id))}
                                          >
                                            Διαγραφή
                                          </AlertDialogAction>
                                        </AlertDialogFooter>
                                      </AlertDialogContent>
                                    </AlertDialog>
                                    {/* ------------------------------------------- */}

                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
