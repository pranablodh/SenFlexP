package com.orela.senflexp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.orela.senflexp.activities.submitTestResult;
import com.orela.senflexp.fileManagement.fileReader;
import com.orela.senflexp.fileManagement.gZip;
import com.orela.senflexp.sharedPreference.sharedPreference;

import java.io.IOException;

public class databaseManager
{
    private databaseHelper dbHelper;
    private static Context context;
    private static SQLiteDatabase database;

    public databaseManager(Context c)
    {
        context = c;
    }

    public databaseManager open() throws SQLException
    {
        dbHelper = new databaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        dbHelper.close();
    }

    public static boolean insert()
    {
        String sensorData = "";
        String iOxyData = "";
        String image = "";

        final String[] testData = sharedPreference.getTestParameters(context);
        ContentValues contentValue = new ContentValues();

        try
        {
            sensorData = gZip.compress(fileReader.read(testData[9], context));
            iOxyData = gZip.compress(fileReader.read(testData[10], context));
            image = gZip.compress(testData[8]);
        }

        catch (IOException e)
        {
            Toast.makeText(context, "Database Error.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }

        contentValue.put("test_id", testData[2]);
        contentValue.put("device_id", testData[1]);
        contentValue.put("patient_name", testData[0]);
        contentValue.put("address", testData[3]);
        contentValue.put("dob", testData[4]);
        contentValue.put("sex", testData[5]);
        contentValue.put("mobile", testData[6]);
        contentValue.put("email", testData[7]);
        contentValue.put("picture", image);
        contentValue.put("test_data", sensorData);
        contentValue.put("ioxy_data", iOxyData);
        Log.d("SQL_DB", "INSERTED");
        return database.insert(databaseHelper.TABLE_NAME, null, contentValue) > 0;
    }

    public static Cursor getData()
    {
        SQLiteDatabase db = database;
        final String createQuery = "SELECT * FROM test_data";
        Cursor cursor = db.rawQuery( createQuery, null );

        if(cursor != null)
        {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public static Cursor dropTable()
    {
        final String createQuery = "DROP TABLE IF EXISTS " + databaseHelper.TABLE_NAME;
        Cursor cursor = database.rawQuery(createQuery, null);
        return cursor;
    }

    public static boolean delete(String id, String device_id)
    {
        return database.delete(databaseHelper.TABLE_NAME, "test_id =? AND device_id =?", new String[]{id, device_id}) > 0;
    }
}
