-- Create user table
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

-- Create thread_category table
CREATE TABLE thread_category (
                                 id   INT AUTO_INCREMENT PRIMARY KEY,
                                 name VARCHAR(255) NOT NULL
);

-- Create thread table
CREATE TABLE thread (
                        id                 INT AUTO_INCREMENT PRIMARY KEY,
                        dateCreated        DATETIME(6) NOT NULL,
                        text               VARCHAR(255) NOT NULL,
                        title              VARCHAR(50) NOT NULL,
                        views              INT NOT NULL,
                        thread_category_id INT NOT NULL,
                        user_id            INT NOT NULL,
                        CONSTRAINT FK2rmkr0vlimyhly7n8460ysfl9 FOREIGN KEY (user_id) REFERENCES user (id),
                        CONSTRAINT FKl7kvoao6mpqr79cii1b6uq8q2 FOREIGN KEY (thread_category_id) REFERENCES thread_category (id)
);

-- Create comment table
CREATE TABLE comment (
                         id                INT AUTO_INCREMENT PRIMARY KEY,
                         text              VARCHAR(255) NOT NULL,
                         parent_comment_id INT NULL,
                         thread_id         INT NOT NULL,
                         user_id           INT NOT NULL,
                         CONSTRAINT FK8kcum44fvpupyw6f5baccx25c FOREIGN KEY (user_id) REFERENCES user (id),
                         CONSTRAINT FKehf7mvstlwwl8fy9ahfo515rm FOREIGN KEY (thread_id) REFERENCES thread (id),
                         CONSTRAINT FKhvh0e2ybgg16bpu229a5teje7 FOREIGN KEY (parent_comment_id) REFERENCES comment (id) ON DELETE CASCADE
);

-- Create complaint table
CREATE TABLE complaint (
                           type       VARCHAR(31) NOT NULL,
                           id         INT AUTO_INCREMENT PRIMARY KEY,
                           reason     ENUM ('INAPPROPRIATE_CONTENT', 'OTHER', 'SPAM') NOT NULL,
                           status     ENUM ('PENDING', 'RESOLVED') NOT NULL,
                           user_id    INT NOT NULL,
                           comment_id INT NULL,
                           thread_id  INT NULL,
                           CONSTRAINT FK1um0lnkkt6ddjylek8kppofnr FOREIGN KEY (thread_id) REFERENCES thread (id),
                           CONSTRAINT FKh8dg5n7ibjyack6pn6e71djj6 FOREIGN KEY (user_id) REFERENCES user (id),
                           CONSTRAINT FKmbv2o7p31vgo1y6ni7h36tvj6 FOREIGN KEY (comment_id) REFERENCES comment (id)
);