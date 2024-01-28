package com.example.har;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WearableSensoryDataActivity extends AppCompatActivity {

    private ListView listPairedDevices;
    private EditText editTextDeviceName;
    private Button btnStart;
    private Button btnStop;

    private BluetoothHandler bluetoothHandler;
    private MySensorManager sensorManager;
    private List<BluetoothDevice> pairedDeviceList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wearable_sensory_data);

        listPairedDevices = findViewById(R.id.listPairedDevices);
        editTextDeviceName = findViewById(R.id.editTextDeviceName);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);

        sensorManager = new MySensorManager(this);

        initializeBluetooth();
        initClicks();

        btnStart.setOnClickListener(view -> startSensoryData());

        btnStop.setOnClickListener(view -> stopSensoryData());
    }


    private void startSensoryData() {
        String deviceName = editTextDeviceName.getText().toString();

        if (deviceName.isEmpty()) {
            showToast("Please enter a device name.");
            return;
        }

        BluetoothHandler.ConnectedThread connectedThread = bluetoothHandler.getConnectedThread();

        if(connectedThread != null){
            sensorManager.registerSensors(connectedThread, deviceName);
            showToast("Sensory data collection started for " + deviceName);
            // Show the stop button
            btnStart.setVisibility(View.GONE);
            btnStop.setVisibility(View.VISIBLE);
        }
        else {
            showToast("First connect to bluetooth server socket");
        }
    }

    private void stopSensoryData() {
        sensorManager.unregisterSensors();
        showToast("Sensory data collection stopped.");
        btnStop.setVisibility(View.GONE);
        btnStart.setVisibility(View.VISIBLE);
    }

    private void initClicks(){
        listPairedDevices.setOnItemClickListener((adapterView, view, position, id) -> {
            Set<BluetoothDevice> pairedDevices = bluetoothHandler.getPairedDevices();
            Object[] objects = pairedDevices.toArray();
            BluetoothDevice device = (BluetoothDevice) objects[position];
            bluetoothHandler.connectToDevice(device);
            showToast("Connecting to: " + device.getName());
        });
    }
    private void initializeBluetooth() {
        bluetoothHandler = new BluetoothHandler(this, new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg_type) {
                switch (msg_type.what) {
                    case Constants.CONNECTED:
                        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.CONNECTING:
                        Toast.makeText(getApplicationContext(), "Connecting...", Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.NO_SOCKET_FOUND:
                        Toast.makeText(getApplicationContext(), "No socket found", Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.PAIRED_DEVICES:
                        updatePairedDevices();
                        break;
                    case Constants.SHOW_LOG:
                        Toast.makeText(getApplicationContext(), msg_type.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        // Check and request Bluetooth permissions
        if (!bluetoothHandler.checkBluetoothPermissions()) {
            bluetoothHandler.requestBluetoothPermissions();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth has been successfully enabled
                bluetoothHandler.checkAndEnableBluetooth();
            } else {
                Toast.makeText(this, "Bluetooth is required for this application. Exiting.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updatePairedDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothHandler.getPairedDevices();
        System.out.println(pairedDevices);
        ArrayAdapter<String> adapter_paired_devices = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        for (BluetoothDevice device : pairedDevices) {
            adapter_paired_devices.add(device.getName() + "\n" + device.getAddress());
        }

        listPairedDevices.setAdapter(adapter_paired_devices);
    }
}
