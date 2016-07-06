package es.uc3m.manager.activities.scan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import es.uc3m.manager.R;

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
        ImageButton button = (ImageButton) findViewById(R.id.cancelButton);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                }
        );
    }

    private void okScanListener() {

        ImageButton button = (ImageButton) findViewById(R.id.okButton);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView codeView = (TextView) findViewById(R.id.editText3);
                        if (codeView != null && codeView.getText() != null && !codeView.getText().toString().isEmpty()) {
                            String code = codeView.getText().toString();
                            Intent menuIntent = new Intent(getApplicationContext(), ScanMenuActivity.class);
                            menuIntent.putExtra("SCAN_RESULT", code);
                            startActivity(menuIntent);
                        }
                    }
                }
        );
    }

}
