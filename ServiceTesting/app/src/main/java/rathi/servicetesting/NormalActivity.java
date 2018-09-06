package rathi.servicetesting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NormalActivity extends AppCompatActivity {

    public static int testInt = 2601;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);
    }

    public static void testNotSoFunCtion(String g)
    {
        String x = "who calls a string g? lol";
    }

    public void testNotSoFunCtion2(String g)
    {
        String x = "who calls a string g? lol";
    }


}
