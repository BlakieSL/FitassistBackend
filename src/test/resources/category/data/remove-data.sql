DELETE FROM exercise_target_muscle;
DELETE FROM plan_category_association;
DELETE FROM recipe_category_association;

DELETE FROM exercise;
DELETE FROM plan;
DELETE FROM recipe;
DELETE FROM food;
DELETE FROM activity;

DELETE FROM target_muscle;
DELETE FROM equipment;
DELETE FROM expertise_level;
DELETE FROM force_type;
DELETE FROM mechanics_type;
DELETE FROM plan_category;
DELETE FROM recipe_category;
DELETE FROM user;
DELETE FROM activity_category;
DELETE FROM food_category;

ALTER TABLE food_category AUTO_INCREMENT = 1;
ALTER TABLE food AUTO_INCREMENT = 1;
ALTER TABLE activity_category AUTO_INCREMENT = 1;
ALTER TABLE activity AUTO_INCREMENT = 1;
ALTER TABLE user AUTO_INCREMENT = 1;
ALTER TABLE plan AUTO_INCREMENT = 1;
ALTER TABLE plan_category AUTO_INCREMENT = 1;
ALTER TABLE plan_category_association AUTO_INCREMENT = 1;
ALTER TABLE recipe AUTO_INCREMENT = 1;
ALTER TABLE recipe_category AUTO_INCREMENT = 1;
ALTER TABLE recipe_category_association AUTO_INCREMENT = 1;
ALTER TABLE equipment AUTO_INCREMENT = 1;
ALTER TABLE expertise_level AUTO_INCREMENT = 1;
ALTER TABLE force_type AUTO_INCREMENT = 1;
ALTER TABLE mechanics_type AUTO_INCREMENT = 1;
ALTER TABLE target_muscle AUTO_INCREMENT = 1;
ALTER TABLE exercise AUTO_INCREMENT = 1;
ALTER TABLE exercise_target_muscle AUTO_INCREMENT = 1;