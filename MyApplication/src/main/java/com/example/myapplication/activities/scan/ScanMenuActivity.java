package com.example.myapplication.activities.scan;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.library.AddActivity;
import com.example.myapplication.activities.library.EditActivity;
import com.example.myapplication.activities.library.RemoveActivity;
import com.example.myapplication.activities.library.ReturnActivity;

public class ScanMenuActivity extends Activity {


    private static final int REQUEST_SCAN_CODE = 0;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //nfcForegroundUtil = new NFCForegroundUtil(this);
        //resolveIntent(getIntent());
        result = this.getIntent().getStringExtra("SCAN_RESULT");
        setContentView(R.layout.activity_scan_menu);
        setUpButtonListeners();
        Toast.makeText(getApplicationContext(), "ID READ: " + result, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNewIntent(Intent intent) {
        //Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setUpButtonListeners() {
        // tabbed
//        this.addAddTabListener();
//        this.addSearchListener();
//        this.addRemoveTabListener();
//        this.addReturnTabListener();
//        this.addWithdrawTabListener();

        this.addAddListener();
        this.addSearchListener();
        this.addRemoveListener();
        this.addReturnListener();
    }


    private void addReturnListener() {
        Button find = (Button) findViewById(R.id.returnButton);
        try {
            //comportamiento FIND
            find.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //lanzamos intent
                            Intent returnActivity = new Intent(getBaseContext(), ReturnActivity.class);
                            returnActivity.putExtra("ID", result);
                            //arrancamos intent
                            startActivity(returnActivity);
                        }
                    }
            );
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
            e.printStackTrace();
        }

    }

    private void addAddListener() {
        Button find = (Button) findViewById(R.id.addButton);
        try {
            //comportamiento FIND
            find.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //lanzamos intent
                            Intent displayResults = new Intent(getBaseContext(), AddActivity.class);
                            displayResults.putExtra("ID", result);
                            //arrancamos intent
                            displayResults.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(displayResults);
                        }
                    }
            );
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
            e.printStackTrace();
        }

    }

    private void addRemoveListener() {
        Button find = (Button) findViewById(R.id.removeButton);
        try {
            //comportamiento FIND
            find.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //lanzamos intent
                            Intent displayResults = new Intent(getBaseContext(), RemoveActivity.class);
                            displayResults.putExtra("ID", result);
                            //arrancamos intent
                            startActivity(displayResults);
                        }
                    }
            );
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
            e.printStackTrace();
        }

    }

    private void addSearchListener() {
        Button find = (Button) findViewById(R.id.searchButton);
        try {
            //comportamiento FIND
            find.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //lanzamos intent
                            Intent displayResults = new Intent(getBaseContext(), EditActivity.class);
                            displayResults.putExtra("ID", result);
                            //arrancamos intent
                            startActivity(displayResults);
                        }
                    }
            );
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
            e.printStackTrace();
        }

    }

    /*
     * NFC STUFF
     */

    void resolveIntent(Intent intent) {
        // Parse the intent
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            // When a tag is discovered we send it to the service to be save. We
            // include a PendingIntent for the service to call back onto. This
            // will cause this activity to be restarted with onNewIntent(). At
            // that time we read it from the database and view it.
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[]{};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
                msgs = new NdefMessage[]{msg};
            }
            // Setup the views
            setTitle("scanned");
        } else {
            Log.e("TAG", "Unknown intent " + intent);
        }
    }

    /*
     * AUTOGENERATED
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

//    /**
//     * A placeholder fragment containing a simple view.
//     */
//    public static class PlaceholderFragment extends Fragment {
//
//        public PlaceholderFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_scan_menu, container, false);
//            return rootView;
//        }
//    }

}
