package com.zuehlke.carrera.bot.service;

import com.zuehlke.carrera.bot.dao.SensorEventDAO;
import com.zuehlke.carrera.bot.dao.SpeedControlDAO;
import com.zuehlke.carrera.bot.model.SensorEvent;
import com.zuehlke.carrera.bot.model.SpeedControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created on 11/10/14.
 * <p/>
 * Ugly hack for storing state
 *
 * @author Florian Specker
 */
@Service
@Scope(value = "singleton")
public class StatefulMemoryDataStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatefulMemoryDataStore.class);

    private SensorEventDAO sensorEventDAO;
    private SpeedControlDAO speedControlDAO;

    private double currentPower;

    public double getCurrentPower() {
        addSpeedControl(new SpeedControl(currentPower, System.currentTimeMillis()));
        return currentPower;
    }

    public void setCurrentPower(double currentPower) {
        addSpeedControl(new SpeedControl(currentPower, System.currentTimeMillis()));
        this.currentPower = currentPower;
    }

    private List<Long> timestamps = new ArrayList<>();
    private List<Double> powerIncrements = new ArrayList<>();

    private List<Long> times = new ArrayList<Long>();


    @Autowired
    public void setSensorEventDAO(SensorEventDAO sensorEventDAO) {
        this.sensorEventDAO = sensorEventDAO;
    }

    @Autowired
    public void setSpeedControlDAO(SpeedControlDAO speedControlDAO) {
        this.speedControlDAO = speedControlDAO;
    }


    public void addTimestamp(Long timestamp) {
        if (timestamps.size() == 0 || timestamp > timestamps.get(timestamps.size() - 1) + 1000) {
            timestamps.add(timestamp);
            if (timestamps.size() == 2) {
                calculatePowerIncrements(timestamps.get(1) - timestamps.get(0));
            }
        }
    }

    public List<Long> getTimestamps() {
        return timestamps;
    }

    public Double getCurrentPowerIncrement() {
        return powerIncrements.get(Math.max(0, Math.min(timestamps.size() - 2, powerIncrements.size() - 1)));
    }

    private void calculatePowerIncrements(long timeLapseOne) {
        int n = 1;
        while (sumDuration(timeLapseOne, n) < 60000) {
            n++;
        }
        double deltaPower = (250 - 100) / (n - 1);
        for (int i = 1; i <= n; i++) {
            powerIncrements.add(i * deltaPower);
        }
    }

    private double sumDuration(long timeLapseOne, int n) {
        double duration = 0;
        for (int i = 0; i <= n; i++) {
            duration += timeLapseOne * 100 * n / (100 * n + i * (250 - 100));
        }
        return duration;
    }

    public List<Long> getTimes() {
        return times;
    }


    /**
     * ** in-memory data store / async data inserter ****
     */
    private Queue<SensorEvent> rawSensorEvents = new ConcurrentLinkedQueue<SensorEvent>();
    private List<SensorEvent> processedEvents = Collections.synchronizedList(new ArrayList<SensorEvent>());
    private Queue<SpeedControl> speedControls = new ConcurrentLinkedQueue<SpeedControl>();

    private void addSpeedControl(SpeedControl speedControl) {
        speedControls.add(speedControl);
        LOGGER.info("Sending power value " + speedControl.getPower());

        if (!speedControls.isEmpty()) {
            processSpeedControls();
        }
    }

    @Async
    private void processSpeedControls() {
        while (!speedControls.isEmpty()) {
            speedControlDAO.insert(speedControls.remove());
        }
    }

    public void addSensorEvent(SensorEvent sensorEvent) {
        rawSensorEvents.add(sensorEvent);

        if (rawSensorEvents.size() > 12) {
            LOGGER.info("got more than 12 raw sensorEvents, triggering async processing now");
            processRawSensorEvents();
        }
    }

    @Async
    private void processRawSensorEvents() {
        while (rawSensorEvents.size() > 3) {
            SensorEvent sensorEvent = rawSensorEvents.remove();
            processedEvents.add(sensorEvent);
            sensorEventDAO.insert(sensorEvent);
        }
    }


}
