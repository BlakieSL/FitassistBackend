-- Insert base data
INSERT INTO expertise_level (name) VALUES
                                       ('Novice'), ('Beginner'), ('Intermediate'), ('Advanced'), ('Expert');

INSERT INTO mechanics_type (name) VALUES
                                      ('Compound'), ('Isolation'), ('Plyometric'), ('Isometric'), ('Ballistic');

INSERT INTO force_type (name) VALUES
                                  ('Push'), ('Pull'), ('Static'), ('Dynamic'), ('Rotational');

INSERT INTO equipment (name) VALUES
                                 ('Dumbbells'), ('Resistance Bands'), ('Kettlebells'), ('Barbell'), ('Medicine Ball');

INSERT INTO target_muscle (name) VALUES
                                     ('Chest'), ('Back'), ('Legs'), ('Arms'), ('Core');

INSERT INTO exercise (description, name, equipment_id, expertise_level_id, force_type_id, mechanics_type_id)
VALUES
    ('Chest press', 'Bench Press', 4, 3, 1, 1),
    ('Back exercise', 'Pull-ups', 1, 2, 2, 1),
    ('Leg workout', 'Squats', 4, 2, 3, 1),
    ('Arm exercise', 'Bicep Curls', 1, 1, 2, 2),
    ('Core workout', 'Plank', 5, 1, 3, 4);

INSERT INTO exercise_target_muscle (priority, exercise_id, target_muscle_id)
VALUES
    (1.0, 1, 1),
    (1.0, 2, 2);

INSERT INTO user (email, password, username, gender, birthday, activity_level, goal, height, weight)
VALUES
    ('test@example.com', '$2a$10$exampleHash', 'test_user', 'MALE', '1990-01-01', 'MODERATELY_ACTIVE', 'BUILD_MUSCLE', 180.00, 75.00);

INSERT INTO user_exercise (user_id, exercise_id)
VALUES
    (1, 1),
    (1, 2);