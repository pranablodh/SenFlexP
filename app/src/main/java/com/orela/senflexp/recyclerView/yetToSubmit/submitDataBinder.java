package com.orela.senflexp.recyclerView.yetToSubmit;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.orela.senflexp.fileManagement.gZip;

import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.TimeZone;

public class submitDataBinder
{
    private String name = "";
    private String testID = "";
    private String deviceID = "";
    private String image = "";
    private String testTime = "";

    public submitDataBinder(String name, String testID, String deviceID, String image, String testTime)
    {
        this.name = name;
        this.testID = testID;
        this.deviceID = deviceID;
        this.image = image;
        this.testTime = testTime;
    }

    public String getName()
    {
        return name;
    }

    public String getTestID()
    {
        return testID;
    }

    public String getImage()
    {
        //return imageBuilder(image);
        return image;
    }

    public String getTestTime()
    {
        //return parseTime(testTime);
        return testTime;
    }

    public String getDeviceID()
    {
        return deviceID;
    }

    private String parseTime(String time)
    {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC+5:30"));
        try
        {
            return Objects.requireNonNull(Objects.requireNonNull(format.parse(time)).toString());
        }

        catch (Exception e)
        {
            return "N.A.";
        }
    }

    private Bitmap imageBuilder(String imageData)
    {
        byte[] decodedString = null;

        try
        {
            decodedString = Base64.decode(gZip.decompress(imageData), Base64.DEFAULT);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
        assert decodedString != null;
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
