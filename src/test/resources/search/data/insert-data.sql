
INSERT INTO expertise_level (name) VALUES ('Beginner'), ('Intermediate'), ('Advanced');
INSERT INTO equipment (name) VALUES ('Barbell'), ('Dumbbell'), ('Bodyweight'), ('Machine'), ('Kettlebell'), ('Pull-up Bar');
INSERT INTO force_type (name) VALUES ('Push'), ('Pull'), ('Legs');
INSERT INTO mechanics_type (name) VALUES ('Compound'), ('Isolation');
INSERT INTO target_muscle (name) VALUES ('Chest'), ('Back'), ('Quadriceps'), ('Hamstrings'), ('Shoulders'), ('Biceps'), ('Triceps'), ('Core'), ('Glutes'), ('Calves');
INSERT INTO activity_category (name) VALUES ('Running'), ('Cycling'), ('Swimming'), ('Sports'), ('Household'), ('Other');
INSERT INTO food_category (name) VALUES ('Fruits'), ('Vegetables'), ('Proteins'), ('Grains'), ('Dairy'), ('Fats');
INSERT INTO plan_type (name) VALUES ('Workout Plan'), ('Nutrition Plan'), ('Hybrid Plan');
INSERT INTO recipe_category (name) VALUES ('Breakfast'), ('Lunch'), ('Dinner'), ('Snack'), ('Dessert');
INSERT INTO role (name) VALUES ('USER'), ('ADMIN');

INSERT INTO user (username, email, password, gender, birthday, height, weight, activity_level, goal)
VALUES ('testuser', 'test@example.com', '$2a$10$examplehashedpassword', 'MALE', '1990-01-01', 180.00, 75.00, 'MODERATELY_ACTIVE', 'BUILD_MUSCLE');

INSERT INTO food (name, calories, protein, carbohydrates, fat, food_category_id) VALUES
                                                                                     ('test', 52.00, 0.26, 13.81, 0.17, (SELECT id FROM food_category WHERE name = 'Fruits')),
                                                                                     ('test', 165.00, 31.00, 0.00, 3.60, (SELECT id FROM food_category WHERE name = 'Proteins')),
                                                                                     ('test', 111.00, 2.59, 23.00, 0.90, (SELECT id FROM food_category WHERE name = 'Grains')),
                                                                                     ('test', 34.00, 2.82, 6.64, 0.37, (SELECT id FROM food_category WHERE name = 'Vegetables')),
                                                                                     ('test', 208.00, 20.42, 0.00, 13.42, (SELECT id FROM food_category WHERE name = 'Proteins')),
                                                                                     ('Greek Yogurt (Plain)', 59.00, 10.00, 3.60, 0.40, (SELECT id FROM food_category WHERE name = 'Dairy'));

INSERT INTO activity (name, met, activity_category_id) VALUES ('Running (8 km/h)', 8.00, (SELECT id FROM activity_category WHERE name = 'Running')), ('Cycling (Leisure)', 4.00, (SELECT id FROM activity_category WHERE name = 'Cycling')), ('Swimming (Moderate)', 6.00, (SELECT id FROM activity_category WHERE name = 'Swimming')), ('Weight Lifting (General)', 3.00, (SELECT id FROM activity_category WHERE name = 'Other')), ('Walking (3.2 km/h)', 2.50, (SELECT id FROM activity_category WHERE name = 'Other')), ('House Cleaning', 3.50, (SELECT id FROM activity_category WHERE name = 'Household'));

INSERT INTO exercise (name, description, equipment_id, expertise_level_id, force_type_id, mechanics_type_id) VALUES ('Bench Press', 'Lying on a bench, press a barbell upwards.', (SELECT id FROM equipment WHERE name = 'Barbell'), (SELECT id FROM expertise_level WHERE name = 'Intermediate'), (SELECT id FROM force_type WHERE name = 'Push'), (SELECT id FROM mechanics_type WHERE name = 'Compound')), ('Pull-up', 'Hang from a bar and pull your body up.', (SELECT id FROM equipment WHERE name = 'Pull-up Bar'), (SELECT id FROM expertise_level WHERE name = 'Intermediate'), (SELECT id FROM force_type WHERE name = 'Pull'), (SELECT id FROM mechanics_type WHERE name = 'Compound')), ('Squats', 'Lower your body by bending knees and hips.', (SELECT id FROM equipment WHERE name = 'Barbell'), (SELECT id FROM expertise_level WHERE name = 'Beginner'), (SELECT id FROM force_type WHERE name = 'Legs'), (SELECT id FROM mechanics_type WHERE name = 'Compound')), ('Bicep Curl (Dumbbell)', 'Curl dumbbells upwards while standing.', (SELECT id FROM equipment WHERE name = 'Dumbbell'), (SELECT id FROM expertise_level WHERE name = 'Beginner'), (SELECT id FROM force_type WHERE name = 'Pull'), (SELECT id FROM mechanics_type WHERE name = 'Isolation')), ('Tricep Extension (Cable)', 'Extend arms using a cable machine.', (SELECT id FROM equipment WHERE name = 'Machine'), (SELECT id FROM expertise_level WHERE name = 'Intermediate'), (SELECT id FROM force_type WHERE name = 'Push'), (SELECT id FROM mechanics_type WHERE name = 'Isolation')), ('Push-up', 'Push body up from plank position.', (SELECT id FROM equipment WHERE name = 'Bodyweight'), (SELECT id FROM expertise_level WHERE name = 'Beginner'), (SELECT id FROM force_type WHERE name = 'Push'), (SELECT id FROM mechanics_type WHERE name = 'Compound'));

INSERT INTO recipe (is_public, name, description, user_id) VALUES
                                                    (true, 'Protein-Packed Breakfast Bowl', 'A healthy bowl with eggs, yogurt, and fruit.', (SELECT id FROM user WHERE username = 'testuser')),
                                                    (true, 'Grilled Chicken & Veggies', 'Simple grilled chicken breast with roasted broccoli.', (SELECT id FROM user WHERE username = 'testuser')),
                                                    (true, 'Salmon & Rice Dinner', 'Baked salmon served with brown rice and steamed vegetables.', (SELECT id FROM user WHERE username = 'testuser'));

INSERT INTO plan (is_public, name, description, plan_type_id, user_id) VALUES
                                                                (true, 'Beginner Full Body Workout', 'A simple plan for starting out.',  (SELECT id FROM plan_type WHERE name = 'Workout Plan'),(SELECT id FROM user WHERE username = 'testuser')),
                                                                (true, 'High-Protein Nutrition Guide', 'Meal ideas focusing on protein intake.', (SELECT id FROM plan_type WHERE name = 'Nutrition Plan'), (SELECT id FROM user WHERE username = 'testuser')),
                                                                (true, 'Fat Loss Hybrid Plan', 'Combines workouts and diet for fat loss.',(SELECT id FROM plan_type WHERE name = 'Hybrid Plan'), (SELECT id FROM user WHERE username = 'testuser'));

