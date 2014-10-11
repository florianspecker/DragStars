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
    private static final double INITIAL_POWER = 100;

    private StatefulMemoryDataStore statefulMemoryDataStore;

    private List<List<SensorEvent>> gyro_list;
    private List<List<SensorEvent>> binary_list;

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
                /*if (StatefulMemoryDataStore.getInstance().getTimes().isEmpty()){
                    StatefulMemoryDataStore.getInstance().getTimes().add(data.getTimeStamp());
                }else if(data.getTimeStamp()-StatefulMemoryDataStore.getInstance().getTimes().get(0)>2000){
                    sendSpeedControl(120);
                }else if(data.getTimeStamp()-StatefulMemoryDataStore.getInstance().getTimes().get(0)>4000){
                    sendSpeedControl(0);
                }*/
                /*int old_count = 0;
                if (statefulMemoryDataStore.getRoundPassedEvents().size() > 3 && data.getType().equals(SensorEventType.ROUND_PASSED)) {
                    for (int i = 0,z=0; i<statefulMemoryDataStore.getProcessedEvents().size(); i++) {
                        if(statefulMemoryDataStore.getProcessedEvents().get(i).equals(SensorEventType.ROUND_PASSED)&&z>1){
                            gyro_list.add(statefulMemoryDataStore.getProcessedEvents().subList(old_count,i-1));
                            old_count = i;
                        }else if (z==1){
                            old_count = i;
                            z++;
                        }else if(statefulMemoryDataStore.getProcessedEvents().get(i).equals(SensorEventType.ROUND_PASSED)){
                            z++;
                        }
                    }
                }
                calculateBinaries(gyro_list);*/
                return setPower(null);

            case ROUND_PASSED:
                // A round has been passed - generated event from the light barrier
                statefulMemoryDataStore.addTimestamp(data.getTimeStamp());
                return setPower(INITIAL_POWER + statefulMemoryDataStore.getCurrentPowerIncrement());
        }
        return 0;
    }

    /*private List<List<SensorEvent>> calculateBinaries(List<List<SensorEvent>> list) {
        int i = 0;
        while(list.listIterator().hasNext()){
            Iterator it = list.get(i).listIterator();
            while(it.hasNext()){
                SensorEvent test = (SensorEvent) it.next();
                float[] gyro_types = test.getGyr();
                float gyro_z = gyro_types[2];

            }
            i++;
        }

        return null;
    }*/

}
