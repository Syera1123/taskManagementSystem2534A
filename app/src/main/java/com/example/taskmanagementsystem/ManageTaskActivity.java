package com.example.taskmanagementsystem;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ManageTaskActivity extends AppCompatActivity {

    private List<Task> allTasks;
    private String[] employees = {"Azim","Ilham","Hanif","Syera","Farah","Hakim","Danial","Nadia"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_task);

        EditText etTaskTitle = findViewById(R.id.etTaskTitle);
        Spinner spEmployee = findViewById(R.id.spEmployee);
        Button btnAssign = findViewById(R.id.btnAssignTask);

        // Dummy allTasks
        allTasks = new ArrayList<>();

        // Spinner adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, employees);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEmployee.setAdapter(adapter);

        btnAssign.setOnClickListener(v -> {
            String taskTitle = etTaskTitle.getText().toString().trim();
            String assignedEmp = spEmployee.getSelectedItem().toString();

            if(taskTitle.isEmpty()) {
                Toast.makeText(this, "Enter task title", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create new task (Pending status default)
            Task task = new Task(taskTitle, "2026-01-01", "Pending", assignedEmp);
            allTasks.add(task);

            Toast.makeText(this, "Task assigned to " + assignedEmp, Toast.LENGTH_SHORT).show();
            etTaskTitle.setText("");
        });
    }
}
