package com.example.taskmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ManagerDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

        Button btnManageTasks = findViewById(R.id.btnManageTasks);
        Button btnViewEmployees = findViewById(R.id.btnViewEmployees);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnManageTasks.setOnClickListener(v -> {
            Toast.makeText(this, "Manage Tasks clicked", Toast.LENGTH_SHORT).show();
        });

        btnViewEmployees.setOnClickListener(v -> {
            Toast.makeText(this, "View Employees clicked", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> finish());
    }
}
