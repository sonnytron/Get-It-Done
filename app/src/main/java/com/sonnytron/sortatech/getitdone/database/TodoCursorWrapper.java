package com.sonnytron.sortatech.getitdone.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Log;

import com.sonnytron.sortatech.getitdone.Todo;
import com.sonnytron.sortatech.getitdone.database.TodoDBSchema.TodoTable;

import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by sonnyrodriguez on 6/22/16.
 */
public class TodoCursorWrapper extends CursorWrapper {
    public TodoCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Todo getTodo() {
        String uuidString = getString(getColumnIndex(TodoTable.Cols.UUID));
        String title = getString(getColumnIndex(TodoTable.Cols.TITLE));
        String status = getString(getColumnIndex(TodoTable.Cols.STATUS));
        int priority = getInt(getColumnIndex(TodoTable.Cols.PRIORITY));
        long date = getLong(getColumnIndex(TodoTable.Cols.DATE));
        int done = getDone(getColumnIndex(TodoTable.Cols.DONE));

        Todo todo = new Todo(UUID.fromString(uuidString));
        todo.setTitle(title);
        todo.setDueDate(new Date(date));
        todo.setStatus(status);
        todo.setDone(done);
        return todo;
    }

    public int getDone(int columnIndex) {
        int value = 0;
        try {
            if (!isNull(columnIndex)) {
                value = getInt(columnIndex);
            }
        } catch (Throwable throwable) {
            Log.e("Cursor Error at GetDone", "GetDone", throwable);
        }
        return value;
    }
}
