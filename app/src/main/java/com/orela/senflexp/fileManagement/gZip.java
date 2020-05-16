package com.orela.senflexp.fileManagement;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class gZip
{
    public static String compress(String string) throws IOException
    {
        try
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
            GZIPOutputStream gos = new GZIPOutputStream(os);
            gos.write(string.getBytes());
            gos.close();
            byte[] compressed = os.toByteArray();
            os.close();
            String text = Base64.encodeToString(compressed, Base64.DEFAULT);
            Log.d("file_###", text);
            return text;
        }

        catch (IOException e)
        {
            e.printStackTrace();
            Log.d("file_###", e.toString());
            return "false";
        }
    }

    public static String decompress(String text) throws IOException
    {
        byte[] compressed = null;
        try
        {
            compressed = Base64.decode(text, Base64.DEFAULT);
        }

        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("file_###", e.toString());
            return "false";
        }

        final int BUFFER_SIZE = 32;
        ByteArrayInputStream is = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
        StringBuilder string = new StringBuilder();
        byte[] data = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = gis.read(data)) != -1)
        {
            string.append(new String(data, 0, bytesRead));
        }
        gis.close();
        is.close();
        Log.d("file_###", string.toString());
        return string.toString();
    }
}
