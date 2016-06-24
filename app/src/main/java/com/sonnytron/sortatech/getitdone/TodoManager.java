package com.sonnytron.sortatech.getitdone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.sonnytron.sortatech.getitdone.database.TodoBaseHelper;
import com.sonnytron.sortatech.getitdone.database.TodoCursorWrapper;
import com.sonnytron.sortatech.getitdone.database.TodoDBSchema;
import com.sonnytron.sortatech.getitdone.database.TodoDBSchema.TodoTable;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Created by sonnyrodriguez on 6/22/16.
 */
public class TodoManager {
    private static TodoManager sTodoManager;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private static Comparator<Todo> descPriority;

    public static TodoManager get(Context context) {
        if (sTodoManager == null) {
            sTodoManager = new TodoManager(context);
        }
        return sTodoManager;
    }

    private TodoManager(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new TodoBaseHelper(mContext)
                .getWritableDatabase();
    }

    public void addTodo(Todo t) {
        ContentValues values = getContentValues(t);
        mDatabase.insert(TodoTable.NAME, null, values);
    }

    public List<Todo> getTodos() {
        List<Todo> todos = new ArrayList<>();
        TodoCursorWrapper cursor = queryTodos(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                todos.add(cursor.getTodo());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        Collections.sort(todos, descPriority);
        return todos;
    }

    public Todo getTodo(UUID id) {
        TodoCursorWrapper cursor = queryTodos(
                TodoTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getTodo();
        } finally {
            cursor.close();
        }
    }

    public File getPhotoFile(Todo todo) {
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFilesDir == null) {
            return null;
        }
        return new File(externalFilesDir, todo.getPhotoFilename());
    }

    public void updateTodo(Todo todo) {
        String uuidString = todo.getId().toString();
        ContentValues values = getContentValues(todo);
        mDatabase.update(TodoTable.NAME, values, TodoTable.Cols.UUID + " = ?", new String[] { uuidString });
    }

    private static ContentValues getContentValues(Todo todo) {
        ContentValues values = new ContentValues();
        values.put(TodoTable.Cols.UUID, todo.getId().toString());
        values.put(TodoTable.Cols.STATUS, todo.getStatus());
        values.put(TodoTable.Cols.PRIORITY, todo.getPriority());
        values.put(TodoTable.Cols.DATE, todo.getDueDate().getTime());
        values.put(TodoTable.Cols.TITLE, todo.getTitle());
        return values;
    }

    private TodoCursorWrapper queryTodos(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(TodoTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null);
        return new TodoCursorWrapper(cursor);
    }

    static {
        descPriority = new Comparator<Todo>() {
            @Override
            public int compare(Todo lhs, Todo rhs) {
                return Integer.valueOf(rhs.getPriority()).compareTo(Integer.valueOf(lhs.getPriority()));
            }
        };
    }
}
