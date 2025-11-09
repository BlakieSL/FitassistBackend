INSERT INTO role (id, name) VALUES
    (1, 'USER'),
    (2, 'ADMIN');

INSERT INTO user (id, birthday, email, gender, password, username) VALUES
    (1, '1990-01-01', 'user1@example.com', 'MALE', '$2a$10$dXJhB0ZtH7yU6Z7bYQn/3eI9dQ2XvV8fLkZzYcR9aB3wC1dS5gF7u', 'user1'),
    (2, '1990-01-01', 'user2@example.com', 'FEMALE', '$2a$10$dXJhB0ZtH7yU6Z7bYQn/3eI9dQ2XvV8fLkZzYcR9aB3wC1dS5gF7u', 'user2');

INSERT INTO user_roles (users_id, roles_id) VALUES
    (1, 1),
    (2, 1);