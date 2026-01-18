import { Link, useParams, useNavigate } from "react-router";
import { useRecipes } from "~/context/RecipesContext";
import { Button } from "~/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "~/components/ui/card";
import type { Difficulty } from "~/lib/api";

const difficultyLabels: Record<string, string> = {
  EASY: "Εύκολη",
  MEDIUM: "Μέτρια",
  HARD: "Δύσκολη",
};

export default function RecipeView() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { getRecipe } = useRecipes();

  if (!id) {
    return (
      <div className="container mx-auto py-8 px-4">
        <p className="text-center text-red-500">Μη έγκυρο ID συνταγής</p>
      </div>
    );
  }

  const recipe = getRecipe(id);

  if (!recipe) {
    return (
      <div className="container mx-auto py-8 px-4">
        <p className="text-center text-neutral-500">Η συνταγή δεν βρέθηκε</p>
        <div className="text-center mt-4">
          <Button asChild variant="outline">
            <Link to="/">Επιστροφή</Link>
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto py-8 px-4 max-w-4xl">
      <div className="mb-6">
        <Button asChild variant="ghost" size="sm">
          <Link to="/">← Επιστροφή</Link>
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-3xl">{recipe.title}</CardTitle>
          <div className="text-sm text-neutral-500 space-y-1 pt-2">
            <p>Κατηγορία: {recipe.category}</p>
            <p>Δυσκολία: {difficultyLabels[recipe.difficulty]}</p>
            <p>Χρόνος: {recipe.totalTimeMinutes} λεπτά</p>
            {recipe.dateCreated && (
              <p>
                Δημιουργήθηκε:{" "}
                {new Date(recipe.dateCreated).toLocaleDateString("el-GR")}
              </p>
            )}
          </div>
        </CardHeader>
        <CardContent className="space-y-6">
          {/* Φωτογραφίες Συνταγής */}
          {recipe.imageUrls && recipe.imageUrls.length > 0 && (
            <div className="flex gap-4 overflow-x-auto pb-4">
              {recipe.imageUrls.map((url, idx) => (
                <img
                  key={idx}
                  src={
                    url.startsWith("http")
                      ? url
                      : `http://localhost:8080/api/recipes/${recipe.id}/photo/${url}`
                  }
                  alt={`Recipe shot ${idx + 1}`}
                  className="h-48 w-auto rounded-md object-cover"
                />
              ))}
            </div>
          )}

          {/* Υλικά */}
          <div>
            <h2 className="text-xl font-semibold mb-3">Υλικά</h2>
            <ul className="list-disc list-inside space-y-1">
              {recipe.ingredients.map((ingredient, index) => (
                <li key={index} className="text-neutral-700">
                  {ingredient.quantity} {ingredient.unit} {ingredient.name}
                </li>
              ))}
            </ul>
          </div>

          {/* Βήματα */}
          <div>
            <h2 className="text-xl font-semibold mb-3">Βήματα Προετοιμασίας</h2>
            <div className="space-y-4">
              {recipe.steps.map((step) => (
                <Card key={step.id}>
                  <CardContent className="pt-6">
                    <div className="flex justify-between items-start mb-2">
                      <h3 className="font-semibold text-lg">
                        {step.stepOrder}. {step.title}
                      </h3>
                      <span className="text-sm text-neutral-500 bg-neutral-100 px-2 py-1 rounded">
                        {step.durationMinutes} λεπτά
                      </span>
                    </div>
                    <p className="text-neutral-700 mb-2">{step.description}</p>
                    {step.imageUrl && (
                      <div className="mt-2">
                        <img
                          src={
                            step.imageUrl.startsWith("http")
                              ? step.imageUrl
                              : `http://localhost:8080/api/recipes/${recipe.id}/steps/${step.id}/photo/${step.imageUrl}`
                          }
                          alt={`Step ${step.stepOrder}`}
                          className="h-32 w-auto rounded-md object-cover"
                        />
                      </div>
                    )}
                  </CardContent>
                </Card>
              ))}
            </div>
          </div>

          {/* Κουμπιά ενεργειών */}
          <div className="flex gap-3 pt-4">
            <Button
              className="flex-1"
              onClick={() => navigate(`/recipes/${id}/execute`)}
            >
              Έναρξη Μαγειρέματος
            </Button>
            <Button asChild variant="outline">
              <Link to={`/recipes/${id}/edit`}>Επεξεργασία</Link>
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
