package com.orela.senflexp.network;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class httpClass
{
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public String http_post_request(String url, String data, String token)
    {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(data, JSON);
        Request request = new Request.Builder().url(url).post(body).addHeader("x-access-token", token).build();
        try
        {
            Response response = client.newCall(request).execute();
            return response.toString();
        }

        catch (Exception ex)
        {
            ex.printStackTrace();
            return "{" + "Status" + ":" + "false." + "}";
        }
    }

    public String http_get_request(String url, String token)
    {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().addHeader("x-access-token", token).build();
        try
        {
            Response response = client.newCall(request).execute();
            return response.toString();
        }

        catch (Exception ex)
        {
            ex.printStackTrace();
            return "{" + "Status" + ":" + "false." + "}";
        }
    }

    public String http_delete_request(String url, String token)
    {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).delete().addHeader("x-access-token", token).build();
        try
        {
            Response response = client.newCall(request).execute();
            return response.toString();
        }

        catch (Exception ex)
        {
            ex.printStackTrace();
            return "{" + "Status" + ":" + "false." + "}";
        }
    }
}
