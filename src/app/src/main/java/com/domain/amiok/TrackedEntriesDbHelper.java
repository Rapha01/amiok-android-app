package com.domain.amiok;

/**
 * Created by user on 06.01.2018.
 */


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.domain.amiok.TrackedEntriesContract.*;

// COMPLETED (1) extend the SQLiteOpenHelper class
public class TrackedEntriesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "amiok.db";
    private static final int DATABASE_VERSION = 1;

    public TrackedEntriesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_TRACKEDENTRY_TABLE = "CREATE TABLE " + TrackedEntry.TABLE_NAME + " (" +
                TrackedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TrackedEntry.COLUMN_ENTRY_ID + " INTEGER NOT NULL" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_TRACKEDENTRY_TABLE);
    }

    // COMPLETED (8) Override the onUpgrade method
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrackedEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}