package com.zuehlke.carrera.bot.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * A SpeedControl contains the power value which controls the
 * velocity of a relay-car.
 * <p/>
 * Created by wgiersche on 06/09/14.
 */
public class SpeedControl implements Serializable {

    private String teamId;
    private String accessCode;
    private double power;
    private long timeStamp;

    /**
     * Empty Constructor for Serialisation
     */
    protected SpeedControl() {
    }


    /**
     * Creates a new SpeedControl with the given power
     *
     * @param power      Power value in range [0-250]
     * @param teamId
     * @param accessCode
     */
    public SpeedControl(double power, String teamId, String accessCode, long timestamp) {

        this.power = power;
        this.accessCode = accessCode;
        this.teamId = teamId;
        this.timeStamp = timestamp;
    }

    /**
     * Get the power
     *
     * @return
     */
    @JsonProperty("power")
    public double getPower() {
        return power;
    }

    @JsonProperty("teamId")
    public String getTeamId() {
        return teamId;
    }

    @JsonProperty("accessCode")
    public String getAccessCode() {
        return accessCode;
    }

    @Override
    public String toString() {
        return getPower() + "";
    }

    @JsonProperty("timeStamp")
    public long getTimeStamp() {
        return timeStamp;
    }

}
