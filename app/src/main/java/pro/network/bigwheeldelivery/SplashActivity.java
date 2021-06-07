package pro.network.bigwheeldelivery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import pro.network.bigwheeldelivery.app.AppConfig;
import pro.network.bigwheeldelivery.order.MainActivityOrder;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedpreferences = getSharedPreferences(AppConfig.mypreference,
                Context.MODE_PRIVATE);

        Thread logoTimer = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (Exception e) {
                    Log.e("Error", e.toString());
                } finally {
                    if (!(sharedpreferences.contains(AppConfig.isLogin)
                            && sharedpreferences.getBoolean(AppConfig.isLogin, false))) {
                        SplashActivity.this.startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivityOrder.class));
                        finish();
                    }
                }
            }
        };
        logoTimer.start();

    }
}