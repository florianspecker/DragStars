package com.zuehlke.carrera.bot.dao;

import com.zuehlke.carrera.bot.model.SensorEvent;
import com.zuehlke.carrera.bot.model.SensorEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

        String sql = "INSERT INTO sensor_events (type, timestamp_sent, acc_x, acc_y,acc_z,gyr_x,gyr_y,gyr_z, " +
                "mag_x,mag_y,mag_z,timestamp_received) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, 1);
            ps.setLong(2, sensorEvent.getTimeStamp());
            ps.setFloat(3, sensorEvent.getAcc()[0]);
            ps.setFloat(4, sensorEvent.getAcc()[1]);
            ps.setFloat(5, sensorEvent.getAcc()[2]);
            ps.setFloat(6, sensorEvent.getGyr()[0]);
            ps.setFloat(7, sensorEvent.getGyr()[1]);
            ps.setFloat(8, sensorEvent.getGyr()[2]);
            ps.setFloat(9, sensorEvent.getMag()[0]);
            ps.setFloat(10, sensorEvent.getMag()[1]);
            ps.setFloat(11, sensorEvent.getMag()[2]);
            ps.setLong(12, System.currentTimeMillis());
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
                if(rs.getType()==1){
                    sensorEvent = new SensorEvent(SensorEventType.CAR_SENSOR_DATA,
                            rs.getLong("timestamp_sent"));
                }else{
                    sensorEvent = new SensorEvent(SensorEventType.ROUND_PASSED,
                            rs.getLong("timestamp_sent"));
                }


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
