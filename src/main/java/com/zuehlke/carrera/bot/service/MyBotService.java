package com.zuehlke.carrera.bot.service;

import com.zuehlke.carrera.bot.model.SensorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Contains the primary Bot AI.
 * Created by paba on 10/5/14.
 */
@Service
public class MyBotService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyBotService.class);
    private static final double INITIAL_POWER = 250;

    private StatefulMemoryDataStore statefulMemoryDataStore;

    @Autowired
    public void setStatefulMemoryDataStore(StatefulMemoryDataStore statefulMemoryDataStore) {
        this.statefulMemoryDataStore = statefulMemoryDataStore;
    }

    private double setPower(Double power) {
        if (null == power) {
            return statefulMemoryDataStore.getCurrentPower();
        } else {
            statefulMemoryDataStore.setCurrentPower(power);
        }
        return power;
    }

    /**
     * Occurs when a race starts.
     */
    public double start() {
        return setPower(INITIAL_POWER);
    }

    public void storeSensorEvents() {
        statefulMemoryDataStore.storeSensorEvents();
    }

    /**
     * Occurs when the bot receives sensor data from the car or the race-track.
     *
     * @param data
     */
    public double handleSensorEvent(SensorEvent data) {
        statefulMemoryDataStore.addSensorEvent(data);
//        switch (data.getType()) {
//            case CAR_SENSOR_DATA:
                // Sensor data from the mounted car sensor
                if (statefulMemoryDataStore.getTimes().isEmpty()) {
                    statefulMemoryDataStore.getTimes().add(System.currentTimeMillis());
                } else if (System.currentTimeMillis() - statefulMemoryDataStore.getTimes().get(0) > 440) {
                    return setPower(150d);
                } else if (System.currentTimeMillis() - statefulMemoryDataStore.getTimes().get(0) > 400) {
                    return setPower(50d);
                }
                return setPower(null);

//            case ROUND_PASSED:
                // A round has been passed - generated event from the light barrier
//                statefulMemoryDataStore.addTimestamp(data.getTimeStamp());
//                return setPower(INITIAL_POWER + statefulMemoryDataStore.getCurrentPowerIncrement());
//        }
        //return 0;
    }


}
