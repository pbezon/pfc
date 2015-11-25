package com.example.myapplication.activities.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.myapplication.R;

/**
 * Created by Snapster on 11/19/2015.
 */
public class SettingsActivity extends Activity{

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch toggle = (Switch) findViewById(R.id.switch2);
        toggle.setChecked(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("manualScan", false));
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences  = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if (isChecked) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("manualScan", true);
                    editor.commit();
                } else {
                    // The toggle is disabled
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("manualScan", false);
                    editor.commit();
                }

            }
        });
    }
}
