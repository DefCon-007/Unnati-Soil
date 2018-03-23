package com.hna.unaati.unnati_soil;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.materialdrawer.DrawerBuilder;

import java.text.DecimalFormat;

public class resultNew extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback,LocationListener {

    private EditText ed_sand,ed_clay,ed_ph,ed_oc;
    private TextView tv_nitrogen,lime,tvLimeContent,tvDolo;
    private MapView mapView;
    private GoogleMap gmap;
    private MarkerOptions mMarkerOptions;
    private  Marker mMarker;
    private gpsTracker gps;
    private Button recal ;

    private LatLng mDefaultLocation = new LatLng(22.3218, 87.3074);
    private int mDefaultZoom = 15;
    private locationData locData;
    private double sand,clay,ph,carbon;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_rice:
//                    mTextMessage.setText(R.string.title_rice);
                    return true;
                case R.id.navigation_maize:
//                    mTextMessage.setText(R.string.title_maize);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_new);
        initialiseVariables();
        recal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sand = Double.parseDouble(ed_sand.getText().toString());
                clay = Double.parseDouble(ed_clay.getText().toString());
                ph = Double.parseDouble(ed_ph.getText().toString());
                carbon = Double.parseDouble(ed_oc.getText().toString());
                prediction pNew = new prediction(sand,clay,ph,carbon);
                fillViews(pNew);
            }
        });

        try {
        Bundle b = getIntent().getExtras();
        Log.d("result","Getting data from intent");
        sand = b.getDouble("sand");
        clay = b.getDouble("clay");
        ph = b.getDouble("ph");
        carbon = b.getDouble("carbon");
        }
        catch (Exception e){
            Log.d("result","Estimating data");
            sand = locData.getSand();
            clay = locData.getClay();
            ph = locData.getph();
            carbon = locData.getCarbon();
        }
        prediction p = new prediction(sand,clay,ph,carbon);
        fillViews(p);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MainActivity.MAP_VIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
        new DrawerBuilder().withActivity(this).build();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void fillViews(prediction p){
        DecimalFormat df = new DecimalFormat("####0.00");
        ed_clay.setText(String.valueOf(p.clay));
        ed_sand.setText(String.valueOf(p.sand));
        ed_ph.setText(String.valueOf(p.pH));
        ed_oc.setText(String.valueOf(p.organicCarbon));
        tv_nitrogen.setText(String.valueOf(p.getNitrogenVolumeRice())+"t/ha");
        //Lime recommendation according to pH
        if ((ph>4.5)  && (ph<5.5) ){
            // quantity = kg of limeStone/ NV
            double quant = 600/1.1 ;
            lime.setVisibility(View.VISIBLE);
            tvDolo.setVisibility(View.VISIBLE);
            tvLimeContent.setVisibility(View.VISIBLE);
            lime.setText(getString(R.string.lime_reco));
            lime.setText(getString(R.string.lime_reco));
            tvLimeContent.setText(String.valueOf(df.format(quant)) + " " + getString(R.string.kg_ha));
        }
        else if ((ph>5.5) && (ph<6.5)){
            double quant = 900/1.1 ;
            tvDolo.setVisibility(View.VISIBLE);
            tvLimeContent.setVisibility(View.VISIBLE);
            lime.setVisibility(View.VISIBLE);
            lime.setText(getString(R.string.lime_reco));
            lime.setText(getString(R.string.lime_reco));
            tvLimeContent.setText(String.valueOf(df.format(quant)) + " " + getString(R.string.kg_ha));
        }
        else if (ph > 6.5){
            lime.setVisibility(View.VISIBLE);
            tvDolo.setVisibility(View.GONE);
            tvLimeContent.setVisibility(View.GONE);
            lime.setText(getString(R.string.no_lime));
        }
//        else {
//            tvDolo.setVisibility(View.GONE);
//            tvLimeContent.setVisibility(View.GONE);
//            lime.setVisibility(View.VISIBLE);
//            lime.setText(getString(R.string.lime_reco));
//        }
    }

    private void initialiseVariables(){
        ed_clay = (EditText) findViewById(R.id.edClay);
        ed_sand = (EditText) findViewById(R.id.edSand);
        ed_oc = (EditText) findViewById(R.id.edOC);
        ed_ph = (EditText) findViewById(R.id.edpH);
        tv_nitrogen = (TextView) findViewById(R.id.tvNitrogenContent);
        mapView = (MapView) findViewById(R.id.mapView);
        lime = (TextView) findViewById(R.id.limeReccom);
        gps = new gpsTracker(this.getApplicationContext());
        mMarkerOptions = new MarkerOptions().position(mDefaultLocation)
                .draggable(false);
        LatLng ll = getLocation();
        locData = new locationData(this.getApplicationContext(),ll.latitude,ll.longitude);
        tvDolo = (TextView) findViewById(R.id.tvLimeLabel);
        tvLimeContent = (TextView) findViewById(R.id.tvlimeContent);
        recal = (Button) findViewById(R.id.buttonRecalculate);
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MainActivity.MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MainActivity.MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(mDefaultZoom);
        mMarker = gmap.addMarker(mMarkerOptions);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(getLocation()));
        gmap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                updateLocation(marker.getPosition());
            }
        });
        gmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent i = new Intent(resultNew.this,MainActivity.class);
                startActivity(i);
            }
        });
//        gmap.setClickable(false);
        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(resultNew.this,MainActivity.class);
                startActivity(i);
            }
        });
    }

    private void updateLocation(LatLng position) {
        mMarker.setPosition(position);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(position));
//        mMarker.setTitle(getAddress(position));
    }

    private LatLng getLocation(){
        gps.getLocation();
        if (gps.canGetLocation){
            LatLng currentPos = new LatLng(gps.getLatitude(),gps.getLongitude());
//            updateLocation(currentPos);
//            lat = gps.getLatitude();
//            lang = gps.getLongitude();
            return currentPos;
        }
        return mDefaultLocation;
    }
}
