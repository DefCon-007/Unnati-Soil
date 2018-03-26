package com.hna.unaati.unnati_soil;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * Created by defcon on 24/03/18.
 */

public class locationData {
    private JSONObject cities_latlng = new JSONObject(); // avoid copy again
    private Context mContext;
    private double lat,lang,sand,clay,ph,carbon;


    public locationData(Context mContext,double lat, double lang){
        this.mContext = mContext;
        this.lang = lang;
        this.lat = lat ;
        getDataFromDataset();
        getEstimatedValues();
    }

    public double getSand(){
        return this.sand;
    }
    public double getClay(){
        return this.clay;
    }
    public double getph(){
        return this.ph;
    }
    public double getCarbon(){
        return this.carbon;
    }

    private void getEstimatedValues(){
        double min = 100000;
        Iterator<String> iter = cities_latlng.keys();
        String lat_lng_min = "";
        while (iter.hasNext()) {
            String key = iter.next();
            String[] lat_lng = key.split(",");
            double dist = distance(lat, lang, Double.parseDouble(lat_lng[0]), Double.parseDouble(lat_lng[1]));
            if (dist < min) {
                min = dist;
                lat_lng_min = key;
            }
        }
        try {
            JSONObject lat_lng_min_json = cities_latlng.getJSONObject(lat_lng_min).getJSONObject("nearest50");

            sand = lat_lng_min_json.getDouble("sand");
            clay = lat_lng_min_json.getDouble("clay");
            ph = lat_lng_min_json.getDouble("ph");
            carbon = lat_lng_min_json.getDouble("carbon");
            Log.d("read", lat_lng_min_json.toString());
            Log.d("read", String.valueOf(carbon));
        } catch (JSONException e) {

            Log.d("read", e.toString());
        }
    }

    private void getDataFromDataset(){

        try {
            InputStreamReader is = new InputStreamReader(mContext.getAssets()
                    .open("data.csv"));

            BufferedReader reader = new BufferedReader(is);
            reader.readLine();
            String line;
            int i =0;
            while ((line = reader.readLine()) != null) {
                i+=1;
                if (i == 1)
                    continue; //to bypass header
                String[] separated = line.split(",");
                //Log.d("read", separated[3]);

                JSONObject soil_data = new JSONObject();
                try {
                    soil_data.put("sand",  Double.parseDouble(separated[7]));
                    soil_data.put("clay",  Double.parseDouble(separated[8]));
                    soil_data.put("ph",  Double.parseDouble(separated[9]));
                    soil_data.put("carbon",  Double.parseDouble(separated[10]));

                    if(cities_latlng.has(separated[3]+","+separated[4])) {
                        cities_latlng.getJSONObject(separated[3]+","+separated[4]).put(separated[6], soil_data); //depth - data
                    }
                    else {
                        JSONObject soil_data_depth = new JSONObject();
                        soil_data_depth.put(separated[6], soil_data);
                        cities_latlng.put(separated[3]+","+separated[4], soil_data_depth);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            Log.d("read", cities_latlng.toString());
        }
        catch (IOException e) {
            Log.d("read", e.toString());

        }
        Iterator<String> iter = cities_latlng.keys();
        String lat_lng_min="";
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                JSONObject obj = cities_latlng.getJSONObject(key);
                Iterator<String> iter2 = obj.keys();
                String nearest50 = "0";
                while (iter2.hasNext()) {
                    String key2 = iter2.next();
                    nearest50 = key2;
                    if (Integer.parseInt(key2) > 50) {
                        break;
                    }
                }
                obj.put("nearest50", obj.getJSONObject(nearest50));
            }
            catch (JSONException e) {
                //
            }
        }
    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        char unit = 'K';
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
