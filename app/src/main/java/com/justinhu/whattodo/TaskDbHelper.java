package com.justinhu.whattodo;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.provider.BaseColumns;

/**
 * Created by justinhu on 2017-02-24.
 */

public class TaskDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TaskEntry.TABLE_NAME + " (" +
                    TaskEntry._ID + " INTEGER PRIMARY KEY," +
                    TaskEntry.COLUMN_NAME_NAME + " TEXT," +
                    TaskEntry.COLUMN_NAME_CATEGORY + " TEXT," +
                    TaskEntry.COLUMN_NAME_PRIORITY + " INTEGER," +
                    TaskEntry.COLUMN_NAME_TRACKABLE + " INTEGER," +
                    TaskEntry.COLUMN_NAME_REPETITION + " INTEGER," +
                    TaskEntry.COLUMN_NAME_DEADLINE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME;
    private SQLiteDatabase mDb = null;
    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        new OpenDbTask().execute();
    }

    public SQLiteDatabase getmDb() {
        return mDb;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private void prepareDb() {
        mDb = getWritableDatabase();
    }

    public void saveTask(TaskContract newTask) {
        if (mDb == null) {
            prepareDb();
        }
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_NAME, newTask.name);
        values.put(TaskEntry.COLUMN_NAME_CATEGORY, newTask.category.name());
        values.put(TaskEntry.COLUMN_NAME_PRIORITY, newTask.priority);
        values.put(TaskEntry.COLUMN_NAME_TRACKABLE, newTask.trackable ? 1 : 0);
        values.put(TaskEntry.COLUMN_NAME_REPETITION, newTask.repetition);
        values.put(TaskEntry.COLUMN_NAME_DEADLINE, newTask.deadline);

// Insert the new row, returning the primary key value of the new row
        long newRowId = mDb.insert(TaskEntry.TABLE_NAME, null, values);


    }

    public Cursor getTasks(){
        if (mDb == null) {
            prepareDb();
        }
        String selectQuery = "SELECT  * FROM " + TaskEntry.TABLE_NAME;
        Cursor cursor = mDb.rawQuery(selectQuery, null);
        return cursor;
    }

    public static class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_PRIORITY = "priority";
        public static final String COLUMN_NAME_TRACKABLE = "trackable";
        public static final String COLUMN_NAME_REPETITION = "repetition";
        public static final String COLUMN_NAME_DEADLINE= "deadline";
    }


    private class OpenDbTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... values) {
            prepareDb();
            return null;
        }

    }
}
