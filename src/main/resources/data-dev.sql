INSERT IGNORE INTO activity_category (id, name) VALUES (1, 'Cardio');

INSERT IGNORE INTO activity (id, name, met, activity_category_id) VALUES (25, 'Running', 9.8, 1);

INSERT IGNORE INTO food_category (id, name) VALUES (1, 'Grains');

INSERT IGNORE INTO food (id, name, calories, protein, fat, carbohydrates, food_category_id)
    VALUES (1, 'Oats', 68.0, 2.40, 1.40, 12.00, 1);
