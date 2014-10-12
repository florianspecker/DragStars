package com.zuehlke.carrera.bot.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A Event which gets fired when something happens on the race-track / car.
 */
public class SensorEvent implements Serializable {

    /**
     * Returns a empty sensor data
     */
    public static final SensorEvent Empty = createEmptyCarSensor();

    /**
     * Creates a new, empty Car-Sensor Event
     *
     * @return
     */
    public static SensorEvent createEmptyCarSensor() {
        return new SensorEvent(new float[3], new float[3], new float[3], 0);
    }

    private SensorEventType type;
    private long timeStamp = -1;
    private float[] acc;
    private float[] gyr;
    private float[] mag;
    private long timeStampReceived = -1;
    private double power = -1;

    /**
     * Empty constructor for serialisation
     */
    protected SensorEvent() {
    }

    /**
     * Creates a new RaceTrackEvent
     *
     * @param type      the type of this event
     * @param timeStamp Time when this event was generated
     */
    public SensorEvent(SensorEventType type, long timeStamp) {
        this.type = type;
        this.timeStamp = timeStamp;
    }

    /**
     * Creates a Sensor-Event of Car-Sensor Data
     *
     * @param acc       Acceleration
     * @param gyr       Gyro-Data
     * @param mag       Magnitude
     * @param timeStamp Time when this event was generated
     */
    public SensorEvent(float[] acc, float[] gyr, float[] mag, long timeStamp) {
        this.acc = Arrays.copyOf(acc, acc.length);
        this.gyr = Arrays.copyOf(gyr, gyr.length);
        this.mag = Arrays.copyOf(mag, mag.length);
        this.timeStamp = timeStamp;
        this.type = SensorEventType.CAR_SENSOR_DATA;
    }

    /**
     * Gets the X, Y, Z Acceleration
     *
     * @return
     */
    @JsonProperty("acc")
    public float[] getAcc() {
        return acc;
    }

    /**
     * Gets the X, Y, Z Gyro Data
     *
     * @return
     */
    @JsonProperty("gyr")
    public float[] getGyr() {
        return gyr;
    }

    /**
     * Gets the X, Y, Z Magnitude
     *
     * @return
     */
    @JsonProperty("mag")
    public float[] getMag() {
        return mag;
    }

    /**
     * Get the time-stamp when this event happened
     *
     * @return
     */
    @JsonProperty("timeStamp")
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * Get the event type
     *
     * @return
     */
    @JsonProperty("type")
    public SensorEventType getType() {
        return type;
    }

    public long getTimeStampReceived() {
        return timeStampReceived;
    }

    public void setTimeStampReceived(long timeStampReceived) {
        this.timeStampReceived = timeStampReceived;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    @Override
    public String toString() {
        return "SensorEvent{" +
                "type=" + type +
                ", timeStamp=" + timeStamp +
                ", timeStampReceived=" + timeStampReceived +
                ", power=" + power +
                ", acc=" + Arrays.toString(acc) +
                ", gyr=" + Arrays.toString(gyr) +
                ", mag=" + Arrays.toString(mag) +
                '}';
    }
}