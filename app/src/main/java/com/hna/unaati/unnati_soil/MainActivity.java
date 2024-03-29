package com.hna.unaati.unnati_soil;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.gesture.Prediction;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback,LocationListener {
    private prediction p;
    private MapView mapView;
    private GoogleMap gmap;
    private static final String PREFS_NAME = "UnnatiPref1kdvbbvw";
    private SharedPreferences shPref ;
    private MarkerOptions mMarkerOptions;
    private  Marker mMarker;
    public static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";



    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(22.3218, 87.3074);
    private double lat,lang;
    private static final int DEFAULT_ZOOM = 15;


    private FloatingSearchView mSearchView;
    private boolean fabClicked = false ;

    private Context mContext;

    private gpsTracker gps;
    private locationData locData;
    private FloatingActionButton fabLocation, fabResult;
    JSONObject labs_latlng = new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("CHECK", "In main activity");

        try {
            InputStreamReader is = new InputStreamReader(getAssets()
                    .open("soilNew.csv"));

            BufferedReader reader = new BufferedReader(is);
            reader.readLine();
            String line;
            int i =0;
            while ((line = reader.readLine()) != null) {
                i+=1;
                if (i == 1)
                    continue; //to bypass header
                String[] separated = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                //Log.d("read", separated[3]);
                JSONObject data_lab = new JSONObject();
                try {
                    if (separated.length == 36) {
                        data_lab.put("name", separated[8]);
                        data_lab.put("email", separated[11]);
                        data_lab.put("mobile", separated[12]);
                        labs_latlng.put(separated[34]+","+separated[35], data_lab);
                    }
                    else {
                        Log.d("read", "except");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            Log.d("read", labs_latlng.toString());
        }
        catch (IOException e) {
            Log.d("read", e.toString());

        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initialiseVariables();


        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
               updateLocation(gps.getLocartionFromString(currentQuery,mContext));
            }
        });
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                //get suggestions based on newQuery

                //pass them on to the search view
//                mSearchView.swapSuggestions(newSuggestions);
            }
        });



        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabClicked = (fabClicked) ? false : true ;




//
//                Snackbar.make(view, getString(R.string.gps_permission_denied), Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }

        });

        fabResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(MainActivity.this, resultNew.class);
                Bundle b = new Bundle();

                b.putDouble("lat", mMarker.getPosition().latitude); //Optional parameters
                b.putDouble("lon", mMarker.getPosition().longitude); //Optional parameters
//                b.putDouble("clay", clay);
//                b.putDouble("ph", ph);
//                b.putDouble("carbon", carbon);
                myIntent.putExtras(b);


                MainActivity.this.startActivity(myIntent);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);



    }

    private void initialiseVariables(){
        mContext = this.getApplicationContext();
        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        mMarkerOptions = new MarkerOptions().position(mDefaultLocation)
                .draggable(true)
                .title("My location");
        gps = new gpsTracker(this);
        fabLocation = (FloatingActionButton) findViewById(R.id.fab_location);
        fabResult = (FloatingActionButton) findViewById(R.id.fab_result);
        lat= mDefaultLocation.latitude;
        lang = mDefaultLocation.longitude;

    }

    public String getAddress(LatLng position) {
        //Function to get information about location from latitude and longitude
        double lat = position.latitude ;
        double lng = position.longitude ;
        Geocoder geocoder = new Geocoder(this.getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();

            Log.v("IGA", "Address" + add);
            return obj.getLocality() + "," + obj.getSubAdminArea();
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return getString(R.string.default_marker_title);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else
            if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            Intent myIntent = new Intent(MainActivity.this, chat.class);
            MainActivity.this.startActivity(myIntent);
        } else if (id == R.id.nav_call) {
            Intent intent = new Intent(Intent.ACTION_CALL);

            intent.setData(Uri.parse("tel:" + "18001801551"));
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},1);
                startActivity(intent);
            }
            else
            {
                startActivity(intent);
            }
        } else if (id == R.id.nav_announcement) {
            Intent myIntent = new Intent(MainActivity.this, announcement.class);
            MainActivity.this.startActivity(myIntent);
        } else if (id == R.id.nav_language) {
            showLanguageSelector();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;


        mMarker = gmap.addMarker(mMarkerOptions);

        Iterator<String> iter = labs_latlng.keys();
        int i = 0;
        while (iter.hasNext()) {
            String key = iter.next();
            String[] lat_lng = key.split(",");
//            try {
                Log.d("read", "start");
                NumberFormat nf = NumberFormat.getInstance();
                double lat = 0, lon = 0;
                try {
                    lat = nf.parse(lat_lng[0]).doubleValue();
                    lon = nf.parse(lat_lng[1]).doubleValue();

                JSONObject obj = labs_latlng.getJSONObject(key);
                LatLng temp = new LatLng(lat,lon);
                gmap.addMarker(new MarkerOptions().position(temp)
                        .title(obj.getString("name"))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        .snippet("Phone: " + obj.getString("mobile") + ", Email:" + obj.getString("email")));
                Log.d("read", temp.toString());
                Log.d("read", "end");
                }
                catch (Exception e) {
                    Log.d("read", e.toString());
                }

//            }

        }


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

        gps.getLocation();
        if (gps.canGetLocation){
            LatLng currentPos = new LatLng(gps.getLatitude(),gps.getLongitude());
            updateLocation(currentPos);
            Log.d("read" , "Lat;"+String.valueOf(gps.getLatitude()));
            Log.d("read", "Lon:"+String.valueOf(gps.getLongitude()));

            lat = gps.getLatitude();
            lang = gps.getLongitude();

        }
        else {
            updateLocation(mDefaultLocation);
        }
    }

    private void updateLocation(LatLng position) {
        mMarker.setPosition(position);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(position));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

        gmap.animateCamera(zoom);

    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String permissions[],
//                                           @NonNull int[] grantResults) {
//        mLocationPermissionGranted = false;
//        switch (requestCode) {
//            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    mLocationPermissionGranted = true;
//                }
//            }
//        }
////        updateLocationUI();
//    }
//    private void getLocationPermission() {
//    /*
//     * Request location permission, so that we can get the location of the
//     * device. The result of the permission request is handled by a callback,
//     * onRequestPermissionsResult.
//     */
//        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            mLocationPermissionGranted = true;
//        } else {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//        }
//    }

    @Override
    public void onLocationChanged(Location location) {
        if (fabClicked) {
            updateLocation(new LatLng(location.getLatitude(),location.getLongitude()));
        }
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



//    private void getDeviceLocation() {
//        /*
//         * Get the best and most recent location of the device, which may be null in rare
//         * cases when a location is not available.
//         */
//        try {
//            if (mLocationPermissionGranted) {
//                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
//                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Location> task) {
//                        if (task.isSuccessful()) {
//                            // Set the map's camera position to the current location of the device.
//                            mLastKnownLocation = task.getResult();
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                                    new LatLng(mLastKnownLocation.getLatitude(),
//                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
//                        } else {
//                            Log.d(TAG, "Current location is null. Using defaults.");
//                            Log.e(TAG, "Exception: %s", task.getException());
//                            mMap.moveCamera(CameraUpdateFactory
//                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
//                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
//                        }
//                    }
//                });
//            }
//        } catch (SecurityException e)  {
//            Log.e("Exception: %s", e.getMessage());
//        }
//    }

    public void showLanguageSelector(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setIcon(R.drawable.translation);
        builderSingle.setTitle(getString(R.string.main_activity_language_select));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("English");
        arrayAdapter.add("বাঙালি");
        arrayAdapter.add("हिंदी");

        builderSingle.setNegativeButton(getString(R.string.main_activity_language_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0 :
                        changeLanguage("en");
                        break;
                    case 1 :
                        changeLanguage("ben");
                        break;
                    case 2 :
                        changeLanguage("hi");
                    default:
                        break;
                }

            }
        });
        builderSingle.show();
    }


    private void changeLanguage(String languageToLoad){
        shPref = this.getSharedPreferences(PREFS_NAME, 0);
        shPref.edit().putString("language", languageToLoad).apply();
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config,getResources().getDisplayMetrics());


    }



}
