import { Link, useParams, useNavigate } from 'react-router';
import { useRecipes } from '~/context/RecipesContext';
import { Button } from '~/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '~/components/ui/card';
import type { Difficulty } from '~/data/mockRecipes';

const difficultyLabels: Record<Difficulty, string> = {
  easy: 'Εύκολη',
  medium: 'Μέτρια',
  hard: 'Δύσκολη'
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
          <CardTitle className="text-3xl">{recipe.name}</CardTitle>
          <div className="text-sm text-neutral-500 space-y-1 pt-2">
            <p>Κατηγορία: {recipe.category}</p>
            <p>Δυσκολία: {difficultyLabels[recipe.difficulty]}</p>
            <p>Συνολικός Χρόνος: {recipe.totalTimeMinutes} λεπτά</p>
          </div>
        </CardHeader>
        <CardContent className="space-y-6">
          {/* Υλικά */}
          <div>
            <h2 className="text-xl font-semibold mb-3">Υλικά</h2>
            <ul className="list-disc list-inside space-y-1">
              {recipe.ingredients.map((ingredient, index) => (
                <li key={index} className="text-neutral-700">
                  {ingredient}
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
                        {step.order}. {step.title}
                      </h3>
                      <span className="text-sm text-neutral-500 bg-neutral-100 px-2 py-1 rounded">
                        {step.durationMinutes} λεπτά
                      </span>
                    </div>
                    <p className="text-neutral-700">{step.description}</p>
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

