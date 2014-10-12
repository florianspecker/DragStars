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
    private static final double INITIAL_POWER = 149;

    private StatefulMemoryDataStore statefulMemoryDataStore;

    @Autowired
    public void setStatefulMemoryDataStore(StatefulMemoryDataStore statefulMemoryDataStore) {
        this.statefulMemoryDataStore = statefulMemoryDataStore;
    }

    private double setPower(Double power) {
        LOGGER.info("Setting power to " + power);
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
        statefulMemoryDataStore.setPosition(-1);
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
                SensorEvent last2 = pastEvents.get(pastEvents.size() - 2);
                if (SensorEventType.ROUND_PASSED.equals(last2.getType())) {
                    last2 = pastEvents.get(pastEvents.size() - 3);
                }
                SensorEvent last4 = pastEvents.get(pastEvents.size() - 4);
                if (SensorEventType.ROUND_PASSED.equals(last4.getType())) {
                    last4 = pastEvents.get(pastEvents.size() - 5);
                }

                if (null != statefulMemoryDataStore.getTimer() && System.currentTimeMillis() > statefulMemoryDataStore.getTimer()) {
                    statefulMemoryDataStore.setTimer(null);
                    return setPower(151d);
                }

                if (last4.getAcc()[1] > 200 && data.getAcc()[1] <= 50) {
                    statefulMemoryDataStore.setTimer(System.currentTimeMillis() + 140);
                    statefulMemoryDataStore.setPosition(1);
                    return setPower(220d);
                }

                if (statefulMemoryDataStore.getPosition() == 1 && last.getAcc()[1] >= 100 && data.getAcc()[1] < 50) {
                    statefulMemoryDataStore.setPosition(2);
                    return setPower(160d);
                }

                if (statefulMemoryDataStore.getPosition() == 2 && last.getAcc()[1] >= 100 && data.getAcc()[1] < 50) {
                    statefulMemoryDataStore.setTimer(System.currentTimeMillis() + 20);
                    statefulMemoryDataStore.setPosition(3);
                    return setPower(150d);
                }

                if (statefulMemoryDataStore.getPosition() == 3 && last.getAcc()[1] < -150 && data.getAcc()[1] > 0) {
                    statefulMemoryDataStore.setPosition(4);
                    return setPower(180d);
                }

                if (statefulMemoryDataStore.getPosition() == 4 && last2.getAcc()[1] >= 80 && data.getAcc()[1] <= 50) {
                    statefulMemoryDataStore.setTimer(System.currentTimeMillis() + 300);
                    statefulMemoryDataStore.setPosition(5);
                    return setPower(300d);
                }

                return setPower(null);
            case ROUND_PASSED:
                if (statefulMemoryDataStore.getPosition() != 1) {
                    statefulMemoryDataStore.setTimer(System.currentTimeMillis() + 100);
                    statefulMemoryDataStore.setPosition(1);
                    return setPower(200d);
                }
                // A round has been passed - generated event from the light barrier
                return setPower(null);
        }
        return 0;
    }

}
