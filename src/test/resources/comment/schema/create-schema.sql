CREATE TABLE thread_category (
                                 id   INT AUTO_INCREMENT PRIMARY KEY,
                                 name VARCHAR(255) NOT NULL
);

CREATE TABLE user (
                      id             INT AUTO_INCREMENT PRIMARY KEY,
                      activity_level ENUM ('LIGHTLY_ACTIVE', 'MODERATELY_ACTIVE', 'SEDENTARY', 'SUPER_ACTIVE', 'VERY_ACTIVE') NULL,
                      birthday       DATE NOT NULL,
                      email          VARCHAR(50) NOT NULL,
                      gender         ENUM ('FEMALE', 'MALE') NOT NULL,
                      goal           ENUM ('BUILD_MUSCLE', 'LOSE_WEIGHT', 'MAINTAIN_WEIGHT') NULL,
                      height         DECIMAL(38, 2) NULL,
                      password       VARCHAR(60) NOT NULL,
                      username       VARCHAR(40) NOT NULL,
                      weight         DECIMAL(38, 2) NULL
);

CREATE TABLE thread (
                        id                 INT AUTO_INCREMENT PRIMARY KEY,
                        dateCreated        DATETIME(6) NOT NULL,
                        text               VARCHAR(255) NOT NULL,
                        title              VARCHAR(50) NOT NULL,
                        views              INT NOT NULL,
                        thread_category_id INT NOT NULL,
                        user_id            INT NOT NULL,
                        CONSTRAINT FK2rmkr0vlimyhly7n8460ysfl9
                            FOREIGN KEY (user_id) REFERENCES user (id),
                        CONSTRAINT FKl7kvoao6mpqr79cii1b6uq8q2
                            FOREIGN KEY (thread_category_id) REFERENCES thread_category (id)
);

CREATE TABLE user_thread (
                             id        INT AUTO_INCREMENT PRIMARY KEY,
                             thread_id INT NOT NULL,
                             user_id   INT NOT NULL,
                             CONSTRAINT FK6to3x5x2bh7baqxdvohs3lg0p
                                 FOREIGN KEY (user_id) REFERENCES user (id),
                             CONSTRAINT FKfdaf07carxbssu63xpu8qa7ya
                                 FOREIGN KEY (thread_id) REFERENCES thread (id)
);

CREATE TABLE comment (
                         id                INT AUTO_INCREMENT PRIMARY KEY,
                         text              VARCHAR(255) NOT NULL,
                         parent_comment_id INT NULL,
                         thread_id         INT NOT NULL,
                         user_id           INT NOT NULL,
                         CONSTRAINT FK8kcum44fvpupyw6f5baccx25c
                             FOREIGN KEY (user_id) REFERENCES user (id),
                         CONSTRAINT FKehf7mvstlwwl8fy9ahfo515rm
                             FOREIGN KEY (thread_id) REFERENCES thread (id),
                         CONSTRAINT FKhvh0e2ybgg16bpu229a5teje7
                             FOREIGN KEY (parent_comment_id) REFERENCES comment (id) ON DELETE CASCADE
);

CREATE TABLE user_comment (
                              id         INT AUTO_INCREMENT PRIMARY KEY,
                              type       ENUM ('DISLIKE', 'LIKE', 'SAVE') NOT NULL,
                              comment_id INT NOT NULL,
                              user_id    INT NOT NULL,
                              CONSTRAINT FK8run8dgvadrrwcwe5xpdscynm
                                  FOREIGN KEY (comment_id) REFERENCES comment (id) ON DELETE CASCADE,
                              CONSTRAINT FKornrskknlmumgdhlohpbcvrw5
                                  FOREIGN KEY (user_id) REFERENCES user (id)
);