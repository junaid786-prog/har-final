package com.example.har;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MobileSensorActivity extends AppCompatActivity {

    private EditText editTextUserId;
    private EditText editTextServerUrl; // Added server URL field
    private Button btnStart;
    private Button btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_sensor);

        editTextUserId = findViewById(R.id.editTextUserId);
        editTextServerUrl = findViewById(R.id.editTextServerUrl); // Added server URL field
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();System.out.println(" click" );
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });
    }

    private void startRecording() {
        String userId = editTextUserId.getText().toString().trim();
        String serverUrl = editTextServerUrl.getText().toString().trim(); // Added server URL field

        // Pass user ID and server URL to SensorDataService
        Intent serviceIntent = new Intent(this, SensorDataService.class);
        serviceIntent.putExtra("user_id", userId);
        serviceIntent.putExtra("server_url", serverUrl); // Added server URL field
        System.out.println(" record" );
        startService(serviceIntent);

        // Update UI
        btnStart.setVisibility(View.GONE);
        btnStop.setVisibility(View.VISIBLE);
    }

    private void stopRecording() {
        // Stop SensorDataService
        Intent serviceIntent = new Intent(this, SensorDataService.class);
        stopService(serviceIntent);

        // Update UI
        btnStart.setVisibility(View.VISIBLE);
        btnStop.setVisibility(View.GONE);
    }
}