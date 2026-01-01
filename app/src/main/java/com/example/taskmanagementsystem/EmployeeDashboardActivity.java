package com.example.taskmanagementsystem;

import android.content.Intent;
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
        Button btnViewTasks = findViewById(R.id.btnViewTasks);
        Button btnLogout = findViewById(R.id.btnLogout);

        String empName = getIntent().getStringExtra("EMP_NAME");
        tvWelcome.setText("Welcome, " + empName);

        btnViewTasks.setOnClickListener(v -> {
          //  Intent intent = new Intent(EmployeeDashboardActivity.this, ViewAssignedTaskActivity.class);
         //  intent.putExtra("EMP_NAME", empName);
          //  startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> finish());
    }
}
