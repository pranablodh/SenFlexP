package com.orela.senflexp.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.orela.senflexp.R;
import com.orela.senflexp.network.api;
import com.orela.senflexp.network.networkListener;
import com.orela.senflexp.network.networkManager;
import com.orela.senflexp.sharedPreference.sharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class landing_page extends AppCompatActivity
{
    private CardView new_test;
    private CardView past_test;
    private CardView my_profile;
    private CardView logout;

    //Dialog Box Element
    private Dialog progressDialog;
    private TextView dialog_text;

    private static final String SHOWCASE_ID = "Landing Page";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        networkManager.getInstance(this);

        //Hiding Action Bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        //UI Elements
        new_test = (CardView) findViewById(R.id.new_test);
        past_test = (CardView) findViewById(R.id.past_test);
        my_profile = (CardView) findViewById(R.id.my_profile);
        logout = (CardView) findViewById(R.id.logout);

        show_show_case_view();

        //On Click Listener
        new_test.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                go_to_new_registration();
            }
        });

        past_test.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(landing_page.this, "Coming Soon..!!", Toast.LENGTH_SHORT).show();
            }
        });

        my_profile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                go_to_my_details();
            }
        });

        logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showProgressDialog();
                httpRequest();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        go_to_login();
    }

    private void go_to_login()
    {
        sharedPreference.deleteTokens(landing_page.this);
        Intent go = new Intent(landing_page.this, login.class);
        startActivity(go);
        finish();
    }

    private void go_to_new_registration()
    {
        Intent go = new Intent(landing_page.this, com.orela.senflexp.activities.new_test.class);
        startActivity(go);
        finish();
    }

    private void go_to_my_details()
    {
        Intent go = new Intent(landing_page.this, com.orela.senflexp.activities.myDetails.class);
        startActivity(go);
        finish();
    }

    private void show_show_case_view()
    {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(100);
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);
        sequence.setConfig(config);
        sequence.addSequenceItem(new_test, "Press Here to Register a New Test", "Got It");
        sequence.addSequenceItem(past_test, "Press Here to See Previous Test", "Got It");
        sequence.addSequenceItem(my_profile, "Press Here to See Your Personal Details", "Got It");
        sequence.addSequenceItem(logout, "Press Here to Close the Current Session.", "Got It");
        sequence.start();
    }

    private void showProgressDialog()
    {
        progressDialog = new Dialog(landing_page.this);
        progressDialog.setContentView(R.layout.dialog_loading);
        dialog_text = (TextView) progressDialog.findViewById(R.id.dialog_text);
        dialog_text.setText(R.string.secure_logout);
        progressDialog.setCancelable(false);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.show();
    }

    private void httpRequest()
    {
        networkManager.getInstance();
        networkManager.httpDelete(api.baseUrl + api.logout, new networkListener<String>()
        {
            @Override
            public void getResult(String object)
            {
                progressDialog.dismiss();
                try
                {
                    JSONObject response = new JSONObject(object);
                    if (response.getBoolean("Status"))
                    {
                        go_to_login();
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(landing_page.this, "Unknown Error!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            @Override
            public void onError(String object)
            {
                progressDialog.dismiss();
                //Log.d("HTTP_REQ_DATA", object);
            }
        });
    }

    private void getData()
    {
        networkManager.getInstance();
        networkManager.httpGet(api.baseUrl + api.userDetails, new networkListener<String>()
        {
            @Override
            public void getResult(String object)
            {
                Log.d("HTTP_REQ_DATA", object);
            }

            @Override
            public void onError(String object)
            {
                Log.d("HTTP_REQ_DATA", object);
            }
        });
    }
}
