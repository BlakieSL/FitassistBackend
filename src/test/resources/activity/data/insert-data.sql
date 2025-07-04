INSERT INTO activity_category (name) VALUES
    ('Walking'),
    ('Running'),
    ('Cycling'),
    ('Swimming');

INSERT INTO activity (met, name, activity_category_id) VALUES
    (3.5, 'Brisk Walking', 1),
    (6.0, 'Jogging', 2),
    (8.0, 'Mountain Biking', 3),
    (7.5, 'Lap Swimming', 4);

INSERT INTO role (name) VALUES
    ('USER'),
    ('ADMIN');

INSERT INTO user (activity_level, birthday, email, gender, goal, height, password, username, weight)
VALUES
    ('MODERATELY_ACTIVE', '1990-05-15', 'john.doe@example.com', 'MALE', 'LOSE_WEIGHT', 180.0, '$2a$10$GzZ3UQwD4YkHxMqX6jBmKuVWJvAe9N7sXtYh1r5nF1I3dP7p6L2', 'john_doe', 75.5),
    (null, '1985-08-22', 'jane.smith@example.com', 'FEMALE', null, null, '$2a$10$GzZ3UQwD4YkHxMqX6jBmKuVWJvAe9N7sXtYh1r5nF1I3dP7p6L2', 'jane_smith', null);

INSERT INTO user_roles (users_id, roles_id) VALUES
    (1, 1),
    (2, 1),
    (2, 2);

INSERT INTO daily_cart (date, user_id) VALUES
    ('2023-10-05', 1),
    ('2023-10-04', 1),
    ('2023-10-05', 2);

INSERT INTO daily_cart_activity (time, activity_id, daily_cart_id) VALUES
    (30, 1, 1),
    (45, 2, 1),
    (60, 3, 2),
    (30, 4, 3);

INSERT INTO user_activity (activity_id, user_id) VALUES
    (1, 1),
    (2, 1),
    (3, 2),
    (4, 2);
