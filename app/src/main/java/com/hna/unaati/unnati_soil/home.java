package com.hna.unaati.unnati_soil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.Locale;

public class home extends AppCompatActivity {
    private static final String PREFS_NAME = "UnnatiPref1kdvbbvw";
    private SharedPreferences shPref ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d("home","In home activity");
        shPref = this.getSharedPreferences(PREFS_NAME, 0);
        if (shPref.getBoolean("my_first_time", true)) {
            showLanguageSelector();
            shPref.edit().putBoolean("my_first_time", false).apply();

        }
        else {
            changeLanguage(shPref.getString("language","en"));
        }

    }

    public void showLanguageSelector(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(home.this);
        builderSingle.setIcon(R.drawable.translation);
        builderSingle.setTitle(getString(R.string.main_activity_language_select));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(home.this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("English");
        arrayAdapter.add("বাঙালি");

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
                    default:
                        break;
                }

            }
        });
        builderSingle.show();
    }


    private void changeLanguage(String languageToLoad){
        shPref.edit().putString("language", languageToLoad).apply();
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config,getResources().getDisplayMetrics());
        Intent i = new Intent(home.this,resultNew.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);

    }
}
