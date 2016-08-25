package es.uc3m.manager.activities.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import es.uc3m.manager.R;
import es.uc3m.manager.service.ItemService;
import es.uc3m.manager.util.Constants;

/**
 * Created by Snapster on 11/19/2015.
 */
public class SettingsActivity extends Activity {

    public static final String PATH = File.separator + "StuffManager";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Switch toggle = (Switch) findViewById(R.id.settingsManualSwitch);
        toggle.setChecked(preferences.getBoolean("manualScan", false));
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    SharedPreferences.Editor editor = preferences.edit();
                    if (buttonView.equals(findViewById(R.id.settingsManualSwitch))) {
                        editor.putBoolean("manualScan", true);
                    }
                    if (buttonView.equals(findViewById(R.id.settingsOfflineSwitch))) {
                        editor.putBoolean("offline", true);
                    }
                    editor.apply();
                } else {
                    // The toggle is disabled
                    SharedPreferences.Editor editor = preferences.edit();
                    if (buttonView.equals(findViewById(R.id.settingsManualSwitch))) {
                        editor.putBoolean("manualScan", false);
                    }
                    if (buttonView.equals(findViewById(R.id.settingsOfflineSwitch))) {
                        editor.putBoolean("offline", false);
                    }
                    editor.apply();
                }

            }
        });

        Switch toogleOffline = (Switch) findViewById(R.id.settingsOfflineSwitch);
        toogleOffline.setChecked(preferences.getBoolean("offline", false));
        toogleOffline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("offline", true);
                    Constants.OFFLINE = true;
                    editor.apply();
                } else {
                    // The toggle is disabled
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("offline", false);
                    Constants.OFFLINE = false;
                    editor.apply();
                }

            }
        });

        Button reloadButton = (Button) findViewById(R.id.reloadServer);
        final String ip = ((TextView)findViewById(R.id.serverIp)).getText().toString();
        final String port = ((TextView)findViewById(R.id.serverIp)).getText().toString();
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = ItemService.getInstance().reload(ip, port);
                if (result) {
                    preferences.edit().putString("serverip", ip);
                    preferences.edit().putString("serverport", port);
                    Toast.makeText(getApplicationContext(), "Reload ok!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error reloading server config", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
