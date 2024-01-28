package com.example.har;

public class SensorData {
    private String device;
    private int type;
    private float x;
    private float y;
    private float z;
    private long timestamp;
    public SensorData(String device, int type, float x, float y, float z) {
        this.device = device;
        this.timestamp = System.currentTimeMillis();
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "SensorData{" +
                "device='" + device + '\'' +
                ", type=" + type +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", timestamp=" + timestamp +
                '}';
    }
}
