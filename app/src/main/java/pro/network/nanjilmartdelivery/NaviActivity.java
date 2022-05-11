package pro.network.nanjilmartdelivery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import pro.network.nanjilmartdelivery.app.AppConfig;
import pro.network.nanjilmartdelivery.product.MainActivityOrder;;

import static pro.network.nanjilmartdelivery.app.AppConfig.mypreference;

import com.google.firebase.messaging.FirebaseMessaging;

public class NaviActivity extends AppCompatActivity {
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);


        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        FirebaseMessaging.getInstance().subscribeToTopic("allDevices_"
                + sharedpreferences.getString(AppConfig.user_id, ""));

        CardView sercice = findViewById(R.id.banner);

        sercice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityOrder.class);
                startActivity(io);

            }
        });

    }
}
