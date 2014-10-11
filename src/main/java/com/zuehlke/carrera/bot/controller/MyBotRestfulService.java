package com.zuehlke.carrera.bot.controller;

import com.zuehlke.carrera.bot.model.SensorEvent;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(MyBotRestfulService.class);

    private final MyBotService myBotService;

    @Autowired
    public MyBotRestfulService(MyBotService myBotService) {
        this.myBotService = myBotService;
    }

    @RequestMapping(value = "ping", method = RequestMethod.GET, produces = "text/plain")
    @ResponseBody
    public String getPing() {
        return "success";
    }

    @RequestMapping(value = "start", method = RequestMethod.POST)
    @ResponseBody
    public double start() {
        LOGGER.info("Start command received");
        return myBotService.start();
    }

    @RequestMapping(value = "sensor", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public double handleSensorEvent(@RequestBody SensorEvent data) {
        long timeStampReceived = System.currentTimeMillis();
        data.setTimeStampReceived(timeStampReceived);
        double power = myBotService.handleSensorEvent(data);
        LOGGER.info("Data received for handleSensorEvent() (" + (System.currentTimeMillis() - timeStampReceived) + "ms): " + data.toString());
        return power;
    }

    @RequestMapping(value = "store", method = RequestMethod.GET, produces = "text/plain")
    @ResponseBody
    public String storeSensorEvents() {
        LOGGER.info("Storing data to DB");
        myBotService.storeSensorEvents();
        return "success";
    }

}