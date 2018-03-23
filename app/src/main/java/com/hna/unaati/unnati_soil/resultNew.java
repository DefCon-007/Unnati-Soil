package com.hna.unaati.unnati_soil;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class resultNew extends AppCompatActivity {

    private EditText ed_sand,ed_clay,ed_ph,ed_oc;
    private TextView tv_nitrogen;
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
        Bundle b = getIntent().getExtras();
        prediction p = new prediction(b.getDouble("sand"),b.getDouble("clay"),b.getDouble("ph"),b.getDouble("carbon"));
        ed_clay.setText(String.valueOf(p.clay));
        ed_sand.setText(String.valueOf(p.sand));
        ed_ph.setText(String.valueOf(p.pH));
        ed_oc.setText(String.valueOf(p.organicCarbon));
        tv_nitrogen.setText(String.valueOf(p.getTotalNitorgen())+"%");

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void initialiseVariables(){
        ed_clay = (EditText) findViewById(R.id.edClay);
        ed_sand = (EditText) findViewById(R.id.edSand);
        ed_oc = (EditText) findViewById(R.id.edOC);
        ed_ph = (EditText) findViewById(R.id.edpH);
        tv_nitrogen = (TextView) findViewById(R.id.tvNitrogenContent);
    }

}
