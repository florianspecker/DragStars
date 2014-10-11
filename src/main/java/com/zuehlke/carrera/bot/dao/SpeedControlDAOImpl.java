package com.zuehlke.carrera.bot.dao;

import com.zuehlke.carrera.bot.model.SpeedControl;
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
 * Implementation for the SpeedControl Data Access Object
 *
 * @author Florian Specker
 */
@Service
public class SpeedControlDAOImpl implements SpeedControlDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpeedControlDAOImpl.class);

    private DataSource dataSource;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insert(SpeedControl speedControl) {

        String sql = "INSERT INTO speed_controls (timestamp_sent, power) VALUES (?, ?)";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, speedControl.getTimeStamp());
            ps.setDouble(2, speedControl.getPower());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            LOGGER.error("Error inserting SpeedControl " + speedControl.toString() + ": "
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

    public SpeedControl findById(int id) {
        String sql = "SELECT timestamp_sent, power " +
                "FROM speed_controls WHERE id = ?";

        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            SpeedControl speedControl = null;
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                speedControl = new SpeedControl(rs.getDouble(2), rs.getLong(1));
            }
            rs.close();
            ps.close();
            return speedControl;
        } catch (SQLException e) {
            LOGGER.error("Error reading SpeedControl with id " + id + ": " + e.toString() + "; " + e.getMessage());
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
