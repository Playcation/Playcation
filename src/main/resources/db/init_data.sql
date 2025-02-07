USE playcation;

INSERT INTO user (email, password, name, role, social)
VALUES ('choLuckUser@choLuckUser.com', 'password', 'name', 'USER', 'NORMAL');

DROP PROCEDURE IF EXISTS InsertUsers;
CREATE PROCEDURE InsertUsers()
BEGIN
    DECLARE i INT DEFAULT 1;

    WHILE i <= 10 DO
            INSERT INTO user (email, password, name, role, social)
            VALUES (CONCAT('user', i, '@user.com'), 'password', 'name', 'USER', 'NORMAL');

            SET i = i + 1; -- 반복 변수 증가
        END WHILE;
END;

CALL InsertUsers();

DROP PROCEDURE IF EXISTS InsertRegist;
CREATE PROCEDURE InsertRegist()
BEGIN
    DECLARE i INT DEFAULT 1;

    WHILE i <= 10 DO
            INSERT INTO regist_manager (user_id, description, mainPicture, title, price)
            VALUES (i, CONCAT(i, ' description'), '', CONCAT('title', i), 1000 * i);

            SET i = i + 1; -- 반복 변수 증가
        END WHILE;
END;

CALL InsertRegist();

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

DROP PROCEDURE IF EXISTS InsertGames;
CREATE PROCEDURE InsertGames()
BEGIN
    DECLARE i INT DEFAULT 1;

    WHILE i <= 10 DO
            INSERT INTO game (title, description, category_id, user_id, price, filePath, imageUrl, status, deletedAt)
            VALUES (CONCAT('game', i), CONCAT('description', i), 1, 1, 10000 + (i * 1000), '', '', 'ON_SAL', null);

            SET i = i + 1; -- 반복 변수 증가
        END WHILE;
END;

CALL InsertGames();

INSERT INTO library (user_id, game_id, favourite)
VALUES (1, 1, 0);

INSERT INTO library (user_id, game_id, favourite)
VALUES (1, 2, 0);

INSERT INTO library (user_id, game_id, favourite)
VALUES (1, 3, 0);

INSERT INTO library (user_id, game_id, favourite)
VALUES (1, 4, 0);

INSERT INTO library (user_id, game_id, favourite)
VALUES (1, 5, 0);

USE playcation;
INSERT INTO event (title, description)
VALUES ('CHRISTMAS EVENT', '메리 크리스마스! 깜짝 이벤트를 진행합니다!');
INSERT INTO event (title, description)
VALUES ( 'WINTER EVENT'
       , '한정 쿠폰 지급! 선착순으로 특별 할인 코드 제공!');