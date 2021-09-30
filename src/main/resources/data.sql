DROP TABLE IF EXISTS DISPENSER;
DROP TABLE IF EXISTS DEVICEDATA;
DROP TABLE IF EXISTS USER;

create table DISPENSER(
    id int NOT NULL auto_increment PRIMARY KEY,
    fluid_level varchar(20),
    local varchar(255),
    used_count varchar(20),
    all_used_count varchar(255),
    MAC_ADDRESS char(40),
    creation_date datetime,
    last_stocked_time datetime
);

CREATE TABLE DEVICE_DATA (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_dispenser INT NOT NULL,
    fluid_level varchar(20),
    local varchar(255),
    used_count varchar(20),
    all_used_count VARCHAR(255),
    updated_time DATETIME,
    stocked boolean,
    FOREIGN KEY (id_dispenser) REFERENCES DISPENSER(id)
);

CREATE TABLE USER (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    CPF varchar(11),
    NAME varchar(255),
    POSITION varchar(255),
    ROLE varchar(22),
    EMAIL varchar(255),
    LOGIN varchar(255),
    PASSWORD varchar(255)
);

INSERT INTO USER (CPF, NAME, POSITION, ROLE, EMAIL, LOGIN, PASSWORD)
VALUES ('12345678901', 'Administrador', 'Admin', 'admin', 'admin@admin.com', 'admin', 'toor');
