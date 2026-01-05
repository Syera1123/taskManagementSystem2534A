package com.example.taskmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmanagementsystem.sharedpref.SharedPrefManager;

public class ManagerDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

        Button btnManageTasks = findViewById(R.id.btnManageTasks);
        Button btnViewEmployees = findViewById(R.id.btnViewEmployees);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnManageTasks.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerDashboardActivity.this, ManageTaskActivity.class);
            startActivity(intent);
        });

        btnViewEmployees.setOnClickListener(v ->
                Toast.makeText(this, "View Employees clicked", Toast.LENGTH_SHORT).show()
        );

        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        // Clear session
        new SharedPrefManager(getApplicationContext()).logout();

        // Redirect to LoginActivity & clear stack
        Intent intent = new Intent(ManagerDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
