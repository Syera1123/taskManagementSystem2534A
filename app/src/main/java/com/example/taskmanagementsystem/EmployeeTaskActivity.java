package com.example.taskmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagementsystem.adapter.ListAdapter;
import com.example.taskmanagementsystem.model.TaskList;
import com.example.taskmanagementsystem.model.User;
import com.example.taskmanagementsystem.remote.ApiUtils;
import com.example.taskmanagementsystem.remote.TaskService;
import com.example.taskmanagementsystem.sharedpref.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeTaskActivity extends AppCompatActivity {

    private TaskService taskService;
    private RecyclerView rvTaskList;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get reference to the RecyclerView bookList
        rvTaskList = findViewById(R.id.rvTaskList);

        FloatingActionButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {

            new androidx.appcompat.app.AlertDialog.Builder(EmployeeTaskActivity.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        clearSessionAndRedirect();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        //register for context menu
        registerForContextMenu(rvTaskList);

        // get user info from SharedPreferences to get token value
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // get book service instance
        taskService = ApiUtils.getTaskService();

        // execute the call. send the user token when sending the query
        taskService.getAllTask(token).enqueue(new Callback<List<TaskList>>() {
            @Override
            public void onResponse(Call<List<TaskList>> call, Response<List<TaskList>> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // Get list of ALL tasks from server
                    List<TaskList> tasks = response.body();

                    // Create a new list to hold ONLY this user's tasks
                    java.util.ArrayList<TaskList> myTasks = new java.util.ArrayList<>();

                    // Get current user's name for comparison
                    String currentUsername = user.getUsername();

                    // For Loop to filter tasks
                    if (tasks != null) {
                        for (int i = 0; i < tasks.size(); i++) {
                            TaskList task = tasks.get(i);

                            // Check if task exists and matches the user
                            if (task.getAssigned_to() != null &&
                                    task.getAssigned_to().equalsIgnoreCase(currentUsername)) {
                                myTasks.add(task);
                            }
                        }
                    }

                    // initialize adapter
                    adapter = new ListAdapter(getApplicationContext(), myTasks);

                    // set adapter to the RecyclerView
                    rvTaskList.setAdapter(adapter);

                    // set layout to recycler view
                    rvTaskList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    // add separator between item in the list
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvTaskList.getContext(),
                            DividerItemDecoration.VERTICAL);
                    rvTaskList.addItemDecoration(dividerItemDecoration);
                }
                else if (response.code() == 401) {
                    // invalid token, ask user to relogin
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    // server return other error
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<TaskList>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
            }
        });
    }

    public void clearSessionAndRedirect() {

        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        TaskList selectedTask = adapter.getSelectedItem();
        Log.d("MyApp", "selected "+selectedTask.toString());    // debug purpose

        if (item.getItemId() == R.id.menu_details) {    // user clicked details contextual menu
            doViewDetails(selectedTask);
        }

        return super.onContextItemSelected(item);
    }

    private void doViewDetails(TaskList selectedTask) {
        Log.d("MyApp:", "viewing details: " + selectedTask.toString());
        // forward user to BookDetailsActivity, passing the selected book id
        Intent intent = new Intent(getApplicationContext(), TaskDetailsActivity.class);
        intent.putExtra("task_id", selectedTask.getId());
        startActivity(intent);
    }


}
