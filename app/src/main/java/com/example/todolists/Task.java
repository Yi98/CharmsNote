package com.example.todolists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Task {
    public static final String TABLE_NAME = "tasks";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TASK = "task";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_SUBTASKS = "subtasks";
    public static final String COLUMN_STATUS = "status";

    private int id;
    private String task;
    private int color;
    private String subtasks;
    private String status;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TASK + " TEXT,"
                    + COLUMN_COLOR + " INTEGER,"
                    + COLUMN_SUBTASKS + " TEXT,"
                    + COLUMN_STATUS + " TEXT"
                    + ")";

    public Task() {
    }

    public Task(int id, String task, int color, String subtasks, String status) {
        this.id = id;
        this.task = task;
        this.color = color;
        this.subtasks = subtasks;
        this.status = status;
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

    public String getSubtasks() {
        return subtasks;
    }

    public String getStatus() {
        return status;
    }


    public void setSubtasks(String subtasks) {
        this.subtasks = subtasks;
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

    public void setStatus(String status) {
        this.status = status;
    }


}
