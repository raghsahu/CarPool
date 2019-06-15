package com.fortin.carpool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String rlat,rlong,TAG="Maps",rplace;
    ArrayList<LatLng> points=new ArrayList<>();
    PolylineOptions lineoptions=new PolylineOptions();
    String plat[],plong[],place[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.backicon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent it=getIntent();
        rplace=it.getStringExtra("place");
        rlat=it.getStringExtra("lat");
        rlong=it.getStringExtra("long");
        plat=rlat.split(" > ");
        plong=rlong.split(" > ");
        place=rplace.split(" > ");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for(int i=0;i<place.length;i++) {
            LatLng sydney = new LatLng(Double.parseDouble(plat[i]), Double.parseDouble(plong[i]));
            mMap.addMarker(new MarkerOptions().position(sydney).title(place[i]));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            points.add(sydney);
        }
        lineoptions.addAll(points);
        lineoptions.color(R.color.colorPrimary);
        lineoptions.width(3);
        mMap.addPolyline(lineoptions);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
