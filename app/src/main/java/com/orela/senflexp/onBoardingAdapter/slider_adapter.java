package com.orela.senflexp.onBoardingAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;
import com.orela.senflexp.R;

public class slider_adapter extends PagerAdapter
{
    Context mCtx;
    LayoutInflater layoutInflater;

    public slider_adapter(Context mCtx)
    {
        this.mCtx = mCtx;
    }

    //Array
    public int[] images = {R.raw.covid_3d_19, R.raw.keep_distance_coronavirus, R.raw.sneezing,
            R.raw.wash_your_hand_covid19_corona, R.raw.wear_mask, R.raw.home_heroes_stay_home_stay_safe,
            R.raw.doctors, R.raw.defeat_covid};

    private String[] headings = {"SARS COVID-19", "Social Distancing", "Mode of Transmission",
            "Handwashing", "Using Mask", "Heroes Stay at Home", "Respect all Medical Satff",
            "Let's Fight Together"};

    private String[] footers = {"The coronavirus COVID-19 pandemic is then defining global health crisis\nof our time and the greatest challenge we have faced since World War Two.",
            "Social distancing, also called “physical distancing,” means keeping\nspace between yourself and other people outside of your home.",
            "The virus that causes COVID-19 is mainly transmitted through droplets\ngenerated when an infected person coughs, sneezes, or exhales.",
            "Proper handwashing not only reduces the spread of Coronavirus (COVID-19),\nit can prevent the spread of other viral illnesses such as cold and flu.",
            "A face covering is one more precaution we can take to help slow the spread of COVID-19 – and is not a\nsubstitute for physical distancing and other prevention measures.",
            "Many countries have urged their people to stay at home as it is the only prevention from contracting COVID-19. Don't worry, staying home can be fun too.",
            "Medicine cures diseases, but only doctors can cure patients.",
            "To beat this worldwide pandemic, the world has to, must come together."};

    @Override
    public int getCount()
    {
        return headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object)
    {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position)
    {
        layoutInflater = (LayoutInflater) mCtx.getSystemService(mCtx.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View view = layoutInflater.inflate(R.layout.onboarding_slide_layout, container, false);

        LottieAnimationView sliderImage = (LottieAnimationView) view.findViewById(R.id.sliderImage);
        TextView header = (TextView) view.findViewById(R.id.header);
        TextView footer = (TextView) view.findViewById(R.id.footer);

        sliderImage.setAnimation(images[position]);
        header.setText(headings[position]);
        footer.setText(footers[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
    {
        container.removeView((LinearLayout) object);
    }
}
