package com.example.har;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SensorDataService extends Service implements SensorEventListener {
    private static final String serverHost = "192.168.1.106"; // Replace with your server's IP address or hostname
    private static final int serverPort = 12345;
    //private final SendSensorDataTask sendSensorDataTask = new SendSensorDataTask();
    // Container to store sensor data lines
    private List<String> sensorDataContainer = new ArrayList<>();

    private SensorManager sensorManager;
    private List<Sensor> sensorList;
    private String userId;
    public static String serverUrl;
    // Add other necessary variables

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        // Initialize other variables if needed
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        userId = intent.getStringExtra("user_id");
        serverUrl = intent.getStringExtra("server_url");
        startSensorListeners();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //sendSensorDataToServer();
        stopSensorListeners();
        super.onDestroy();
    }
    private void sendSensorDataToServer() {
        // Check if there is data in the container before sending
        if (!sensorDataContainer.isEmpty()) {
            // Execute the AsyncTask to send the accumulated sensor data
            //new SendSensorDataTask().execute(sensorDataContainer);
            // Clear the container after sending the data
            //sensorDataContainer.clear();
        }
    }
    private void startSensorListeners() {
        for (Sensor sensor : sensorList) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void stopSensorListeners() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Process sensor data and send it to the server
        // You need to implement this part based on your server communication logic
        //System.out.println(" sensor change");
        float[] values = event.values;
        String sensorType = getSensorType(event.sensor.getType());
        String sensorData = buildSensorDataString(sensorType, values);

        Log.d("SensorDataService", "Sensor Data: " + sensorData);
        // Store the sensor data line in the container
        //sensorDataContainer.add(sensorData);

        // Now, you can send this data to the server using a network library
        new SendSensorDataTask().execute(sensorData);
        //new SendSensorDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, sensorData);
        // Now, you can send this data to the server using the single AsyncTask instance
        //sendSensorDataTask.execute(sensorData);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }
    /*private void sendSensorDataToServer(String data) {
        try {
            URL url = new URL(serverUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            // Enable input/output streams
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            // Write data to the server
            DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
            outputStream.writeBytes(data);
            outputStream.flush();
            outputStream.close();

            // Get the response from the server (optional)
            int responseCode = urlConnection.getResponseCode();
            Log.d("SensorDataService", "Server Response Code: " + responseCode);

            urlConnection.disconnect();
        } catch (Exception e) {
            Log.e("SensorDataService", "Error sending data to server: " + e.getMessage());
        }
    }*/


    // Use the same port as the server
    private static class SendSensorDataTask extends AsyncTask<String, Void, Integer> {

        Socket socket=null;

        //protected Integer doInBackground(String... params) {
        @Override
        protected Integer doInBackground(String... params) {
            int responseCode = -1;
            try {
                //Static ServerHost
                //Socket socket = new Socket(serverHost, serverPort);
                //Dynamic ServerHost //static url string
                socket = new Socket(serverUrl, serverPort);


                OutputStream outputStream = socket.getOutputStream();
                //PrintWriter writer = new PrintWriter(outputStream, true);
                //writer.print(params[0]);
                Log.d("Sensor", "Sensor Data: Before BufferedWriter" );
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                writer.write(params[0]);
                writer.flush();

                Log.d("Sensor", "Sensor Data: After BufferedWriter" );

                // Iterate through the lines in the container and send each line separately
                //for (String sensorData : params[0]) {
                //    Log.d("Sensor", "Sensor Data: " + sensorData);
                //    writer.write(sensorData);
                //writer.newLine();  // Add a newline to separate lines
                //    writer.flush();
                // }
                System.out.println("Data Sent");
                /*
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeUTF(params[0]);
                outputStream.flush();
*/              writer.close();
                BufferedReader reader=(new BufferedReader(new InputStreamReader(socket.getInputStream())));
                responseCode = reader.read();
                System.out.println("Response"+responseCode);

                //socket.close();
                //System.out.println(responseCode);
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (socket != null && !socket.isClosed()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return responseCode;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            Log.d("SensorDataService", "Server Response Code: " + responseCode);
        }
    }

        /*private static void sendSensorDataToServer(String data) {
            try {
                Log.d("Senddata", "senddata ");
                        Socket socket = new Socket(serverHost, serverPort);

                // Create a DataOutputStream to send data to the server
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeUTF(data);
                outputStream.flush();

                // Create a DataInputStream to read the response from the server
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                // Read the response code from the server
                int responseCode = inputStream.readInt();

                // Log the response code
                System.out.println("Server Response Code: " + responseCode);
                // Write data to the server


                // Close the socket
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

    private String getSensorType(int sensorType) {
        String sensorName = "";

        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                sensorName = "ACCELEROMETER";
                break;
            case Sensor.TYPE_GYROSCOPE:
                sensorName = "GYROSCOPE";
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                sensorName= "Magnetometer";
                break;
            default:
                sensorName = "UNKNOWN";
                break;
        }

        // Assuming sensor names are stored as strings
        if (!sensorName.equals("UNKNOWN")) {
            return sensorName;
        }

        // If the sensor type is unknown, try to get the sensor name dynamically
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(sensorType);

        if (!sensorList.isEmpty()) {
            Sensor sensor = sensorList.get(0);
            return sensor.getName().toUpperCase(); // Assuming the sensor names are in uppercase
        } else {
            return "UNKNOWN";
        }
    }


    private String buildSensorDataString(String sensorType, float[] values) {
        // Format sensor data string as needed for the server
        // Adding user ID and timestamp at the beginning
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}