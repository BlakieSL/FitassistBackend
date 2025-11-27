INSERT INTO user (activity_level, birthday, email, gender, goal, height, password, username, weight) VALUES
                                                                                                         ('LIGHTLY_ACTIVE', '1990-05-15', 'user1@example.com', 'MALE', 'BUILD_MUSCLE', 175.50, '$2a$10$examplehash', 'john_doe', 75.20),
                                                                                                         ('MODERATELY_ACTIVE', '1985-08-22', 'user2@example.com', 'FEMALE', 'LOSE_WEIGHT', 165.00, '$2a$10$examplehash', 'jane_smith', 68.50),
                                                                                                         ('SEDENTARY', '1995-02-10', 'user3@example.com', 'MALE', 'MAINTAIN_WEIGHT', 180.20, '$2a$10$examplehash', 'mike_jones', 82.00),
                                                                                                         ('VERY_ACTIVE', '1988-11-30', 'user4@example.com', 'FEMALE', 'BUILD_MUSCLE', 168.50, '$2a$10$examplehash', 'sarah_williams', 62.30),
                                                                                                         ('SUPER_ACTIVE', '1992-07-18', 'user5@example.com', 'MALE', 'LOSE_WEIGHT', 172.80, '$2a$10$examplehash', 'david_brown', 78.60);

INSERT INTO thread_category (name) VALUES
                                       ('Fitness'),
                                       ('Nutrition'),
                                       ('Training Programs'),
                                       ('Weight Loss'),
                                       ('General Discussion');

INSERT INTO thread (created_at, text, title, views, thread_category_id, user_id) VALUES
                                                                                      (NOW(), 'What is the best workout for beginners?', 'Beginner Workout', 125, 1, 1),
                                                                                          (NOW(), 'High protein meal ideas for muscle gain', 'Protein Meals', 89, 2, 2),
                                                                                          (NOW(), '5-day split vs full body workout', 'Workout Split', 42, 3, 3),
                                                                                      (NOW(), 'How to stay motivated during weight loss', 'Weight Loss Tips', 156, 4, 4),
                                                                                      (NOW(), 'Introduce yourself here!', 'New Members', 210, 5, 5);

INSERT INTO comment (text, created_at, parent_comment_id, thread_id, user_id) VALUES
                                                                                   ('I recommend starting with bodyweight exercises', '2025-11-13 08:35:00.123456', NULL, 1, 2),
                                                                                   ('Push-ups and squats are great for beginners', '2025-11-13 08:36:00.654321', NULL, 1, 3),
                                                                                   ('Thanks for the advice!', '2025-11-13 08:37:00.987654', 1, 1, 1),
                                                                                   ('Chicken and quinoa is my go-to meal', '2025-11-13 08:38:00.456789', NULL, 2, 4),
                                                                                   ('Don''t forget about eggs and Greek yogurt', '2025-11-13 08:39:00.321654', NULL, 2, 5),
                                                                                   ('I prefer full body workouts 3x week', '2025-11-13 08:40:00.789123', NULL, 3, 1),
                                                                                   ('Tracking progress helps me stay motivated', '2025-11-13 08:41:00.101101', NULL, 4, 2),
                                                                                   ('Welcome everyone!', '2025-11-13 08:42:00.202202', NULL, 5, 3),
                                                                                   ('Nice to meet you all!', '2025-11-13 08:43:00.303303', 8, 5, 4);
INSERT INTO complaint (type, reason, status, user_id, comment_id, thread_id) VALUES
                                                                                 ('COMMENT_COMPLAINT', 'INAPPROPRIATE_CONTENT', 'PENDING', 1, 3, NULL),
                                                                                 ('THREAD_COMPLAINT', 'SPAM', 'RESOLVED', 2, NULL, 2),
                                                                                 ('COMMENT_COMPLAINT', 'OTHER', 'PENDING', 3, 5, NULL),
                                                                                 ('THREAD_COMPLAINT', 'INAPPROPRIATE_CONTENT', 'PENDING', 4, NULL, 4),
                                                                                 ('COMMENT_COMPLAINT', 'SPAM', 'RESOLVED', 5, 7, NULL);