package pro.network.nanjilmartdelivery.map;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import pro.network.nanjilmartdelivery.R;
import pro.network.nanjilmartdelivery.app.AppConfig;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{


    private static LatLng LOCATION = new LatLng(0, 0);
    protected ProgressDialog pDialog;
    private GoogleMap mMap;
    double latVAl = 0.0, longVAl=0.0;
    String addressVall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        latVAl = Double.parseDouble(getIntent().getStringExtra("Latitude"));
        longVAl = Double.parseDouble(getIntent().getStringExtra("Longitude"));
        addressVall = (getIntent().getStringExtra("Address"));
        LOCATION = new LatLng(latVAl, longVAl);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LOCATION = new LatLng(latVAl, longVAl);
        mMap.addMarker(new MarkerOptions().position(LOCATION).title(addressVall));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LOCATION));
    }
}
