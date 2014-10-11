package com.zuehlke.carrera.bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuehlke.carrera.bot.dao.SensorEventDAO;
import com.zuehlke.carrera.bot.dao.SpeedControlDAO;
import com.zuehlke.carrera.bot.model.SensorEvent;
import com.zuehlke.carrera.bot.model.SpeedControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

import static com.zuehlke.carrera.bot.util.Constants.*;

/**
 * Contains the primary Bot AI.
 * Created by paba on 10/5/14.
 */
@Service
public class MyBotService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyBotService.class);

    private final Client client;
    private final WebTarget relayRestApi;

    private final float MAX_Y_ACCELERATION = 40;

    private SensorEventDAO sensorEventDAO;
    private SpeedControlDAO speedControlDAO;

    private double initial_power = 60;
    private double add_power;
    private List<Long> list_of_timestamps;
    private long lap_time;

    /**
     * Creates a new MyBotService
     */
    public MyBotService() {
        client = ClientBuilder.newClient();
        relayRestApi = client.target(BASE_URL).path("/ws/rest");
    }

    @Autowired
    public void setSensorEventDAO(SensorEventDAO sensorEventDAO) {
        this.sensorEventDAO = sensorEventDAO;
    }

    @Autowired
    public void setSpeedControlDAO(SpeedControlDAO speedControlDAO) {
        this.speedControlDAO = speedControlDAO;
    }


    /**
     * Occurs when a race starts.
     */
    public void start() {
        // TODO Maybe send initial velocity here...
        sendSpeedControl(initial_power);
    }

    /**
     * Occurs when the bot receives sensor data from the car or the race-track.
     *
     * @param data
     */
    public void handleSensorEvent(SensorEvent data) {
        sensorEventDAO.insert(data);
        switch (data.getType()) {
            case CAR_SENSOR_DATA:
                // Sensor data from the mounted car sensor
                // TODO Handle Car sensor data more intelligently

                break;

            case ROUND_PASSED:
                // A round has been passed - generated event from the light barrier
                list_of_timestamps = sensorEventDAO.findByTimeRange();
                if(list_of_timestamps.size()>1){
                    lap_time = list_of_timestamps.get(list_of_timestamps.size()-1)-
                            list_of_timestamps.get(list_of_timestamps.size()-2);
                    int k = 0;
                    long total_time = 0;
                    for(int i=0;i<20;i++){
                        for(int z = 0;z<i;z++){
                            total_time = lap_time * 50 * i/(50*i+z*(180-50));
                        }
                        if(total_time>180){
                            k = i;
                            break;
                        }
                    }
                    k = k-1;
                    add_power = (180 - 50) / k;
                    sendSpeedControl(initial_power + add_power);
                }
                break;
        }
    }

    /**
     * Sends the given power to the race-track using the rest API
     *
     * @param power Power value in the range of [0 - 250]
     */
    public void sendSpeedControl(double power) {
        SpeedControl control = new SpeedControl(power, TEAM_ID, ACCESS_CODE, new Date().getTime());
        speedControlDAO.insert(control);
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(os, control);

            Response response = relayRestApi.path("relay/speed").request()
                    .post(Entity.entity(new String(os.toByteArray()), MediaType.APPLICATION_JSON));

            int status = response.getStatus();
            MultivaluedMap<String, String> headers = response.getStringHeaders();
            StringBuilder sb = new StringBuilder();
            for (String key : headers.keySet()) {
                sb.append(key).append('=').append(headers.getFirst(key)).append("; ");
            }
            LOGGER.info("After sending SpeedControl: Got status " + status + "; headers: " + sb.toString());
        } catch (Exception e) {
            LOGGER.error("Error trying to send SpeedControl: " + e.getClass() + ", " + e.getMessage());
            e.printStackTrace();
        }
    }


}
