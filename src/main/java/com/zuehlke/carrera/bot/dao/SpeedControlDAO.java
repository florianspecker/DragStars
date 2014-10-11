package com.zuehlke.carrera.bot.dao;

import com.zuehlke.carrera.bot.model.SpeedControl;

/**
 * Created on 11/10/14.
 * <p/>
 * Interface for the SpeedControl Data Access Object
 *
 * @author Florian Specker
 */
public interface SpeedControlDAO {

    public void insert(SpeedControl speedControl);

    public SpeedControl findById(int id);

}
