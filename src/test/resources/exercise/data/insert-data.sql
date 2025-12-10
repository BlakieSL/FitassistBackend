-- Insert base data
INSERT INTO expertise_level (name) VALUES
('Novice'), ('Beginner'), ('Intermediate'), ('Advanced'), ('Expert');

INSERT INTO mechanics_type (name) VALUES
('Compound'), ('Isolation'), ('Plyometric'), ('Isometric'), ('Ballistic');

INSERT INTO force_type (name) VALUES
('Push'), ('Pull'), ('Static'), ('Dynamic'), ('Rotational');

INSERT INTO equipment (name) VALUES
('Dumbbells'),
('Resistance Bands'),
('Kettlebells'),
('Barbell'),
('Medicine Ball');

INSERT INTO target_muscle (name) VALUES
('Chest'), ('Back'), ('Legs'), ('Arms'), ('Core');

INSERT INTO exercise (description, name, equipment_id, expertise_level_id, force_type_id, mechanics_type_id) VALUES
('Chest press', 'Bench Press', 4, 3, 1, 1),
('Back exercise', 'Pull-ups', 1, 2, 2, 1),
('Leg workout', 'Squats', 4, 2, 3, 1),
('Arm exercise', 'Bicep Curls', 1, 1, 2, 2),
('Core workout', 'Plank', 5, 1, 3, 4);

INSERT INTO exercise_target_muscle (priority, exercise_id, target_muscle_id) VALUES
(1.0, 1, 1),
(1.0, 2, 2);

INSERT INTO user (email, password, username, gender, birthday, activity_level, goal, height, weight) VALUES
('test@example.com', '$2a$10$exampleHash', 'test_user', 'MALE', '1990-01-01', 'MODERATELY_ACTIVE', 'BUILD_MUSCLE', 180.00, 75.00);

INSERT INTO user_exercise (user_id, exercise_id, created_at) VALUES
(1, 1, NOW()),
(1, 2, NOW());


INSERT INTO plan (is_public, name, description, structure_type, user_id, created_at) VALUES
(true,'3-Day Beginner Plan', 'A 3-day plan for beginners', 'WEEKLY_SPLIT', 1, NOW()),
(true, '5-Day Strength Plan', 'Full body strength training', 'FIXED_PROGRAM', 1, NOW()),
(true, '4-Day Fat Loss Plan', 'Cardio and HIIT focused', 'WEEKLY_SPLIT', 1, NOW());

INSERT INTO workout (name, duration, plan_id, order_index, rest_days_after) VALUES
('Full Body Strength', 60, 1, 1, 1),
('Upper Body Push', 45, 1, 2, 1),
('Lower Body Focus', 60, 2, 1, 2),
('Cardio & Core', 30, 3, 1, 0);

INSERT INTO workout_set (order_index, rest_seconds, workout_id) VALUES
(1, 60, 1),
(2, 90, 1),
(1, 60, 2),
(1, 60, 3),
(1, 120, 4);

INSERT INTO workout_set_exercise (exercise_id, workout_set_id, repetitions, weight, order_index) VALUES
(1, 1, 10, 50.00, 1),
(2, 1, 12, 30.00, 2),
(3, 2, 8, 80.00, 1),
(4, 3, 15, 20.00, 1),
(1, 4, 12, 55.00, 2),
(3, 4, 10, 90.00, 3),
(2, 5, 3, 0.00, 1);