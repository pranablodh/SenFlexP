package com.orela.senflexp.network;

import org.json.JSONObject;

public class jsonMaker
{
    public static String string_to_json(String[] key, String[] values)
    {
        if(key.length != values.length)
        {
            return "{" + "Status" + ":" + "false." + "}";
        }

        try
        {
            JSONObject jsonObj = new JSONObject();
            for (int i = 0; i < key.length; i++)
            {
                jsonObj.put(key[i], values[i]);
            }
            return jsonObj.toString();
        }

        catch (Exception e)
        {
            e.printStackTrace();
            return "{" + "Status" + ":" + "false1." + "}";
        }
    }
}
