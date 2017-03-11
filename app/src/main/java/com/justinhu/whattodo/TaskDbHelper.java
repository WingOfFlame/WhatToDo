package com.justinhu.whattodo;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by justinhu on 2017-02-24.
 *
 * http://www.androiddesignpatterns.com/2012/05/correctly-managing-your-sqlite-database.html
 */

class TaskDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "TaskDbHelper";
    private static TaskDbHelper sInstance;
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "Task.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TaskEntry.TABLE_NAME + " (" +
                    TaskEntry._ID + " INTEGER PRIMARY KEY," +
                    TaskEntry.COLUMN_NAME_NAME + " TEXT," +
                    TaskEntry.COLUMN_NAME_CATEGORY + " TEXT," +
                    TaskEntry.COLUMN_NAME_PRIORITY + " INTEGER," +
                    TaskEntry.COLUMN_NAME_TRACKABLE + " INTEGER," +
                    TaskEntry.COLUMN_NAME_COUNTDOWN + " INTEGER," +
                    TaskEntry.COLUMN_NAME_COUNTUP + " INTEGER," +
                    TaskEntry.COLUMN_NAME_DEADLINE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME;

    static synchronized TaskDbHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new TaskDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        new OpenDbTask().execute();
    }


    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate");
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        Log.i(TAG, "onUpgrade");
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    void saveNewTask(Task newTask) {
        SQLiteDatabase mDb = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_NAME, newTask.name);
        values.put(TaskEntry.COLUMN_NAME_CATEGORY, newTask.category.name());
        values.put(TaskEntry.COLUMN_NAME_PRIORITY, newTask.priority);
        values.put(TaskEntry.COLUMN_NAME_TRACKABLE, newTask.trackable ? 1 : 0);
        values.put(TaskEntry.COLUMN_NAME_COUNTDOWN, newTask.countDown);
        values.put(TaskEntry.COLUMN_NAME_COUNTUP, newTask.countUp);
        values.put(TaskEntry.COLUMN_NAME_DEADLINE, newTask.deadline);

        if(newTask.getId() > 0){
            mDb.update(TaskEntry.TABLE_NAME, values,"_id = ? ", new String[] { Integer.toString(newTask.getId()) } );
        }else{
            mDb.insert(TaskEntry.TABLE_NAME, null, values);
        }

    }

    Cursor getTasksCursor(){//TODO:create test for this

        SQLiteDatabase mDb = getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TaskEntry.TABLE_NAME;
        Cursor cursor = mDb.rawQuery(selectQuery, null);

        Log.i(TAG,"Return task cursor: "+cursor.toString());

        return cursor;
    }

    void deleteTask(int id) {
        SQLiteDatabase mDb = getWritableDatabase();
        mDb.delete(TaskEntry.TABLE_NAME, "_id = ?", new String[] { Integer.toString(id)});
    }

    void deleteMultiTask(ArrayList<String> toDelete) {
        String args = TextUtils.join(",", toDelete);
        SQLiteDatabase mDb = getWritableDatabase();
        mDb.delete(TaskEntry.TABLE_NAME, "_id IN (" + args + ")", null);
    }

    private static class TaskEntry implements BaseColumns {
        static final String TABLE_NAME = "task";
        static final String COLUMN_NAME_NAME = "name";
        static final String COLUMN_NAME_CATEGORY = "category";
        static final String COLUMN_NAME_PRIORITY = "priority";
        static final String COLUMN_NAME_TRACKABLE = "trackable";
        static final String COLUMN_NAME_COUNTDOWN = "countDown";
        static final String COLUMN_NAME_COUNTUP = "countUp";
        static final String COLUMN_NAME_DEADLINE= "deadline";

    }


    private class OpenDbTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... values) {
            getWritableDatabase();
            return null;
        }

    }
}
