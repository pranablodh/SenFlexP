package com.orela.senflexp.bleDataParsing;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.orela.senflexp.R;

public class bleDataParser
{
    private static final int[] batteryImageID = {R.drawable.battery_100, R.drawable.battery_85, R.drawable.battery_65,
                                    R.drawable.battery_50, R.drawable.battery_30, R.drawable.battery_15,
                                    R.drawable.battery_0};

    //Temperature Sensor Data Conversion
    public static int rawBleDataConversion(byte[] bytes)
    {
        int signCheckTemp_int = 0;
        int sensorValueHighTemp_int = 0;
        int sensorValueLowTemp_int = 0;

        try
        {
            signCheckTemp_int = (bytes[1]);

            if (signCheckTemp_int < 0)
            {
                sensorValueHighTemp_int = signCheckTemp_int + 256;
            }

            else
            {
                sensorValueHighTemp_int = signCheckTemp_int;
            }

            signCheckTemp_int = (bytes[0]);

            if (signCheckTemp_int < 0)
            {
                sensorValueLowTemp_int = signCheckTemp_int  + 256;
            }

            else
            {
                sensorValueLowTemp_int = signCheckTemp_int;
            }

            Log.d("Sensor Val", String.valueOf(sensorValueHighTemp_int * 256 + sensorValueLowTemp_int));
            return (sensorValueHighTemp_int * 256 + sensorValueLowTemp_int);
        }

        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    //Battery Level Indicator Function
    public static int battery_level(byte[] bytes)
    {
        int signCheckBattery_int = 0;
        int batteryValueHigh_int = 0;
        int batteryValueLow_int = 0;

        int batteryCurrentValue = 0;

        try
        {
            signCheckBattery_int = (bytes[1]);

            if (signCheckBattery_int < 0)
            {
                batteryValueHigh_int = signCheckBattery_int + 256;
            }
            else
            {
                batteryValueHigh_int = signCheckBattery_int;
            }

            signCheckBattery_int = (bytes[0]);

            if (signCheckBattery_int < 0)
            {
                batteryValueLow_int = signCheckBattery_int + 256;
            }
            else
            {
                batteryValueLow_int = signCheckBattery_int;
            }


            batteryCurrentValue = batteryValueHigh_int * 256 + batteryValueLow_int;

            final double batteryVoltage = (5.77 * (double) batteryCurrentValue) / 1000.0;
            //final int batteryPercentage = (int) (3.6 + ((batteryVoltage - 3.6) / 0.006266));
            final int batteryPercentage = (int) ((175.41525 * batteryVoltage) - 630.04685);

            if (batteryPercentage > 85 && batteryPercentage <= 120)
            {
                return batteryImageID[0];
            }
            else if (batteryPercentage > 65 && batteryPercentage <= 85)
            {
                return batteryImageID[1];
            }
            else if (batteryPercentage > 50 && batteryPercentage <= 65)
            {
                return batteryImageID[2];
            }
            else if (batteryPercentage > 30 && batteryPercentage <= 55)
            {
                return batteryImageID[3];
            }
            else if (batteryPercentage > 15 && batteryPercentage <= 30)
            {
                return batteryImageID[4];
            }
            else if (batteryPercentage > 0 && batteryPercentage <= 15)
            {
                return batteryImageID[5];
            }
            else
            {
                return batteryImageID[6];
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
            assert batteryImageID != null;
            return batteryImageID[6];
        }
    }

    //Pulse Oximeter Data Conversion
    public static int[] pulse_oxi_converter(byte[] bytes)
    {
        final int[] pulseOximeterDataBufferArray = new int[11];
        int[] oximeter_parameters = new int[]{-10, -10, -10};

        //Convert Unsigned Byte to Signed Integer
        for(int i = 0;i<bytes.length;i++)
        {
            if (bytes[i] < 0)
            {
                pulseOximeterDataBufferArray[i] = bytes[i] + 256;
            }
            else
            {
                pulseOximeterDataBufferArray[i] = bytes[i];
            }
        }

        if(bytes.length == 4)
        {
            oximeter_parameters[0] = pulseOximeterDataBufferArray[1]; //Heart Rate
            oximeter_parameters[1] = pulseOximeterDataBufferArray[2]; //Oxygen Saturation
            oximeter_parameters[2] = pulseOximeterDataBufferArray[3]; //Perfusion Index

            return oximeter_parameters;
        }

        else
        {
            return oximeter_parameters;
        }
    }
}