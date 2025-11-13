INSERT INTO thread_category (name) VALUES
                                       ('General Discussion'),
                                       ('Fitness Tips'),
                                       ('Nutrition'),
                                       ('Workout Plans'),
                                       ('Success Stories');

INSERT INTO user (activity_level, birthday, email, gender, goal, height, password, username, weight) VALUES
                                                                                                         ('MODERATELY_ACTIVE', '1990-05-15', 'user1@example.com', 'MALE', 'BUILD_MUSCLE', 180.00, '$2a$10$exampleHash1', 'fitness_lover', 80.00),
                                                                                                         ('VERY_ACTIVE', '1988-08-22', 'user2@example.com', 'FEMALE', 'LOSE_WEIGHT', 165.00, '$2a$10$exampleHash2', 'gym_queen', 65.00),
                                                                                                         ('SEDENTARY', '1995-02-10', 'user3@example.com', 'MALE', 'MAINTAIN_WEIGHT', 175.00, '$2a$10$exampleHash3', 'office_worker', 75.00),
                                                                                                         ('SUPER_ACTIVE', '1992-11-30', 'user4@example.com', 'FEMALE', 'BUILD_MUSCLE', 170.00, '$2a$10$exampleHash4', 'marathon_runner', 60.00),
                                                                                                         ('LIGHTLY_ACTIVE', '1985-07-04', 'user5@example.com', 'MALE', 'LOSE_WEIGHT', 182.00, '$2a$10$exampleHash5', 'weekend_warrior', 90.00);

INSERT INTO thread (dateCreated, text, title, views, thread_category_id, user_id) VALUES
                                                                                      (NOW(), 'What are your favorite fitness apps?', 'Favorite Apps', 150, 1, 1),
                                                                                      (NOW(), 'How to properly do deadlifts?', 'Deadlift Form', 320, 2, 2),
                                                                                      (NOW(), 'Best protein sources for vegetarians', 'Vegetarian Protein', 210, 3, 3),
                                                                                      (NOW(), 'My 12-week strength training program', 'Strength Program', 180, 4, 4),
                                                                                      (NOW(), 'How I lost 20kg in 6 months', 'Weight Loss Journey', 450, 5, 5);

INSERT INTO user_thread (thread_id, user_id) VALUES
                                                 (1, 2), (1, 3),
                                                 (2, 1), (2, 4),
                                                 (3, 2), (3, 5),
                                                 (4, 1), (4, 3),
                                                 (5, 2), (5, 4);

INSERT INTO comment (text, dateCreated, parent_comment_id, thread_id, user_id) VALUES
                                                                                   ('I really like MyFitnessPal for tracking', '2023-11-12 10:00:00.123456', NULL, 1, 2),
                                                                                   ('Keep your back straight and lift with legs', '2023-11-12 10:05:00.654321', NULL, 2, 1),
                                                                                   ('Lentils and quinoa are great options', '2023-11-12 10:10:00.987654', NULL, 3, 4),
                                                                                   ('How many days per week is this program?', '2023-11-12 10:15:00.456789', NULL, 4, 3),
                                                                                   ('Congrats on your achievement!', '2023-11-12 10:20:00.321654', NULL, 5, 5),
                                                                                   ('I prefer Cronometer for micronutrients', '2023-11-12 10:25:00.789123', 1, 1, 3),
                                                                                   ('Thanks for the tip!', '2023-11-12 10:30:00.101101', 6, 2, 2),
                                                                                   ('Don''t forget about tofu and tempeh', '2023-11-12 10:35:00.202202', 7, 3, 5),
                                                                                   ('It''s a 5-day split program', '2023-11-12 10:40:00.303303', 4, 4, 4),
                                                                                   ('Thank you! It was hard work', '2023-11-12 10:45:00.404404', 5, 5, 1);

INSERT INTO user_comment (type, comment_id, user_id) VALUES
                                                         ('LIKE', 1, 1), ('LIKE', 1, 3),
                                                         ('DISLIKE', 2, 4),
                                                         ('SAVE', 3, 2), ('SAVE', 3, 5),
                                                         ('LIKE', 4, 1), ('LIKE', 4, 2),
                                                         ('DISLIKE', 5, 3),
                                                         ('SAVE', 6, 4),
                                                         ('LIKE', 7, 5),
                                                         ('DISLIKE', 8, 1),
                                                         ('SAVE', 9, 2), ('SAVE', 9, 3),
                                                         ('LIKE', 10, 4);