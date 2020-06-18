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

import org.json.JSONObject;

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

        contentValue.put("timestamp", testData[11]);
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
        contentValue.put("verify", testData[13]);
        contentValue.put("sample_time", testData[14]);
        contentValue.put("specimen_type", testData[15]);

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

    public static JSONObject getSingleData(String test_id, String device_id)
    {
        SQLiteDatabase db = database;
        final String createQuery = "SELECT * FROM test_data WHERE test_id =? AND device_id =?";
        Cursor cursor = db.rawQuery(createQuery, new String[]{test_id, device_id},null);

        if(cursor != null)
        {
            cursor.moveToFirst();
        }

        JSONObject object = new JSONObject();

        try
        {
            assert cursor != null;
            object.put("test_id", cursor.getString(2));
            object.put("device_id", cursor.getString(3));
            object.put("patient_name", cursor.getString(4));
            object.put("dob", cursor.getString(6));
            object.put("address", cursor.getString(5));
            object.put("sex", cursor.getString(7));
            object.put("mobile", cursor.getString(8));
            object.put("email", cursor.getString(9));
            object.put("test_data", cursor.getString(11));
            object.put("ioxy_data", cursor.getString(12));
            object.put("test_time", cursor.getString(1));
            object.put("picture", cursor.getString(10));
            object.put("veri_flag", cursor.getString(13));
            object.put("sample_time", cursor.getString(14));
            object.put("specimen", cursor.getString(15));
        }

        catch (Exception e)
        {
            Log.d("SQL_SELECT", e.toString());
            e.printStackTrace();
        }

        cursor.close();
        return object;
    }

    public static Cursor dropTable()
    {
        final String createQuery = "DROP TABLE IF EXISTS " + databaseHelper.TABLE_NAME;
        Cursor cursor = database.rawQuery(createQuery, null);
        return cursor;
    }

    public static boolean delete(String test_id, String device_id)
    {
        return database.delete(databaseHelper.TABLE_NAME, "test_id =? AND device_id =?", new String[]{test_id, device_id}) > 0;
    }
}
