package com.zuehlke.carrera.bot.dao;

import com.zuehlke.carrera.bot.model.SensorEvent;

/**
 * Created on 11/10/14.
 * <p/>
 * TODO description
 *
 * @author Florian Specker
 */
public interface SensorEventDAO {
    public void insert(SensorEvent sensorEvent);
    public SensorEvent findById(int id);
}
