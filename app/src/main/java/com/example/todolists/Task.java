package com.example.todolists;

public class Task {
    public static final String TABLE_NAME = "tasks";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TASK = "task";
    public static final String COLUMN_COLOR = "color";

    private int id;
    private String task;
    private int color;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TASK + " TEXT,"
                    + COLUMN_COLOR + " INTEGER"
                    + ")";

    public Task() {
    }

    public Task(int id, String task, int color) {
        this.id = id;
        this.task = task;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public String getNote() {
        return task;
    }

    public int getColor() {
        return color;
    }

    public void setNote(String task) {
        this.task = task;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
