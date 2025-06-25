CREATE TABLE activity_category (
   id   INT AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(50) NOT NULL
);

CREATE TABLE activity (
  id                   INT AUTO_INCREMENT PRIMARY KEY,
  met                  DECIMAL(38, 2) NOT NULL,
  name                 VARCHAR(50)    NOT NULL,
  activity_category_id INT            NOT NULL,
  CONSTRAINT FKdp1iuiugdyvggej0rb7srlbqr
      FOREIGN KEY (activity_category_id) REFERENCES activity_category (id)
);

CREATE TABLE user (
  id             INT AUTO_INCREMENT PRIMARY KEY,
  activity_level ENUM ('LIGHTLY_ACTIVE', 'MODERATELY_ACTIVE', 'SEDENTARY', 'SUPER_ACTIVE', 'VERY_ACTIVE') NULL,
  birthday       DATE                                                                                     NOT NULL,
  email          VARCHAR(50)                                                                              NOT NULL,
  gender         ENUM ('FEMALE', 'MALE')                                                                  NOT NULL,
  goal           ENUM ('BUILD_MUSCLE', 'LOSE_WEIGHT', 'MAINTAIN_WEIGHT')                                  NULL,
  height         DECIMAL(38, 2)                                                                           NULL,
  password       VARCHAR(60)                                                                              NOT NULL,
  username       VARCHAR(40)                                                                              NOT NULL,
  weight         DECIMAL(38, 2)                                                                           NULL
);

CREATE TABLE role (
  id   INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(5) NOT NULL
);

CREATE TABLE user_roles (
    users_id INT NOT NULL,
    roles_id INT NOT NULL,
    PRIMARY KEY (users_id, roles_id),
    CONSTRAINT FK7ecyobaa59vxkxckg6t355l86
        FOREIGN KEY (users_id) REFERENCES user (id),
    CONSTRAINT FKj9553ass9uctjrmh0gkqsmv0d
        FOREIGN KEY (roles_id) REFERENCES role (id)
);

CREATE TABLE daily_cart (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    date    DATE NOT NULL,
    user_id INT  NOT NULL,
    CONSTRAINT FKlp039xb3s16oatvn8js5ywr4f
        FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE daily_cart_activity (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    time          INT NOT NULL,
    activity_id   INT NOT NULL,
    daily_cart_id INT NOT NULL,
    CONSTRAINT FKdgesrmrhq8h50f65y57nan844
     FOREIGN KEY (activity_id) REFERENCES activity (id),
    CONSTRAINT FKsk898dh43l6p4csbx0b656qe8
     FOREIGN KEY (daily_cart_id) REFERENCES daily_cart (id)
);

CREATE TABLE user_activity (
   id          INT AUTO_INCREMENT PRIMARY KEY,
   activity_id INT NOT NULL,
   user_id     INT NOT NULL,
   CONSTRAINT FKlw9o1xb2ki2hnwq1o3kk5dlja
       FOREIGN KEY (activity_id) REFERENCES activity (id),
   CONSTRAINT FKp78clcyf5okycdv9teohsr2kq
       FOREIGN KEY (user_id) REFERENCES user (id)
);