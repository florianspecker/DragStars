-- sensor_events table
CREATE TABLE sensor_events
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    type INT NOT NULL,
    timestamp_sent BIGINT NOT NULL,
    acc_x FLOAT ,
    acc_y FLOAT ,
    acc_z FLOAT ,
    gyr_x FLOAT ,
    gyr_y FLOAT ,
    gyr_z FLOAT ,
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
