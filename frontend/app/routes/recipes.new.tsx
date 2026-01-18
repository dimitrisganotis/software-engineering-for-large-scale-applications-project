import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router";
import { useRecipes } from "~/context/RecipesContext";
import {
  api,
  type Recipe,
  type RecipeStep,
  type Ingredient,
  type Difficulty,
} from "~/lib/api";
import { Button } from "~/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "~/components/ui/card";
import { Input } from "~/components/ui/input";
import { Label } from "~/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "~/components/ui/select";
import { Textarea } from "~/components/ui/textarea";
import { Checkbox } from "~/components/ui/checkbox";

export default function RecipeForm() {
  const navigate = useNavigate();
  const { id } = useParams();
  const { recipes, getRecipe, createRecipe, updateRecipe, refreshRecipes } =
    useRecipes();
  // We compare with string because url params are strings
  const isEdit = !!id;

  const [name, setName] = useState("");
  const [category, setCategory] = useState("");
  const [difficulty, setDifficulty] = useState<Difficulty>("EASY");
  const [totalTimeMinutes, setTotalTimeMinutes] = useState("");

  // Structured Ingredients
  const [ingredients, setIngredients] = useState<Ingredient[]>([]);

  const [steps, setSteps] = useState<RecipeStep[]>([]);

  // Photo Files
  const [recipePhoto, setRecipePhoto] = useState<File | null>(null);
  const [existingPhotos, setExistingPhotos] = useState<string[]>([]);
  const [stepPhotos, setStepPhotos] = useState<Record<number, File>>({}); // index -> file

  useEffect(() => {
    if (isEdit && id) {
      // Find directly from recipes array to ensure reactivity when data loads
      const recipe = recipes.find((r) => r.id.toString() === id.toString());
      if (recipe) {
        setName(recipe.title);
        setCategory(recipe.category);
        setDifficulty(recipe.difficulty);
        setTotalTimeMinutes(recipe.totalTimeMinutes.toString());
        setIngredients(recipe.ingredients);
        setSteps(recipe.steps);
        setExistingPhotos(recipe.imageUrls || []);
      }
    }
  }, [isEdit, id, recipes]);

  // --- Ingredient Handlers ---
  const handleAddIngredient = () => {
    setIngredients([...ingredients, { name: "", quantity: 0, unit: "" }]);
  };

  const handleUpdateIngredient = (
    index: number,
    field: keyof Ingredient,
    value: string | number,
  ) => {
    const updated = [...ingredients];
    updated[index] = { ...updated[index], [field]: value };
    setIngredients(updated);
  };

  const handleRemoveIngredient = (index: number) => {
    setIngredients(ingredients.filter((_, i) => i !== index));
  };

  // --- Step Handlers ---
  const handleAddStep = () => {
    const newStep: RecipeStep = {
      stepOrder: steps.length + 1,
      title: "",
      description: "",
      durationMinutes: 0,
      ingredients: [], // Init empty linked ingredients
    };
    setSteps([...steps, newStep]);
  };

  const handleUpdateStep = (
    index: number,
    field: keyof RecipeStep,
    value: any,
  ) => {
    const updated = [...steps];
    updated[index] = { ...updated[index], [field]: value };
    setSteps(updated);
  };

  const handleRemoveStep = (index: number) => {
    const updated = steps.filter((_, i) => i !== index);
    updated.forEach((step, i) => {
      step.stepOrder = i + 1;
    });
    setSteps(updated);
  };

  const toggleStepIngredient = (stepIndex: number, ingredient: Ingredient) => {
    const step = steps[stepIndex];
    const currentLinked = step.ingredients || [];
    const exists = currentLinked.some((i) => i.name === ingredient.name); // Match by name or some ID if available

    let newLinked;
    if (exists) {
      newLinked = currentLinked.filter((i) => i.name !== ingredient.name);
    } else {
      newLinked = [...currentLinked, ingredient];
    }
    handleUpdateStep(stepIndex, "ingredients", newLinked);
  };

  // --- Submit Handler ---
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const recipeData: any = {
      title: name,
      category,
      difficulty,
      totalTimeMinutes: parseInt(totalTimeMinutes, 10),
      ingredients,
      steps,
    };
    // Initialize prepTime same as total for simplicity or add field
    recipeData.prepTimeMinutes = recipeData.totalTimeMinutes;

    try {
      if (isEdit && id) {
        await updateRecipe(id, recipeData);
        // Handle photos for edit if needed (skipped for MVP simplicity or add logic)
      } else {
        // 1. Create Recipe
        const savedRecipe = await api.createRecipe(recipeData);

        // 2. Upload Recipe Photo
        if (savedRecipe.id && recipePhoto) {
          await api.uploadPhoto(savedRecipe.id, recipePhoto);
        }

        // 3. Upload Step Photos
        if (savedRecipe.id && savedRecipe.steps) {
          for (let i = 0; i < savedRecipe.steps.length; i++) {
            const file = stepPhotos[i];
            const stepId = savedRecipe.steps[i].id;
            if (file && stepId) {
              await api.uploadStepPhoto(savedRecipe.id, stepId, file);
            }
          }
        }
      }
      await refreshRecipes();
      navigate("/");
    } catch (err) {
      console.error(err);
      alert("Failed to save recipe");
    }
  };

  return (
    <div className="container mx-auto py-8 px-4 max-w-4xl">
      <div className="mb-6">
        <Button variant="ghost" size="sm" onClick={() => navigate("/")}>
          ← Επιστροφή στην Αρχική
        </Button>
      </div>
      <Card>
        <CardHeader>
          <CardTitle className="text-2xl">
            {isEdit ? "Επεξεργασία Συνταγής" : "Νέα Συνταγή"}
          </CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* --- Basic Info --- */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="name">Όνομα Συνταγής</Label>
                <Input
                  id="name"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  required
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="category">Κατηγορία</Label>
                <Select value={category} onValueChange={setCategory}>
                  <SelectTrigger>
                    <SelectValue placeholder="Επιλέξτε" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="PASTA">Zυμαρικά</SelectItem>
                    <SelectItem value="MEAT">Κρέας</SelectItem>
                    <SelectItem value="VEGETARIAN">Χορτοφαγικό</SelectItem>
                    <SelectItem value="DESSERT">Γλυκό</SelectItem>
                    <SelectItem value="SOUP">Σούπα</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="difficulty">Δυσκολία</Label>
                <Select
                  value={difficulty}
                  onValueChange={(v) => setDifficulty(v as Difficulty)}
                >
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="EASY">Εύκολη</SelectItem>
                    <SelectItem value="MEDIUM">Μέτρια</SelectItem>
                    <SelectItem value="HARD">Δύσκολη</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="totalTime">Χρόνος (λεπτά)</Label>
                <Input
                  type="number"
                  value={totalTimeMinutes}
                  onChange={(e) => setTotalTimeMinutes(e.target.value)}
                  required
                />
              </div>

              <div className="space-y-2 col-span-2">
                <Label htmlFor="photo">Φωτογραφία Συνταγής</Label>
                {/* Preview existing photos */}
                {isEdit && id && existingPhotos.length > 0 && (
                  <div className="flex gap-2 mb-2 overflow-x-auto">
                    {existingPhotos.map((url, idx) => (
                      <img
                        key={idx}
                        src={
                          url.startsWith("http")
                            ? url
                            : `http://localhost:8080/api/recipes/${id}/photo/${url}`
                        }
                        alt={`Exists ${idx}`}
                        className="h-16 w-16 object-cover rounded border"
                      />
                    ))}
                  </div>
                )}
                <Input
                  id="photo"
                  type="file"
                  onChange={(e) =>
                    e.target.files && setRecipePhoto(e.target.files[0])
                  }
                />
              </div>
            </div>

            {/* --- Ingredients --- */}
            <div className="space-y-4 border p-4 rounded-md">
              <div className="flex justify-between items-center">
                <Label className="text-lg">Υλικά</Label>
                <Button
                  type="button"
                  onClick={handleAddIngredient}
                  variant="outline"
                  size="sm"
                >
                  + Υλικό
                </Button>
              </div>
              {ingredients.map((ing, i) => (
                <div key={i} className="flex gap-2 items-center">
                  <Input
                    placeholder="Όνομα"
                    value={ing.name}
                    onChange={(e) =>
                      handleUpdateIngredient(i, "name", e.target.value)
                    }
                    className="flex-2"
                    required
                  />
                  <Input
                    type="number"
                    placeholder="Ποσότητα"
                    value={ing.quantity}
                    onChange={(e) =>
                      handleUpdateIngredient(
                        i,
                        "quantity",
                        parseFloat(e.target.value),
                      )
                    }
                    className="w-24"
                    required
                  />
                  <Input
                    placeholder="Μονάδα"
                    value={ing.unit}
                    onChange={(e) =>
                      handleUpdateIngredient(i, "unit", e.target.value)
                    }
                    className="w-24"
                    required
                  />
                  <Button
                    type="button"
                    variant="ghost"
                    onClick={() => handleRemoveIngredient(i)}
                  >
                    ✕
                  </Button>
                </div>
              ))}
            </div>

            {/* --- Steps --- */}
            <div className="space-y-4 border p-4 rounded-md">
              <div className="flex justify-between items-center">
                <Label className="text-lg">Βήματα</Label>
                <Button
                  type="button"
                  onClick={handleAddStep}
                  variant="outline"
                  size="sm"
                >
                  + Βήμα
                </Button>
              </div>

              {steps.map((step, index) => (
                <Card key={index} className="bg-neutral-50">
                  <CardContent className="pt-6 space-y-3">
                    <div className="flex justify-between">
                      <span className="font-bold">Βήμα {index + 1}</span>
                      <Button
                        type="button"
                        variant="ghost"
                        size="sm"
                        onClick={() => handleRemoveStep(index)}
                      >
                        Αφαίρεση
                      </Button>
                    </div>

                    <Input
                      placeholder="Τίτλος Βήματος"
                      value={step.title}
                      onChange={(e) =>
                        handleUpdateStep(index, "title", e.target.value)
                      }
                      required
                    />
                    <Textarea
                      placeholder="Περιγραφή"
                      value={step.description}
                      onChange={(e) =>
                        handleUpdateStep(index, "description", e.target.value)
                      }
                      required
                    />

                    <div>
                      <Label>Διάρκεια (λεπτά)</Label>
                      <Input
                        type="number"
                        placeholder="Διάρκεια (λεπτά)"
                        value={step.durationMinutes}
                        onChange={(e) =>
                          handleUpdateStep(
                            index,
                            "durationMinutes",
                            parseInt(e.target.value),
                          )
                        }
                        required
                      />
                    </div>

                    {/* Step Photo */}
                    <div>
                      <Label>Φωτογραφία Βήματος</Label>
                      {step.imageUrl && (
                        <div className="mb-2">
                          <img
                            src={
                              step.imageUrl.startsWith("http")
                                ? step.imageUrl
                                : `http://localhost:8080/api/recipes/${id}/steps/${step.id}/photo/${step.imageUrl}`
                            }
                            alt="Step"
                            className="h-20 w-auto rounded object-cover"
                          />
                        </div>
                      )}
                      <Input
                        type="file"
                        onChange={(e) =>
                          e.target.files &&
                          setStepPhotos((prev) => ({
                            ...prev,
                            [index]: e.target.files![0],
                          }))
                        }
                      />
                    </div>

                    {/* Link Ingredients */}
                    <div className="pt-2">
                      <Label>Υλικά Βήματος:</Label>
                      <div className="flex flex-wrap gap-2 mt-1">
                        {ingredients.map((ing, ingIdx) => (
                          <div
                            key={ingIdx}
                            className="flex items-center space-x-2"
                          >
                            <Checkbox
                              id={`step-${index}-ing-${ingIdx}`}
                              checked={step.ingredients?.some(
                                (si) => si.name === ing.name,
                              )}
                              onChange={() => toggleStepIngredient(index, ing)}
                            />
                            <label
                              htmlFor={`step-${index}-ing-${ingIdx}`}
                              className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
                            >
                              {ing.name}
                            </label>
                          </div>
                        ))}
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>

            <div className="flex gap-3 pt-4">
              <Button type="submit" className="flex-1">
                Αποθήκευση
              </Button>
              <Button
                type="button"
                variant="outline"
                onClick={() => navigate("/")}
              >
                Ακύρωση
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
