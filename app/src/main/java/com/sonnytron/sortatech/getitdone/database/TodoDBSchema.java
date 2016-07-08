package com.sonnytron.sortatech.getitdone.database;

/**
 * Created by sonnyrodriguez on 6/22/16.
 */
public class TodoDBSchema {
    public static final class TodoTable {
        public static final String NAME = "todoItems";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String PRIORITY = "priority";
            public static final String STATUS = "status";
            public static final String DONE = "done";
        }
    }
}
