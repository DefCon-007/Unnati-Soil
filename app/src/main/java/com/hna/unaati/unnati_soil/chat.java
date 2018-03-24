package com.hna.unaati.unnati_soil;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class chat extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    ListView listView;
    private void setText(final ArrayAdapter<String> adapter){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        // Mapping with your XML view
        Button clickButton = (Button) findViewById(R.id.btn);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                EditText edit = (EditText)findViewById(R.id.msg);
                String result = edit.getText().toString();
                Log.d("read","clicked");
                if (result.length() != 0) {
                    Log.d("read","clicked2");
                    post_message(result);
                }

                refresh_content();
            }
        });
        refresh_content();

        // Instanciating Adapter

    }

    public void refresh_content() {

        /// Getting list of Strings from your resource
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    try {
                        listView = (ListView) findViewById(R.id.listview);

                        URL url = null;
                        String response = null;
                        Log.d("read", "started");
                        String parameters = "param1=value1&param2=value2";
                        url = new URL("https://salty-castle-46140.herokuapp.com/getchats");
                        //create the connection
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoOutput(true);
                        connection.setRequestProperty("Content-Type",
                                "application/x-www-form-urlencoded");
                        //set the request method to GET
                        connection.setRequestMethod("GET");
                        //get the output stream from the connection you created
                        OutputStreamWriter request = new OutputStreamWriter(connection.getOutputStream());
                        //write your data to the ouputstream
                        request.write(parameters);
                        request.flush();
                        request.close();
                        String line = "";
                        //create your inputsream
                        Log.d("read", "start2");
                        InputStreamReader isr = new InputStreamReader(
                                connection.getInputStream());
                        //read in the data from input stream, this can be done a variety of ways
                        BufferedReader reader = new BufferedReader(isr);
                        StringBuilder sb = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        Log.d("read", "start3");
                        //get the string version of the response data
                        response = sb.toString();
                        //do what you want with the data now

                        //always remember to close your input and output streams
                        isr.close();
                        reader.close();
                        JSONObject temp = new JSONObject(response);
                        Log.d("read", response);
                        Log.d("read", temp.toString());

                        JSONArray t = temp.getJSONArray("output");
                        response =  t.toString();
                        String[] testArray = response.split("\",\"");
                        if (testArray.length >= 1) {
                            testArray[0] = testArray[0].substring(2);
                        }

                        List<String> testList = Arrays.asList(testArray);
                        adapter = new ArrayAdapter<>(getBaseContext(),
                                android.R.layout.simple_list_item_1, testList);
                        setText(adapter);

                        // setting adapter on listview

                    } catch (IOException e) {
                        Log.e("HTTP GET:", e.toString());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }

    public void post_message(final String message) {

        /// Getting list of Strings from your resource
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    try {
                        listView = (ListView) findViewById(R.id.listview);

                        URL url = null;
                        String response = null;
                        Log.d("read", "started here");
                        String parameters = "sender=user&message=" + message;
                        url = new URL("https://salty-castle-46140.herokuapp.com/chat2");
                        //create the connection
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoOutput(true);
                        connection.setRequestProperty("Content-Type",
                                "application/x-www-form-urlencoded");
                        //set the request method to GET
                        connection.setRequestMethod("GET");
                        //get the output stream from the connection you created
                        OutputStreamWriter request = new OutputStreamWriter(connection.getOutputStream());
                        //write your data to the ouputstream
                        request.write(parameters);
                        request.flush();
                        request.close();
                        String line = "";
                        //create your inputsream
                        Log.d("read", "start2");
                        InputStreamReader isr = new InputStreamReader(
                                connection.getInputStream());
                        //read in the data from input stream, this can be done a variety of ways
                        BufferedReader reader = new BufferedReader(isr);
                        StringBuilder sb = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        Log.d("read", "start3");
                        //get the string version of the response data
                        response = sb.toString();
                        //do what you want with the data now

                        //always remember to close your input and output streams
                        isr.close();
                        reader.close();
                        Log.d("read", response);
                    } catch (IOException e) {
                        Log.e("HTTP GET:", e.toString());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }
}
