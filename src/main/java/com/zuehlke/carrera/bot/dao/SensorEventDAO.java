package com.zuehlke.carrera.bot.dao;

import com.zuehlke.carrera.bot.model.SensorEvent;

import java.util.Collection;

/**
 * Created on 11/10/14.
 * <p/>
 * Interface for the SensorEvent Data Access Object
 *
 * @author Florian Specker
 */
public interface SensorEventDAO {

    public void insert(Collection<SensorEvent> sensorEvents);

}
