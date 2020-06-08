package com.orela.senflexp.recyclerView.submittedTest;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.orela.senflexp.fileManagement.gZip;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.TimeZone;

public class submittedTestDataBinder
{
    private String name = "";
    private String testID = "";
    private String testTime = "";
    private String reportGen = "";
    private String deviceID = "";
    private String image = "";

    public submittedTestDataBinder(String name, String testID, String testTime, String reportGen, String deviceID, String image)
    {
        this.name = name;
        this.testID = testID;
        this.testTime = testTime;
        this.reportGen = reportGen;
        this.deviceID = deviceID;
        this.image = image;
    }

    public String getName()
    {
        return name;
    }

    public String getTestID()
    {
        return testID;
    }

    public String getTestTime()
    {
        return parseTime(testTime);
    }

    public String getReportGen()
    {
        if(reportGen.equalsIgnoreCase("Y"))
        {
            return "Yes.";
        }

        return "No.";
    }

    public String getDeviceID()
    {
        return deviceID;
    }

    public Bitmap getImage()
    {
        return imageBuilder(image);
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
