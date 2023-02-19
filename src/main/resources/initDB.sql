CREATE DATABASE IF NOT EXISTS sport;

ALTER DATABASE sport
    DEFAULT CHARACTER SET utf8
    DEFAULT COLLATE utf8_general_ci;

USE sport;

DROP TABLE IF EXISTS players;
DROP TABLE IF EXISTS teams;

CREATE TABLE teams
(
    id        int NOT NULL AUTO_INCREMENT,
    team_name varchar(60),
    city      varchar(60),
    stadium   varchar(60),
    founded   date,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE players
(
    id                    int NOT NULL AUTO_INCREMENT,
    player_name           varchar(60),
    player_number         int,
    position              varchar(60),
    university_or_country varchar(60),
    date_of_birth         date,
    team_id               int,
    PRIMARY KEY (id),
    FOREIGN KEY (team_id) REFERENCES teams (id)
) ENGINE = InnoDB;

INSERT INTO teams
VALUES (1, 'Detroit Pistons', 'Detroit', 'Little Caesars Arena', '1937-01-01'),
       (2, 'Atlanta Hawks', 'Atlanta', 'State Farm Arena', '1946-01-01');

INSERT INTO players
VALUES (1, 'Trae Young', 11, 'G', 'Oklahoma', '1998-09-19', 2);

