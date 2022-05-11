package pro.network.nanjilmartdelivery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;

import pro.network.nanjilmartdelivery.app.AppConfig;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        sharedpreferences = getSharedPreferences(AppConfig.mypreference,
                Context.MODE_PRIVATE);
        FirebaseMessaging.getInstance().subscribeToTopic("allDevices_"
                + sharedpreferences.getString(AppConfig.user_id, ""));
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