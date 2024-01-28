package com.example.har;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find buttons by ID
        Button btnCollectData = findViewById(R.id.btnCollectData);
        Button btnRegisterDevice = findViewById(R.id.btnRegisterDevice);

        // Set click listeners for the buttons
        btnCollectData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RecordSensoryDataActivity.class);
                startActivity(intent);
            }
        });

        btnRegisterDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CollectWearableDataActivity.class);
                startActivity(intent);
            }
        });
    }
}
