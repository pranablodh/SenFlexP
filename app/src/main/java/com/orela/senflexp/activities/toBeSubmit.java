package com.orela.senflexp.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.orela.senflexp.R;
import com.orela.senflexp.network.networkManager;
import com.orela.senflexp.recyclerView.submittedTest.submittedTestDataBinder;
import com.orela.senflexp.recyclerView.yetToSubmit.submitDataAdapter;
import com.orela.senflexp.recyclerView.yetToSubmit.submitDataBinder;

import java.util.ArrayList;
import java.util.List;

public class toBeSubmit extends AppCompatActivity
{
    //Recycler View
    private RecyclerView testList;
    private List<submitDataBinder> testData;
    private TextView emptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_be_submit);
        networkManager.getInstance(this);

        //UI Elements
        emptyMessage = (TextView) findViewById(R.id.emptyMessage);
        testList = (RecyclerView) findViewById(R.id.testList);
        testList.setHasFixedSize(true);
        testList.setLayoutManager(new LinearLayoutManager(toBeSubmit.this, LinearLayoutManager.VERTICAL, false));

        inflateRecyclerView("X");
    }

    @Override
    public void onBackPressed()
    {
        goToDash();
    }

    private void goToDash()
    {
        Intent go = new Intent(toBeSubmit.this, pastTestDash.class);
        startActivity(go);
        finish();
    }

    private void inflateRecyclerView(String object)
    {
        try
        {
            testData = new ArrayList<>();
            testData.add(new submitDataBinder("Pranab", "100", "12:02:2020", "Y"));
            testData.add(new submitDataBinder("Pranab", "100", "12:02:2020", "Y"));
            testData.add(new submitDataBinder("Pranab", "100", "12:02:2020", "Y"));

            submitDataAdapter adapterList  = new submitDataAdapter(toBeSubmit.this, testData);
            testList.setAdapter(adapterList);
        }

        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("RECY_VIEW", e.toString());
        }
    }
}
