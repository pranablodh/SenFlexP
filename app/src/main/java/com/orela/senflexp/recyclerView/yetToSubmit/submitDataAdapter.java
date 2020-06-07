package com.orela.senflexp.recyclerView.yetToSubmit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.orela.senflexp.R;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class submitDataAdapter extends RecyclerView.Adapter<submitDataAdapter.submitDataViewHolder>
{
    private Context mCtx;
    private List<submitDataBinder> submitTest;

    public submitDataAdapter(Context mCtx, List<submitDataBinder> submitTest)
    {
        this.mCtx = mCtx;
        this.submitTest = submitTest;
    }

    @NonNull
    @Override
    public submitDataAdapter.submitDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
    {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.card_submit_test, null);
        return new submitDataAdapter.submitDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final submitDataViewHolder submitDataViewHolder, final int i)
    {
        submitDataBinder aDataBind = submitTest.get(i);
        submitDataViewHolder.name.setText(aDataBind.getName());
        submitDataViewHolder.testId.setText(aDataBind.getTestID());
        submitDataViewHolder.deviceId.setText(aDataBind.getDeviceID());

        submitDataViewHolder.submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(mCtx, String.valueOf(i), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return submitTest.size();
    }

    class submitDataViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        TextView testId;
        TextView deviceId;
        CircleImageView image;
        Button submit;

        submitDataViewHolder(@NonNull View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            testId = itemView.findViewById(R.id.testId);
            deviceId = itemView.findViewById(R.id.deviceId);
            image = itemView.findViewById(R.id.image);
            submit = itemView.findViewById(R.id.submit);
        }
    }
}
