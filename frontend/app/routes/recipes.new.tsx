import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router';
import { useRecipes } from '~/context/RecipesContext';
import { Button } from '~/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '~/components/ui/card';
import { Input } from '~/components/ui/input';
import { Label } from '~/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '~/components/ui/select';
import { Textarea } from '~/components/ui/textarea';
import type { Difficulty, RecipeStep } from '~/data/mockRecipes';

export default function RecipeForm() {
  const navigate = useNavigate();
  const { id } = useParams();
  const { getRecipe, createRecipe, updateRecipe } = useRecipes();
  const isEdit = !!id;

  const [name, setName] = useState('');
  const [category, setCategory] = useState('');
  const [difficulty, setDifficulty] = useState<Difficulty>('easy');
  const [totalTimeMinutes, setTotalTimeMinutes] = useState('');
  const [ingredients, setIngredients] = useState('');
  const [steps, setSteps] = useState<RecipeStep[]>([]);

  // Φόρτωση δεδομένων για επεξεργασία
  useEffect(() => {
    if (isEdit && id) {
      const recipe = getRecipe(id);
      if (recipe) {
        setName(recipe.name);
        setCategory(recipe.category);
        setDifficulty(recipe.difficulty);
        setTotalTimeMinutes(recipe.totalTimeMinutes.toString());
        setIngredients(recipe.ingredients.join('\n'));
        setSteps(recipe.steps);
      }
    }
  }, [isEdit, id, getRecipe]);

  const handleAddStep = () => {
    const newStep: RecipeStep = {
      id: `step-${Date.now()}`,
      order: steps.length + 1,
      title: '',
      description: '',
      durationMinutes: 0
    };
    setSteps([...steps, newStep]);
  };

  const handleUpdateStep = (index: number, field: keyof RecipeStep, value: string | number) => {
    const updated = [...steps];
    updated[index] = { ...updated[index], [field]: value };
    setSteps(updated);
  };

  const handleRemoveStep = (index: number) => {
    const updated = steps.filter((_, i) => i !== index);
    // Επαναταξινόμηση
    updated.forEach((step, i) => {
      step.order = i + 1;
    });
    setSteps(updated);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const ingredientsList = ingredients
      .split('\n')
      .map(i => i.trim())
      .filter(i => i.length > 0);

    const recipe = {
      name,
      category,
      difficulty,
      totalTimeMinutes: parseInt(totalTimeMinutes, 10),
      ingredients: ingredientsList,
      steps
    };

    if (isEdit && id) {
      await updateRecipe(id, recipe);
    } else {
      await createRecipe(recipe);
    }

    navigate('/');
  };

  return (
    <div className="container mx-auto py-8 px-4 max-w-4xl">
      <Card>
        <CardHeader>
          <CardTitle className="text-2xl">
            {isEdit ? 'Επεξεργασία Συνταγής' : 'Νέα Συνταγή'}
          </CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Όνομα */}
            <div className="space-y-2">
              <Label htmlFor="name">Όνομα Συνταγής</Label>
              <Input
                id="name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
            </div>

            {/* Κατηγορία */}
            <div className="space-y-2">
              <Label htmlFor="category">Κατηγορία</Label>
              <Input
                id="category"
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                required
              />
            </div>

            {/* Δυσκολία */}
            <div className="space-y-2">
              <Label htmlFor="difficulty">Δυσκολία</Label>
              <Select value={difficulty} onValueChange={(v) => setDifficulty(v as Difficulty)}>
                <SelectTrigger id="difficulty">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="easy">Εύκολη</SelectItem>
                  <SelectItem value="medium">Μέτρια</SelectItem>
                  <SelectItem value="hard">Δύσκολη</SelectItem>
                </SelectContent>
              </Select>
            </div>

            {/* Συνολικός χρόνος */}
            <div className="space-y-2">
              <Label htmlFor="totalTime">Συνολικός Χρόνος (λεπτά)</Label>
              <Input
                id="totalTime"
                type="number"
                min="1"
                value={totalTimeMinutes}
                onChange={(e) => setTotalTimeMinutes(e.target.value)}
                required
              />
            </div>

            {/* Υλικά */}
            <div className="space-y-2">
              <Label htmlFor="ingredients">Υλικά (ένα ανά γραμμή)</Label>
              <Textarea
                id="ingredients"
                value={ingredients}
                onChange={(e) => setIngredients(e.target.value)}
                rows={6}
                placeholder="Π.χ.&#10;500g μακαρόνια&#10;2 κρεμμύδια&#10;..."
                required
              />
            </div>

            {/* Βήματα */}
            <div className="space-y-4">
              <div className="flex justify-between items-center">
                <Label>Βήματα Προετοιμασίας</Label>
                <Button type="button" onClick={handleAddStep} variant="outline" size="sm">
                  + Προσθήκη Βήματος
                </Button>
              </div>

              {steps.map((step, index) => (
                <Card key={step.id}>
                  <CardContent className="pt-6 space-y-3">
                    <div className="flex justify-between items-center">
                      <span className="font-semibold text-sm text-neutral-500">
                        Βήμα {step.order}
                      </span>
                      <Button
                        type="button"
                        variant="ghost"
                        size="sm"
                        onClick={() => handleRemoveStep(index)}
                      >
                        Αφαίρεση
                      </Button>
                    </div>

                    <div className="space-y-2">
                      <Label htmlFor={`step-title-${index}`}>Τίτλος Βήματος</Label>
                      <Input
                        id={`step-title-${index}`}
                        value={step.title}
                        onChange={(e) => handleUpdateStep(index, 'title', e.target.value)}
                        required
                      />
                    </div>

                    <div className="space-y-2">
                      <Label htmlFor={`step-desc-${index}`}>Περιγραφή</Label>
                      <Textarea
                        id={`step-desc-${index}`}
                        value={step.description}
                        onChange={(e) => handleUpdateStep(index, 'description', e.target.value)}
                        rows={3}
                        required
                      />
                    </div>

                    <div className="space-y-2">
                      <Label htmlFor={`step-duration-${index}`}>Διάρκεια (λεπτά)</Label>
                      <Input
                        id={`step-duration-${index}`}
                        type="number"
                        min="1"
                        value={step.durationMinutes}
                        onChange={(e) => handleUpdateStep(index, 'durationMinutes', parseInt(e.target.value, 10))}
                        required
                      />
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>

            {/* Κουμπιά υποβολής */}
            <div className="flex gap-3 pt-4">
              <Button type="submit" className="flex-1">
                {isEdit ? 'Ενημέρωση' : 'Δημιουργία'}
              </Button>
              <Button type="button" variant="outline" onClick={() => navigate('/')}>
                Ακύρωση
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}

