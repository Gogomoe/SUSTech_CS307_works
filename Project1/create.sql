DROP TABLE IF EXISTS city;
CREATE TABLE city
(
    id             SERIAL,
    name           VARCHAR(10) NOT NULL,
    englishName    VARCHAR(70) NOT NULL,
    zipCode        VARCHAR(10) NOT NULL,
    confirmedCount INT         NOT NULL,
    suspectedCount INT         NOT NULL,
    curedCount     INT         NOT NULL,
    deadCount      INT         NOT NULL,
    updateTime     TIMESTAMP   NOT NULL,
    PRIMARY KEY (id)
);
