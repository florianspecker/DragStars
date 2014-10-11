package com.zuehlke.carrera.bot.service;

import com.zuehlke.carrera.bot.dao.SensorEventDAO;
import com.zuehlke.carrera.bot.dao.SpeedControlDAO;
import com.zuehlke.carrera.bot.model.SensorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import static com.zuehlke.carrera.bot.util.Constants.BASE_URL;

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

    private double initial_power = 100;

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

    private double setPower(Double power) {
        if (null == power) {

            // TODO return stored value
            return StatefulMemoryDataStore.getInstance().getCurrentPower();
        } else {
            StatefulMemoryDataStore.getInstance().setCurrentPower(power);
            // TODO store
        }
        return power;
    }

    /**
     * Occurs when a race starts.
     */
    public double start() {
        // TODO Maybe send initial velocity here...
        return setPower(initial_power);
    }

    /**
     * Occurs when the bot receives sensor data from the car or the race-track.
     *
     * @param data
     */
    public double handleSensorEvent(SensorEvent data) {
        sensorEventDAO.insert(data);
        switch (data.getType()) {
            case CAR_SENSOR_DATA:
                // Sensor data from the mounted car sensor
                // TODO Handle Car sensor data more intelligently
                /*if (StatefulMemoryDataStore.getInstance().getTimes().isEmpty()){
                    StatefulMemoryDataStore.getInstance().getTimes().add(data.getTimeStamp());
                }else if(data.getTimeStamp()-StatefulMemoryDataStore.getInstance().getTimes().get(0)>2000){
                    sendSpeedControl(120);
                }else if(data.getTimeStamp()-StatefulMemoryDataStore.getInstance().getTimes().get(0)>4000){
                    sendSpeedControl(0);
                }*/
                return setPower(null);

            case ROUND_PASSED:
                // A round has been passed - generated event from the light barrier
                StatefulMemoryDataStore.getInstance().addTimestamp(data.getTimeStamp());
                return setPower(initial_power + StatefulMemoryDataStore.getInstance().getCurrentPowerIncrement());
        }
        return 0;
    }

}
