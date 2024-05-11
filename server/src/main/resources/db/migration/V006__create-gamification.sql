CREATE TABLE gamification_find_plant
(
    id              SERIAL PRIMARY KEY,
    plant_id        INTEGER   NOT NULL,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time   TIMESTAMP
);

CREATE TABLE gamification_find_plant_user
(
    game_id  INTEGER     NOT NULL,
    username VARCHAR(50) NOT NULL,
    PRIMARY KEY (game_id, username)
);