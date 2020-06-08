package com.orela.senflexp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.orela.senflexp.R;
import com.orela.senflexp.network.api;
import com.orela.senflexp.network.networkListener;
import com.orela.senflexp.network.networkManager;
import com.orela.senflexp.recyclerView.submittedTest.submittedTestAdapter;
import com.orela.senflexp.recyclerView.submittedTest.submittedTestDataBinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class submittedTest extends AppCompatActivity
{
    private EditText nameFilter;
    private Button previous;
    private Button next;
    private LinearLayout emptyMessage;
    private LinearLayout buttonContainer;

    //Recycler View
    private RecyclerView testList;
    private List<submittedTestDataBinder> testData;

    //Dialog Box Element
    private Dialog progressDialog;
    private TextView dialog_text;
    private LottieAnimationView animation;
    private submittedTestAdapter adapterList;

    //Offset Variable
    private int offsetVariable = 0;
    private Boolean isLoading = false;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submitted_test);
        networkManager.getInstance(this);

        //Hiding Action Bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        //UI Elements
        nameFilter = (EditText) findViewById(R.id.nameFilter);
        previous = (Button) findViewById(R.id.previous);
        next = (Button) findViewById(R.id.next);
        emptyMessage = (LinearLayout) findViewById(R.id.emptyMessage);
        testList = (RecyclerView) findViewById(R.id.testList);
        buttonContainer = (LinearLayout) findViewById(R.id.buttonContainer);
        testList.setHasFixedSize(true);
        testList.setLayoutManager(new LinearLayoutManager(submittedTest.this, LinearLayoutManager.VERTICAL, false));

        //On Click Listener
        previous.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(offsetVariable <= 0)
                {
                    offsetVariable = 0;
                    return;
                }
                offsetVariable -= 10;

                showProgressDialog(R.raw.downloading, R.string.fetching_test_data);
                httpRequest();
            }
        });

        next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                offsetVariable += 10;
                showProgressDialog(R.raw.downloading, R.string.fetching_test_data);
                httpRequest();
            }
        });

        nameFilter.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(!hasFocus)
                {
                    showProgressDialog(R.raw.downloading, R.string.fetching_test_data);
                    offsetVariable = 0;
                    httpRequest();
                }
            }
        });
        showProgressDialog(R.raw.downloading, R.string.fetching_test_data);
        httpRequest();
    }

    @Override
    public void onBackPressed()
    {
        goToDash();
    }

    private void goToDash()
    {
        Intent go = new Intent(submittedTest.this, pastTestDash.class);
        startActivity(go);
        finish();
    }

    private void showProgressDialog(int animationAsset, int text)
    {
        progressDialog = new Dialog(submittedTest.this);
        progressDialog.setContentView(R.layout.dialog_loading);
        dialog_text = (TextView) progressDialog.findViewById(R.id.dialog_text);
        animation = (LottieAnimationView) progressDialog.findViewById(R.id.animation);
        animation.setAnimation(animationAsset);
        dialog_text.setText(text);
        progressDialog.setCancelable(false);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.show();
    }

    private void httpRequest()
    {
        JSONObject body = new JSONObject();
        try
        {
            body.put("name", nameFilter.getText().toString());
            body.put("offset", offsetVariable);
        }

        catch (Exception e)
        {
            Log.d("TEST_DET", e.toString());
            e.printStackTrace();
            hideRecyclerView();
        }

        networkManager.getInstance();
        networkManager.httpPost(api.baseUrl + api.testList, body, new networkListener<String>()
        {
            @Override
            public void getResult(String object)
            {
                progressDialog.dismiss();
                Log.d("TEST_DET", object);
                inflateRecyclerView(object);
            }

            @Override
            public void onError(String object)
            {
                progressDialog.dismiss();
                Log.d("TEST_DET", object);
                hideRecyclerView();
            }

            @Override
            public void noConnection(String object)
            {
                progressDialog.dismiss();
                Log.d("TEST_DET", object);
                hideRecyclerViewNoInternet();
            }
        });
    }

    private void inflateRecyclerView(String object)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                testList.setVisibility(View.VISIBLE);
                emptyMessage.setVisibility(View.GONE);
                nameFilter.setEnabled(true);
            }
        });

        try
        {
            JSONObject response = new JSONObject(object);
            testData = new ArrayList<>();
            JSONArray dataArray = new JSONArray(response.getString("Data"));

            for(int i = 0; i< dataArray.length(); i++)
            {
                JSONObject responseData = new JSONObject(dataArray.getJSONObject(i).toString());
                String test_id = responseData.getString("test_id");
                String test_time = responseData.getString("test_time");
                String patient_name = responseData.getString("patient_name");
                String processed_flag = responseData.getString("processed_flag");
                String serial_no = responseData.getString("serial_no");
                String image = responseData.getString("picture");
                testData.add(new submittedTestDataBinder(patient_name, test_id, test_time, processed_flag, serial_no, image));
            }

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    adapterList  = new submittedTestAdapter(submittedTest.this, testData);
                    testList.setAdapter(adapterList);
                }
            });
        }

        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("JSON_PARSE_!", e.toString());
            hideRecyclerView();
        }
    }

    private void hideRecyclerView()
    {
        testData.clear();
        testList.removeAllViewsInLayout();

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                testList.setVisibility(View.GONE);
                emptyMessage.setVisibility(View.VISIBLE);
                nameFilter.setEnabled(false);
                nameFilter.setText("");
            }
        });
    }

    private void hideRecyclerViewNoInternet()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                testList.setVisibility(View.GONE);
                emptyMessage.setVisibility(View.VISIBLE);
                nameFilter.setEnabled(false);
                nameFilter.setText("");
                previous.setEnabled(false);
                next.setEnabled(false);
            }
        });
    }
}
