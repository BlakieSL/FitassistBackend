INSERT INTO media (image_name, parent_id, parentType) VALUES
('image1.jpg', 1, 'FOOD'),
('image2.png', 2, 'EXERCISE'),
('image3.gif', 1, 'RECIPE'),
('image4.jpeg', 4, 'COMMENT'),
('profile2.jpg', 2, 'USER');

INSERT INTO user ( activity_level, birthday, email, gender,  goal,  height, password, username,  weight) VALUES
('MODERATELY_ACTIVE','1990-05-15','john.doe@example.com','MALE','LOSE_WEIGHT',175.5,'Dimas@123','Doe',  80.3),
('LIGHTLY_ACTIVE','1992-08-20','jane.smith@example.com','FEMALE', 'MAINTAIN_WEIGHT',165.0,   'Password@456',   'JaneSmith',   65.5 );


INSERT INTO recipe (is_public, description, name, user_id, created_at) VALUES
(true, 'A healthy chicken salad with avocado and quinoa.','Chicken Quinoa Salad',   1,     NOW());
