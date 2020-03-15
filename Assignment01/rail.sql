CREATE TABLE city
(
    id   SERIAL      NOT NULL,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (code)
);

CREATE TABLE station
(
    id      SERIAL      NOT NULL,
    name    VARCHAR(50) NOT NULL,
    code    VARCHAR(6)  NOT NULL,
    city_id INT         NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (city_id) REFERENCES city (id),
    UNIQUE (code)
);

CREATE TABLE train
(
    id          SERIAL      NOT NULL,
    code        VARCHAR(10) NOT NULL,
    basic_price INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (code)
);

CREATE TABLE seat
(
    id          SERIAL      NOT NULL,
    name        VARCHAR(20) NOT NULL,
    basic_price INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (name)
);

CREATE TABLE train_seat
(
    train_id   INT NOT NULL,
    seat_id    INT NOT NULL,
    seat_count INT NOT NULL,
    PRIMARY KEY (train_id, seat_id),
    FOREIGN KEY (train_id) REFERENCES train (id),
    FOREIGN KEY (seat_id) REFERENCES seat (id)
);

CREATE TABLE train_line
(
    id             SERIAL      NOT NULL,
    name           VARCHAR(50) NOT NULL,
    depart_station INT         NOT NULL,
    arrive_station INT         NOT NULL,
    depart_time    TIMESTAMP   NOT NULL,
    arrive_time    TIMESTAMP   NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE train_line_station
(
    train_line_id INT       NOT NULL,
    station_id    INT       NOT NULL,
    arrive_time   TIMESTAMP NOT NULL,
    depart_time   TIMESTAMP NOT NULL,
    PRIMARY KEY (train_line_id, station_id),
    FOREIGN KEY (train_line_id) REFERENCES train_line (id),
    FOREIGN KEY (station_id) REFERENCES station (id)
);

CREATE TABLE ticket
(
    id            SERIAL NOT NULL,
    train_id      INT    NOT NULL,
    train_line_id INT    NOT NULL,
    from_station  INT    NOT NULL,
    to_station    INT    NOT NULL,
    date          DATE   NOT NULL,
    seat_id       INT    NOT NULL,
    seat_position INT    NOT NULL,
    price         INT    NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (train_id, train_line_id, date),
    UNIQUE (train_id, train_line_id, date, seat_id, seat_position),
    FOREIGN KEY (train_id) REFERENCES train (id),
    FOREIGN KEY (train_line_id) REFERENCES train_line (id),
    FOREIGN KEY (from_station) REFERENCES station (id),
    FOREIGN KEY (to_station) REFERENCES station (id),
    FOREIGN KEY (seat_id) REFERENCES seat (id)
);

CREATE TABLE user_info
(
    id      SERIAL       NOT NULL,
    id_card CHAR(18)     NOT NULL,
    name    VARCHAR(100) NOT NULL,
    phone   VARCHAR(30)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (id_card)
);

CREATE TABLE order_info
(
    id          SERIAL    NOT NULL,
    ticket_id   INT       NOT NULL,
    user_id     INT       NOT NULL,
    status      INT       NOT NULL,
    create_date TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (ticket_id, user_id),
    FOREIGN KEY (ticket_id) REFERENCES ticket (id),
    FOREIGN KEY (user_id) REFERENCES user_info (id)
)