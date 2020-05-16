package com.orela.senflexp.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.orela.senflexp.R;

public class beepSound extends Service
{
    MediaPlayer mp;

    public beepSound()
    {
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        return null;
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onCreate()
    {
        mp = MediaPlayer.create(this, R.raw.beep_sound);
        mp.setLooping(true);
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        mp.start();
        return Service.START_NOT_STICKY;
    }

    public void onDestroy()
    {
        mp.stop();
        mp.release();
        stopSelf();
        super.onDestroy();
    }
}
