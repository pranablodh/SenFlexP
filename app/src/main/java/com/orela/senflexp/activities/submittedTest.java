package com.orela.senflexp.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.orela.senflexp.R;
import com.orela.senflexp.network.api;
import com.orela.senflexp.network.networkListener;
import com.orela.senflexp.network.networkManager;
import com.orela.senflexp.recyclerView.submittedTest.submittedTestAdapter;
import com.orela.senflexp.recyclerView.submittedTest.submittedTestDataBinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class submittedTest extends AppCompatActivity
{
    private EditText nameFilter;
    private Button previous;
    private Button next;

    //Recycler View
    private RecyclerView testList;
    private List<submittedTestDataBinder> testData;

    //Dialog Box Element
    private Dialog progressDialog;
    private TextView dialog_text;
    private LottieAnimationView animation;

    //Offset Variable
    private int offsetVariable = 0;

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
        testList = (RecyclerView) findViewById(R.id.testList);
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
                    Toast.makeText(submittedTest.this, nameFilter.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        inflateRecyclerView("X");
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
        networkManager.getInstance();
        networkManager.httpGet(api.baseUrl + api.testList, new networkListener<String>()
        {
            @Override
            public void getResult(String object)
            {
                progressDialog.dismiss();
                Log.d("TEST_DET", object);
            }

            @Override
            public void onError(String object)
            {
                progressDialog.dismiss();
                Log.d("TEST_DET", object);
            }

            @Override
            public void noConnection(String object)
            {
                progressDialog.dismiss();
                Log.d("TEST_DET", object);
            }
        });
    }

    private void inflateRecyclerView(String object)
    {
        try
        {
            testData = new ArrayList<>();
            testData.add(new submittedTestDataBinder("Pranab", "100", "12:02:2020", "Y", "SENP-001"
            , "xzy"));
            testData.add(new submittedTestDataBinder("Pranab", "100", "12:02:2020", "Y", "SENP-001"
                    , "xzy"));
            testData.add(new submittedTestDataBinder("Pranab", "100", "12:02:2020", "Y", "SENP-001"
                    , "xzy"));
            submittedTestAdapter adapterList  = new submittedTestAdapter(submittedTest.this, testData);
            testList.setAdapter(adapterList);
        }

        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("RECY_VIEW", e.toString());
        }
    }
}
