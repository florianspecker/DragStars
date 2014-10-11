package com.zuehlke.carrera.bot.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 11/10/14.
 * <p/>
 * Ugly hack for storing state
 *
 * @author Florian Specker
 */
public class StatefulMemoryDataStore {

    private static StatefulMemoryDataStore instance;

    public synchronized static StatefulMemoryDataStore getInstance() {
        if (null == instance) {
            instance = new StatefulMemoryDataStore();
        }
        return instance;
    }

    private List<Long> timestamps = new ArrayList<>();
    private List<Double> powerIncrements = new ArrayList<>();

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
        while (sumDuration(timeLapseOne, n) < 180000) {
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

}
