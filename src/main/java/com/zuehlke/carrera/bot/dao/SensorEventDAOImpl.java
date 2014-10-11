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
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public void insert(SensorEvent sensorEvent) {

        String sql = "INSERT INTO sensor_events (type, timestamp_sent, timestamp_received, " +
                "acc_x, acc_y, acc_z, gyr_x, gyr_y, gyr_z, " +
                "mag_x, mag_y, mag_z) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, SensorEventType.CAR_SENSOR_DATA.equals(sensorEvent.getType()) ? 0 : 1);
            ps.setLong(2, sensorEvent.getTimeStamp());
            ps.setLong(3, sensorEvent.getTimeStampReceived());
            ps.setFloat(4, sensorEvent.getAcc()[0]);
            ps.setFloat(5, sensorEvent.getAcc()[1]);
            ps.setFloat(6, sensorEvent.getAcc()[2]);
            ps.setFloat(7, sensorEvent.getGyr()[0]);
            ps.setFloat(8, sensorEvent.getGyr()[1]);
            ps.setFloat(9, sensorEvent.getGyr()[2]);
            ps.setFloat(10, sensorEvent.getMag()[0]);
            ps.setFloat(11, sensorEvent.getMag()[1]);
            ps.setFloat(12, sensorEvent.getMag()[2]);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            LOGGER.error("Error inserting SensorEvent " + sensorEvent.toString() + ": "
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

    public SensorEvent findById(int id) {
        String sql = "SELECT type, timestamp_sent, timestamp_received, " +
                "acc_x, acc_y, acc_z, gyr_x, gyr_y, gyr_z, mag_x, mag_y, mag_z " +
                "FROM sensor_events WHERE id = ?";

        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            SensorEvent sensorEvent = null;
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt(1) == 1) {
                    // ROUND_PASSED
                    sensorEvent = new SensorEvent(SensorEventType.ROUND_PASSED,
                            rs.getLong(2));
                    sensorEvent.setTimeStampReceived(rs.getLong(3));
                } else {
                    // CAR_SENSOR_DATA
                    sensorEvent = new SensorEvent(
                            new float[]{rs.getFloat(4), rs.getFloat(5), rs.getFloat(6)},
                            new float[]{rs.getFloat(7), rs.getFloat(8), rs.getFloat(9)},
                            new float[]{rs.getFloat(10), rs.getFloat(11), rs.getFloat(12)},
                            rs.getLong(2));
                    sensorEvent.setTimeStampReceived(rs.getLong(3));
                }
            }
            rs.close();
            ps.close();
            return sensorEvent;
        } catch (SQLException e) {
            LOGGER.error("Error reading SensorEvent with id " + id + ": " + e.toString() + "; " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

}
