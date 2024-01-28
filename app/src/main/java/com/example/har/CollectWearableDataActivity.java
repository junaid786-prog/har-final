package com.example.har;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CollectWearableDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_wearable_data);

        Button btnBecomeServer = findViewById(R.id.btnBecomeServer);
        Button btnBecomeWearable = findViewById(R.id.btnBecomeWearable);

        btnBecomeServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to BecomeBluetoothServerActivity
                Intent intent = new Intent(CollectWearableDataActivity.this, BecomeBluetoothServerActivity.class);
                startActivity(intent);
            }
        });

        btnBecomeWearable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to WearableSensoryDataActivity
                Intent intent = new Intent(CollectWearableDataActivity.this, WearableSensoryDataActivity.class);
                startActivity(intent);
            }
        });
    }
}
