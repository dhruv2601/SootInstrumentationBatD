package rathi.servicetesting;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent normalService = new Intent(MainActivity.this, TestService.class);
        startService(normalService);

        Intent normalService1 = new Intent(MainActivity.this, TestService.class);
        startService(normalService1);

        Intent foregroundService = new Intent(this, TestForegroundService.class);
        Intent foregroundService1 = new Intent(this, TestForegroundService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(foregroundService);
            startForegroundService(foregroundService1);
        }
        else {
            startService(foregroundService);
            startService(foregroundService1);
        }

        Intent foregroundServiceLocation = new Intent(this, TestLocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(foregroundServiceLocation);
        }
        else {
            startService(foregroundServiceLocation);
        }

        Intent inherit = new Intent(MainActivity.this, CallingServiceAct.class);
        startService(inherit);
        stopService(inherit);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(manager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();

        TestForegroundService testForegroundService = new TestForegroundService();
        testForegroundService.startForeground(2601, notification);

//        NormalActivity normalActivity = new NormalActivity();  // see if this creates an onCreate ka instance in Jimple
//
//        NormalActivity.testNotSoFunCtion("g");              // see if this shows an instance of Normal Act in Virtual Invoke

//        normalActivity.testNotSoFunCtion2("g");             // see if this shows an instance of Normal Act in Virtual Invoke to Static or Special invoke
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

    public void getServiceStarted()
    {
        Intent inherit = new Intent(MainActivity.this , TestLocationService.class);
        startService(inherit);
    }
}