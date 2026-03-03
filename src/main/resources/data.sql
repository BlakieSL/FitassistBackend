INSERT IGNORE INTO role (name) VALUES ('USER');
INSERT IGNORE INTO role (name) VALUES ('ADMIN');
INSERT IGNORE INTO role (name) VALUES ('MODERATOR');

INSERT IGNORE INTO user (id, username, email, password, gender, birthday, height, weight, goal, activity_level)
    VALUES (63, 'performance_test', 'performance_test@example.com', '$2a$10$xGw68gpb.Rb4TMGPjotwIOxMPU1DjOL9Ecef9Tl9F49/1uF1ppbpy', null, null, null, null, null, null);

INSERT IGNORE INTO user_roles (users_id, roles_id)
    SELECT 63, id FROM role WHERE name = 'USER';
