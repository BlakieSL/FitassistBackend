INSERT INTO user (id, username, email, password, birthday, gender)
VALUES (1, 'testuser', 'test@example.com', 'password', '1990-01-01', 'MALE'),
       (2, 'testuser2', 'test@gmail.com', 'password', '1990-01-01', 'FEMALE');

INSERT INTO food_category (id, name)
VALUES (1, 'Vegetables'),
       (2, 'Proteins'),
       (3, 'Grains');

INSERT INTO food (id, name, calories, carbohydrates, fat, protein, food_category_id)
VALUES (1, 'Carrot', 41.0, 9.60, 0.20, 0.90, 1),
       (2, 'Chicken Breast', 165.0, 0.00, 3.60, 31.00, 2),
       (3, 'Rice', 130.0, 28.00, 0.30, 2.70, 3);

INSERT INTO recipe_category (id, name)
VALUES (1, 'Vegetarian'),
       (2, 'High-Protein'),
       (3, 'Quick Meals');

INSERT INTO recipe (is_public, id, name, description, user_id, created_at)
VALUES (true, 1, 'Vegetable Stir Fry', 'Healthy vegetable dish', 1, NOW()),
       (true, 2, 'Grilled Chicken', 'High protein meal', 1, NOW()),
       (true, 3, 'Chicken Rice Bowl', 'Balanced meal with protein and carbs', 1, NOW()),
       (false, 4, 'Secret Recipe', 'This is a private recipe', 1, NOW());

INSERT INTO recipe_category_association (id, recipe_id, recipe_category_id)
VALUES (1, 1, 1),
       (2, 1, 3),
       (3, 2, 2),
       (4, 3, 2),
       (5, 3, 3);

INSERT INTO recipe_food (id, quantity, recipe_id, food_id)
VALUES (1, 200.00, 1, 1),
       (2, 150.00, 2, 2),
       (3, 100.00, 3, 2),
       (4, 150.00, 3, 3);

INSERT INTO user_recipe (id, type, recipie_id, user_id, created_at)
VALUES (1, 'SAVE', 1, 1, NOW()),
       (2, 'LIKE', 2, 1, NOW()),
       (3, 'SAVE', 3, 1, NOW()),
       (4, 'LIKE', 3, 1, NOW());

INSERT INTO text (type, id, order_index, text, title, recipe_id)
VALUES ('RECIPE_INSTRUCTION', 1, 1, 'Chop all vegetables', 'Prep', 1),
       ('RECIPE_INSTRUCTION', 2, 2, 'Stir fry for 5 minutes', 'Cooking', 1),
       ('RECIPE_INSTRUCTION', 3, 1, 'Season chicken with salt and pepper', 'Prep', 2),
       ('RECIPE_INSTRUCTION', 4, 2, 'Grill for 6 minutes per side', 'Cooking', 2),
       ('RECIPE_INSTRUCTION', 5, 1, 'Cook rice according to package', 'Rice', 3),
       ('RECIPE_INSTRUCTION', 6, 2, 'Grill chicken separately', 'Chicken', 3),
       ('RECIPE_INSTRUCTION', 7, 3, 'Combine in bowl', 'Assembly', 3);
