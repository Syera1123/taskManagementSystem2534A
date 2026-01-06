package com.example.taskmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmanagementsystem.model.User;
import com.example.taskmanagementsystem.model.FailLogin;
import com.example.taskmanagementsystem.remote.ApiUtils;
import com.example.taskmanagementsystem.remote.UserService;
import com.example.taskmanagementsystem.sharedpref.SharedPrefManager;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private ProgressBar progressBar;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.etUsername);
        edtPassword = findViewById(R.id.etPassword);
        progressBar = findViewById(R.id.progressBar);
        btnLogin = findViewById(R.id.btnLogin);

        progressBar.setVisibility(View.GONE);

        btnLogin.setOnClickListener(v -> loginClicked());
    }

    private void loginClicked() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (!validateLogin(username, password)) return;

        doLogin(username, password);
    }

    private boolean validateLogin(String username, String password) {
        if (username.isEmpty()) {
            showToast("Username or Email is required");
            return false;
        }
        if (password.isEmpty()) {
            showToast("Password is required");
            return false;
        }
        return true;
    }

    private void doLogin(String username, String password) {
        UserService userService = ApiUtils.getUserService();
        Call<User> call;

        if (username.contains("@")) {
            // login menggunakan email
            call = userService.loginEmail(username, password);
        } else {
            // login menggunakan username
            call = userService.loginUsername(username, password);
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().getToken() != null) {
                    User user = response.body();
                    showToast("Login successful");

                    // simpan session
                    SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
                    spm.storeUser(user);

                    // redirect berdasarkan role
                    if ("manager".equalsIgnoreCase(user.getRole())) {
                        startActivity(new Intent(LoginActivity.this, ManagerDashboardActivity.class));
                    } else {
                        startActivity(new Intent(LoginActivity.this, EmployeeTaskActivity.class)
                                .putExtra("EMP_NAME", user.getUsername()));
                    }
                    finish();

                } else {
                    try {
                        String errorResp = response.errorBody().string();
                        FailLogin e = new Gson().fromJson(errorResp, FailLogin.class);
                        showToast(e.getError().getMessage());
                    } catch (Exception e) {
                        showToast("Login failed");
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                showToast("Server error: " + t.getMessage());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}












