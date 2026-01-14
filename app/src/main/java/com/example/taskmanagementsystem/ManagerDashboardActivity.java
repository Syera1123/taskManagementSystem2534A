package com.example.taskmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.taskmanagementsystem.sharedpref.SharedPrefManager;

public class ManagerDashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

        findViewById(R.id.btnManageTasks).setOnClickListener(v ->
                startActivity(new Intent(this, ManageTaskActivity.class)));

        findViewById(R.id.btnViewEmployees).setOnClickListener(v ->
                startActivity(new Intent(this, ViewEmployeesActivity.class)));

        findViewById(R.id.btnReportTask).setOnClickListener(v ->
                startActivity(new Intent(this, ReportActivity.class)));

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            new SharedPrefManager(this).logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}