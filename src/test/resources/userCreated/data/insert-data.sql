INSERT INTO role (name) VALUES
('USER'), ('ADMIN'), ('MOD');

INSERT INTO plan_type (name) VALUES
('Workout');

INSERT INTO plan_category (name) VALUES
('Strength');

INSERT INTO recipe_category (name) VALUES
('Breakfast');

INSERT INTO thread_category (name) VALUES
('General');

INSERT INTO user (username, email, password, birthday, gender, height, weight, activity_level, goal) VALUES
('testuser', 'user@test.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '1990-01-01', 'MALE', 180.00, 75.00, 'MODERATELY_ACTIVE', 'BUILD_MUSCLE'),
('adminuser', 'admin@test.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '1985-05-15', 'FEMALE', 165.00, 60.00, 'VERY_ACTIVE', 'LOSE_WEIGHT'),
('moduser', 'mod@test.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '1995-11-20', 'MALE', 175.00, 70.00, 'LIGHTLY_ACTIVE', 'MAINTAIN_WEIGHT');

INSERT INTO user_roles (users_id, roles_id) VALUES
(1, 1),
(2, 1), (2, 2),
(3, 1), (3, 3);

INSERT INTO plan (is_public, name, description, user_id, plan_type_id, created_at) VALUES
(true, 'Summer Cut', '12-week fat loss program', 1, 1, NOW()),
(true, 'Strength Builder', '6-month strength program', 1, 1, NOW()),
(true, 'Balanced Lifestyle', 'General fitness maintenance', 2, 1, NOW());

INSERT INTO plan_category_association (plan_id, plan_category_id) VALUES
(1, 1),
(2, 1),
(3, 1);

INSERT INTO recipe (is_public, name, description, user_id, created_at) VALUES
(true, 'Protein Shake', 'Quick post-workout shake', 1, NOW()),
(true, 'Chicken & Rice', 'Simple meal prep recipe', 1, NOW()),
(true, 'Greek Yogurt Parfait', 'Healthy breakfast option', 2, NOW());

INSERT INTO recipe_category_association (recipe_id, recipe_category_id) VALUES
(1, 1),
(2, 1),
(3, 1);

INSERT INTO thread (title, text, created_at, views, user_id, thread_category_id) VALUES
('Getting started with fitness', 'What are some good beginner tips?', NOW(), 25, 1, 1);

INSERT INTO comment (text, created_at, thread_id, user_id, parent_comment_id) VALUES
('Start with bodyweight exercises and focus on form!', NOW(), 1, 1, NULL),
('Thanks for the advice!', NOW(), 1, 1, 1);

INSERT INTO user_plan (user_id, plan_id, type, created_at) VALUES
(1, 1, 'SAVE', NOW()),
(1, 2, 'SAVE', NOW());

INSERT INTO user_recipe (user_id, recipie_id, type, created_at) VALUES
(1, 1, 'LIKE', NOW()),
(1, 2, 'SAVE', NOW());

INSERT INTO user_comment (user_id, comment_id, type, created_at) VALUES
(1, 1, 'LIKE', NOW()),
(1, 2, 'LIKE', NOW());

INSERT INTO user_thread (user_id, thread_id, created_at) VALUES
(1, 1, NOW());