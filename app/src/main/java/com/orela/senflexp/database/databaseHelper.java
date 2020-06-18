package com.orela.senflexp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class databaseHelper extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "patient_master";
    public static final String TABLE_NAME = "test_data";

    public databaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String createQuery = "CREATE TABLE IF NOT EXISTS test_data(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                "test_id TEXT NOT NULL, device_id TEXT NOT NULL, patient_name TEXT NOT NULL, address TEXT NOT NULL," +
                "dob TEXT NOT NULL, sex TEXT NOT NULL, mobile TEXT NOT NULL, email TEXT NOT NULL, picture TEXT NOT NULL," +
                "test_data TEXT NOT NULL, ioxy_data TEXT NOT NULL, verify TEXT NOT NULL, sample_time DATETIME NOT NULL, " +
                "specimen_type TEXT NOT NULL, UNIQUE(patient_name, mobile, email, timestamp) ON CONFLICT REPLACE)";
        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        final String createQuery = "DROP TABLE IF EXISTS test_data";
        db.execSQL(createQuery);
    }
}
