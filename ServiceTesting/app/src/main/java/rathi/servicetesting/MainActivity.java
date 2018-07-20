package rathi.servicetesting;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
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

    }
}