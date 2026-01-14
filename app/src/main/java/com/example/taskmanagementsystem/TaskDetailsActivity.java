package com.example.taskmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.taskmanagementsystem.model.TaskList;
import com.example.taskmanagementsystem.model.User;
import com.example.taskmanagementsystem.remote.ApiUtils;
import com.example.taskmanagementsystem.remote.TaskService;
import com.example.taskmanagementsystem.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDetailsActivity extends AppCompatActivity {

    private TaskService taskService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // retrieve book details based on selected id

        // get book id sent by BookListActivity, -1 if not found
        Intent intent = getIntent();
        int taskId = intent.getIntExtra("task_id", -1);

        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // get book service instance
        taskService = ApiUtils.getTaskService();

        // execute the API query. send the token and book id
        taskService.getTask(token, taskId).enqueue(new Callback<TaskList>() {

            @Override
            public void onResponse(Call<TaskList> call, Response<TaskList> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // server return success

                    // get book object from response
                    TaskList task = response.body();

                    // get references to the view elements
                    TextView tvTitle = findViewById(R.id.tvTitle);
                    TextView tvDesc = findViewById(R.id.tvDescription);
                    TextView tvCreatedBy = findViewById(R.id.tvCreatedBy);
                    TextView tvAssignedTo = findViewById(R.id.tvAssignTo);
                    TextView tvStatus = findViewById(R.id.tvStatus);
                    TextView tvCreatedDate = findViewById(R.id.tvCreatedDate);
                    TextView tvFinishedDate = findViewById(R.id.tvFinishedDate);

                    // set values
                    tvTitle.setText(task.getTitle());
                    tvDesc.setText(task.getDescription());
                    tvCreatedBy.setText(task.getCreated_task_by());
                    tvAssignedTo.setText(task.getAssigned_to());
                    tvStatus.setText(task.getStatus());
                    tvCreatedDate.setText(task.getCreate_date());
                    tvFinishedDate.setText(task.getFinish_date());

                }
                else if (response.code() == 401) {
                    // unauthorized error. invalid token, ask user to relogin
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    // server return other error
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<TaskList> call, Throwable t) {
                Toast.makeText(null, "Error connecting", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void clearSessionAndRedirect() {
        // clear the shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        // terminate this activity
        finish();

        // forward to Login Page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }
}