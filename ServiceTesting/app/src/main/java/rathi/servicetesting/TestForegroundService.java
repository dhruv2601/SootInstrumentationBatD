package rathi.servicetesting;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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

//        some stuff to do as a background service
//        Log.d(TAG,"Background Service Running");

//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(manager) : "";
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
//        Notification notification = notificationBuilder.setOngoing(true)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentText("Running from Test Foreground Service")
//                .setCategory(NotificationCompat.CATEGORY_SERVICE)
//                .build();
//
//        startForeground(ID_SERVICE, notification);
    }

    public void foregroundInvokedFromChild(String s)
    {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(manager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();
        startForeground(ID_SERVICE, notification);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void stopForegroundInvokedFromChild(String s)
    {
        stopForeground(Service.STOP_FOREGROUND_REMOVE);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG,"Foreground service is running");

        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager)
    {
        String channelID = "foreground_service_channelID";
        String channelName = "Immortal Foreground Service";
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelID;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}