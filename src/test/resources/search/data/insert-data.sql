INSERT INTO expertise_level (name) VALUES
('Beginner'), ('Intermediate'), ('Advanced');

INSERT INTO equipment (name) VALUES
('Barbell'), ('Dumbbell'), ('Bodyweight'), ('Machine'), ('Kettlebell'), ('Pull-up Bar');

INSERT INTO force_type (name) VALUES
('Push'), ('Pull'), ('Legs');

INSERT INTO mechanics_type (name) VALUES
('Compound'), ('Isolation');

INSERT INTO target_muscle (name) VALUES
('Chest'), ('Back'), ('Quadriceps'), ('Hamstrings'), ('Shoulders'), ('Biceps'), ('Triceps'), ('Core'), ('Glutes'), ('Calves');

INSERT INTO activity_category (name) VALUES
('Running'), ('Cycling'), ('Swimming'), ('Sports'), ('Household'), ('Other');

INSERT INTO food_category (name) VALUES
('Fruits'), ('Vegetables'), ('Proteins'), ('Grains'), ('Dairy'), ('Fats');

INSERT INTO recipe_category (name) VALUES
('Breakfast'), ('Lunch'), ('Dinner'), ('Snack'), ('Dessert');

INSERT INTO role (name) VALUES
('USER'), ('ADMIN');

INSERT INTO user (username, email, password, gender, birthday, height, weight, activity_level, goal) VALUES
('testuser', 'test@example.com', '$2a$10$examplehashedpassword', 'MALE', '1990-01-01', 180.00, 75.00, 'MODERATELY_ACTIVE', 'BUILD_MUSCLE');

INSERT INTO food (name, calories, protein, carbohydrates, fat, food_category_id) VALUES
('test', 52.00, 0.26, 13.81, 0.17, 1),
('test', 165.00, 31.00, 0.00, 3.60, 3),
('test', 111.00, 2.59, 23.00, 0.90, 4),
('test', 34.00, 2.82, 6.64, 0.37, 2),
('test', 208.00, 20.42, 0.00, 13.42, 3),
('Greek Yogurt (Plain)', 59.00, 10.00, 3.60, 0.40, 5);

INSERT INTO activity (name, met, activity_category_id) VALUES
('Running (8 km/h)', 8.00, 1),
('Cycling (Leisure)', 4.00, 2),
('Swimming (Moderate)', 6.00, 3),
('Weight Lifting (General)', 3.00, 6),
('Walking (3.2 km/h)', 2.50, 6),
('House Cleaning', 3.50, 5);

INSERT INTO exercise (name, description, equipment_id, expertise_level_id, force_type_id, mechanics_type_id) VALUES
('Bench Press', 'Lying on a bench, press a barbell upwards.', 1, 2, 1, 1),
('Pull-up', 'Hang from a bar and pull your body up.', 6, 2, 2, 1),
('Squats', 'Lower your body by bending knees and hips.', 1, 1, 3, 1),
('Bicep Curl (Dumbbell)', 'Curl dumbbells upwards while standing.', 2, 1, 2, 2),
('Tricep Extension (Cable)', 'Extend arms using a cable machine.', 4, 2, 1, 2),
('Push-up', 'Push body up from plank position.', 3, 1, 1, 1);

INSERT INTO recipe (is_public, name, description, user_id, created_at) VALUES
(true, 'Protein-Packed Breakfast Bowl', 'A healthy bowl with eggs, yogurt, and fruit.', 1, NOW()),
(true, 'Grilled Chicken & Veggies', 'Simple grilled chicken breast with roasted broccoli.', 1, NOW()),
(true, 'Salmon & Rice Dinner', 'Baked salmon served with brown rice and steamed vegetables.', 1, NOW());

INSERT INTO plan (is_public, name, description, structure_type, user_id, created_at) VALUES
(true, 'Beginner Full Body Workout', 'A simple plan for starting out.', 'WEEKLY_SPLIT', 1, NOW()),
(true, 'High-Protein Nutrition Guide', 'Meal ideas focusing on protein intake.', 'FIXED_PROGRAM', 1, NOW()),
(true, 'Fat Loss Hybrid Plan', 'Combines workouts and diet for fat loss.', 'WEEKLY_SPLIT', 1, NOW());