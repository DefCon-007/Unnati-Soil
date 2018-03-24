package com.hna.unaati.unnati_soil;

import android.content.Intent;
import android.graphics.Typeface;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.materialdrawer.DrawerBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class resultNew extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback,LocationListener {

    private EditText ed_sand,ed_clay,ed_ph,ed_oc;
    private TextView tv_nitrogen,lime,tvLimeContent,tvDolo;
    private MapView mapView;
    private GoogleMap gmap;
    private MarkerOptions mMarkerOptions;
    private  Marker mMarker;
    private gpsTracker gps;
    private Button recal ;
    private prediction pd;
    public DecimalFormat df = new DecimalFormat("####0.00");
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
                prediction pd = new prediction(sand,clay,ph,carbon);
                fillViews(pd);
            }
        });

        try {
        Bundle b = getIntent().getExtras();
        Log.d("result","Getting data from intent");
        double lat = b.getDouble("lat");
        double lon = b.getDouble("lon");
            locData = new locationData(this.getApplicationContext(),lat, lon);
        }
        catch (Exception e){
            Log.d("result","Estimating data");

        }
        sand = locData.getSand();
        clay = locData.getClay();
        ph = locData.getph();
        carbon = locData.getCarbon();
        pd = new prediction(sand,clay,ph,carbon);
        fillViews(pd);
        addSeprator("Nitrogen","NitrogenDEsc");
        addNitrogen("nitrogen");
        addPhos();
        addPot();
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

    private void addSeprator(String name,String Desc){
        LinearLayout myRoot = (LinearLayout) findViewById(R.id.llOuter);
        LinearLayout a = new LinearLayout(this);
        a.setPadding(6,6,6,6);
        a.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT );

        TextView tvlabel = new TextView(this);
        tvlabel.setLayoutParams(layoutParams);
        tvlabel.setTextSize(20);
        tvlabel.setTypeface(null, Typeface.BOLD_ITALIC);
        tvlabel.setText((CharSequence) name);



        TextView tvamnt = new TextView(this);
        tvamnt.setTextSize(15);
//        tvamnt.m
        tvamnt.setLayoutParams(layoutParams);
        tvamnt.setText((CharSequence) Desc);

        a.addView(tvlabel);
        a.addView(tvamnt);
        myRoot.addView(a);
    }

    private void fillViews(prediction p){

        ed_clay.setText(String.valueOf(p.clay));
        ed_sand.setText(String.valueOf(p.sand));
        ed_ph.setText(String.valueOf(p.pH));
        ed_oc.setText(String.valueOf(p.organicCarbon));
        tv_nitrogen.setText(String.valueOf(p.getNitrogenVolumeRice())+"t/ha");
        //Lime recommendation according to pH
        if ((ph>4.5)  && (ph<5.5) ){
            // quantity = kg of limeStone/ NV
            double quant = p.getLimeStoneValueLow()/1.1 ;
            lime.setVisibility(View.VISIBLE);
            tvDolo.setVisibility(View.VISIBLE);
            tvLimeContent.setVisibility(View.VISIBLE);
            lime.setText(getString(R.string.lime_reco));
            lime.setText(getString(R.string.lime_reco));
            tvLimeContent.setText(String.valueOf(df.format(quant)) + " " + getString(R.string.kg_ha));
        }
        else if ((ph>5.5) && (ph<6.5)){
            double quant = p.getLimeStoneValueHigh()/1.1 ;
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


        // Adding fertiliser views
    }
    private void addFertiliserResult(String label, String value){

        LinearLayout myRoot = (LinearLayout) findViewById(R.id.llOuter);
        LinearLayout a = new LinearLayout(this);
        a.setPadding(5,5,5,5);
        a.setOrientation(LinearLayout.HORIZONTAL);
        a.setWeightSum(1);
        a.setPadding(5,5,5,5);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, (float) 0.5);

        TextView tvlabel = new TextView(this);
        tvlabel.setLayoutParams(layoutParams);
        tvlabel.setText((CharSequence) label);



        TextView tvamnt = new TextView(this);
        tvamnt.setLayoutParams(layoutParams);
        tvamnt.setText((CharSequence) value);

        a.addView(tvlabel);
        a.addView(tvamnt);
        myRoot.addView(a);
    }


//    public void calculateNitrogenFertilizer(){
//        try {
////            Log.d("jsoc",loadJSONFromAsset("nitrogen"));
////            JSONObject obj = new JSONObject(loadJSONFromAsset("nitrogen"));
//            JSONArray m_jArry = obj.getJSONArray("formules");
//            ArrayList<HashMap<String, Double>> formList = new ArrayList<HashMap<String, Double>>();
//            HashMap<String, Double> m_li;
//
//            for (int i = 0; i < m_jArry.length(); i++) {
//                JSONObject jo_inside = m_jArry.getJSONObject(i);
////                Log.d("Details-->", jo_inside.getString("formule"));
//                String name = jo_inside.getString("name");
//                Double per = Double.valueOf(jo_inside.getString("percentage"));
//                Double fertliserAmount = pd.getNitrogenFertilizer(per);
//                Log.d("NIT",name);
//                addFertiliserResult(name,String.valueOf(fertliserAmount) + " " + getString(R.string.kg_ha));
//
////                //Add your values in your `ArrayList` as below:
////                m_li = new HashMap<String, Double>();
////                m_li.put("name", name);
////                m_li.put("per", per);
////
////                formList.add(m_li);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//

    public void addNitrogen(String fileName) {
        try {
            InputStreamReader is = new InputStreamReader(this.getAssets()
                    .open(fileName+".csv"));

            BufferedReader reader = new BufferedReader(is);
            reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {
                String[] separated = line.split(",");
                Double fertliserAmount = pd.getNitrogenFertilizer(Double.parseDouble(separated[1]));
                addFertiliserResult(separated[0],String.valueOf(df.format(fertliserAmount)) + " " + getString(R.string.kg_ha));
                //Log.d("read", separated[3]);

//                JSONObject soil_data = new JSONObject();
//                try {
//                    soil_data.put("sand",  Double.parseDouble(separated[7]));
//                    soil_data.put("clay",  Double.parseDouble(separated[8]));
//                    soil_data.put("ph",  Double.parseDouble(separated[9]));
//                    soil_data.put("carbon",  Double.parseDouble(separated[10]));
//
//                    if(cities_latlng.has(separated[3]+","+separated[4])) {
//                        cities_latlng.getJSONObject(separated[3]+","+separated[4]).put(separated[6], soil_data); //depth - data
//                    }
//                    else {
//                        JSONObject soil_data_depth = new JSONObject();
//                        soil_data_depth.put(separated[6], soil_data);
//                        cities_latlng.put(separated[3]+","+separated[4], soil_data_depth);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
            }
//            Log.d("read", cities_latlng.toString());
        }
        catch (IOException e) {
            Log.d("read", e.toString());

        }
    }
    public void addPhos() {
        try {
            InputStreamReader is = new InputStreamReader(this.getAssets()
                    .open("phos.csv"));

            BufferedReader reader = new BufferedReader(is);

            String line;
            while ((line = reader.readLine()) != null) {

                String[] separated = line.split(",");
                Double fertliserAmount = pd.getPhosphorusFertilizer(Double.parseDouble(separated[1]));
                addFertiliserResult(separated[0],String.valueOf(df.format(fertliserAmount)) + " " + getString(R.string.kg_ha));
                //Log.d("read", separated[3]);

//                JSONObject soil_data = new JSONObject();
//                try {
//                    soil_data.put("sand",  Double.parseDouble(separated[7]));
//                    soil_data.put("clay",  Double.parseDouble(separated[8]));
//                    soil_data.put("ph",  Double.parseDouble(separated[9]));
//                    soil_data.put("carbon",  Double.parseDouble(separated[10]));
//
//                    if(cities_latlng.has(separated[3]+","+separated[4])) {
//                        cities_latlng.getJSONObject(separated[3]+","+separated[4]).put(separated[6], soil_data); //depth - data
//                    }
//                    else {
//                        JSONObject soil_data_depth = new JSONObject();
//                        soil_data_depth.put(separated[6], soil_data);
//                        cities_latlng.put(separated[3]+","+separated[4], soil_data_depth);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
            }
//            Log.d("read", cities_latlng.toString());
        }
        catch (IOException e) {
            Log.d("read", e.toString());

        }
    }


    public void addPot() {
        Log.d("CHECK","Add pot called");
        try {
            InputStreamReader is = new InputStreamReader(this.getAssets()
                    .open("pot.csv"));

            BufferedReader reader = new BufferedReader(is);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] separated = line.split(",");
                Double fertliserAmount = pd.getPotassiumFertilizer(Double.parseDouble(separated[1]));
                addFertiliserResult(separated[0],String.valueOf(df.format(fertliserAmount)) + " " + getString(R.string.kg_ha));
                //Log.d("read", separated[3]);

//                JSONObject soil_data = new JSONObject();
//                try {
//                    soil_data.put("sand",  Double.parseDouble(separated[7]));
//                    soil_data.put("clay",  Double.parseDouble(separated[8]));
//                    soil_data.put("ph",  Double.parseDouble(separated[9]));
//                    soil_data.put("carbon",  Double.parseDouble(separated[10]));
//
//                    if(cities_latlng.has(separated[3]+","+separated[4])) {
//                        cities_latlng.getJSONObject(separated[3]+","+separated[4]).put(separated[6], soil_data); //depth - data
//                    }
//                    else {
//                        JSONObject soil_data_depth = new JSONObject();
//                        soil_data_depth.put(separated[6], soil_data);
//                        cities_latlng.put(separated[3]+","+separated[4], soil_data_depth);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
            }
//            Log.d("read", cities_latlng.toString());
        }
        catch (IOException e) {
            Log.d("read", e.toString());

        }
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
                .draggable(true);
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
        if (mMarker != null)
            return mMarker.getPosition();
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
