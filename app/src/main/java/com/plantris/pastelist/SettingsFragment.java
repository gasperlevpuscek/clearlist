package com.plantris.pastelist;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        super(R.layout.settings_view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText editEmail = view.findViewById(R.id.editEmail);
        TextInputEditText editPassword = view.findViewById(R.id.editPassword);
        Button buttonLogin = view.findViewById(R.id.buttonLogin);
        Button buttonSignIn = view.findViewById(R.id.buttonSignIn);
        Button buttonSync = view.findViewById(R.id.buttonSync);
        Button buttonLoad = view.findViewById(R.id.buttonLoad);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            editEmail.setText(currentUser.getEmail());
        }

        // Sign In creates the user (Registers)
        buttonSignIn.setOnClickListener(v -> registerUser(editEmail, editPassword));

        // Log In authenticates the user
        buttonLogin.setOnClickListener(v -> loginUser(editEmail, editPassword));

        // Sync tasks to DB
        buttonSync.setOnClickListener(v -> syncTasksToFirebase());

        // Load tasks from DB
        buttonLoad.setOnClickListener(v -> loadTasksFromFirebase());
    }

    private void loadTasksFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();
        DatabaseReference tasksRef = FirebaseDatabase
                .getInstance("https://pastelistdb-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users")
                .child(uid)
                .child("tasks");

        tasksRef.get().addOnSuccessListener(snapshot -> {
            try (DatabaseInsert dbHelper = new DatabaseInsert(requireContext())) {
                dbHelper.deleteAllData();

                for (com.google.firebase.database.DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    if ("debug".equals(taskSnapshot.getKey())) continue;

                    String title = taskSnapshot.child("title").getValue(String.class);
                    String description = taskSnapshot.child("description").getValue(String.class);
                    String date = taskSnapshot.child("date").getValue(String.class);
                    String time = taskSnapshot.child("time").getValue(String.class);
                    Boolean isCompletedVal = taskSnapshot.child("isCompleted").getValue(Boolean.class);
                    boolean isCompleted = isCompletedVal != null && isCompletedVal;
                    Integer reminder = taskSnapshot.child("reminderMinutesBefore").getValue(Integer.class);

                    if (title == null) continue;

                    long newTaskId = dbHelper.insertEntry(title, description, date, time, isCompleted, reminder);

                    com.google.firebase.database.DataSnapshot subtasksSnapshot = taskSnapshot.child("subtasks");
                    if (subtasksSnapshot.exists()) {
                        for (com.google.firebase.database.DataSnapshot subtaskSnap : subtasksSnapshot.getChildren()) {
                            String stTitle = subtaskSnap.child("title").getValue(String.class);
                            String stDesc = subtaskSnap.child("description").getValue(String.class);
                            Boolean stCompVal = subtaskSnap.child("isCompleted").getValue(Boolean.class);
                            boolean stComp = stCompVal != null && stCompVal;

                            if (stTitle == null) continue;

                            dbHelper.insertSubtask(newTaskId, stTitle, stDesc, stComp);
                        }
                    }
                }
                Toast.makeText(requireContext(), "Loaded from Database", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Load error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Load failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void syncTasksToFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();
        DatabaseReference tasksRef = FirebaseDatabase
                .getInstance("https://pastelistdb-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users")
                .child(uid)
                .child("tasks");

        try (DatabaseInsert dbHelper = new DatabaseInsert(requireContext())) {
            ArrayList<TodoItem> tasks = dbHelper.readAllEntries();
            Toast.makeText(requireContext(),
                    "Tasks count: " + tasks.size(),
                    Toast.LENGTH_LONG).show();
            Map<String, Object> tasksMap = new HashMap<>();

            for (TodoItem task : tasks) {
                Map<String, Object> taskData = new HashMap<>();
                taskData.put("id", task.getId());
                taskData.put("title", task.getTitle());
                taskData.put("description", task.getDescription());
                taskData.put("date", task.getDate());
                taskData.put("time", task.getTime());
                taskData.put("isCompleted", task.isCompleted());
                if (task.getReminderMinutesBefore() != null) {
                    taskData.put("reminderMinutesBefore", task.getReminderMinutesBefore());
                }

                ArrayList<SubtaskItem> subtasks = dbHelper.readSubtasksForTask(task.getId());
                if (!subtasks.isEmpty()) {
                    Map<String, Object> subtasksMap = new HashMap<>();
                    for (SubtaskItem subtask : subtasks) {
                        Map<String, Object> subtaskData = new HashMap<>();
                        subtaskData.put("id", subtask.getId());
                        subtaskData.put("title", subtask.getTitle());
                        subtaskData.put("description", subtask.getDescription());
                        subtaskData.put("isCompleted", subtask.isCompleted());
                        subtasksMap.put(String.valueOf(subtask.getId()), subtaskData);
                    }
                    taskData.put("subtasks", subtasksMap);
                }

                tasksMap.put(String.valueOf(task.getId()), taskData);
            }
            tasksRef.child("debug").setValue("working");
            tasksRef.setValue(tasksMap)
                    .addOnSuccessListener(aVoid -> Toast.makeText(requireContext(), "Synced to Database", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(requireContext(), "Sync failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error linking db: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loginUser(TextInputEditText editEmail, TextInputEditText editPassword) {
        String email = getTextValue(editEmail);
        String password = getTextValue(editPassword);

        if (email.isEmpty()) {
            editEmail.setError("Email required");
            return;
        }

        if (password.isEmpty()) {
            editPassword.setError("Password required");
            return;
        }

        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult ->
                        Toast.makeText(requireContext(), "Logged in successfully", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void registerUser(TextInputEditText editEmail, TextInputEditText editPassword) {
        String email = getTextValue(editEmail);
        String password = getTextValue(editPassword);

        if (email.isEmpty()) {
            editEmail.setError("Email required");
            return;
        }

        if (password.isEmpty()) {
            editPassword.setError("Password required");
            return;
        }

        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if (authResult.getUser() == null) {
                        Toast.makeText(requireContext(), "Auth failed: User is null", Toast.LENGTH_LONG).show();
                        return;
                    }
                    String uid = authResult.getUser().getUid();

                    DatabaseReference userRef = FirebaseDatabase
                            .getInstance("https://pastelistdb-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference("users")
                            .child(uid)
                            .child("settings");

                    userRef.child("email").setValue(email)
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(requireContext(), "Account created", Toast.LENGTH_SHORT).show()
                            )
                            .addOnFailureListener(e ->
                                    Toast.makeText(requireContext(), "Database failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Auth failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private String getTextValue(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }
}