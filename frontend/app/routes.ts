import { type RouteConfig, index, route } from "@react-router/dev/routes";

export default [
  index("routes/recipes.tsx"),
  route("recipes/new", "routes/recipes.new.tsx"),
  route("recipes/:id", "routes/recipes.$id.tsx"),
  route("recipes/:id/edit", "routes/recipes.$id.edit.tsx"),
  route("recipes/:id/execute", "routes/recipes.$id.execute.tsx"),
] satisfies RouteConfig;
