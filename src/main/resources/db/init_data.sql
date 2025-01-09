USE playcation;

INSERT INTO user (email, password, name, role, social)
VALUES ('choLuckUser@choLuckUser.com', 'password', 'name', 'USER', 'NORMAL');

INSERT INTO game (title, description, category, user_id, price, image_url, status)
VALUES ('game1', 'description1', 'rpg', 1, 10000, '', 'ON_SAL');

INSERT INTO game (title, description, category, user_id, price, image_url, status)
VALUES ('game2', 'description2', 'rpg', 1, 20000, '', 'ON_SAL');

INSERT INTO game (title, description, category, user_id, price, image_url, status)
VALUES ('game3', 'description2', 'rpg', 1, 20000, '', 'ON_SAL');

INSERT INTO game (title, description, category, user_id, price, image_url, status)
VALUES ('game4', 'description2', 'rpg', 1, 20000, '', 'ON_SAL');

INSERT INTO game (title, description, category, user_id, price, image_url, status)
VALUES ('game5', 'description2', 'rpg', 1, 20000, '', 'ON_SAL');
