package pro.network.shopbazaardelivery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import pro.network.shopbazaardelivery.product.MainActivityOrder;
import pro.network.shopbazaardelivery.service.MainActivityService;

import static pro.network.shopbazaardelivery.app.AppConfig.mypreference;

public class NaviActivity extends AppCompatActivity {
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);


        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);


        CardView product = findViewById(R.id.stock);
        CardView sercice = findViewById(R.id.banner);



        product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityService.class);
                startActivity(io);

            }
        });
        sercice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityOrder.class);
                startActivity(io);

            }
        });

    }
}
