package pro.network.shopbazaardelivery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import pro.network.shopbazaardelivery.app.AppConfig;
import pro.network.shopbazaardelivery.service.MainActivityService;

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
                    if (sharedpreferences.contains(AppConfig.isLogin) && sharedpreferences.getBoolean(AppConfig.isLogin, false)) {
                        SplashActivity.this.startActivity(new Intent(SplashActivity.this, NaviActivity.class));
                    } else {
                        SplashActivity.this.startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }
                    finish();
                }
            }
        };
        logoTimer.start();

    }
}