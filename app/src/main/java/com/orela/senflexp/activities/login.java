package com.orela.senflexp.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.orela.senflexp.R;
import com.orela.senflexp.inputValidator.inputValidator;
import com.orela.senflexp.network.api;
import com.orela.senflexp.network.networkListener;
import com.orela.senflexp.network.networkManager;
import com.orela.senflexp.sharedPreference.sharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class login extends AppCompatActivity
{
    private Button login;
    private EditText email_mobile;
    private EditText password;
    private CheckBox remember_me;
    private TextView forget_password;

    //Variables for Permission
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int INITIAL_REQUEST = 1337;
    private String[] Permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_PHONE_STATE};

    //Dialog Box Element
    private Dialog progressDialog;
    private TextView dialog_text;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        networkManager.getInstance(this);

        //Hiding Action Bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        //UI Elements
        login = (Button) findViewById(R.id.login);
        email_mobile = (EditText) findViewById(R.id.email_mobile);
        password = (EditText) findViewById(R.id.password);
        remember_me = (CheckBox) findViewById(R.id.remember_me);
        forget_password = (TextView) findViewById(R.id.forget_password);

        update_edit_text();

        //On Click Listener
        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!inputValidator.checkMobileNumber(email_mobile.getText().toString()) &&
                   !inputValidator.checkEmail(email_mobile.getText().toString()))
                {
                    email_mobile.setError("Please Enter a Valid Mobile Number or Email ID");
                    return;
                }

                else if(password.getText().toString().length() == 0)
                {
                    password.setError("Field Cannot be Empty");
                    return;
                }

                else
                {
                    email_mobile.setError(null);
                    password.setError(null);
                }
                showProgressDialog();
                httpRequest();
            }
        });

        forget_password.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            }
        });

        checkingPermission();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }

    private void go_to_landing_page()
    {
        Intent go = new Intent(login.this, landing_page.class);
        startActivity(go);
        finish();
    }

    //Shared Preferences Data Fetching
    private void update_edit_text()
    {
        email_mobile.setText(sharedPreference.getCredentials(login.this)[0]);
        password.setText(sharedPreference.getCredentials(login.this)[1]);
    }

    //Checking Permission
    private void checkingPermission()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(!canWriteExternalStorage() || !canAccessFineLocation() || !canReadExternalStorage()
                    || !canAccessPhoneState() || !canAccessCamera())
            {
                requestPermissions(Permissions, INITIAL_REQUEST);
            }

            statusCheck();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean canAccessFineLocation()
    {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean canWriteExternalStorage()
    {
        return(hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean canReadExternalStorage()
    {
        return(hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean canAccessPhoneState()
    {
        return(hasPermission(Manifest.permission.READ_PHONE_STATE));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean canAccessCamera()
    {
        return(hasPermission(Manifest.permission.CAMERA));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasPermission(String perm)
    {
        return(PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm));
    }

    //Location Service Checking
    public void statusCheck()
    {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        assert manager != null;
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            //buildAlertMessageNoGps();
            displayLocationSettingsRequest(login.this);
        }
    }

    //Dialog to Open Location Settings
    private void displayLocationSettingsRequest(Context context)
    {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>()
        {
            @Override
            public void onResult(LocationSettingsResult result)
            {
                final Status status = result.getStatus();
                switch (status.getStatusCode())
                {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d("Location", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.d("Location", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try
                        {
                            status.startResolutionForResult(login.this, REQUEST_CHECK_SETTINGS);
                        }

                        catch (IntentSender.SendIntentException e)
                        {
                            Log.d("Location", "PendingIntent unable to execute request.");
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d("Location", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    private void showProgressDialog()
    {
        progressDialog = new Dialog(login.this);
        progressDialog.setContentView(R.layout.dialog_loading);
        dialog_text = (TextView) progressDialog.findViewById(R.id.dialog_text);
        dialog_text.setText(R.string.secure_login_text);
        progressDialog.setCancelable(false);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.show();
    }

    //Http Request Handler
    private void httpRequest()
    {
        JSONObject object = new JSONObject();
        try
        {
            object.put("user", email_mobile.getText().toString());
            object.put("password", password.getText().toString());
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        networkManager.getInstance();
        networkManager.loginController(api.baseUrl + api.login, object, new networkListener<String>()
        {
            @Override
            public void getResult(String object)
            {
                progressDialog.dismiss();
                try
                {
                    JSONObject response = new JSONObject(object);
                    JSONObject data = new JSONArray(response.getString("Data")).getJSONObject(0);
                    loginController(data.getString("Access_Token"), data.getString("Refresh_Token"));
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(login.this, "Unknown Error!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onError(String object)
            {
                progressDialog.dismiss();
                try
                {
                    JSONObject response = new JSONObject(object);
                    final String message = response.getString("Message");
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(login.this, message, Toast.LENGTH_LONG).show();
                        }
                    });
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(login.this, "Unknown Error!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void loginController(String accessToken, String refreshToken)
    {
        if(remember_me.isChecked())
        {
            sharedPreference.deleteCredentials(login.this);
            sharedPreference.storeCredentials(email_mobile.getText().toString(), password.getText().toString(), login.this);
        }

        else
        {
             sharedPreference.deleteCredentials(login.this);
        }

        sharedPreference.deleteTokens(login.this);
        sharedPreference.storeTokens(accessToken, refreshToken, login.this);
        go_to_landing_page();
    }
}
