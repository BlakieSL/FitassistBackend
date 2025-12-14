DELETE
FROM text
WHERE type = 'RECIPE_INSTRUCTION';
DELETE
FROM user_recipe;
DELETE
FROM recipe_food;
DELETE
FROM recipe_category_association;
DELETE
FROM recipe;
DELETE
FROM recipe_category;
DELETE
FROM food;
DELETE
FROM food_category;
DELETE
FROM user
WHERE id = 1;