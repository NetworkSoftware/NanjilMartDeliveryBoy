package pro.network.shopbazaardelivery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;

import pro.network.shopbazaardelivery.app.AppConfig;
import pro.network.shopbazaardelivery.app.BaseActivity;
import pro.network.shopbazaardelivery.app.GlideApp;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void startDemo() {
        setContentView(R.layout.activity_settings);

        TextView userNameHeader = findViewById(R.id.userNameHeader);
        TextView userPhoneHeader = findViewById(R.id.userPhoneHeader);
        userNameHeader.setText(sharedpreferences.getString(AppConfig.name, ""));
        userPhoneHeader.setText(sharedpreferences.getString(AppConfig.phone, ""));

        ImageView navLogo = findViewById(R.id.navLogo);
        ImageView aadhar = findViewById(R.id.aadhar);
        ImageView license = findViewById(R.id.license);
        GlideApp.with(SettingsActivity.this)
                .applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.avatar))
                .load(sharedpreferences.getString(AppConfig.profile, "")).into(navLogo);

        GlideApp.with(SettingsActivity.this)
                .applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.avatar))
                .load(sharedpreferences.getString(AppConfig.license, "")).into(license);

        GlideApp.with(SettingsActivity.this)
                .applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.avatar))
                .load(sharedpreferences.getString(AppConfig.aadhar, "")).into(aadhar);
        ((TextView) findViewById(R.id.logout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(AppConfig.isLogin, false);
                editor.remove(AppConfig.name);
                editor.remove(AppConfig.phone);
                editor.remove(AppConfig.password);
                editor.remove(AppConfig.profile);
                editor.remove(AppConfig.license);
                editor.remove(AppConfig.aadhar);
                editor.remove(AppConfig.auth_key);
                editor.remove(AppConfig.user_id);
                editor.commit();
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                finishAffinity();
            }

        });

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
