CREATE TABLE food_category (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               name VARCHAR(255) NOT NULL,
                               UNIQUE (name));

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
                              ON DELETE CASCADE);
