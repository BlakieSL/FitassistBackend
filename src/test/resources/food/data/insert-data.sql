INSERT INTO food_category (name)
VALUES ('Fruits'),
       ('Proteins');

INSERT INTO food (name, calories, carbohydrates, fat, protein, food_category_id)
VALUES ('Apple', 95, 25, 0.3, 0.5, 1),
       ('Banana', 105, 27, 0.3, 1.3, 1),
       ('Chicken Breast', 165, 0, 3.6, 31, 2),
       ('Eggs', 70, 1, 5, 6, 2),
       ('Greek Yogurt', 100, 6, 0, 10, 2);

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
        175.00, 70.00),
       ('user2@example.com', '$2a$10$exampleHash2', 'user2', 'FEMALE', '1992-03-15', 'SEDENTARY', 'LOSE_WEIGHT', 165.00,
        60.00);

INSERT INTO daily_cart (date, user_id)
VALUES ('2025-04-01', 1),
       ('2025-04-02', 1),
       ('2025-04-03', 1),
       ('2025-04-01', 2),
       ('2025-04-02', 2),
       ('2025-04-03', 2);

INSERT INTO daily_cart_food (daily_cart_id, food_id, quantity)
VALUES (1, 1, 2.0),
       (1, 3, 1.0),
       (1, 5, 1.5),
       (2, 2, 1.0),
       (2, 4, 2.0),
       (2, 5, 1.0),
       (3, 1, 3.0),
       (3, 2, 1.0),
       (3, 4, 1.0),
       (4, 1, 2.0),
       (4, 3, 1.0),
       (4, 5, 1.0),
       (5, 2, 1.0),
       (5, 4, 2.0),
       (5, 5, 1.0),
       (6, 1, 3.0),
       (6, 2, 1.0),
       (6, 4, 1.0);

INSERT INTO daily_cart_activity (daily_cart_id, activity_id, time)
VALUES (1, 1, 30),
       (1, 4, 45),
       (2, 2, 20),
       (2, 5, 30),
       (3, 3, 40),
       (3, 4, 60),
       (4, 1, 45),
       (4, 5, 30),
       (5, 2, 25),
       (5, 4, 40),
       (6, 3, 50),
       (6, 5, 30);

INSERT INTO recipe_category (name)
VALUES ('Breakfast'),
       ('Lunch'),
       ('Dinner');

INSERT INTO recipe (is_public, name, description, user_id, created_at)
VALUES (true, 'Apple Pie', 'Delicious apple pie recipe', 1, NOW()),
       (true, 'Banana Smoothie', 'Healthy banana smoothie', 1, NOW()),
       (true, 'Chicken Salad', 'Protein-rich chicken salad', 2, NOW());

INSERT INTO recipe_food (recipe_id, food_id, quantity)
VALUES (1, 1, 500),
       (2, 2, 200),
       (2, 5, 150),
       (3, 3, 300),
       (3, 1, 100);

INSERT INTO user_food (food_id, user_id, created_at)
VALUES (1, 1, NOW()),
       (1, 2, NOW()),
       (2, 1, NOW()),
       (3, 2, NOW());