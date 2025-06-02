INSERT INTO media (imageName, parent_id, parentType) VALUES
('image1.jpg', 1, 'FOOD'),
('image2.png', 2, 'EXERCISE'),
('image3.gif', 3, 'RECIPE'),
('image4.jpeg', 4, 'COMMENT');

INSERT INTO user (
    activity_level,
    birthday,
    calculated_calories,
    email,
    gender,
    goal,
    height,
    name,
    password,
    surname,
    weight
) VALUES (
             'MODERATELY_ACTIVE',
             '1990-05-15',
             2500.0,
             'john.doe@example.com',
             'MALE',
             'LOSE_WEIGHT',
             175.5,
             'John',
             'Dimas@123',
             'Doe',
             80.3
         );


INSERT INTO recipe (description,name,text,user_id) VALUES (
             'A healthy chicken salad with avocado and quinoa.',
             'Chicken Quinoa Salad',
             'Mix cooked quinoa, grilled chicken breast, avocado, cherry tomatoes, and olive oil.',
             1
);
