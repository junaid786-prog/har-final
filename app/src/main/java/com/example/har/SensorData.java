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

    public String buildSensorDataString(String sensorType, float[] values, String userId) {
        StringBuilder builder = new StringBuilder("Source: Smart_Phone").append(", User ID: ").append(userId).append(", Timestamp: ").append(System.currentTimeMillis()).append(", ");

        // Adding sensor type
        builder.append("Sensor Type: "+sensorType).append(": ");

        for (float value : values) {
            builder.append(value).append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        builder.append("\n");
        return builder.toString();
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
