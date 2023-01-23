package pro.network.nanjilmartdelivery;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

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

        goToNext();

    }

    private void goToNext() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(SplashActivity.this, POST_NOTIFICATIONS) != PermissionChecker.PERMISSION_GRANTED) {
                SplashActivity.this.requestPermissions(new String[]{POST_NOTIFICATIONS}, 21);
            }else{
                startActivity();
            }
        }else{
            startActivity();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!(requestCode == 21 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(getApplicationContext(), "Permission denied, Unable to show notifications", Toast.LENGTH_SHORT).show();
        }
        startActivity();
    }

    private void startActivity() {
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