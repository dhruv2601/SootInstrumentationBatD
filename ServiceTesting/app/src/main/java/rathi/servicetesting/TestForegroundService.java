package rathi.servicetesting;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class TestForegroundService extends Service {
    public static final String TAG = "TestForegroundService";
    private static final int ID_SERVICE = 2601;

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(manager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();
        startForeground(ID_SERVICE, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager)
    {
        String channelID = "foreground_service_channelid";
        String channelName = "Immortal Foreground Service";
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelID;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG,"Foreground service is running");

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
