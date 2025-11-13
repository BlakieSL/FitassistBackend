INSERT INTO role (name) VALUES
                            ('USER'), ('ADMIN'), ('MOD');

INSERT INTO activity_category (name) VALUES
                                         ('Running'), ('Cycling'), ('Swimming'), ('Weight Training');

INSERT INTO food_category (name) VALUES
                                     ('Fruits'), ('Vegetables'), ('Dairy'), ('Meat'), ('Grains');

INSERT INTO recipe_category (name) VALUES
                                       ('Breakfast'), ('Lunch'), ('Dinner'), ('Snack'), ('Dessert');

INSERT INTO plan_category (name) VALUES
                                     ('Weight Loss'), ('Muscle Gain'), ('Maintenance'), ('Endurance');

INSERT INTO plan_type (name) VALUES
                                 ('Diet'), ('Workout'), ('Mixed');

INSERT INTO thread_category (name) VALUES
                                       ('General'), ('Nutrition'), ('Workouts'), ('Progress');

INSERT INTO expertise_level (name) VALUES
                                       ('Beginner'), ('Intermediate'), ('Advanced');

INSERT INTO equipment (name) VALUES
                                 ('Dumbbells'), ('Barbell'), ('Kettlebell'), ('Resistance Bands'), ('None');

INSERT INTO force_type (name) VALUES
                                  ('Push'), ('Pull'), ('Static');

INSERT INTO mechanics_type (name) VALUES
                                      ('Compound'), ('Isolation');

INSERT INTO target_muscle (name) VALUES
                                     ('Chest'), ('Back'), ('Legs'), ('Arms'), ('Shoulders'), ('Core');

INSERT INTO user (username, email, password, birthday, gender, height, weight, activity_level, goal) VALUES
                                                                                                         ('testuser', 'user@test.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '1990-01-01', 'MALE', 180.00, 75.00, 'MODERATELY_ACTIVE', 'BUILD_MUSCLE'),
                                                                                                         ('adminuser', 'admin@test.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '1985-05-15', 'FEMALE', 165.00, 60.00, 'VERY_ACTIVE', 'LOSE_WEIGHT'),
                                                                                                         ('moduser', 'mod@test.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '1995-11-20', 'MALE', 175.00, 70.00, 'LIGHTLY_ACTIVE', 'MAINTAIN_WEIGHT');

INSERT INTO user_roles (users_id, roles_id) VALUES
                                                (1, 1),
                                                (2, 1), (2, 2),
                                                (3, 1), (3, 3);

INSERT INTO activity (name, met, activity_category_id) VALUES
                                                           ('Running (6 mph)', 9.8, 1),
                                                           ('Cycling (moderate)', 7.5, 2),
                                                           ('Swimming (freestyle)', 8.3, 3),
                                                           ('Weight Training (general)', 6.0, 4);

INSERT INTO food (name, calories, protein, carbohydrates, fat, food_category_id) VALUES
                                                                                     ('Apple', 52.00, 0.30, 14.00, 0.20, 1),
                                                                                     ('Chicken Breast', 165.00, 31.00, 0.00, 3.60, 4),
                                                                                     ('Brown Rice', 111.00, 2.60, 23.00, 0.90, 5),
                                                                                     ('Broccoli', 34.00, 2.80, 6.60, 0.40, 2),
                                                                                     ('Greek Yogurt', 59.00, 10.00, 3.60, 0.40, 3);

INSERT INTO exercise (name, description, equipment_id, expertise_level_id, force_type_id, mechanics_type_id) VALUES
                                                                                                                 ('Bench Press', 'Basic chest exercise', 2, 1, 1, 1),
                                                                                                                 ('Pull-up', 'Upper body compound exercise', 5, 2, 2, 1),
                                                                                                                 ('Squat', 'Fundamental lower body exercise', 2, 1, 1, 1),
                                                                                                                 ('Bicep Curl', 'Arm isolation exercise', 1, 1, 2, 2);

INSERT INTO exercise_target_muscle (exercise_id, target_muscle_id, priority) VALUES
                                                                                 (1, 1, 1.00),
                                                                                 (2, 2, 1.00), (2, 5, 0.80),
                                                                                 (3, 3, 1.00),
                                                                                 (4, 4, 1.00);

INSERT INTO recipe (is_public, name, description, user_id) VALUES
                                                    (true, 'Protein Shake', 'Quick post-workout shake', 1),
                                                    (true, 'Chicken & Rice', 'Simple meal prep recipe', 2),
                                                    (true, 'Greek Yogurt Parfait', 'Healthy breakfast option', 3);

INSERT INTO recipe_category_association (recipe_id, recipe_category_id) VALUES
                                                                            (1, 4),
                                                                            (2, 2), (2, 3),
                                                                            (3, 1);

INSERT INTO recipe_food (recipe_id, food_id, quantity) VALUES
                                                           (1, 5, 200.00),
                                                           (2, 2, 150.00), (2, 3, 100.00),
                                                           (3, 5, 150.00), (3, 1, 100.00);

INSERT INTO plan (is_public, name, description, user_id, plan_type_id) VALUES
                                                                (true, 'Summer Cut', '12-week fat loss program', 1, 3),
                                                                (true, 'Strength Builder', '6-month strength program', 2, 2),
                                                                (true, 'Balanced Lifestyle', 'General fitness maintenance', 3, 3);

INSERT INTO plan_category_association (plan_id, plan_category_id) VALUES
                                                                      (1, 1),
                                                                      (2, 2),
                                                                      (3, 3);

INSERT INTO workout (name, duration, plan_id) VALUES
                                                  ('Upper Body A', 60.00, 1),
                                                  ('Lower Body A', 45.00, 1),
                                                  ('Push Day', 75.00, 2),
                                                  ('Pull Day', 75.00, 2),
                                                  ('Full Body', 50.00, 3);

INSERT INTO workout_set_group (workout_id, orderIndex, restSeconds) VALUES
                                                                        (1, 1, 90), (1, 2, 60),
                                                                        (2, 1, 120),
                                                                        (3, 1, 90), (3, 2, 90),
                                                                        (4, 1, 90),
                                                                        (5, 1, 60);

INSERT INTO workout_set (workout_set_group_id, exercise_id, repetitions, weight) VALUES
                                                                                     (1, 1, 10.00, 60.00), (1, 1, 8.00, 70.00), (1, 1, 6.00, 80.00),
                                                                                     (2, 4, 12.00, 10.00), (2, 4, 10.00, 12.50),
                                                                                     (3, 3, 8.00, 100.00), (3, 3, 8.00, 100.00), (3, 3, 8.00, 100.00),
                                                                                     (4, 1, 5.00, 80.00), (4, 1, 5.00, 80.00), (4, 1, 5.00, 80.00),
                                                                                     (5, 2, 8.00, 0.00), (5, 2, 8.00, 0.00),
                                                                                     (6, 3, 10.00, 60.00), (6, 1, 10.00, 50.00), (6, 4, 12.00, 8.00);

INSERT INTO thread (title, text, dateCreated, views, user_id, thread_category_id) VALUES
                                                                                      ('Getting started with fitness', 'What are some good beginner tips?', NOW(), 25, 1, 1),
                                                                                      ('Protein intake question', 'How much protein should I consume daily?', NOW(), 42, 2, 2),
                                                                                      ('Squat form check', 'Looking for feedback on my squat technique', NOW(), 18, 3, 3);

INSERT INTO comment (text, dateCreated, thread_id, user_id, parent_comment_id) VALUES
                                                                                   ('Start with bodyweight exercises and focus on form!', '2025-11-13 20:50:00.123456', 1, 2, NULL),
                                                                                   ('Thanks for the advice!', '2025-11-13 20:51:00.654321', 1, 1, 1),
                                                                                   ('1.6-2.2g per kg of body weight is a good range', '2025-11-13 20:52:00.987654', 2, 3, NULL),
                                                                                   ('I aim for 30% of my calories from protein', '2025-11-13 20:53:00.456789', 2, 1, NULL),
                                                                                   ('Post a video for better feedback', '2025-11-13 20:54:00.321654', 3, 2, NULL);
INSERT INTO user_activity (user_id, activity_id) VALUES
                                                     (1, 1), (1, 4),
                                                     (2, 2), (2, 3),
                                                     (3, 1), (3, 2), (3, 4);

INSERT INTO user_food (user_id, food_id) VALUES
                                             (1, 2), (1, 3),
                                             (2, 1), (2, 5),
                                             (3, 4), (3, 5);

INSERT INTO user_exercise (user_id, exercise_id) VALUES
                                                     (1, 1),
                                                     (1, 3),
                                                     (2, 2),
                                                     (2, 4),
                                                     (3, 1),
                                                     (3, 2),
                                                     (3, 3);

INSERT INTO user_recipe (user_id, recipie_id, type) VALUES
                                                        (1, 2, 'LIKE'), (1, 2, 'SAVE'),
                                                        (2, 3, 'LIKE'),
                                                        (3, 1, 'SAVE');

INSERT INTO user_plan (user_id, plan_id, type) VALUES
                                                   (1, 1, 'SAVE'),
                                                   (2, 2, 'SAVE'),
                                                   (2, 2, 'LIKE');

INSERT INTO user_comment (user_id, comment_id, type) VALUES
                                                         (1, 3, 'LIKE'),
                                                         (2, 1, 'LIKE'),
                                                         (3, 5, 'LIKE');

INSERT INTO user_thread (user_id, thread_id) VALUES
                                                 (1, 2),
                                                 (2, 3),
                                                 (3, 2);

INSERT INTO daily_cart (user_id, date) VALUES
                                           (1, CURDATE()),
                                           (2, CURDATE()),
                                           (3, CURDATE());

INSERT INTO daily_cart_activity (daily_cart_id, activity_id, time) VALUES
                                                                       (1, 1, 30),
                                                                       (2, 3, 45),
                                                                       (3, 4, 60);

INSERT INTO daily_cart_food (daily_cart_id, food_id, quantity) VALUES
                                                                   (1, 2, 200.00), (1, 3, 150.00),
                                                                   (2, 5, 200.00), (2, 1, 100.00),
                                                                   (3, 4, 150.00), (3, 2, 150.00);