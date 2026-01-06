package com.example.taskmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmanagementsystem.sharedpref.SharedPrefManager;

public class EmployeeDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        //Button btnViewTasks = findViewById(R.id.btnViewTasks);
        Button btnLogout = findViewById(R.id.btnLogout);

        String empName = getIntent().getStringExtra("EMP_NAME");
        tvWelcome.setText("Welcome, " + empName);

        //btnViewTasks.setOnClickListener(v -> {
            // Intent ke ViewAssignedTaskActivity boleh ditambah di sini
        //});

        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        // Clear session
        new SharedPrefManager(getApplicationContext()).logout();

        // Redirect ke LoginActivity & clear activity stack
        Intent intent = new Intent(EmployeeDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
