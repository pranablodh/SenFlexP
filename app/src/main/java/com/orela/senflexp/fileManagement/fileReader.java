package com.orela.senflexp.fileManagement;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class fileReader
{
    public static String read(String fileName, Context mCtx)
    {
        try
        {
            File root = new File(mCtx.getExternalFilesDir(null), "SenFlexP");
            final File filepath = new File(root, fileName + ".txt");
            StringBuilder text = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(filepath));
            String line = "";
            while ((line = br.readLine()) != null)
            {
                text.append(line);
                text.append("\n");
            }
            br.close();
            Log.d("file_###", text.toString());
            return  text.toString();
        }

        catch(IOException e)
        {
            e.printStackTrace();
            Log.d("file_###", e.toString());
            return "false";
        }
    }
}
