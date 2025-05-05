package com.example.ticketgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class TicketFormActivity extends AppCompatActivity {

    private TextInputEditText editDate, editSession, editName, editBirthdate, editGates, editOpening;
    private TextInputEditText editCourt, editStartingTime, editAccess, editStair, editRow, editSeat, editCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_form);

        // Initialize form fields
        editDate = findViewById(R.id.editDate);
        editSession = findViewById(R.id.editSession);
        editName = findViewById(R.id.editName);
        editBirthdate = findViewById(R.id.editBirthdate);
        editGates = findViewById(R.id.editGates);
        editOpening = findViewById(R.id.editOpening);
        editCourt = findViewById(R.id.editCourt);
        editStartingTime = findViewById(R.id.editStartingTime);
        editAccess = findViewById(R.id.editAccess);
        editStair = findViewById(R.id.editStair);
        editRow = findViewById(R.id.editRow);
        editSeat = findViewById(R.id.editSeat);
        editCategory = findViewById(R.id.editCategory);

        Button btnGenerate = findViewById(R.id.btnGenerate);
        btnGenerate.setOnClickListener(v -> {
            Intent intent = new Intent(TicketFormActivity.this, TicketActivity.class);
            
            // Pass all form data to TicketActivity
            intent.putExtra("date", getTextOrDefault(editDate, "DIMANCHE 26 MAI 2024"));
            intent.putExtra("session", getTextOrDefault(editSession, "SESSION DE JOURNÉE"));
            intent.putExtra("name", getTextOrDefault(editName, "M Pierre Victor"));
            intent.putExtra("birthdate", getTextOrDefault(editBirthdate, "20/08/1996"));
            intent.putExtra("gates", getTextOrDefault(editGates, "1, 12, 30"));
            intent.putExtra("opening", getTextOrDefault(editOpening, "10:00"));
            intent.putExtra("court", getTextOrDefault(editCourt, "Court Suzanne-Lenglen"));
            intent.putExtra("startingTime", getTextOrDefault(editStartingTime, "10:30"));
            intent.putExtra("access", getTextOrDefault(editAccess, "L16"));
            intent.putExtra("stair", getTextOrDefault(editStair, "16"));
            intent.putExtra("row", getTextOrDefault(editRow, "12"));
            intent.putExtra("seat", getTextOrDefault(editSeat, "80"));
            intent.putExtra("category", getTextOrDefault(editCategory, "CATÉGORIE 1"));
            
            startActivity(intent);
        });
    }

    private String getTextOrDefault(TextInputEditText editText, String defaultValue) {
        String text = editText.getText() != null ? editText.getText().toString().trim() : "";
        return text.isEmpty() ? defaultValue : text;
    }
}
