INSERT INTO user (id, activity_level, birthday, email, gender, goal, height, password, username, weight) VALUES
(1, 'MODERATELY_ACTIVE', '1990-05-15', 'user1@example.com', 'MALE', 'BUILD_MUSCLE', 180.00, '$2a$10$exampleHash1', 'fitness_lover', 80.00),
(2, 'VERY_ACTIVE', '1985-08-22', 'user2@example.com', 'MALE', 'MAINTAIN_WEIGHT', 175.00, '$2a$10$exampleHash2', 'gym_enthusiast', 75.00),
(3, 'SEDENTARY', '1992-11-10', 'user3@example.com', 'MALE', 'LOSE_WEIGHT', 185.00, '$2a$10$exampleHash3', 'office_worker', 90.00);

INSERT INTO plan_type (id, name) VALUES
(1, 'Workout'),
(2, 'Meal'),
(3, 'Hybrid');

INSERT INTO plan_category (id, name) VALUES
(1, 'Strength Training'),
(2, 'Cardio'),
(3, 'Weight Loss');

INSERT INTO equipment (id, name) VALUES
(1, 'Dumbbells'),
(2, 'Barbell');

INSERT INTO expertise_level (id, name) VALUES
(1, 'Beginner'),
(2, 'Intermediate');

INSERT INTO mechanics_type (id, name) VALUES
(1, 'Compound'),
(2, 'Isolation');

INSERT INTO force_type (id, name) VALUES
(1, 'Push'),
(2, 'Pull');

INSERT INTO target_muscle (id, name) VALUES
(1, 'Chest'),
(2, 'Back'),
(3, 'Shoulders');

INSERT INTO exercise (id, description, name, equipment_id, expertise_level_id, force_type_id, mechanics_type_id) VALUES
(1, 'Standard bench press', 'Barbell Bench Press', 2, 2, 1, 1),
(2, 'Overhead press', 'Shoulder Press', 1, 1, 1, 1),
(3, 'Bent-over row', 'Barbell Row', 2, 2, 2, 1);

INSERT INTO exercise_target_muscle (exercise_id, target_muscle_id, priority) VALUES
(1, 1, 1.0), (1, 3, 0.8),
(2, 3, 1.0), (2, 1, 0.6),
(3, 2, 1.0), (3, 3, 0.6);

INSERT INTO plan (is_public, id, description, name, plan_type_id, user_id, created_at) VALUES
(true, 1, 'Beginner workout plan', 'Beginner Strength', 1, 1, NOW()),
(true, 2, 'High protein meal plan', 'Muscle Gain Diet', 2, 1, NOW()),
(true, 3, 'Cardio challenge', 'Cardio Blast', 1, 2, NOW()),
(true, 4, 'Weight loss program', 'Fat Burner', 3, 3, NOW()),
(false, 5, 'Private workout plan', 'Private Plan', 1, 1, NOW());

INSERT INTO plan_category_association (id, plan_id, plan_category_id) VALUES
(1, 1, 1),
(2, 1, 3),
(3, 2, 3),
(4, 3, 2),
(5, 4, 3),
(6, 5, 1);

INSERT INTO user_plan (id, type, plan_id, user_id, created_at) VALUES
(1, 'LIKE', 1, 2, NOW()),
(2, 'LIKE', 3, 1, NOW()),
(3, 'SAVE', 2, 3, NOW()),
(4, 'DISLIKE', 1, 3, NOW()),
(5, 'SAVE', 4, 1, NOW()),
(6, 'DISLIKE', 4, 1, NOW());

INSERT INTO workout (id, duration, name, plan_id, orderIndex) VALUES
(1, 60.0, 'Upper Body', 1, 1),
(2, 45.0, 'Lower Body', 1, 2),
(3, 30.0, 'HIIT Session', 3, 1),
(4, 50.0, 'Full Body', 4, 1);

INSERT INTO workout_set_group (id, orderIndex, restSeconds, workout_id) VALUES
(1, 1, 60, 1),
(2, 2, 60, 1),
(3, 1, 90, 2),
(4, 1, 30, 3);

INSERT INTO workout_set (id, repetitions, weight, exercise_id, workout_set_group_id, orderIndex) VALUES
(1, 10, 135.0, 1, 1, 1),
(2, 10, 135.0, 1, 1, 2),
(3, 8, 135.0, 1, 1, 3),
(4, 12, 30.0, 2, 2, 1),
(5, 12, 30.0, 2, 2, 2),
(6, 8, 185.0, 3, 3, 1),
(7, 15, 0.0, 2, 4, 1);

INSERT INTO text (type, id, orderIndex, text, title, plan_id) VALUES
('PLAN_INSTRUCTION', 1, 1, 'Warm up for 10 minutes before starting', 'Warm Up', 1),
('PLAN_INSTRUCTION', 2, 2, 'Perform 3 sets of 8-12 reps for each exercise', 'Workout Structure', 1),
('PLAN_INSTRUCTION', 3, 1, 'Meal prep on Sundays for the whole week', 'Meal Prep', 2),
('PLAN_INSTRUCTION', 4, 1, 'Start with 5 minute warm up', 'Cardio Warm Up', 3),
('PLAN_INSTRUCTION', 5, 1, 'Circuit training: 3 rounds', 'Workout Structure', 4);

INSERT INTO media (id, imageName, parent_id, parentType) VALUES
(1, 'plan1_image1.jpg', 1, 'PLAN'),
(2, 'plan1_image2.jpg', 1, 'PLAN'),
(3, 'user1_profile.jpg', 1, 'USER');