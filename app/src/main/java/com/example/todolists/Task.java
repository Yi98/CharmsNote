package com.example.todolists;

public class Task {
    public static final String TABLE_NAME = "tasks";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TASK = "task";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_SUBTASKS = "subtasks";

    private int id;
    private String task;
    private int color;
    private String subtasks;

    public static String strSeparator = "__,__";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TASK + " TEXT,"
                    + COLUMN_COLOR + " INTEGER,"
                    + COLUMN_SUBTASKS + " TEXT"
                    + ")";

    public Task() {
    }

    public Task(int id, String task, int color, String subtasks) {
        this.id = id;
        this.task = task;
        this.color = color;
        this.subtasks = subtasks;
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

    public String getSubtasks() {
        return subtasks;
    }

    public static String convertArrayToString(String[] array){
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];
            // Do not append comma at the end of last element
            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }

    public static String[] convertStringToArray(String str){
        String[] arr = str.split(strSeparator);
        return arr;
    }


}
