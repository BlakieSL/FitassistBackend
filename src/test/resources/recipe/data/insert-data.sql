INSERT INTO user (id, username, email, password, birthday, gender)
VALUES (1, 'testuser', 'test@example.com', 'password', '1990-01-01', 'MALE'),
       (2, 'seconduser', 'second@example.com', 'password', '1992-05-15', 'FEMALE');

INSERT INTO food_category (id, name)
VALUES (1, 'Vegetables'),
       (2, 'Proteins'),
       (3, 'Grains');

INSERT INTO food (id, name, calories, carbohydrates, fat, protein, food_category_id)
VALUES (1, 'Carrot', 41, 9.6, 0.2, 0.9, 1),
       (2, 'Chicken Breast', 165, 0, 3.6, 31, 2),
       (3, 'Rice', 130, 28, 0.3, 2.7, 3);

INSERT INTO recipe_category (id, name)
VALUES (1, 'Vegetarian'),
       (2, 'High-Protein'),
       (3, 'Quick Meals');

INSERT INTO recipe (is_public, id, name, description, minutes_to_prepare, user_id, created_at)
VALUES (true, 1, 'Vegetable Stir Fry', 'Healthy vegetable dish', 15, 1, NOW()),
       (true, 2, 'Grilled Chicken', 'High protein meal', 25, 1, NOW()),
       (true, 3, 'Chicken Rice Bowl', 'Balanced meal with protein and carbs', 30, 2, NOW()),
       (false, 4, 'Secret Recipe', 'This is a private recipe', 20, 1, NOW()),
       (true, 5, 'Pasta Primavera', 'Fresh vegetable pasta', 35, 2, NOW());

INSERT INTO recipe_category_association (id, recipe_id, recipe_category_id)
VALUES (1, 1, 1),
       (2, 1, 3),
       (3, 2, 2),
       (4, 3, 2),
       (5, 3, 3),
       (6, 4, 1),
       (7, 5, 1),
       (8, 5, 3);

INSERT INTO recipe_food (id, quantity, recipe_id, food_id)
VALUES (1, 200, 1, 1),
       (2, 150, 2, 2),
       (3, 100, 3, 2),
       (4, 150, 3, 3),
       (5, 100, 5, 1);

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
       ('RECIPE_INSTRUCTION', 7, 3, 'Combine in bowl', 'Assembly', 3),
       ('RECIPE_INSTRUCTION', 8, 1, 'Cook pasta al dente', 'Pasta', 5),
       ('RECIPE_INSTRUCTION', 9, 2, 'Saute vegetables', 'Vegetables', 5);

INSERT INTO media (id, image_name, parent_id, parentType)
VALUES (1, 'recipe1_image1.jpg', 1, 'RECIPE'),
       (2, 'recipe1_image2.jpg', 1, 'RECIPE'),
       (3, 'user1_profile.jpg', 1, 'USER');