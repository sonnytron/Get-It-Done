package com.sonnytron.sortatech.getitdone.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sonnytron.sortatech.getitdone.database.TodoDBSchema.TodoTable;

/**
 * Created by sonnyrodriguez on 6/22/16.
 */
public class TodoBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "todoItems.db";

    public TodoBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TodoTable.NAME + "(" +
            TodoTable.Cols.UUID + ", " +
            TodoTable.Cols.TITLE + ", " +
            TodoTable.Cols.DATE + ", " +
            TodoTable.Cols.PRIORITY + ", " +
            TodoTable.Cols.STATUS + ", " +
            TodoTable.Cols.DONE + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
