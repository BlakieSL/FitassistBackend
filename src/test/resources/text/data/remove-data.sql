DELETE FROM user_exercise WHERE id = 1;

DELETE FROM text WHERE plan_id = 3;
DELETE FROM text WHERE recipe_id = 1;
DELETE FROM text WHERE exercise_id = 1;

DELETE FROM plan WHERE id = 3;
DELETE FROM plan_type WHERE id = 1;

DELETE FROM recipe WHERE id = 1;

DELETE FROM food WHERE id = 1;
DELETE FROM food_category WHERE id = 1;

DELETE FROM exercise WHERE id = 1;
DELETE FROM mechanics_type WHERE id = 1;
DELETE FROM force_type WHERE id = 1;
DELETE FROM expertise_level WHERE id = 1;
DELETE FROM equipment WHERE id = 1;

DELETE FROM user_roles WHERE users_id IN (1, 2, 3);
DELETE FROM user WHERE id IN (1, 2, 3);
DELETE FROM role WHERE id IN (1, 2);