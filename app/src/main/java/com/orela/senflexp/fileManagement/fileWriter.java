package com.orela.senflexp.fileManagement;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class fileWriter
{
    public static void write(String fileName, String data, Context mCtx)
    {
        try
        {
            File root = new File(mCtx.getExternalFilesDir(null), "SenFlexP");
            if (!root.exists())
            {
                root.mkdirs();
            }
            final File filepath = new File(root, fileName + ".txt");
            FileWriter writer = new FileWriter(filepath, true);
            writer.append(String.valueOf(System.currentTimeMillis()));
            writer.append(",");
            writer.append(data);
            writer.append("\n");
            writer.flush();
            writer.close();
            Log.d("file_###", "W");
        }

        catch(IOException e)
        {
            e.printStackTrace();
            Log.d("file_###", e.toString());
        }
    }
}
