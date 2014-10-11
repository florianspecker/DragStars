-- sensor_events table
CREATE TABLE sensor_events
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    type INT NOT NULL,
    timestamp_sent BIGINT NOT NULL,
    acc_x FLOAT NOT NULL,
    acc_y FLOAT NOT NULL,
    acc_z FLOAT NOT NULL,
    gyr_x FLOAT NOT NULL,
    gyr_y FLOAT NOT NULL,
    gyr_z FLOAT NOT NULL,
    mag_x FLOAT NOT NULL,
    mag_y FLOAT NOT NULL,
    mag_z FLOAT NOT NULL,
    timestamp_received BIGINT NOT NULL
);

-- speed_controls table
CREATE TABLE speed_controls
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    timestamp_sent BIGINT NOT NULL,
    timestamp_received BIGINT,
    power DOUBLE NOT NULL
);
