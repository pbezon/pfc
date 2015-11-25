package com.example.myapplication.activities.scan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.activities.MainActivity;

/**
 * Created by Snapster on 11/25/2015.
 */
public class ManualInputActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_insert);
        this.okScanListener();
        this.cancelScanListener();
    }

    private void cancelScanListener() {
        Button button = (Button) findViewById(R.id.cancelButton);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent mainManu = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mainManu);
                    }
                }
        );

    }

    private void okScanListener() {

        Button button = (Button) findViewById(R.id.okButton);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView codeView = (TextView) findViewById(R.id.editText3);
                        if (codeView != null && codeView.getText() != null && !codeView.getText().toString().isEmpty()) {
                            String code = codeView.getText().toString();
                            Intent menuIntent = new Intent(getApplicationContext(), ScanMenuActivity.class);
                            menuIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            menuIntent.putExtra("SCAN_RESULT", code);
                            startActivity(menuIntent);
                        }
                    }
                }
        );
    }

}
