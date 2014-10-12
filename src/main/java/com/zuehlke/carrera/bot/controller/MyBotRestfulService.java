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

import java.util.StringTokenizer;

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
/*/
    @RequestMapping(value = "test", method = RequestMethod.POST)
    @ResponseBody
    public void test(@RequestBody String data) {
        LOGGER.info("data received: " + data);
    }

    private SensorEvent parseSensorEventJson(String json) {
        if (null == json || json.isEmpty()) {
            return null;
        }
        SensorEvent sensorEvent = new SensorEvent();

        StringBuilder sb = new StringBuilder(json);
        sb.delete(0, sb.indexOf("{"));
        sb.deleteCharAt(sb.lastIndexOf("}"));

        int lastBracket = sb.lastIndexOf("[");
        if (lastBracket >= -1) {
            StringTokenizer st = new StringTokenizer(sb.substring(lastBracket, sb.lastIndexOf("]")), ",");
            float magX = Float.parseFloat(st.nextToken().trim());
            float magY = Float.parseFloat(st.nextToken().trim());
            float magZ = Float.parseFloat(st.nextToken().trim());

            int lastPos = lastBracket;
            lastBracket = sb.lastIndexOf("[", lastPos);
            st = new StringTokenizer(sb.substring(lastBracket, sb.lastIndexOf("]", lastPos)), ",");
            float gyrX = Float.parseFloat(st.nextToken().trim());
            float gyrY = Float.parseFloat(st.nextToken().trim());
            float gyrZ = Float.parseFloat(st.nextToken().trim());

            lastPos = lastBracket;
            lastBracket = sb.lastIndexOf("[", lastPos);
            st = new StringTokenizer(sb.substring(lastBracket, sb.lastIndexOf("]", lastPos)), ",");
            float accX = Float.parseFloat(st.nextToken().trim());
            float accY = Float.parseFloat(st.nextToken().trim());
            float accZ = Float.parseFloat(st.nextToken().trim());

            lastPos = sb.lastIndexOf(",", lastBracket);
            long timestamp = Long.parseLong(sb.substring(sb.lastIndexOf(":", lastPos), lastPos).trim());
        }

        st.nextToken()
        String mag = sb.sub
        return null;
    }
// */
}