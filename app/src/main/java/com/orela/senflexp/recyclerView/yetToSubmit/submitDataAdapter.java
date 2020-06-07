package com.orela.senflexp.recyclerView.yetToSubmit;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.orela.senflexp.R;
import com.orela.senflexp.activities.submitTestResult;
import com.orela.senflexp.network.api;
import com.orela.senflexp.network.networkListener;
import com.orela.senflexp.network.networkManager;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class submitDataAdapter extends RecyclerView.Adapter<submitDataAdapter.submitDataViewHolder>
{
    private Context mCtx;
    private List<submitDataBinder> submitTest;

    //Dialog Box Element
    private Dialog progressDialog;
    private TextView dialog_text;
    private LottieAnimationView animation;

    //Activity UI Elements
    private TextView emptyMessage;
    private RecyclerView testList;

    public submitDataAdapter(Context mCtx, List<submitDataBinder> submitTest)
    {
        this.mCtx = mCtx;
        this.submitTest = submitTest;
        networkManager.getInstance(mCtx);
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

        //Dialog Initializer
        progressDialog = new Dialog(mCtx);

        submitDataViewHolder.submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //showProgressDialog(R.raw.uploading, R.string.uploading_test_data);
                removeAt(i);
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
            emptyMessage = (TextView) ((Activity)mCtx).findViewById(R.id.emptyMessage);
            testList = (RecyclerView) ((Activity)mCtx).findViewById(R.id.testList);
        }
    }

    private void removeAt(int position)
    {
        submitTest.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, submitTest.size());

        if(submitTest.size() == 0)
        {
            testList.setVisibility(View.GONE);
            emptyMessage.setVisibility(View.VISIBLE);
        }
    }

    private void httpRequest(JSONObject object, int position)
    {
        networkManager.getInstance();
        networkManager.httpPost(api.baseUrl + api.submitTest, object, new networkListener<String>()
        {
            @Override
            public void getResult(String object)
            {

            }

            @Override
            public void onError(String object)
            {

            }

            @Override
            public void noConnection(String object)
            {

            }
        });
    }

    private void showProgressDialog(int animationAsset, int text)
    {
        progressDialog = new Dialog(mCtx);
        progressDialog.setContentView(R.layout.dialog_loading);
        dialog_text = (TextView) progressDialog.findViewById(R.id.dialog_text);
        animation = (LottieAnimationView) progressDialog.findViewById(R.id.animation);
        animation.setAnimation(animationAsset);
        dialog_text.setText(text);
        progressDialog.setCancelable(false);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.show();
    }
}
