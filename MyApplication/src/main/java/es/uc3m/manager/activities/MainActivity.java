package es.uc3m.manager.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import es.uc3m.manager.R;
import es.uc3m.manager.activities.contacts.Constants;
import es.uc3m.manager.activities.display.DisplayResults;
import es.uc3m.manager.activities.scan.ManualInputActivity;
import es.uc3m.manager.activities.scan.ScanMenuActivity;
import es.uc3m.manager.activities.settings.SettingsActivity;

public class MainActivity extends Activity {

    private static final String MIME_TEXT_PLAIN = "text/plain";
    private static final String TAG = "ScanMenuActivity";
    private static final int REQUEST_SCAN_CODE = 0;
    private static final int REQUEST_CONTACTPICKER = 1;
    private static final int REQUEST_CALENDAR_EVENT = 2;
    private long event_id;
    private NfcAdapter mNfcAdapter;

    private static boolean isIntentAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static long getNewEventId(ContentResolver cr) {
        Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI, new String[]{"MAX(_id) as max_id"}, null, null, "_id");
        cursor.moveToFirst();
        long max_val = cursor.getLong(cursor.getColumnIndex("max_id"));
        cursor.close();
        return max_val + 1;
    }

    public static long getLastEventId(ContentResolver cr) {
        Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI, new String[]{"MAX(_id) as max_id"}, null, null, "_id");
        cursor.moveToFirst();
        cursor.close();
        return cursor.getLong(cursor.getColumnIndex("max_id"));
    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    private static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
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
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    private static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFolder();
        Constants.OFFLINE = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("offline", false);
        setContentView(R.layout.main_menu);
        this.addScanListener();
        this.addGetAllFromServerListener();
        this.addSettingsListener();

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        handleIntent(getIntent());
    }

    private void setupFolder() {
        File photoFolder = null;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            photoFolder = new File(Environment.getExternalStorageDirectory() + SettingsActivity.PATH);
        } else {
            /* save the folder in internal memory of phone */
            photoFolder = new File("/data/data" + SettingsActivity.PATH);
        }
        if (!photoFolder.exists()) {
            photoFolder.mkdir();
        }
    }

    private void addSettingsListener() {
        ImageButton getAll = (ImageButton) findViewById(R.id.settingsImageButton);
        getAll.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //lanzamos intent
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                    }
                }
        );
    }

    private void addGetAllFromServerListener() {
        ImageButton getAll = (ImageButton) findViewById(R.id.collectionImageButton);
        getAll.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //lanzamos intent
                        startActivity(new Intent(getApplicationContext(), DisplayResults.class));
                    }
                }
        );
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, mNfcAdapter);

        super.onPause();
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

    private void addScanListener() {
        final ImageButton scan = (ImageButton) findViewById(R.id.scanImageButton);
        //comportamiento SCAN
        scan.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        if (!preferences.getBoolean("manualScan", false)) {
                            //lanzamos intent
                            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                            if (MainActivity.isIntentAvailable(getApplicationContext(), intent)) {
                                //esperamos resultado
                                startActivityForResult(intent, REQUEST_SCAN_CODE);
                            } else {
                                Toast.makeText(getApplicationContext(), "Install zxing application for camera scanning", Toast.LENGTH_LONG).show();
                                intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("market://details?id=com.google.zxing.client.android"));
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Manual input..", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), ManualInputActivity.class);
                            startActivity(intent);
                        }
                    }
                }
        );
    }

//    private void addContactsListener() {
//        Button contacts = (Button) findViewById(R.id.readContacts);
//        contacts.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(Intent.ACTION_PICK,
//                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
//                        startActivityForResult(intent, REQUEST_CONTACTPICKER);
//                    }
//                }
//        );
//    }
//
//    private void addCalendarListener() {
//        Button contacts = (Button) findViewById(R.id.addCalendarEvent);
//        contacts.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        addCalendarEvent();
//
//                    }
//                }
//        );
//    }



    private void addCalendarEventNoFeedback() {

        Calendar cal = Calendar.getInstance();
        ContentValues calEvent = new ContentValues();
        calEvent.put(CalendarContract.Events.CALENDAR_ID, 1); // XXX pick)
        calEvent.put(CalendarContract.Events.TITLE, "Entrega/recogida");
        calEvent.put(CalendarContract.Events.DTSTART, cal.getTimeInMillis());
        calEvent.put(CalendarContract.Events.DTEND, cal.getTimeInMillis() + 60 * 60 * 1000);
        calEvent.put(CalendarContract.Events.DESCRIPTION, "Entrega recogida de lo que sea");
        calEvent.put(CalendarContract.Events.EVENT_TIMEZONE, cal.getTimeZone().getDisplayName());
        Uri uri = getContentResolver().insert(CalendarContract.Events.CONTENT_URI, calEvent);

        // The returned Uri contains the content-retriever URI for
        // the newly-inserted event, including its id
        int id = Integer.parseInt(uri.getLastPathSegment());
        Toast.makeText(getApplicationContext(), "Created Calendar Event " + id,
                Toast.LENGTH_SHORT).show();
    }

//    private void addViewCalendarListener() {
//        Button contacts = (Button) findViewById(R.id.viewCalendarEvent);
//        contacts.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        viewCalendarEvent();
//
//                    }
//                }
//        );
//    }

    private void viewCalendarEvent() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri.Builder uri = CalendarContract.Events.CONTENT_URI.buildUpon();
        uri.appendPath(Long.toString(event_id));
        intent.setData(uri.build());
        startActivity(intent);
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
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CONTACTPICKER) {
            if (resultCode == RESULT_OK) {
                Uri contentUri = intent.getData();
                String contactId = contentUri.getLastPathSegment();
                //final String[] projection = new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS};
                final Cursor cursor = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?",
                        new String[]{contactId},
                        null
                );

                while (cursor.moveToNext()) {
                    String email = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    String emailType = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                }
                cursor.close();
            }
        }

        if (requestCode == REQUEST_SCAN_CODE) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan
                Intent menuIntent = new Intent(this, ScanMenuActivity.class);
                menuIntent.putExtra("SCAN_RESULT", contents);
                menuIntent.putExtra("SCAN_RESULT_FORMAT", format);
                startActivity(menuIntent);
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }

        if (requestCode == REQUEST_CALENDAR_EVENT) {
            if (resultCode == RESULT_OK) {
                String a = "";
                a.toString();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void scannedCode(String code) {
        Intent menuIntent = new Intent(this, ScanMenuActivity.class);
        menuIntent.putExtra("SCAN_RESULT", code);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        startActivity(menuIntent);
    }

    /**
     * NFC Reader
     */
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
                scannedCode(result);
            }
        }
    }
}
