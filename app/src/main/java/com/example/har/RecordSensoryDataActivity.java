package com.example.har;

import android.app.Dialog;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class RecordSensoryDataActivity extends AppCompatActivity {

    private Button btnConnect, btnStart, btnStop, btnChooseSensors;
    private Dialog chooseSensorsDialog;
    private SocketIO socketHandler;
    private ConnectionHandler connectionHandler;
    private MySensorManager sensorManager;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_sensory_data);

        btnConnect = findViewById(R.id.btnConnect);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnChooseSensors = findViewById(R.id.btnChooseSensors);
        connectionHandler = new ConnectionHandler(this, new Handler());

        initSocketHandler();
        sensorManager = new MySensorManager(this);

        EditText editDeviceId = findViewById(R.id.editDeviceId);
        EditText editServerUrl = findViewById(R.id.editServerUrl);

        setupDialog();

        btnConnect.setOnClickListener(view -> {
            deviceId = editDeviceId.getText().toString();
            String serverUrl = editServerUrl.getText().toString();

            if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(serverUrl)) {
                showToast("Please enter Device ID and Server URL");
            } else {
                if (true){
                    try {
                        String url[] = serverUrl.split(":");
                        String ip = url[0];
                        int port = Integer.parseInt(url[1]);
                       // socketHandler.connect(serverUrl);
                        connectionHandler.connect(ip, port);
                    } catch (Exception e) {
                        System.out.println(e);
                        showToast("Error occurred: " + e.getMessage());
                    }
                } else {
                    showToast("Not Valid Url");
                }
            }
        });


        btnStart.setOnClickListener(view -> {
            showToast("Start");
            sensorManager.registerSensors(socketHandler, deviceId);
            btnStart.setVisibility(View.GONE);
            btnStop.setVisibility(View.VISIBLE);
        });

        btnStop.setOnClickListener(view -> {
            showToast("Stop");
            sensorManager.unregisterSensors();
            socketHandler.stop();
            btnStart.setVisibility(View.VISIBLE);
            btnStop.setVisibility(View.GONE);
        });

        btnChooseSensors.setOnClickListener(view -> {
            populateSensorList();
            chooseSensorsDialog.show();
        });
    }

    private void setupDialog() {
        chooseSensorsDialog = new Dialog(this);
        chooseSensorsDialog.setContentView(R.layout.dialog_choose_sensors);
        chooseSensorsDialog.setCancelable(true);

        Button btnSelectSensors = chooseSensorsDialog.findViewById(R.id.btnSelectSensors);

        btnSelectSensors.setOnClickListener(view -> {
            handleSelectedSensors();
            chooseSensorsDialog.dismiss();
        });
    }

    private void populateSensorList() {
        LinearLayout sensorContainer = chooseSensorsDialog.findViewById(R.id.sensorContainer);
        sensorContainer.removeAllViews(); // Clear previous sensor views

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        for (Sensor sensor : sensorList) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(sensor.getName());
            sensorContainer.addView(checkBox);
        }
    }

    private void handleSelectedSensors() {
        LinearLayout sensorContainer = chooseSensorsDialog.findViewById(R.id.sensorContainer);

        StringBuilder selectedSensors = new StringBuilder("Selected Sensors: ");
        for (int i = 0; i < sensorContainer.getChildCount(); i++) {
            View childView = sensorContainer.getChildAt(i);
            if (childView instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) childView;
                if (checkBox.isChecked()) {
                    selectedSensors.append(checkBox.getText()).append(", ");
                }
            }
        }

        showToast(selectedSensors.toString());
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void initSocketHandler(){
        socketHandler = new SocketIO(this, new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg_type) {
                switch (msg_type.what) {
                    case Constants.SOCKET_CONNECTED:
                        showToast("Socket Connected Successfully");
                        btnConnect.setVisibility(View.GONE);
                        btnStart.setVisibility(View.VISIBLE);
                        btnStop.setVisibility(View.GONE);
                        break;
                    case Constants.SOCKET_ERROR:
                        showToast("Error While Connecting To Server");;
                        break;
                }
            }
        });
    }
}
