package com.orela.senflexp.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.orela.senflexp.R;
import com.orela.senflexp.database.databaseManager;
import com.orela.senflexp.network.networkManager;
import com.orela.senflexp.recyclerView.submittedTest.submittedTestDataBinder;
import com.orela.senflexp.recyclerView.yetToSubmit.submitDataAdapter;
import com.orela.senflexp.recyclerView.yetToSubmit.submitDataBinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class toBeSubmit extends AppCompatActivity
{
    //Recycler View
    private RecyclerView testList;
    private List<submitDataBinder> testData;
    private CardView emptyMessage;

    //DB Element
    private databaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_be_submit);
        networkManager.getInstance(this);
        dbManager = new databaseManager(this);

        //UI Elements
        emptyMessage = (CardView) findViewById(R.id.emptyMessage);
        testList = (RecyclerView) findViewById(R.id.testList);
        testList.setHasFixedSize(true);
        testList.setLayoutManager(new LinearLayoutManager(toBeSubmit.this, LinearLayoutManager.VERTICAL, false));

        inflateRecyclerView();
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

    private void inflateRecyclerView()
    {
        try
        {
            dbManager.open();
            Cursor cursor = databaseManager.getData();
            testData = new ArrayList<>();
            Log.d("SQL_SELECT", Arrays.toString(cursor.getColumnNames()));

            while (!cursor.isAfterLast())
            {
                Log.d("SQL_FETCH", String.valueOf(1));
                testData.add(new submitDataBinder(cursor.getString(4), cursor.getString(2),
                        cursor.getString(3), cursor.getString(10), cursor.getString(1)));
                cursor.moveToNext();
            }

            //testData.add(new submitDataBinder("Pranab", "100", "12:02:2020", "Y", "04:30:01"));

            if(testData.size() == 0)
            {
                hideRecyclerView();
                return;
            }

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    submitDataAdapter adapterList  = new submitDataAdapter(toBeSubmit.this, testData);
                    testList.setAdapter(adapterList);
                }
            });

            cursor.close();
            dbManager.close();
        }

        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("RECY_VIEW", e.toString());
            hideRecyclerView();
        }
    }

    private void hideRecyclerView()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                testList.setVisibility(View.GONE);
                emptyMessage.setVisibility(View.VISIBLE);
            }
        });
    }
}
