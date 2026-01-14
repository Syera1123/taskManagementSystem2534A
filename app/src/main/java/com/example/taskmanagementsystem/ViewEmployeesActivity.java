package com.example.taskmanagementsystem;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmanagementsystem.adapter.EmployeeAdapter;
import com.example.taskmanagementsystem.model.User;
import com.example.taskmanagementsystem.remote.ApiUtils;
import com.example.taskmanagementsystem.remote.TaskService;
import com.example.taskmanagementsystem.sharedpref.SharedPrefManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewEmployeesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_employees);

        RecyclerView rv = findViewById(R.id.rvEmployees);
        rv.setLayoutManager(new LinearLayoutManager(this));

        String token = new SharedPrefManager(this).getUser().getToken();
        TaskService service = ApiUtils.getTaskService();

        service.getAllUsers(token).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    rv.setAdapter(new EmployeeAdapter(response.body()));
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(ViewEmployeesActivity.this, "Error Fetching", Toast.LENGTH_SHORT).show();
            }
        });
    }
}