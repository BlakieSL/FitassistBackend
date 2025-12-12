INSERT INTO role (id, name) VALUES
(1, 'USER'),
(2, 'ADMIN');

INSERT INTO user (id, birthday, email, gender, password, username) VALUES
(1, '1990-01-01', 'user1@example.com', 'MALE', '$2a$10$dXJhB0ZtH7yU6Z7bYQn/3eI9dQ2XvV8fLkZzYcR9aB3wC1dS5gF7u', 'user1'),
(2, '1990-01-01', 'admin@example.com', 'MALE', '$2a$10$dXJhB0ZtH7yU6Z7bYQn/3eI9dQ2XvV8fLkZzYcR9aB3wC1dS5gF7u', 'admin'),
(3, '1990-01-01', 'user2@example.com', 'MALE', '$2a$10$dXJhB0ZtH7yU6Z7bYQn/3eI9dQ2XvV8fLkZzYcR9aB3wC1dS5gF7u', 'user2');

INSERT INTO user_roles (users_id, roles_id) VALUES
(1, 1),
(2, 2),
(3, 1);

INSERT INTO equipment (id, name) VALUES
(1, 'Dumbbells');

INSERT INTO expertise_level (id, name) VALUES
(1, 'Beginner');

INSERT INTO force_type (id, name) VALUES
(1, 'Push');

INSERT INTO mechanics_type (id, name) VALUES
(1, 'Compound');

INSERT INTO exercise (id, description, name, equipment_id, expertise_level_id, force_type_id, mechanics_type_id) VALUES
(1, 'Standard pushup exercise', 'Pushups', 1, 1, 1, 1);

INSERT INTO plan (is_public, id, description, name, structure_type, user_id, created_at) VALUES
(true, 1, 'Beginner workout plan', 'Beginner Plan', 'WEEKLY_SPLIT', 1, NOW()),
(false, 2, 'Advanced workout plan', 'Advanced Plan', 'FIXED_PROGRAM', 2, NOW());

INSERT INTO workout (id, duration, name, plan_id, order_index, rest_days_after) VALUES
(1, 30, 'Morning Workout', 1, 1, 1),
(2, 45, 'Evening Workout', 2, 1, 0);

INSERT INTO workout_set (id, order_index, rest_seconds, workout_id) VALUES
(1, 1, 60, 1),
(2, 1, 90, 2);

INSERT INTO workout_set_exercise (id, repetitions, weight, exercise_id, workout_set_id, order_index) VALUES
(1, 10, 20.0, 1, 1, 1),
(2, 10, 20.0, 1, 2, 1);