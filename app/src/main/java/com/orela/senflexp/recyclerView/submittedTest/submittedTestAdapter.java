package com.orela.senflexp.recyclerView.submittedTest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orela.senflexp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class submittedTestAdapter extends RecyclerView.Adapter<submittedTestAdapter.submittedTestViewHolder>
{
    private Context mCtx;
    private List<submittedTestDataBinder> submittedTest;

    public submittedTestAdapter(Context mCtx, List<submittedTestDataBinder> submittedTest)
    {
        this.mCtx = mCtx;
        this.submittedTest = submittedTest;
    }

    @NonNull
    @Override
    public submittedTestAdapter.submittedTestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
    {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.card_submitted_test, null);
        return new submittedTestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final submittedTestViewHolder submittedTestViewHolder, final int i)
    {
        submittedTestDataBinder aDataBind = submittedTest.get(i);
        submittedTestViewHolder.name.setText(aDataBind.getName());
        submittedTestViewHolder.testId.setText(aDataBind.getTestID());
        submittedTestViewHolder.testTime.setText(aDataBind.getTestTime());
        submittedTestViewHolder.submissionTime.setText(aDataBind.getSubmissionTime());
        submittedTestViewHolder.report.setText(aDataBind.getReportGen());
        submittedTestViewHolder.deviceId.setText(aDataBind.getDeviceID());

        try
        {
            submittedTestViewHolder.image.setImageBitmap(aDataBind.getImage());
        }

        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("JSON_PARSE_!", e.toString());
        }
    }

    @Override
    public int getItemCount()
    {
        return submittedTest.size();
    }

    class submittedTestViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        TextView testId;
        TextView testTime;
        TextView submissionTime;
        TextView report;
        TextView deviceId;
        CircleImageView image;

        submittedTestViewHolder(@NonNull View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            testId = itemView.findViewById(R.id.testId);
            testTime = itemView.findViewById(R.id.testTime);
            submissionTime = itemView.findViewById(R.id.submissionTime);
            report = itemView.findViewById(R.id.report);
            deviceId = itemView.findViewById(R.id.deviceId);
            image = itemView.findViewById(R.id.image);
        }
    }

    public void removeAll()
    {
        submittedTest.clear();
        notifyDataSetChanged();
    }
}
