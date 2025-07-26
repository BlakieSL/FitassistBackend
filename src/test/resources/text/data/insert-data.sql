INSERT INTO role (id, name) VALUES (1, 'USER'), (2, 'ADMIN');

INSERT INTO user (id, birthday, email, gender, password, username) VALUES
                                                                       (1, '1990-01-01', 'user1@example.com', 'MALE', '$2a$10$dXJhB0ZtH7yU6Z7bYQn/3eI9dQ2XvV8fLkZzYcR9aB3wC1dS5gF7u', 'user1'),
                                                                       (2, '1990-01-01', 'user2@example.com', 'MALE', '$2a$10$dXJhB0ZtH7yU6Z7bYQn/3eI9dQ2XvV8fLkZzYcR9aB3wC1dS5gF7u', 'user2'),
                                                                       (3, '1990-01-01', 'admin@example.com', 'MALE', '$2a$10$dXJhB0ZtH7yU6Z7bYQn/3eI9dQ2XvV8fLkZzYcR9aB3wC1dS5gF7u', 'admin');

INSERT INTO user_roles (users_id, roles_id) VALUES
                                                (1, 1),
                                                (2, 1),
                                                (3, 2);

INSERT INTO equipment (id, name) VALUES (1, 'Dumbbells');

INSERT INTO expertise_level (id, name) VALUES (1, 'Beginner');

INSERT INTO force_type (id, name) VALUES (1, 'Push');

INSERT INTO mechanics_type (id, name) VALUES (1, 'Compound');

INSERT INTO exercise (id, description, name, equipment_id, expertise_level_id, force_type_id, mechanics_type_id)
VALUES (1, 'Standard pushup exercise', 'Pushups', 1, 1, 1, 1);

INSERT INTO food_category (id, name) VALUES (1, 'Vegetables');

INSERT INTO food (id, calories, carbohydrates, fat, name, protein, food_category_id)
VALUES (1, 35.0, 7.0, 0.2, 'Carrot', 0.8, 1);

INSERT INTO recipe (is_public, id, description, name, user_id)
VALUES (true, 1, 'Healthy carrot soup', 'Carrot Soup', 1),
       (false, 2, 'Private recipe', 'Private Recipe', 1);
INSERT INTO plan_type (id, name) VALUES (1, 'Fitness');

INSERT INTO plan (is_public, id, description, name, plan_type_id, user_id)
VALUES (true, 3, 'Beginner workout plan', 'Beginner Plan', 1, 1);
INSERT INTO text (type, orderIndex, text, exercise_id) VALUES
                                                           ('EXERCISE_INSTRUCTION', 0, 'Starting position: Lie prone...', 1),
                                                           ('EXERCISE_INSTRUCTION', 1, 'Lower your body...', 1),
                                                           ('EXERCISE_INSTRUCTION', 2, 'Push back up...', 1);

INSERT INTO text (type, orderIndex, text, recipe_id) VALUES
                                                         ('RECIPE_INSTRUCTION', 0, 'Peel and chop carrots...', 1),
                                                         ('RECIPE_INSTRUCTION', 1, 'Simmer for 20 minutes...', 1);

INSERT INTO text (type, orderIndex, text, plan_id) VALUES
    ('PLAN_INSTRUCTION', 0, 'Start with warm-up...', 3);

INSERT INTO user_exercise (id, exercise_id, user_id) VALUES (1, 1, 1);