package com.example.notepad;

import android.provider.BaseColumns;

public class MemoContract{

    private MemoContract(){}

    public static final class MemoEntry implements BaseColumns {
        public static final String TABLE_NAME = "memodb";
        public static final String _ID = "_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_TEXT = "text";
        public static final String TIME_STUMP = "time";
    }
}
