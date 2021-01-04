package com.orela.senflexp.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.orela.senflexp.R;
import com.orela.senflexp.network.api;
import com.orela.senflexp.network.networkListener;
import com.orela.senflexp.network.networkManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class myDetails extends AppCompatActivity
{
    private Button back;
    private Button refreshButton;
    private ProgressBar progressBar;
    private TextView name;
    private TextView userCode;
    private TextView assignedDevice;
    private TextView dob;
    private TextView activationTime;
    private TextView email;
    private TextView mobile;
    private TextView house;
    private TextView locality;
    private TextView landMark;
    private TextView police;
    private TextView postOffice;
    private TextView district;
    private TextView pinCode;
    private TextView addressType;

    //Dialog Box Element
    private Dialog progressDialog;
    private TextView dialog_text;
    private LottieAnimationView animation;

    private static final String SHOWCASE_ID = "My Profile";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_details);
        networkManager.getInstance(this);

        //Hiding Action Bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        //UI Elements
        back = (Button) findViewById(R.id.back);
        refreshButton = (Button) findViewById(R.id.refreshButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        name = (TextView) findViewById(R.id.name);
        userCode = (TextView) findViewById(R.id.userCode);
        assignedDevice = (TextView) findViewById(R.id.assignedDevice);
        dob = (TextView) findViewById(R.id.dob);
        activationTime = (TextView) findViewById(R.id.activationTime);
        email = (TextView) findViewById(R.id.email);
        mobile = (TextView) findViewById(R.id.mobile);
        house = (TextView) findViewById(R.id.house);
        locality = (TextView) findViewById(R.id.locality);
        landMark = (TextView) findViewById(R.id.landMark);
        police = (TextView) findViewById(R.id.police);
        postOffice = (TextView) findViewById(R.id.postOffice);
        district = (TextView) findViewById(R.id.district);
        pinCode = (TextView) findViewById(R.id.pinCode);
        addressType = (TextView) findViewById(R.id.addressType);

        //show_show_case_view();

        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToLandingPage();
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                progressBar.setVisibility(View.VISIBLE);
                getDevice();
            }
        });
        showProgressDialog();
        httpRequest();
    }

    @Override
    public void onBackPressed()
    {
        goToLandingPage();
        super.onBackPressed();
    }

    private void goToLandingPage()
    {
        Intent go = new Intent(myDetails.this, com.orela.senflexp.activities.landing_page.class);
        startActivity(go);
        finish();
    }

    private void show_show_case_view()
    {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(100);
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);
        sequence.setConfig(config);
        sequence.addSequenceItem(refreshButton, "Press Here to Get Your Assigned Device Details", "Got It");
        sequence.addSequenceItem(back, "Press Here to Back", "Got It");
        sequence.start();
    }

    private void showProgressDialog()
    {
        progressDialog = new Dialog(myDetails.this);
        progressDialog.setContentView(R.layout.dialog_loading);
        dialog_text = (TextView) progressDialog.findViewById(R.id.dialog_text);
        animation = (LottieAnimationView) progressDialog.findViewById(R.id.animation);
        animation.setAnimation(R.raw.downloading);
        dialog_text.setText(R.string.getting_details);
        progressDialog.setCancelable(false);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.show();
    }

    private void httpRequest()
    {
        networkManager.getInstance();
        networkManager.httpGet(api.baseUrl + api.userDetails, new networkListener<String>()
        {
            @Override
            public void getResult(String object)
            {
                progressDialog.dismiss();
                updateText(object);
            }

            @Override
            public void onError(String object)
            {
                progressDialog.dismiss();
                updateText(object);
            }

            @Override
            public void noConnection(String object)
            {
                progressDialog.dismiss();
            }
        });
    }

    private void getDevice()
    {
        networkManager.getInstance();
        networkManager.httpGet(api.baseUrl + api.assignedDevice, new networkListener<String>()
        {
            @Override
            public void getResult(String object)
            {
                progressBar.setVisibility(View.INVISIBLE);
                updateDevice(object);
            }

            @Override
            public void onError(String object)
            {
                progressBar.setVisibility(View.INVISIBLE);
                updateDevice(object);
            }

            @Override
            public void noConnection(String object)
            {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void updateDevice(String object)
    {
        try
        {
            final JSONObject response = new JSONObject(object);
            if(!response.getBoolean("Status"))
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        assignedDevice.setText(R.string.no_device);
                    }
                });
                return;
            }

            final JSONObject data = new JSONArray(response.getString("Data")).getJSONObject(0);

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        assignedDevice.setText(data.getString("serial_no"));
                    }

                    catch (Exception e)
                    {
                        Toast.makeText(myDetails.this, "Unknown Error!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }

        catch (Exception e)
        {
            e.printStackTrace();
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(myDetails.this, "Unknown Error!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateText(String object)
    {
        try
        {
            final JSONObject response = new JSONObject(object);
            final String message = response.getString("Message");
            if(!response.getBoolean("Status"))
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(myDetails.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }

            final JSONObject data = new JSONArray(response.getString("Data")).getJSONObject(0);

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        format.setTimeZone(TimeZone.getTimeZone("UTC+5:30"));
                        String birthDate = data.getString("date_of_birth").split("T")[0];
                        String activationDate = Objects.requireNonNull(format.parse(data.getString("activation_time"))).toString();
                        name.setText(data.getString("full_name").replace("  ", " "));
                        userCode.setText(data.getString("user_code"));
                        dob.setText(birthDate);
                        activationTime.setText(activationDate);
                        email.setText(data.getString("primary_email"));
                        mobile.setText(data.getString("primary_mobile"));
                        house.setText(data.getString("house_apartment"));
                        locality.setText(data.getString("locality"));
                        landMark.setText(data.getString("landmark"));
                        police.setText(data.getString("police_station"));
                        postOffice.setText(data.getString("post_office"));
                        district.setText(data.getString("district"));
                        pinCode.setText(data.getString("pincode"));
                        addressType.setText(data.getString("address_type"));
                    }

                    catch (Exception e)
                    {
                        Toast.makeText(myDetails.this, "Unknown Error!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        Log.d("USER_DETAILS", e.toString());
                    }
                }
            });
        }

        catch (Exception e)
        {
            e.printStackTrace();
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(myDetails.this, "Unknown Error!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
