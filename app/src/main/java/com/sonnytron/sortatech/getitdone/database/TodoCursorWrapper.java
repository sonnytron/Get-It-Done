package com.sonnytron.sortatech.getitdone.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.sonnytron.sortatech.getitdone.Todo;
import com.sonnytron.sortatech.getitdone.database.TodoDBSchema.TodoTable;

import java.util.Date;
import java.util.UUID;

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
        boolean done = (getInt(getColumnIndex(TodoTable.Cols.DONE)) == 1);

        Todo todo = new Todo(UUID.fromString(uuidString));
        todo.setTitle(title);
        todo.setDueDate(new Date(date));
        todo.setStatus(status);
        todo.setDone(done);
        return todo;
    }
}
