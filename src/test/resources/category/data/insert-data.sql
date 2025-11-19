-- Insert data for independent tables first
INSERT INTO food_category (name) VALUES
                                     ('Fruits'), ('Vegetables'), ('Grains'), ('Proteins'), ('Dairy');

INSERT INTO activity_category (name) VALUES
                                         ('Cardio'), ('Strength'), ('Flexibility'), ('Balance'), ('Sports');

INSERT INTO plan_type (name) VALUES
                                 ('Weight Loss'), ('Muscle Gain'), ('Maintenance'), ('Athletic'), ('Rehabilitation');

INSERT INTO plan_category (name) VALUES
                                     ('Beginner'), ('Intermediate'), ('Advanced'), ('Short-Term'), ('Long-Term');

INSERT INTO recipe_category (name) VALUES
                                       ('Breakfast'), ('Lunch'), ('Dinner'), ('Snack'), ('Dessert');

INSERT INTO equipment (name) VALUES
                                 ('Dumbbells'), ('Resistance Bands'), ('Kettlebells'), ('Barbell'), ('Medicine Ball');

INSERT INTO expertise_level (name) VALUES
                                       ('Novice'), ('Beginner'), ('Intermediate'), ('Advanced'), ('Expert');

INSERT INTO force_type (name) VALUES
                                  ('Push'), ('Pull'), ('Static'), ('Dynamic'), ('Rotational');

INSERT INTO mechanics_type (name) VALUES
                                      ('Compound'), ('Isolation'), ('Plyometric'), ('Isometric'), ('Ballistic');

INSERT INTO target_muscle (name) VALUES
                                     ('Chest'), ('Back'), ('Legs'), ('Arms'), ('Core');

INSERT INTO user (username, password, email, birthday, gender) VALUES
                                                 ('user1', 'pass1', 'user1@example.com', '1990-01-01', 'MALE'),
                                                 ('user2', 'pass2', 'user2@example.com', '1990-01-02', 'FEMALE'),
                                                 ('user3', 'pass3', 'user3@example.com', '1990-01-03', 'MALE'),
                                                 ('user4', 'pass4', 'user4@example.com', '1990-01-04', 'FEMALE'),
                                                 ('user5', 'pass5', 'user5@example.com', '1990-01-05', 'MALE');

INSERT INTO food (name, calories, protein, fat, carbohydrates, food_category_id) VALUES
                                                                                     ('Apple', 52, 0.3, 0.2, 14, 1),
                                                                                     ('Carrot', 41, 0.9, 0.2, 10, 2),
                                                                                     ('Oats', 389, 17, 7, 66, 3),
                                                                                     ('Chicken', 165, 31, 3.6, 0, 4),
                                                                                     ('Yogurt', 59, 10, 0.4, 3.6, 5);

INSERT INTO activity (met, name, activity_category_id) VALUES
                                                           (7.0, 'Running', 1),
                                                           (5.0, 'Weightlifting', 2),
                                                           (3.0, 'Yoga', 3),
                                                           (2.5, 'Tai Chi', 4),
                                                           (8.0, 'Basketball', 5);

INSERT INTO plan (is_public, description, name, plan_type_id, user_id) VALUES
                                                                (true, 'Weight loss program', 'Slim Down', 1, 1),
                                                                (true, 'Muscle building plan', 'Gain Muscle', 2, 2),
                                                                (true, 'Maintain current fitness', 'Stay Fit', 3, 3),
                                                                (true, 'Athletic performance', 'Peak Performance', 4, 4),
                                                                (true, 'Injury recovery', 'Recovery Plan', 5, 5);

INSERT INTO recipe (is_public, description, name, user_id) VALUES
                                                    (true,'Healthy breakfast', 'Morning Oats', 1),
                                                    (true, 'Quick lunch', 'Veggie Wrap', 2),
                                                    (true, 'Family dinner', 'Chicken Rice', 3),
                                                    (true, 'Afternoon snack', 'Yogurt Bowl', 4),
                                                    (true, 'Sweet treat', 'Fruit Parfait', 5);

INSERT INTO exercise (description, name, equipment_id, expertise_level_id, force_type_id, mechanics_type_id) VALUES
                                                                                                                 ('Chest press', 'Bench Press', 4, 3, 1, 1),
                                                                                                                 ('Back exercise', 'Pull-ups', 1, 2, 2, 1),
                                                                                                                 ('Leg workout', 'Squats', 4, 2, 3, 1),
                                                                                                                 ('Arm exercise', 'Bicep Curls', 1, 1, 2, 2),
                                                                                                                 ('Core workout', 'Plank', 5, 1, 3, 4);

-- Insert association tables
INSERT INTO plan_category_association (plan_id, plan_category_id) VALUES
                                                                      (1, 1), (2, 2), (3, 3), (4, 4), (5, 5);

INSERT INTO recipe_category_association (recipe_id, recipe_category_id) VALUES
                                                                            (1, 1), (2, 2), (3, 3), (4, 4), (5, 5);

INSERT INTO exercise_target_muscle (priority, exercise_id, target_muscle_id) VALUES
                                                                                 (1.0, 1, 1),
                                                                                 (1.0, 2, 2),
                                                                                 (1.0, 3, 3),
                                                                                 (1.0, 4, 4),
                                                                                 (1.0, 5, 5);

INSERT INTO food_category (name) VALUES ('Fruits1');

INSERT INTO activity_category (name) VALUES ('Cardio1');

INSERT INTO target_muscle (name) VALUES ('Chest1');

INSERT INTO plan_category (name) VALUES ('Beginner1');

INSERT INTO recipe_category (name) VALUES ('Breakfast1');