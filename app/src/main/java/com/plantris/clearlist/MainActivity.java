package com.plantris.clearlist;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            ArrayList<TodoItem> todoList = new ArrayList<>();
            AddTask adapter = new AddTask(todoList);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);




            Button addtaskbutton = findViewById(R.id.add_task_button);
            EditText editText = findViewById(R.id.text_input_field);

            addtaskbutton.setOnClickListener(V -> {
                String text = editText.getText().toString().trim();

                if (!text.isEmpty()) {
                    todoList.add(new TodoItem(text));
                    adapter.notifyItemInserted(todoList.size() - 1);
                    editText.setText("");
                }
            });


            return insets;

        });
    }
}