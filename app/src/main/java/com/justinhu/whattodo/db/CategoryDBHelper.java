package com.justinhu.whattodo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.justinhu.whattodo.model.Category;

import java.util.List;

/**
 * Created by justinhu on 2017-03-12.
 */

public class CategoryDBHelper extends SQLiteOpenHelper {


    private static class CategoryEntry implements BaseColumns {
        static final String TABLE_NAME = "category";
        static final String COLUMN_NAME_NAME = "name";
        static final String COLUMN_NAME_COLOR = "color";
        static final String COLUMN_NAME_ICON = "icon";
        static final String COLUMN_NAME_PRIORITY = "priority";
        static final String COLUMN_NAME_DEFAULT = "somename";

    }

    private static final String TAG = "CategoryDBHelper";
    private static CategoryDBHelper sInstance;
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Category.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +
                    CategoryEntry._ID + " INTEGER PRIMARY KEY," +
                    CategoryEntry.COLUMN_NAME_NAME + " TEXT," +
                    CategoryEntry.COLUMN_NAME_COLOR + " TEXT," +
                    CategoryEntry.COLUMN_NAME_ICON + " TEXT," +
                    CategoryEntry.COLUMN_NAME_PRIORITY + " INTEGER,"+
                    CategoryEntry.COLUMN_NAME_DEFAULT + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME;


    public static synchronized CategoryDBHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new CategoryDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private CategoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate");
        db.execSQL(SQL_CREATE_ENTRIES);
        loadInDefault(db);

    }

    private void loadInDefault(SQLiteDatabase db) {
        ContentValues workCategory = new ContentValues();
        workCategory.put(CategoryEntry.COLUMN_NAME_NAME, "string_category_work");
        workCategory.put(CategoryEntry.COLUMN_NAME_COLOR, "#F44336");
        workCategory.put(CategoryEntry.COLUMN_NAME_ICON, "ic_category_work");
        workCategory.put(CategoryEntry.COLUMN_NAME_PRIORITY, 6);
        workCategory.put(CategoryEntry.COLUMN_NAME_DEFAULT, 1);
        db.insert(CategoryEntry.TABLE_NAME, null, workCategory);

        ContentValues studyCategory = new ContentValues();
        studyCategory.put(CategoryEntry.COLUMN_NAME_NAME, "string_category_study");
        studyCategory.put(CategoryEntry.COLUMN_NAME_COLOR, "#FF9800");
        studyCategory.put(CategoryEntry.COLUMN_NAME_ICON, "ic_category_study");
        studyCategory.put(CategoryEntry.COLUMN_NAME_PRIORITY, 5);
        studyCategory.put(CategoryEntry.COLUMN_NAME_DEFAULT, 1);
        db.insert(CategoryEntry.TABLE_NAME, null, studyCategory);


        ContentValues defaultCategory = new ContentValues();
        defaultCategory.put(CategoryEntry.COLUMN_NAME_NAME, "string_category_default");
        defaultCategory.put(CategoryEntry.COLUMN_NAME_COLOR, "#9E9E9E");
        defaultCategory.put(CategoryEntry.COLUMN_NAME_ICON, "ic_category_default");
        defaultCategory.put(CategoryEntry.COLUMN_NAME_PRIORITY, 4);
        defaultCategory.put(CategoryEntry.COLUMN_NAME_DEFAULT, 1);
        db.insert(CategoryEntry.TABLE_NAME, null, defaultCategory);

        ContentValues workoutCategory = new ContentValues();
        workoutCategory.put(CategoryEntry.COLUMN_NAME_NAME, "string_category_workout");
        workoutCategory.put(CategoryEntry.COLUMN_NAME_COLOR, "#009688");
        workoutCategory.put(CategoryEntry.COLUMN_NAME_ICON, "ic_category_workout");
        workoutCategory.put(CategoryEntry.COLUMN_NAME_PRIORITY, 3);
        workoutCategory.put(CategoryEntry.COLUMN_NAME_DEFAULT, 1);
        db.insert(CategoryEntry.TABLE_NAME, null, workoutCategory);

        ContentValues personalCategory = new ContentValues();
        personalCategory.put(CategoryEntry.COLUMN_NAME_NAME, "string_category_personal");
        personalCategory.put(CategoryEntry.COLUMN_NAME_COLOR, "#4CAF50");
        personalCategory.put(CategoryEntry.COLUMN_NAME_ICON, "ic_category_personal");
        personalCategory.put(CategoryEntry.COLUMN_NAME_PRIORITY, 2);
        personalCategory.put(CategoryEntry.COLUMN_NAME_DEFAULT, 1);
        db.insert(CategoryEntry.TABLE_NAME, null, personalCategory);

        ContentValues relaxCategory = new ContentValues();
        relaxCategory.put(CategoryEntry.COLUMN_NAME_NAME, "string_category_relax");
        relaxCategory.put(CategoryEntry.COLUMN_NAME_COLOR, "#8BC34A");
        relaxCategory.put(CategoryEntry.COLUMN_NAME_ICON, "ic_category_relax");
        relaxCategory.put(CategoryEntry.COLUMN_NAME_PRIORITY, 1);
        relaxCategory.put(CategoryEntry.COLUMN_NAME_DEFAULT, 1);
        db.insert(CategoryEntry.TABLE_NAME, null, relaxCategory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade");
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public Cursor getCategory(){

        SQLiteDatabase mDb = getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + CategoryEntry.TABLE_NAME;
        Cursor cursor = mDb.rawQuery(selectQuery, null);

        Log.i(TAG,"Return category cursor: "+cursor.toString());

        return cursor;
    }

    public void saveCategory(List<Category> results) {
        SQLiteDatabase mDb = getWritableDatabase();
        for(Category c : results){
            ContentValues values = new ContentValues();

            values.put(CategoryEntry.COLUMN_NAME_NAME, c.name);
            values.put(CategoryEntry.COLUMN_NAME_COLOR, c.color);
            values.put(CategoryEntry.COLUMN_NAME_ICON, c.icon);
            values.put(CategoryEntry.COLUMN_NAME_PRIORITY, c.getPriority());
            values.put(CategoryEntry.COLUMN_NAME_DEFAULT, c.getIsDefault());

            if(c.getId() > 0){
                mDb.update(CategoryEntry.TABLE_NAME, values,"_id = ? ", new String[] { Integer.toString(c.getId()) } );
            }else{
                mDb.insert(CategoryEntry.TABLE_NAME, null, values);
            }
        }

    }
}
