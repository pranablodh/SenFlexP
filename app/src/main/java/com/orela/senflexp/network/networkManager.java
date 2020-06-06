package com.orela.senflexp.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.orela.senflexp.activities.login;
import com.orela.senflexp.sharedPreference.sharedPreference;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class networkManager
{
    private static final String TAG = "networkManager";
    private static networkManager instance = null;
    private static Context mCtx = null;
    private static JSONObject object = null;

    private networkManager(Context mCtx)
    {
        networkManager.mCtx = mCtx;
    }

    public static synchronized networkManager getInstance(Context mCtx)
    {
        if (null == instance)
        {
            instance = new networkManager(mCtx);
        }
        return instance;
    }

    public static synchronized networkManager getInstance()
    {
        if (null == instance)
        {
            throw new IllegalStateException(networkManager.class.getSimpleName() +  " is not initialized, call getInstance(...) first");
        }
        return instance;
    }

    public static void loginController(String url, JSONObject object, final networkListener<String> listener)
    {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(JSON, object.toString());
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Request request, IOException e)
            {
                showError();
                listener.noConnection(e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException
            {
                if(response.code() == 200 || response.code() == 201)
                {
                    listener.getResult(response.body().string());
                }

                else
                {
                    listener.onError(response.body().string());
                }
            }
        });
    }

    public static void httpPost(String url, JSONObject object, final networkListener<String> listener)
    {
        interceptor Interceptor = new interceptor();
        String accessToken = sharedPreference.getAccessTokens(networkManager.mCtx);
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(JSON, object.toString());
        client.interceptors().add(Interceptor);
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(url)
                .addHeader("x-access-token", accessToken)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Request request, IOException e)
            {
                showError();
                listener.noConnection(e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException
            {
                if(response.code() == 200 || response.code() == 201)
                {
                    listener.getResult(response.body().string());
                }

                else
                {
                    listener.onError(response.body().string());
                }
            }
        });
    }

    public static void httpGet(String url, final networkListener<String> listener)
    {
        interceptor Interceptor = new interceptor();
        String accessToken = sharedPreference.getAccessTokens(networkManager.mCtx);
        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(Interceptor);
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(url)
                .addHeader("Content-Type","application/json")
                .addHeader("x-access-token", accessToken)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Request request, IOException e)
            {
                showError();
                listener.noConnection(e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException
            {
                if(response.code() == 200 || response.code() == 201)
                {
                    listener.getResult(response.body().string());
                }

                else
                {
                    listener.onError(response.body().string());
                }
            }
        });
    }

    public static void httpDelete(String url, final networkListener<String> listener)
    {
        Log.d("HTTP_REQ_DATA", "HTTP Del Called");
        Log.d("HTTP_REQ_DATA", url);
        interceptor Interceptor = new interceptor();
        String accessToken = sharedPreference.getAccessTokens(networkManager.mCtx);
        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(Interceptor);
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(url)
                .addHeader("Content-Type","application/json")
                .addHeader("x-access-token", accessToken)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Request request, IOException e)
            {
                showError();
                listener.noConnection(e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException
            {
                final int responseCode = response.code();
                final String responseBody = response.body().string();
                Log.d("HTTP_REQ_DATA_2", responseBody);

                if(responseCode == 200)
                {
                    listener.getResult(responseBody);
                }

                else
                {
                    listener.onError(responseBody);
                }
            }
        });
    }

    public static int refreshAccessToken()
    {
        Log.d("HTTP_REQ_DATA", "RT Called 1");
        String url = api.baseUrl + api.newAccessToken;
        final String accessToken = sharedPreference.getAccessTokens(networkManager.mCtx);
        final String refreshToken = sharedPreference.getRefreshTokens(networkManager.mCtx);
        OkHttpClient client = new OkHttpClient();
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(url)
                .addHeader("x-access-token", accessToken)
                .addHeader("x-refresh-token", refreshToken)
                .method("POST", RequestBody.create(null, new byte[0]))
                .build();

        int responseCode = 401;
        try
        {
            Response response = client.newCall(request).execute();
            responseCode = response.code();

            if(responseCode == 200)
            {
                String responseBody = response.body().string();
                accessTokenStore(responseBody);
                Log.d("HTTP_REQ_DATA", responseBody);
            }
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }

        return responseCode;
    }

    private static void accessTokenStore(final String response)
    {
        try
        {
            JSONObject data = new JSONObject(response);
            JSONArray tokenData = new JSONArray(data.getString("Data"));
            Log.d("HTTP_REQ_DATA", data.toString());
            Log.d("HTTP_REQ_DATA", tokenData.toString());
            String accessToken = tokenData.getJSONObject(0).getString("Access_Token");
            sharedPreference.storeAccessToken(accessToken, networkManager.mCtx);
        }

        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("HTTP_REQ_DATA", e.toString());
        }
    }

    public static String getAccessToken()
    {
        return sharedPreference.getAccessTokens(networkManager.mCtx);
    }

    public static void goToLogin()
    {
        sharedPreference.deleteTokens(networkManager.mCtx);
        Intent go = new Intent(networkManager.mCtx, login.class);
        ((Activity)networkManager.mCtx).runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(networkManager.mCtx, "Session Expired!", Toast.LENGTH_LONG).show();
            }
        });
        ((Activity)networkManager.mCtx).startActivity(go);
        ((Activity)networkManager.mCtx).finish();
    }

    private static void showError()
    {
        ((Activity)networkManager.mCtx).runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(networkManager.mCtx, "Internet Connection Issue!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
