package com.orela.senflexp.network;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class interceptor implements Interceptor
{
    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        builder.header("Content-Type", "application/json");
        String accessToken = networkManager.getAccessToken();
        setAuthHeader(builder, accessToken);
        request = builder.build();
        Response response = chain.proceed(request);
        final int responseCode = response.code();

        if(responseCode == 403)
        {
            String currentToken = networkManager.getAccessToken();

            if(currentToken != null && currentToken.equals(accessToken))
            {
                int code = networkManager.refreshAccessToken();

                if(code == 401)
                {
                    networkManager.goToLogin();
                    return response;
                }
            }

            if(networkManager.getAccessToken() != null)
            {
                setAuthHeader(builder, networkManager.getAccessToken());
                request = builder.build();
                return chain.proceed(request);
            }
        }
        return response;
    }

    private void setAuthHeader(Request.Builder builder, String token)
    {
        if (token != null)
        {
            builder.header("x-access-token", token);
        }
    }
}
