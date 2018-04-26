package com.example.krist.time2washproject;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;

public class MyService extends Service{
    ArrayList<WashingTime> myTimes;

    public class LocalBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }
    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        myTimes = new ArrayList<>();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public ArrayList<WashingTime> getMyTimes(){
        return myTimes;
    }

}
