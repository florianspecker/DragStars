package com.zuehlke.carrera.bot.app;

import com.zuehlke.carrera.bot.service.StatefulMemoryDataStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 11/10/14.
 * <p/>
 * TODO description
 *
 * @author Florian Specker
 */
public class Test {

    public static void main(String[] args) {
        StatefulMemoryDataStore store = StatefulMemoryDataStore.getInstance();
        store.addTimestamp(50000l);
        store.addTimestamp(75000l);
        System.out.println(store.getCurrentPowerIncrement());
        store.addTimestamp(95000l);
        System.out.println(store.getCurrentPowerIncrement());
        store.addTimestamp(110000l);
        System.out.println(store.getCurrentPowerIncrement());
        System.out.println(store.getCurrentPowerIncrement());
        System.out.println(store.getCurrentPowerIncrement());
    }

    public static void main3(String[] args) {
        List<Long> list_of_timestamps = new ArrayList<>();
        list_of_timestamps.add(50000l);
        list_of_timestamps.add(75000l);

        long lap_time = list_of_timestamps.get(1) - list_of_timestamps.get(0);
        int k = 0;
        for (int i = list_of_timestamps.size() - 1; i < 200; i++) {
            long total_time = 0;
            for (int z = 0; z < i; z++) {
                total_time += lap_time * 60 * i / (60 * i + z * (180 - 60));
            }
            if (total_time > 180000) {
                k = i;
                break;
            }
            System.out.println("total time: " + total_time);
        }
        long add_power = (180 - 60) / (k > 1 ? k - 1 : 1);
        System.out.println(add_power);

    }


    public static void main2(String[] args) {
        List<Long> list_of_timestamps = new ArrayList<>();
        list_of_timestamps.add(50000l);
        list_of_timestamps.add(75000l);
        list_of_timestamps.add(96500l);

        long lap_time = list_of_timestamps.get(1) - list_of_timestamps.get(0);
        int k = 0;
        for (int i = list_of_timestamps.size() - 1; i < 200; i++) {
            long total_time = 0;
            for (int z = 0; z < i; z++) {
                total_time += lap_time * 60 * i / (60 * i + z * (180 - 60));
            }
            if (total_time > 180000) {
                k = i;
                break;
            }
            System.out.println("total time: " + total_time);
        }
        long add_power = (180 - 60) / (k > 1 ? k - 1 : 1);
        System.out.println(add_power);

    }
}
