package com.zuehlke.carrera.bot.dao;

import com.zuehlke.carrera.bot.model.SensorEvent;
import com.zuehlke.carrera.bot.model.SensorEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Created on 11/10/14.
 * <p/>
 * Implementation for the SensorEvent Data Access Object
 *
 * @author Florian Specker
 */
@Service
public class SensorEventDAOImpl implements SensorEventDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorEventDAOImpl.class);

    private DataSource dataSource;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insert(Collection<SensorEvent> sensorEvents) {

        String sql = "INSERT INTO sensor_events (type, timestamp_sent, timestamp_received, " +
                "acc_x, acc_y, acc_z, gyr_x, gyr_y, gyr_z, power" +
                ") VALUES (0, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sql_round = "INSERT INTO sensor_events (type, timestamp_sent, timestamp_received" +
                ") VALUES (1, ?, ?)";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement psCar = conn.prepareStatement(sql);
            PreparedStatement psRound = conn.prepareStatement(sql_round);

            for (SensorEvent sensorEvent : sensorEvents) {
                if (SensorEventType.CAR_SENSOR_DATA.equals(sensorEvent.getType())) {
                    psCar.setLong(1, sensorEvent.getTimeStamp());
                    psCar.setLong(2, sensorEvent.getTimeStampReceived());
                    psCar.setFloat(3, sensorEvent.getAcc()[0]);
                    psCar.setFloat(4, sensorEvent.getAcc()[1]);
                    psCar.setFloat(5, sensorEvent.getAcc()[2]);
                    psCar.setFloat(6, sensorEvent.getGyr()[0]);
                    psCar.setFloat(7, sensorEvent.getGyr()[1]);
                    psCar.setFloat(8, sensorEvent.getGyr()[2]);
                    psCar.setDouble(9, sensorEvent.getPower());
                    psCar.addBatch();
                } else {
                    psRound.setLong(1, sensorEvent.getTimeStamp());
                    psRound.setLong(2, sensorEvent.getTimeStampReceived());
                    psRound.addBatch();
                }
            }
            psCar.executeBatch();
            psRound.executeBatch();
            psCar.close();
            psRound.close();
        } catch (SQLException e) {
            LOGGER.error("Error inserting SensorEvents : "
                    + e.toString() + "; " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignore) {
                }
            }
        }
    }

}
