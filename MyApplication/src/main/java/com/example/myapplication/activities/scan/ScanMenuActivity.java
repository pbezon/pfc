package com.example.myapplication.activities.scan;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
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

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class ScanMenuActivity extends Activity {


    private static final int REQUEST_SCAN_CODE = 0;
    private String scannedCode;

    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "ScanMenuActivity";

    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //nfcForegroundUtil = new NFCForegroundUtil(this);
        //resolveIntent(getIntent());
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();

        }

        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "NFC OK: " + scannedCode, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "NFC KO!!: " + scannedCode, Toast.LENGTH_SHORT).show();
        }

        handleIntent(getIntent());
        scannedCode = this.getIntent().getStringExtra("SCAN_RESULT");
        setContentView(R.layout.activity_scan_menu);
        setUpButtonListeners();
        Toast.makeText(getApplicationContext(), "ID READ: " + scannedCode, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);

    }

    @Override
    public void onResume() {
        super.onResume();
        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, mNfcAdapter);
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
                            returnActivity.putExtra("ID", scannedCode);
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
                            displayResults.putExtra("ID", scannedCode);
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
                            displayResults.putExtra("ID", scannedCode);
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
                            displayResults.putExtra("ID", scannedCode);
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


    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, mNfcAdapter);

        super.onPause();
    }


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
            intent.putExtra("SCAN_RESULT", msgs.toString());
        } else {
            Log.e("TAG", "Unknown intent " + intent);
        }
    }


    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    /**
     * @param activity The corresponding {@link Activity } requesting to stop the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
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


    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                getIntent().putExtra("SCAN_RESULT", result);
                scannedCode = result;
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }
        }
    }
}
