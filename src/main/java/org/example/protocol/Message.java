package org.example.protocol;

public class Message {
    static final int SIZE = 8;
    private final SensorType type;
    private final float measurement;

    public Message(SensorType type, float measurement) {
        this.type = type;
        this.measurement = measurement;
    }

    public SensorType getType() {
        return this.type;
    }

    public float getMeasurement() {
        return this.measurement;
    }
}
