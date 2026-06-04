package com.plantris.pastelist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class SearchTasks extends AppCompatActivity {

    private TextInputEditText searchTaskInput;
    private TodoAdapter adapter;
    private ArrayList<TodoItem> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_tasks);

        searchTaskInput = findViewById(R.id.searchTaskInput);
        RecyclerView searchResultsView = findViewById(R.id.searchResultsView);

        searchResults = new ArrayList<>();
        adapter = new TodoAdapter(
                searchResults,
                (changedItem, isCompleted, position) -> {
                    try (DatabaseInsert dbHelper = new DatabaseInsert(SearchTasks.this)) {
                        dbHelper.updateCompleted(changedItem.getId(), isCompleted);
                    }
                    searchResults.remove(position);
                    adapter.notifyItemRemoved(position);
                },
                (item, position) -> EditTask.show(SearchTasks.this, item, new EditTask.OnTaskActionListener() {
                    @Override
                    public void onDuplicateRequested(@NonNull TodoItem sourceItem) {
                        try (DatabaseInsert dbHelper = new DatabaseInsert(SearchTasks.this)) {
                            dbHelper.insertEntry(
                                    sourceItem.getTitle(),
                                    sourceItem.getDescription(),
                                    sourceItem.getDate(),
                                    sourceItem.getTime(),
                                    sourceItem.isCompleted(),
                                    sourceItem.getReminderMinutesBefore()
                            );
                        }
                        performSearch(getSearchText());
                    }

                    @Override
                    public void onDeleteConfirmed(@NonNull TodoItem sourceItem) {
                        try (DatabaseInsert dbHelper = new DatabaseInsert(SearchTasks.this)) {
                            dbHelper.deleteEntry(sourceItem.getId());
                        }
                        performSearch(getSearchText());
                    }

                    @Override
                    public void onSaveRequested(@NonNull TodoItem sourceItem) {
                        try (DatabaseInsert dbHelper = new DatabaseInsert(SearchTasks.this)) {
                            dbHelper.updateEntry(
                                    sourceItem.getId(),
                                    sourceItem.getTitle(),
                                    sourceItem.getDescription(),
                                    sourceItem.getDate(),
                                    sourceItem.getTime(),
                                    sourceItem.getReminderMinutesBefore(),
                                    sourceItem.getCategory()
                            );
                        }
                        performSearch(getSearchText());
                    }
                }),
                null
        );

        searchResultsView.setLayoutManager(new LinearLayoutManager(this));
        searchResultsView.setAdapter(adapter);

        searchTaskInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s == null ? "" : s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void performSearch(String query) {
        searchResults.clear();

        try (DatabaseInsert dbHelper = new DatabaseInsert(this)) {
            ArrayList<TodoItem> allTasks = dbHelper.readAllEntries();
            String lowerQuery = query == null ? "" : query.trim().toLowerCase();

            for (TodoItem task : allTasks) {
                String title = task.getTitle() == null ? "" : task.getTitle().toLowerCase();
                String category = task.getCategory() == null ? "" : task.getCategory().toLowerCase();

                if (title.contains(lowerQuery) || category.contains(lowerQuery)) {
                    searchResults.add(task);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private String getSearchText() {
        Editable text = searchTaskInput.getText();
        return text == null ? "" : text.toString();
    }
}
