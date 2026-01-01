package com.example.taskmanagementsystem;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EmployeeDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Get employee name from LoginActivity
        String empName = getIntent().getStringExtra("EMP_NAME");

        tvWelcome.setText("Welcome, " + empName);

        btnLogout.setOnClickListener(v -> finish());
    }
}
