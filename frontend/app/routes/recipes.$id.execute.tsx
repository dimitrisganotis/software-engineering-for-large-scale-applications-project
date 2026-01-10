import { useState, useEffect } from 'react';
import { Link, useParams, useNavigate } from 'react-router';
import { useRecipes } from '~/context/RecipesContext';
import { Button } from '~/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '~/components/ui/card';
import { Progress } from '~/components/ui/progress';

export default function RecipeExecute() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { getRecipe } = useRecipes();

  const [currentStepIndex, setCurrentStepIndex] = useState(0);
  const [elapsedTime, setElapsedTime] = useState(0);

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

  const currentStep = recipe.steps[currentStepIndex];
  const isCompleted = currentStepIndex >= recipe.steps.length;

  // Υπολογισμός συνολικής προόδου με βάση το χρόνο
  const totalTime = recipe.totalTimeMinutes;
  const timeUpToCurrentStep = recipe.steps
    .slice(0, currentStepIndex)
    .reduce((sum, step) => sum + step.durationMinutes, 0);
  const progressValue = isCompleted ? 100 : (timeUpToCurrentStep / totalTime) * 100;

  const handleNextStep = () => {
    if (currentStepIndex < recipe.steps.length) {
      setCurrentStepIndex(currentStepIndex + 1);
      setElapsedTime(timeUpToCurrentStep + (currentStep?.durationMinutes || 0));
    }
  };

  const handleFinish = () => {
    navigate(`/recipes/${id}`);
  };

  // Οθόνη ολοκλήρωσης
  if (isCompleted) {
    return (
      <div className="container mx-auto py-8 px-4 max-w-3xl">
        <Card>
          <CardHeader>
            <CardTitle className="text-2xl text-center">Συγχαρητήρια!</CardTitle>
          </CardHeader>
          <CardContent className="text-center space-y-6">
            <p className="text-lg">
              Ολοκληρώσατε τη συνταγή <strong>{recipe.name}</strong>
            </p>
            <p className="text-neutral-500">
              Συνολικός χρόνος: {recipe.totalTimeMinutes} λεπτά
            </p>
            <div className="flex gap-3 justify-center pt-4">
              <Button onClick={handleFinish}>Προβολή Συνταγής</Button>
              <Button asChild variant="outline">
                <Link to="/">Επιστροφή στη Λίστα</Link>
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>
    );
  }

  // Οθόνη εκτέλεσης βήματος
  return (
    <div className="container mx-auto py-8 px-4 max-w-3xl">
      <Card>
        <CardHeader>
          <CardTitle className="text-2xl">{recipe.name}</CardTitle>
          <p className="text-sm text-neutral-500">
            Βήμα {currentStepIndex + 1} από {recipe.steps.length}
          </p>
        </CardHeader>
        <CardContent className="space-y-6">
          {/* Πρόοδος με βάση το χρόνο */}
          <div className="space-y-2">
            <div className="flex justify-between text-sm">
              <span className="text-neutral-500">Πρόοδος</span>
              <span className="font-medium">{Math.round(progressValue)}%</span>
            </div>
            <Progress value={progressValue} />
            <p className="text-xs text-neutral-500 text-center">
              {timeUpToCurrentStep} / {totalTime} λεπτά
            </p>
          </div>

          {/* Τρέχον βήμα */}
          {currentStep && (
            <Card className="border-2 border-neutral-200">
              <CardContent className="pt-6 space-y-4">
                <div className="flex justify-between items-start">
                  <h3 className="text-xl font-semibold">{currentStep.title}</h3>
                  <span className="text-sm bg-neutral-100 px-3 py-1 rounded">
                    {currentStep.durationMinutes} λεπτά
                  </span>
                </div>
                <p className="text-neutral-700 leading-relaxed">
                  {currentStep.description}
                </p>
              </CardContent>
            </Card>
          )}

          {/* Επόμενα βήματα (προεπισκόπηση) */}
          {currentStepIndex < recipe.steps.length - 1 && (
            <div className="space-y-2">
              <h4 className="text-sm font-medium text-neutral-500">Επόμενο βήμα:</h4>
              <p className="text-sm text-neutral-600">
                {recipe.steps[currentStepIndex + 1].title}
              </p>
            </div>
          )}

          {/* Κουμπιά ενεργειών */}
          <div className="flex gap-3 pt-4">
            <Button onClick={handleNextStep} className="flex-1">
              {currentStepIndex === recipe.steps.length - 1
                ? 'Ολοκλήρωση'
                : 'Επόμενο Βήμα'}
            </Button>
            <Button
              variant="outline"
              onClick={() => navigate(`/recipes/${id}`)}
            >
              Ακύρωση
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}

