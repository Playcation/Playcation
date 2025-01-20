USE playcation;

INSERT INTO user (email, password, name, role, social)
VALUES ('choLuckUser@choLuckUser.com', 'password', 'name', 'USER', 'NORMAL');

INSERT INTO category (categoryName)
VALUES ('rpg');

INSERT INTO category (categoryName)
VALUES ('shooting');

INSERT INTO category (categoryName)
VALUES ('roguelike');

INSERT INTO category (categoryName)
VALUES ('action rpg');

INSERT INTO category (categoryName)
VALUES ('fps');

INSERT INTO game (title, description, category_id, user_id, price, imageUrl, status)
VALUES ('game1', 'description1', 1, 1, 10000, '', 'ON_SAL');

INSERT INTO game (title, description, category_id, user_id, price, imageUrl, status)
VALUES ('game2', 'description2', 1, 1, 20000, '', 'ON_SAL');

INSERT INTO game (title, description, category_id, user_id, price, imageUrl, status)
VALUES ('game3', 'description2', 1, 1, 20000, '', 'ON_SAL');

INSERT INTO game (title, description, category_id, user_id, price, imageUrl, status)
VALUES ('game4', 'description2', 2, 1, 20000, '', 'ON_SAL');

INSERT INTO game (title, description, category_id, user_id, price, imageUrl, status)
VALUES ('game5', 'description2', 2, 1, 20000, '', 'ON_SAL');

INSERT INTO library (user_id, game_id, favourite)
VALUES (1, 1, FALSE);

INSERT INTO library (user_id, game_id, favourite)
VALUES (1, 2, FALSE);

INSERT INTO library (user_id, game_id, favourite)
VALUES (1, 3, FALSE);

INSERT INTO library (user_id, game_id, favourite)
VALUES (1, 4, FALSE);

INSERT INTO library (user_id, game_id, favourite)
VALUES (1, 5, FALSE);
