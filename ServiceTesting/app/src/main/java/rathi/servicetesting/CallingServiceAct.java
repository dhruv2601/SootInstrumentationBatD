package rathi.servicetesting;

import android.util.Log;

import junit.framework.Test;

public class CallingServiceAct extends TestForegroundService {
    public static final String TAG = "CallingServiceAct";

    @Override
    public void onCreate() {
        foregroundInvokedFromChild("Dhruv");
        stopForegroundInvokedFromChild("Rathi");
//      --
        MainActivity mainActivity = new MainActivity();
        mainActivity.onCreate(null);
//      --
        Log.d(TAG,"CallingService running");
        super.onCreate();
    }
}