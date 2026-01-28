INSERT INTO food_category (name)
VALUES ('Fruits'),
       ('Proteins');

INSERT INTO food (name, calories, carbohydrates, fat, protein, food_category_id)
VALUES ('Apple', 95.0, 25.00, 0.30, 0.50, 1),
       ('Banana', 105.0, 27.00, 0.30, 1.30, 1),
       ('Chicken Breast', 165.0, 0.00, 3.60, 31.00, 2),
       ('Eggs', 70.0, 1.00, 5.00, 6.00, 2),
       ('Greek Yogurt', 100.0, 6.00, 0.00, 10.00, 2);

INSERT INTO activity_category (name)
VALUES ('Cardio'),
       ('Strength');

INSERT INTO activity (name, met, activity_category_id)
VALUES ('Walking', 3.5, 1),
       ('Running', 8.0, 1),
       ('Cycling', 6.0, 1),
       ('Weight Lifting', 6.0, 2),
       ('Bodyweight Workout', 5.0, 2);

INSERT INTO user (email, password, username, gender, birthday, activity_level, goal, height, weight)
VALUES ('user1@example.com', '$2a$10$exampleHash1', 'user1', 'MALE', '1990-01-01', 'MODERATELY_ACTIVE', 'BUILD_MUSCLE',
        175.0, 70.0),
       ('user2@example.com', '$2a$10$exampleHash2', 'user2', 'FEMALE', '1992-03-15', 'SEDENTARY', 'LOSE_WEIGHT', 165.0,
        60.0);

INSERT INTO daily_cart (date, user_id)
VALUES ('2025-04-01', 1),
       ('2025-04-02', 1),
       ('2025-04-03', 1),
       ('2025-04-01', 2),
       ('2025-04-02', 2),
       ('2025-04-03', 2);

INSERT INTO daily_cart_food (daily_cart_id, food_id, quantity)
VALUES (1, 1, 2.00),
       (1, 3, 1.00),
       (1, 5, 1.50),
       (2, 2, 1.00),
       (2, 4, 2.00),
       (2, 5, 1.00),
       (3, 1, 3.00),
       (3, 2, 1.00),
       (3, 4, 1.00),
       (4, 1, 2.00),
       (4, 3, 1.00),
       (4, 5, 1.00),
       (5, 2, 1.00),
       (5, 4, 2.00),
       (5, 5, 1.00),
       (6, 1, 3.00),
       (6, 2, 1.00),
       (6, 4, 1.00);

INSERT INTO daily_cart_activity (daily_cart_id, activity_id, time, weight)
VALUES (1, 1, 30, 70.00),
       (1, 4, 45, 70.00),
       (2, 2, 20, 70.00),
       (2, 5, 30, 70.00),
       (3, 3, 40, 70.00),
       (3, 4, 60, 70.00),
       (5, 2, 25, 60.00),
       (5, 4, 40, 60.00),
       (6, 3, 50, 60.00),
       (6, 5, 30, 60.00);

INSERT INTO recipe_category (name)
VALUES ('Breakfast'),
       ('Lunch'),
       ('Dinner');

INSERT INTO recipe (is_public, name, description, user_id, created_at)
VALUES (true, 'Apple Pie', 'Delicious apple pie recipe', 1, NOW()),
       (true, 'Banana Smoothie', 'Healthy banana smoothie', 1, NOW()),
       (true, 'Chicken Salad', 'Protein-rich chicken salad', 2, NOW());

INSERT INTO recipe_food (recipe_id, food_id, quantity)
VALUES (1, 1, 500.00),
       (2, 2, 200.00),
       (2, 5, 150.00),
       (3, 3, 300.00),
       (3, 1, 100.00);

INSERT INTO user_food (food_id, user_id, created_at)
VALUES (1, 1, NOW()),
       (1, 2, NOW()),
       (2, 1, NOW()),
       (3, 2, NOW());
