package com.orela.senflexp.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orela.senflexp.R;
import com.orela.senflexp.onBoardingAdapter.slider_adapter;
import com.orela.senflexp.sharedPreference.sharedPreference;

public class onboarding extends AppCompatActivity
{

    //On Boarding Element
    private ViewPager viewPager;
    private LinearLayout buttonContainer;
    private slider_adapter SliderAdapter;
    private TextView[] mDots;
    private Button previous;
    private Button next;
    private int mCurrentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        //Hiding Action Bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        //UI Elements
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        buttonContainer = (LinearLayout) findViewById(R.id.buttonContainer);
        previous = (Button) findViewById(R.id.previous);
        next = (Button) findViewById(R.id.next);
        previous.setVisibility(View.INVISIBLE);

        //On Boarding Element Variables
        SliderAdapter = new slider_adapter(this);
        viewPager.setAdapter(SliderAdapter);

        //UI Elements
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        buttonContainer = (LinearLayout) findViewById(R.id.buttonContainer);

        //Adding Dots
        addDotsIndicator(0);

        viewPager.addOnPageChangeListener(viewListener);

        //On Click Listener
        next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(next.getText().equals("Finish"))
                {
                    sharedPreference.deleteOnboarding(onboarding.this);
                    sharedPreference.storeOnboarding(true, onboarding.this);
                    go_to_login();
                }
                viewPager.setCurrentItem(mCurrentPage + 1);
            }
        });

        previous.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                viewPager.setCurrentItem(mCurrentPage - 1);
            }
        });
    }

    //Dots Adding Function
    public void addDotsIndicator(int position)
    {
        mDots = new TextView[SliderAdapter.getCount()];
        buttonContainer.removeAllViews();

        for(int i = 0; i < mDots.length; i++)
        {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(ContextCompat.getColor(this, R.color.semiTransparentColor));

            buttonContainer.addView(mDots[i]);
        }

        if(mDots.length > 0)
        {
            mDots[position].setTextColor(ContextCompat.getColor(this, R.color.lightGreen));
        }
    }

    //On Boarding Flipper
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {

        }

        @Override
        public void onPageSelected(int position)
        {
            addDotsIndicator(position);
            mCurrentPage = position;

            if(position == 0)
            {
                previous.setEnabled(false);
                previous.setVisibility(View.INVISIBLE);
                next.setEnabled(true);
            }

            else if (position == mDots.length - 1)
            {
                previous.setEnabled(true);
                previous.setVisibility(View.VISIBLE);
                next.setEnabled(true);
                next.setText(getString(R.string.finish_button));
            }

            else
            {
                previous.setEnabled(true);
                previous.setVisibility(View.VISIBLE);
                next.setEnabled(true);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {

        }
    };

    private void go_to_login()
    {
        Intent go = new Intent(onboarding.this, login.class);
        startActivity(go);
        finish();
    }
}
