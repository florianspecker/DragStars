package com.zuehlke.carrera.bot.service;

import com.zuehlke.carrera.bot.dao.SensorEventDAO;
import com.zuehlke.carrera.bot.model.SensorEvent;
import com.zuehlke.carrera.bot.model.SensorEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    private double currentPower;

    public double getCurrentPower() {
        return currentPower;
    }

    public void setCurrentPower(double currentPower) {
        this.currentPower = currentPower;
    }

    private List<Long> timestamps = new ArrayList<>();
    private List<Double> powerIncrements = new ArrayList<>();

    private List<Long> times = new ArrayList<Long>();

    private int lapCounter = 0;

    public int getLapCounter() {
        return lapCounter;
    }

    public void incrementLapCounter() {
        lapCounter += 5;
    }

    @Autowired
    public void setSensorEventDAO(SensorEventDAO sensorEventDAO) {
        this.sensorEventDAO = sensorEventDAO;
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

    public Long timer;

    public Long getTimer() {
        return timer;
    }

    public void setTimer(Long timer) {
        this.timer = timer;
    }

    private int position = -1;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * ** in-memory data store / async data inserter ****
     */
    private List<SensorEvent> sensorEvents = new ArrayList<SensorEvent>();
    private List<SensorEvent> roundPassedEvents = new ArrayList<SensorEvent>();
    private List<Integer> roundPassedEventsIndexes = new ArrayList<>();

    public List<SensorEvent> getSensorEvents() {
        return sensorEvents;
    }

    public List<SensorEvent> getRoundPassedEvents() {
        return roundPassedEvents;
    }

    public List<Integer> getRoundPassedEventsIndexes() {
        return roundPassedEventsIndexes;
    }

    public void addSensorEvent(SensorEvent sensorEvent) {
        sensorEvent.setPower(currentPower);
        sensorEvents.add(sensorEvent);
        if (SensorEventType.ROUND_PASSED.equals(sensorEvent.getType())) {
            if (roundPassedEvents.isEmpty() ||
                    roundPassedEvents.get(roundPassedEvents.size() - 1).getTimeStamp() + 1000 < sensorEvent.getTimeStamp()) {
                roundPassedEvents.add(sensorEvent);
                roundPassedEventsIndexes.add(sensorEvents.size() - 1);
            }
        }
    }

    public void storeSensorEvents() {
        sensorEventDAO.insert(sensorEvents);
        sensorEvents.clear();
        roundPassedEvents.clear();
    }


}
