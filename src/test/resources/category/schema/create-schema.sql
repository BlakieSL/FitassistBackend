CREATE TABLE food_category (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               name VARCHAR(255) NOT NULL,
                               UNIQUE (name)
);

CREATE TABLE food (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(50) NOT NULL,
                      calories DECIMAL(38, 2) NOT NULL,
                      protein DECIMAL(38, 2) NOT NULL,
                      fat DECIMAL(38, 2) NOT NULL,
                      carbohydrates DECIMAL(38, 2) NOT NULL,
                      food_category_id INT NOT NULL,
                      CONSTRAINT fk_food_category
                          FOREIGN KEY (food_category_id)
                              REFERENCES food_category(id)
                              ON DELETE CASCADE
);

CREATE TABLE activity_category (
                                   id INT AUTO_INCREMENT PRIMARY KEY,
                                   name VARCHAR(50) NOT NULL
);

CREATE TABLE activity (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          met DECIMAL(38, 2) NOT NULL,
                          name VARCHAR(50) NOT NULL,
                          activity_category_id INT NOT NULL,
                          CONSTRAINT fk_activity_category
                              FOREIGN KEY (activity_category_id)
                                  REFERENCES activity_category(id)
                                  ON DELETE CASCADE
);

CREATE TABLE user (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      username VARCHAR(50) NOT NULL UNIQUE,
                      password VARCHAR(100) NOT NULL,
                      email VARCHAR(100) NOT NULL UNIQUE,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE plan_type (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(50) NOT NULL
);

CREATE TABLE plan (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      description VARCHAR(255) NOT NULL,
                      name VARCHAR(100) NOT NULL,
                      is_public BOOLEAN NOT NULL,
                      plan_type_id INT NOT NULL,
                      user_id INT NOT NULL,
                      CONSTRAINT fk_plan_type
                          FOREIGN KEY (plan_type_id)
                              REFERENCES plan_type(id),
                      CONSTRAINT fk_user_plan
                          FOREIGN KEY (user_id)
                              REFERENCES user(id)
);

CREATE TABLE plan_category (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               name VARCHAR(50) NOT NULL
);

CREATE TABLE plan_category_association (
                                           id INT AUTO_INCREMENT PRIMARY KEY,
                                           plan_id INT NOT NULL,
                                           plan_category_id INT NOT NULL,
                                           CONSTRAINT fk_plan_association
                                               FOREIGN KEY (plan_id)
                                                   REFERENCES plan(id),
                                           CONSTRAINT fk_plan_category
                                               FOREIGN KEY (plan_category_id)
                                                   REFERENCES plan_category(id)
);

CREATE TABLE recipe (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        description VARCHAR(255) NOT NULL,
                        name VARCHAR(100) NOT NULL,
                        is_public BOOLEAN NOT NULL,
                        user_id INT NOT NULL,
                        CONSTRAINT fk_user_recipe
                            FOREIGN KEY (user_id)
                                REFERENCES user(id)
);

CREATE TABLE recipe_category (
                                 id INT AUTO_INCREMENT PRIMARY KEY,
                                 name VARCHAR(50) NOT NULL
);

CREATE TABLE recipe_category_association (
                                             id INT AUTO_INCREMENT PRIMARY KEY,
                                             recipe_id INT NOT NULL,
                                             recipe_category_id INT NOT NULL,
                                             CONSTRAINT fk_recipe_association
                                                 FOREIGN KEY (recipe_id)
                                                     REFERENCES recipe(id),
                                             CONSTRAINT fk_recipe_category
                                                 FOREIGN KEY (recipe_category_id)
                                                     REFERENCES recipe_category(id)
);

CREATE TABLE equipment (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(50) NOT NULL
);

CREATE TABLE expertise_level (
                                 id INT AUTO_INCREMENT PRIMARY KEY,
                                 name VARCHAR(50) NOT NULL
);

CREATE TABLE force_type (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(50) NOT NULL
);

CREATE TABLE mechanics_type (
                                id INT AUTO_INCREMENT PRIMARY KEY,
                                name VARCHAR(50) NOT NULL
);

CREATE TABLE target_muscle (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               name VARCHAR(50) NOT NULL
);

CREATE TABLE exercise (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          description VARCHAR(255) NOT NULL,
                          name VARCHAR(100) NOT NULL,
                          equipment_id INT NOT NULL,
                          expertise_level_id INT NOT NULL,
                          force_type_id INT NOT NULL,
                          mechanics_type_id INT NOT NULL,
                          CONSTRAINT fk_equipment
                              FOREIGN KEY (equipment_id)
                                  REFERENCES equipment(id),
                          CONSTRAINT fk_expertise_level
                              FOREIGN KEY (expertise_level_id)
                                  REFERENCES expertise_level(id),
                          CONSTRAINT fk_force_type
                              FOREIGN KEY (force_type_id)
                                  REFERENCES force_type(id),
                          CONSTRAINT fk_mechanics_type
                              FOREIGN KEY (mechanics_type_id)
                                  REFERENCES mechanics_type(id)
);

CREATE TABLE exercise_target_muscle (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        priority DECIMAL(38, 2) NOT NULL,
                                        exercise_id INT NOT NULL,
                                        target_muscle_id INT NOT NULL,
                                        CONSTRAINT fk_exercise_target
                                            FOREIGN KEY (exercise_id)
                                                REFERENCES exercise(id),
                                        CONSTRAINT fk_target_muscle
                                            FOREIGN KEY (target_muscle_id)
                                                REFERENCES target_muscle(id)
);
