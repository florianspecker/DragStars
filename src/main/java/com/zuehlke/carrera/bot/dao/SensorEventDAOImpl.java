package com.zuehlke.carrera.bot.dao;

import com.zuehlke.carrera.bot.model.SensorEvent;
import com.zuehlke.carrera.bot.model.SensorEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;
/**
 *
 * @author Florian Specker
 */
@Service
public class SensorEventDAOImpl implements SensorEventDAO {

    private DataSource dataSource;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insert(SensorEvent sensorEvent){

        String sql = "INSERT INTO sensor_events (type, timestamp_sent, timestamp_received) VALUES (?, ?, ?)";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, 1);
            ps.setLong(2, System.currentTimeMillis());
            ps.setLong(3, System.currentTimeMillis());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
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

    public SensorEvent findById(int id){
        String sql = "SELECT * FROM sensor_events WHERE id = ?";

        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            SensorEvent sensorEvent = null;
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                sensorEvent = new SensorEvent(SensorEventType.CAR_SENSOR_DATA,
                        rs.getLong("timestamp_sent")
                );
            }
            rs.close();
            ps.close();
            return sensorEvent;
        } catch (SQLException e) {
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
