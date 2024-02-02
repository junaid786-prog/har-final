package com.example.har;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.net.URL;
import java.util.Set;

public class BecomeBluetoothServerActivity extends AppCompatActivity {

    private EditText editTextServerUrl;
    private ListView listConnectedDevices;
    private Button btnConnect;
    private Button btnDisconnect;

   private BluetoothHandler bluetoothHandler;
   private SocketIO socketHandler;
   private ConnectionHandler connectionHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_bluetooth_server);

        editTextServerUrl = findViewById(R.id.editTextServerUrl);
        listConnectedDevices = findViewById(R.id.listConnectedDevices);
        btnConnect = findViewById(R.id.btnConnect);
        btnDisconnect = findViewById(R.id.btnDisconnect);

        initializeBluetooth();
        initSocketHandler();
        initConnectionHandler();

        btnConnect.setOnClickListener(view -> {
            String serverUrl = editTextServerUrl.getText().toString();
            if (!serverUrl.isEmpty() && Utility.isValidUrl(serverUrl)) {
                try {
                    URL url = new URL(serverUrl);
                    String host = url.getHost();
                    int port = url.getPort();
                    System.out.println("URL \n" + host + " " + port);
                    connectionHandler.connect(host, port);
                    //socketHandler.connect(serverUrl);
                } catch (Exception e) {
                    showToast("Error occurred: " + e.getMessage());
                }

                if(connectionHandler != null){
                    System.out.println("Not Null");
                    bluetoothHandler.startAcceptingConnection();
                }
            }
            else {
                showToast("Please enter a valid URL.");
                return;
            }
            showToast("Connection started.");
        });
        btnDisconnect.setOnClickListener(view -> {
            connectionHandler.disconnect();
            //socketHandler.stop();
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void initializeBluetooth() {
        bluetoothHandler = new BluetoothHandler(this, new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg_type) {
                switch (msg_type.what) {
                    case Constants.MESSAGE_READ:
                        Log.d("Ac", "message received");
                        String receivedMessage = (String) msg_type.obj;
                        connectionHandler.sendMessage(receivedMessage);
                        //socketHandler.sendMessage(receivedMessage);
                        //updateReceivedMessage(receivedMessage);
                        break;
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
        } else {
            // Permissions already granted, proceed with your initialization
            bluetoothHandler.startAcceptingConnection();
        }
    }

    private void initSocketHandler(){
        socketHandler = new SocketIO(this, new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg_type) {
                switch (msg_type.what) {
                    case Constants.SOCKET_CONNECTED:
                        showToast("Socket Connected Successfully");
                        btnConnect.setVisibility(View.GONE);
                        btnDisconnect.setVisibility(View.VISIBLE);
                        break;
                    case Constants.SOCKET_ERROR:
                        showToast("Error While Connecting To Server");;
                        break;
                }
            }
        });
    }
    private void initConnectionHandler(){
        connectionHandler = new ConnectionHandler(this, new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg_type) {
                switch (msg_type.what) {
                    case Constants.SOCKET_CONNECTED:
                        showToast("Socket Connected Successfully");
                        btnConnect.setVisibility(View.GONE);
                        btnDisconnect.setVisibility(View.VISIBLE);
                        break;
                    case Constants.SOCKET_DISCONNECTED:
                        showToast("Socket Disconnected Successfully");
                        btnConnect.setVisibility(View.VISIBLE);
                        btnDisconnect.setVisibility(View.GONE);
                    case Constants.SOCKET_ERROR:
                        showToast("Error While Connecting To Server");;
                        break;
                }
            }
        });
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

    private void updatePairedDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothHandler.getPairedDevices();
        System.out.println(pairedDevices);
        ArrayAdapter<String> adapter_paired_devices = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        for (BluetoothDevice device : pairedDevices) {
            adapter_paired_devices.add(device.getName() + "\n" + device.getAddress());
        }

        listConnectedDevices.setAdapter(adapter_paired_devices);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
