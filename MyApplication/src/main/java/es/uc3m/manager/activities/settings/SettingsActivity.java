package es.uc3m.manager.activities.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CompoundButton;
import android.widget.Switch;

import es.uc3m.manager.R;
import es.uc3m.manager.activities.contacts.Constants;

/**
 * Created by Snapster on 11/19/2015.
 */
public class SettingsActivity extends Activity {

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch toggle = (Switch) findViewById(R.id.settingsManualSwitch);
        toggle.setChecked(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("manualScan", false));
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if (isChecked) {
                    SharedPreferences.Editor editor = preferences.edit();
                    if (buttonView.equals(findViewById(R.id.settingsManualSwitch))) {
                        editor.putBoolean("manualScan", true);
                    }
                    if (buttonView.equals(findViewById(R.id.settingsOfflineSwitch))) {
                        editor.putBoolean("offline", true);
                    }
                    editor.commit();
                } else {
                    // The toggle is disabled
                    SharedPreferences.Editor editor = preferences.edit();
                    if (buttonView.equals(findViewById(R.id.settingsManualSwitch))) {
                        editor.putBoolean("manualScan", false);
                    }
                    if (buttonView.equals(findViewById(R.id.settingsOfflineSwitch))) {
                        editor.putBoolean("offline", false);
                    }
                    editor.commit();
                }

            }
        });

        Switch toogleOffline = (Switch) findViewById(R.id.settingsOfflineSwitch);
        toogleOffline.setChecked(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("offline", false));
        toogleOffline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if (isChecked) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("offline", true);
                    Constants.OFFLINE = true;
                    editor.commit();
                } else {
                    // The toggle is disabled
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("offline", false);
                    Constants.OFFLINE = false;
                    editor.commit();
                }

            }
        });
    }
}
