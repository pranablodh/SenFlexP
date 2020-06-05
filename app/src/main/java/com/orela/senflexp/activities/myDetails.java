package com.orela.senflexp.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orela.senflexp.R;
import com.orela.senflexp.network.api;
import com.orela.senflexp.network.networkListener;
import com.orela.senflexp.network.networkManager;

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
            }
        });
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

    private void httpRequest()
    {
        networkManager.getInstance();
        networkManager.httpGet(api.baseUrl + api.userDetails, new networkListener<String>()
        {
            @Override
            public void getResult(String object)
            {
                Log.d("USER_DETAILS", object);
            }

            @Override
            public void onError(String object)
            {
                Log.d("USER_DETAILS", object);
            }
        });
    }
}
