package rathi.servicetesting;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class TestService extends Service {

    public static final String TAG = "TestService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG,"Service has been triggered");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
