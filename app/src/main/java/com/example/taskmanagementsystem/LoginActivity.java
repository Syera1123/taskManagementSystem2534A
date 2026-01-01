package com.example.taskmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    String[] employees = {
            "Ali", "Aisyah", "Amin", "Siti",
            "Farah", "Hakim", "Danial", "Nadia"
    };
    String manager = "Manager";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etUsername = findViewById(R.id.etUsername);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();

            boolean isEmployee = false;
            for (String emp : employees) {
                if (emp.equalsIgnoreCase(username)) {
                    isEmployee = true;
                    break;
                }
            }

            if (isEmployee) {
                Intent intent = new Intent(LoginActivity.this, EmployeeDashboardActivity.class);
                intent.putExtra("EMP_NAME", username);
                startActivity(intent);
            }
            else if (username.equalsIgnoreCase(manager)) {
                Intent intent = new Intent(LoginActivity.this, ManagerDashboardActivity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(this, "Invalid login", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
