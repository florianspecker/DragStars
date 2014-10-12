package com.zuehlke.carrera.bot.service;

import com.zuehlke.carrera.bot.model.SensorEvent;
import com.zuehlke.carrera.bot.model.SensorEventType;
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
    private static final double INITIAL_POWER = 160;

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
        switch (data.getType()) {
            case CAR_SENSOR_DATA:
                // Sensor data from the mounted car sensor
                List<SensorEvent> pastEvents = statefulMemoryDataStore.getSensorEvents();
                if (pastEvents.size() < 2) {
                    return setPower(null);
                }

                SensorEvent last = pastEvents.get(pastEvents.size() - 1);
                if (SensorEventType.ROUND_PASSED.equals(last.getType())) {
                    last = pastEvents.get(pastEvents.size() - 2);
                }

                if (statefulMemoryDataStore.getCurrentPower() == 0) {
                    return setPower(1d);
                }
                if (statefulMemoryDataStore.getCurrentPower() == 1) {
                    return setPower(150d);
                }
                float gyrZDiff = data.getGyr()[2] - last.getGyr()[2];
                if (gyrZDiff < -5) {
                    // curve ahead!
                    if (statefulMemoryDataStore.getCurrentPower() == 220) {
                        return setPower(0d);
                    }
                    return setPower(150d);
                }
                if (gyrZDiff > 0 && gyrZDiff < 1 && data.getGyr()[2] > 500) {
                    // straight ahead!
                    return setPower(220d);
                }

                return setPower(null);
            case ROUND_PASSED:
                // A round has been passed - generated event from the light barrier
                return setPower(null);
        }
        return 0;
    }

}
