package com.zuehlke.carrera.bot.controller;

import com.zuehlke.carrera.bot.dao.SensorEventDAO;
import com.zuehlke.carrera.bot.model.SensorEvent;
import com.zuehlke.carrera.bot.model.SensorEventType;
import com.zuehlke.carrera.bot.service.MyBotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * REST API of the Bot
 * Created by P. Buettiker on 10/5/14.
 */
@Controller
@RequestMapping(value = "myBot", produces = "application/json")
public class MyBotRestfulService {

    private final MyBotService myBotService;
    private final SensorEventDAO sensorEventDAO;

    @Autowired
    public MyBotRestfulService(MyBotService myBotService, SensorEventDAO sensorEventDAO){
        this.myBotService = myBotService;
        this.sensorEventDAO = sensorEventDAO;
    }

    @RequestMapping(value = "ping", method = RequestMethod.GET, produces = "text/plain")
    @ResponseBody
    public String getPing() {
        sensorEventDAO.insert(new SensorEvent(SensorEventType.CAR_SENSOR_DATA, System.currentTimeMillis()));
        return "success";
    }

    @RequestMapping(value = "start", method = RequestMethod.POST)
    @ResponseBody
    public void start() {
        myBotService.start();
    }

    @RequestMapping(value = "sensor", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public void handleSensorEvent(@RequestBody SensorEvent data) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Data:",data);
        logger.info(data.toString());
        myBotService.handleSensorEvent(data);
    }

}