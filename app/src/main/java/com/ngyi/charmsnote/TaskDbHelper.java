package com.ngyi.charmsnote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TaskDbHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "tasks_db";

    public static String strSeparator = "__,__";


    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create tasks table
        db.execSQL(Task.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Task.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertTask(String task, int color, ArrayList<String> subtasks, ArrayList<String> status) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        String concatTasks = convertArrayToString(subtasks);
        String concatStatus = convertArrayToString(status);

        values.put(Task.COLUMN_TASK, task);
        values.put(Task.COLUMN_COLOR, color);
        values.put(Task.COLUMN_SUBTASKS, concatTasks);
        values.put(Task.COLUMN_STATUS, concatStatus);

        // insert row
        long id = db.insert(Task.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Task getTask(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Task.TABLE_NAME,
                new String[]{Task.COLUMN_ID, Task.COLUMN_TASK, Task.COLUMN_COLOR, Task.COLUMN_SUBTASKS, Task.COLUMN_STATUS},
                Task.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare task object
        Task task = new Task(
                cursor.getInt(cursor.getColumnIndex(Task.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Task.COLUMN_TASK)),
                cursor.getInt(cursor.getColumnIndex(Task.COLUMN_COLOR)),
                cursor.getString(cursor.getColumnIndex(Task.COLUMN_SUBTASKS)),
                cursor.getString(cursor.getColumnIndex(Task.COLUMN_STATUS))
        );

        // close the db connection
        cursor.close();

        return task;
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Task.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getInt(cursor.getColumnIndex(Task.COLUMN_ID)));
                task.setTask(cursor.getString(cursor.getColumnIndex(Task.COLUMN_TASK)));
                task.setColor(cursor.getInt(cursor.getColumnIndex(Task.COLUMN_COLOR)));
                task.setSubtasks(cursor.getString(cursor.getColumnIndex(Task.COLUMN_SUBTASKS)));
                task.setStatus(cursor.getString(cursor.getColumnIndex(Task.COLUMN_STATUS)));

                tasks.add(task);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return tasks list
        return tasks;
    }

    public int getTasksCount() {
        String countQuery = "SELECT  * FROM " + Task.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Task.COLUMN_TASK, task.getTask());
        values.put(Task.COLUMN_COLOR, task.getColor());
        values.put(Task.COLUMN_SUBTASKS, task.getSubtasks());
        values.put(Task.COLUMN_STATUS, task.getStatus());


        // updating row
        return db.update(Task.TABLE_NAME, values, Task.COLUMN_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
    }


    public void deleteTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Task.TABLE_NAME, Task.COLUMN_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
        db.close();
    }


    public static String convertArrayToString(ArrayList<String> array){
        String str = "";
        for (int i = 0;i<array.size(); i++) {
            str = str+array.get(i);
            // Do not append comma at the end of last element
            if(i<array.size()-1){
                str = str+strSeparator;
            }
        }
        return str;
    }


    public static ArrayList<String> convertStringToArray(String str){
        ArrayList<String> arr = new ArrayList<String>(Arrays.asList(str.split(strSeparator)));
        return arr;
    }
}
