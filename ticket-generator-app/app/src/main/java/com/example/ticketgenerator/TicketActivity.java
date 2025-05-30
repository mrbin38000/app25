package com.example.ticketgenerator;

import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.drawable.GradientDrawable;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TicketActivity extends AppCompatActivity {

    private TextView txtDate, txtSession, txtName, txtBirthdate, txtGates, txtOpening;
    private TextView txtCourt, txtStartingTime, txtAccess, txtRealTime;
    private TextView txtStair, txtRow, txtSeat, txtCategory;
    private ImageView imgQrCode;
    private View leftBorder, topBorder, rightBorder, bottomBorder;
    private FrameLayout dynamicBorderLayout;
    private final Handler handler = new Handler(Looper.getMainLooper());





    private final Runnable timeUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateCurrentTime();
            handler.postDelayed(this, 1000); // Update every second
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        // Initialize views
        initializeViews();


        // Get intent data
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Set ticket data from intent extras
            setTicketData(extras);
        }

        // Start time updates
      //  startTimeUpdates();
    }

    private void initializeViews() {
        txtDate = findViewById(R.id.txtDate);
        txtSession = findViewById(R.id.txtSession);
        txtName = findViewById(R.id.txtName);
       // txtBirthdate = findViewById(R.id.txtBirthdate);
        txtGates = findViewById(R.id.txtGates);
        txtOpening = findViewById(R.id.txtOpening);
        txtCourt = findViewById(R.id.txtCourt);
        txtStartingTime = findViewById(R.id.txtStartingTime);
        txtAccess = findViewById(R.id.txtAccess);
        // txtRealTime = findViewById(R.id.txtRealTime);
        txtStair = findViewById(R.id.txtStair);
        txtRow = findViewById(R.id.txtRow);
        txtSeat = findViewById(R.id.txtSeat);
        txtCategory = findViewById(R.id.txtCategory);
        imgQrCode = findViewById(R.id.imgQrCode);


    }

    private void setTicketData(Bundle extras) {
        txtDate.setText(extras.getString("date"));
        txtSession.setText(extras.getString("session"));
        txtName.setText(extras.getString("name"));
       // txtBirthdate.setText("Naissance: " + extras.getString("birthdate"));
        txtGates.setText(extras.getString("gates"));
        txtOpening.setText(extras.getString("opening"));
        txtCourt.setText(extras.getString("court"));
        txtStartingTime.setText("Accès au(x) court(s) pas avant " + extras.getString("startingTime"));
        txtAccess.setText("Accès " + extras.getString("access"));
        txtStair.setText(extras.getString("stair"));
        txtRow.setText(extras.getString("row"));
        txtSeat.setText(extras.getString("seat"));
        txtCategory.setText(extras.getString("category"));
    }

    private void updateCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        txtRealTime.setText(currentTime);
    }


    private void startTimeUpdates() {
        handler.post(timeUpdateRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove callbacks to prevent memory leaks
        handler.removeCallbacks(timeUpdateRunnable);
    }
}