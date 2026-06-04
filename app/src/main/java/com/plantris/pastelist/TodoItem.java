package com.plantris.pastelist;

public class TodoItem {

    private long id;
    private String title;
    private String description;
    private String date;
    private String time;
    private Integer reminderMinutesBefore;
    private boolean isCompleted;
    private String category;

    public TodoItem(String title, String description, String date, String time) {
        this(-1L, title, description, date, time, null, false, null);
    }

    public TodoItem(String title, String description, String date, String time, boolean isCompleted) {
        this(-1L, title, description, date, time, null, isCompleted, null);
    }

    public TodoItem(long id, String title, String description, String date, String time, Integer reminderMinutesBefore, boolean isCompleted
    ) {
        this(id, title, description, date, time, reminderMinutesBefore, isCompleted, null);
    }

    public TodoItem(long id, String title, String description, String date, String time, Integer reminderMinutesBefore, boolean isCompleted, String category
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.reminderMinutesBefore = reminderMinutesBefore;
        this.isCompleted = isCompleted;
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Integer getReminderMinutesBefore() {
        return reminderMinutesBefore;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}