package com.orela.senflexp.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.orela.senflexp.R;
import com.orela.senflexp.sharedPreference.sharedPreference;

public class splash_screen extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Hiding Action Bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if(!sharedPreference.getFlag(splash_screen.this))
                {
                    sharedPreference.deleteTokens(splash_screen.this);
                    go_to_onboarding();
                }

                else if(!sharedPreference.getAccessTokens(splash_screen.this).isEmpty() &&
                        !sharedPreference.getRefreshTokens(splash_screen.this).isEmpty())
                {
                    go_to_landing();
                }

                else
                {
                    go_to_login();
                }
            }
        },3000);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }

    private void go_to_onboarding()
    {
        Intent go = new Intent(splash_screen.this, onboarding.class);
        startActivity(go);
        finish();
    }

    private void go_to_login()
    {
        Intent go = new Intent(splash_screen.this, login.class);
        startActivity(go);
        finish();
    }

    private void go_to_landing()
    {
        Intent go = new Intent(splash_screen.this, landing_page.class);
        startActivity(go);
        finish();
    }
}
