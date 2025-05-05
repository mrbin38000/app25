package com.example.ticketgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStartForm = findViewById(R.id.btnStartForm);
        btnStartForm.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TicketFormActivity.class);
            startActivity(intent);
        });
    }
}