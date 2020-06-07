package com.orela.senflexp.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.orela.senflexp.R;

public class pastTestDash extends AppCompatActivity
{
    private CardView submitted;
    private CardView yet_to_submit;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_test_dash);

        //Hiding Action Bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        //UI Elements
        submitted = (CardView) findViewById(R.id.submitted);
        yet_to_submit = (CardView) findViewById(R.id.yet_to_submit);

        submitted.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goSubmitted();
            }
        });

        yet_to_submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goYetSubmitted();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        goToDash();
    }

    private void goToDash()
    {
        Intent go = new Intent(pastTestDash.this, landing_page.class);
        startActivity(go);
        finish();
    }

    private void goSubmitted()
    {
        Intent go = new Intent(pastTestDash.this, submittedTest.class);
        startActivity(go);
        finish();
    }

    private void goYetSubmitted()
    {
        Intent go = new Intent(pastTestDash.this, toBeSubmit.class);
        startActivity(go);
        finish();
    }
}
