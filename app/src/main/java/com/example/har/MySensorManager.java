package com.example.har;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.google.gson.Gson;

public class MySensorManager implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor gravity;
    private String deviceName;
    private IMessageDelegator messageDelegator;

    public MySensorManager(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }

    public void registerSensors(IMessageDelegator messageDelegator, String deviceName) {
        this.messageDelegator = messageDelegator;
        this.deviceName = deviceName;
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL);

        System.out.println("Registered: " + deviceName);
    }

    // Unregister sensor listeners
    public void unregisterSensors() {
        System.out.println("Un Registered: " + deviceName);
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensor_type = event.sensor.getType();

        float[] values = event.values;
        SensorData data = new SensorData(deviceName, sensor_type, values[0], values[1], values[2]);
        String jsonData = new Gson().toJson(data);
        messageDelegator.sendMessage(jsonData);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        System.out.println("Accuracy changed: " + accuracy);
    }
}
