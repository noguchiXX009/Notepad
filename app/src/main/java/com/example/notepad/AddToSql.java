package com.example.notepad;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AddToSql extends SQLiteOpenHelper {

    private static final int DB_VERSION = 3;

    private static final String DBNAME = "Memo.db";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " +
                    MemoContract.MemoEntry.TABLE_NAME + " (" +
                    MemoContract.MemoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MemoContract.MemoEntry.COLUMN_TITLE + " TEXT ," +
                    MemoContract.MemoEntry.COLUMN_TEXT + " TEXT ," +
                    MemoContract.MemoEntry.TIME_STUMP + " TEXT);";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + MemoContract.MemoEntry.TABLE_NAME;

    public AddToSql(@Nullable Context context) {
        super(context, DBNAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                SQL_CREATE_ENTRIES
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(
                SQL_DELETE_ENTRIES
        );
        onCreate(db);
    }
}
