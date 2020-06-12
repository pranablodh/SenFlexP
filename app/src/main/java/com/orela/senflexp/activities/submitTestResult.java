package com.orela.senflexp.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.orela.senflexp.R;
import com.orela.senflexp.database.databaseHelper;
import com.orela.senflexp.database.databaseManager;
import com.orela.senflexp.fileManagement.fileReader;
import com.orela.senflexp.fileManagement.gZip;
import com.orela.senflexp.network.api;
import com.orela.senflexp.network.networkListener;
import com.orela.senflexp.network.networkManager;
import com.orela.senflexp.sharedPreference.sharedPreference;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class submitTestResult extends AppCompatActivity
{
    //UI Elements
    private TextView name;
    private TextView testId;
    private TextView address;
    private TextView dob;
    private TextView sex;
    private TextView mobile;
    private TextView email;
    private CircleImageView image;
    private Button submit;
    private Button later;

    //Dialog Box Element
    private Dialog progressDialog;
    private TextView dialog_text;
    private LottieAnimationView animation;

    //DB Element
    private databaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_test_result);
        networkManager.getInstance(this);
        dbManager = new databaseManager(this);

        //Hiding Action Bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        //UI Elements
        name = (TextView) findViewById(R.id.name);
        testId = (TextView) findViewById(R.id.testId);
        address = (TextView) findViewById(R.id.address);
        dob = (TextView) findViewById(R.id.dob);
        sex = (TextView) findViewById(R.id.sex);
        mobile = (TextView) findViewById(R.id.mobile);
        email = (TextView) findViewById(R.id.email);
        submit = (Button) findViewById(R.id.submit);
        later = (Button) findViewById(R.id.later);
        image = (CircleImageView) findViewById(R.id.image);

        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                requestBody();
            }
        });

        later.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveData();
            }
        });

        requestBody();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        goToDash();
    }

    private void showProgressDialog(final int animationAsset, final int text)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                progressDialog = new Dialog(submitTestResult.this);
                progressDialog.setContentView(R.layout.dialog_loading);
                dialog_text = (TextView) progressDialog.findViewById(R.id.dialog_text);
                animation = (LottieAnimationView) progressDialog.findViewById(R.id.animation);
                animation.setAnimation(animationAsset);
                dialog_text.setText(text);
                progressDialog.setCancelable(false);
                Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });
    }

    private void imageBuilder(String imageData)
    {
        byte[] decodedString = Base64.decode(imageData, Base64.DEFAULT);
        final Bitmap restoredImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                image.setImageBitmap(restoredImage);
            }
        });
    }

    private void requestBody()
    {
        String sensorData = "";
        String iOxyData = "";
        String image = "";
        JSONObject object = new JSONObject();
        final String[] testData = sharedPreference.getTestParameters(submitTestResult.this);
        imageBuilder(testData[8]);

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                name.setText(testData[0]);
                testId.setText(testData[2]);
                address.setText(testData[3]);
                dob.setText(testData[4]);
                sex.setText(testData[5]);
                mobile.setText(testData[6]);
                email.setText(testData[7]);
            }
        });

        try
        {
            sensorData = gZip.compress(fileReader.read(testData[9], submitTestResult.this));
            iOxyData = gZip.compress(fileReader.read(testData[10], submitTestResult.this));
            image = gZip.compress(testData[8]);
        }

        catch (IOException e)
        {
            Toast.makeText(submitTestResult.this, "Unable to Prepare Data Before Sending.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }

        if(sensorData.isEmpty() || image.isEmpty())
        {
            Toast.makeText(submitTestResult.this, "Unable to Prepare Data Before Sending.", Toast.LENGTH_SHORT).show();
            return;
        }

        try
        {
            object.put("test_id", testData[2]);
            object.put("device_id", testData[1]);
            object.put("patient_name", testData[0]);
            object.put("address", testData[3]);
            object.put("dob", testData[4]);
            object.put("sex", testData[5]);
            object.put("mobile", testData[6]);
            object.put("email", testData[7]);
            object.put("test_time", testData[11]);
            object.put("last_lab_test_id", testData[12]);
            object.put("picture", image);
            object.put("test_data", sensorData);
            object.put("ioxy_data", iOxyData);
            object.put("veri_flag", testData[13]);
            showProgressDialog(R.raw.uploading, R.string.uploading_test_data);
            httpRequest(object);
        }

        catch (Exception e)
        {
            Toast.makeText(submitTestResult.this, "Unable to Prepare Data Before Sending.", Toast.LENGTH_SHORT).show();
            Log.d("TEST_DATA", e.toString());
            e.printStackTrace();
        }
    }

    private void httpRequest(JSONObject object)
    {
        networkManager.httpPost(api.baseUrl + api.submitTest, object, new networkListener<String>()
        {
            @Override
            public void getResult(String object)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        progressDialog.dismiss();
                        Toast.makeText(submitTestResult.this, "Data Successfully Uploaded.", Toast.LENGTH_SHORT).show();
                    }
                });
                goToDash();
            }

            @Override
            public void onError(String object)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        progressDialog.dismiss();
                        Toast.makeText(submitTestResult.this, "Data Uploading Failed.", Toast.LENGTH_SHORT).show();
                        saveData();
                        goToDash();
                    }
                });
            }

            @Override
            public void noConnection(String object)
            {
                progressDialog.dismiss();
                saveData();
                goToDash();
            }
        });
    }

    private void saveData()
    {
        try
        {
            dbManager.open();
            if(!databaseManager.insert())
            {
                dbManager.close();
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(submitTestResult.this, "Unable to Store Data Locally.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            else
            {
                dbManager.close();
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(submitTestResult.this, "Data Stored Locally.", Toast.LENGTH_SHORT).show();
                    }
                });
                dbManager.close();
            }
            goToDash();
        }

        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("SQL_DB", e.toString());
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(submitTestResult.this, "Unable to Store Data Locally.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void goToDash()
    {
        Intent go = new Intent(submitTestResult.this, landing_page.class);
        startActivity(go);
        finish();
    }
}
