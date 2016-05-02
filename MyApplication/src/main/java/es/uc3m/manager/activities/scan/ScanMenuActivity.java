package es.uc3m.manager.activities.scan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import es.uc3m.manager.R;
import es.uc3m.manager.activities.library.AddActivity;
import es.uc3m.manager.activities.library.EditActivity;
import es.uc3m.manager.activities.library.RemoveActivity;
import es.uc3m.manager.activities.library.ReturnActivity;

public class ScanMenuActivity extends Activity {

    public static final String TAG = "ScanMenuActivity";
    private static final int REQUEST_SCAN_CODE = 0;
    private String scannedCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannedCode = this.getIntent().getStringExtra("SCAN_RESULT");
        setContentView(R.layout.activity_scan_menu);
        setUpButtonListeners();
        Toast.makeText(getApplicationContext(), "ID READ: " + scannedCode, Toast.LENGTH_LONG).show();
    }

    /*
     * Menu bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scan_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * Private methods
     */
    private void setUpButtonListeners() {
        this.addAddListener();
        this.addSearchListener();
        this.addRemoveListener();
        this.addReturnListener();
    }


    private void addReturnListener() {
        Button returnButton = (Button) findViewById(R.id.returnButton);
        setupActivityListener(returnButton, ReturnActivity.class);
    }

    private void addAddListener() {
        Button addButton = (Button) findViewById(R.id.addButton);
        setupActivityListener(addButton, AddActivity.class);
    }

    private void addRemoveListener() {
        Button removeButton = (Button) findViewById(R.id.removeButton);
        setupActivityListener(removeButton, RemoveActivity.class);
    }

    private void addSearchListener() {
        Button searchButton = (Button) findViewById(R.id.searchButton);
        setupActivityListener(searchButton, EditActivity.class);
    }

    private void setupActivityListener(Button button, final Class clazz) {
        try {
            button.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //lanzamos intent
                            Intent returnActivity = new Intent(getBaseContext(), clazz);
                            returnActivity.putExtra("ID", scannedCode);
                            //arrancamos intent
                            startActivity(returnActivity);
                        }
                    }
            );
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

}
