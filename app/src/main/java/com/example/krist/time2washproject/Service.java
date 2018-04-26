package com.example.krist.time2washproject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class Service extends Service {
    public Service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
