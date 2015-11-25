package com.example.myapplication.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.display.DisplayResults;
import com.example.myapplication.activities.scan.ManualInputActivity;
import com.example.myapplication.activities.scan.ScanMenuActivity;
import com.example.myapplication.activities.settings.SettingsActivity;
import com.example.myapplication.nfc.NFCForegroundUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends Activity {

    private static final int REQUEST_SCAN_CODE = 0;
    private static final int REQUEST_CONTACTPICKER = 1;
    private static final int REQUEST_CALENDAR_EVENT = 2;
    long event_id;

    PopupWindow popupWindow;

    NFCForegroundUtil nfcForegroundUtil;

    public static boolean isIntentAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        this.addScanListener();
//        this.addContactsListener();
//        this.addCalendarListener();
        this.addGetAllFromServerListener();
//        this.addViewCalendarListener();
        //nfcForegroundUtil = new NFCForegroundUtil(this);
        //resolveIntent(getIntent());

        this.addSettingsListener();
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
    public void onPause() {
        super.onPause();
        // nfcForegroundUtil.disableForeground();
    }

    @Override
    public void onResume() {
        super.onResume();

        long prev_id = getLastEventId(getContentResolver());

        // if prev_id == mEventId, means there is new events created
        // and we need to insert new events into local sqlite database.
        if (prev_id == event_id) {
            // do database insert

        }

//        nfcForegroundUtil.enableForeground();
//
//        if (!nfcForegroundUtil.getNfc().isEnabled()){
//            Toast.makeText(getApplicationContext(), "Please activate NFC and press Back to return to the application!", Toast.LENGTH_LONG).show();
//            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
//        }

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

    public void addScanListener() {
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



    private void addContactsListener() {
        Button contacts = (Button) findViewById(R.id.readContacts);
        contacts.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                        startActivityForResult(intent, REQUEST_CONTACTPICKER);
                    }
                }
        );
    }

    private void addCalendarListener() {
        Button contacts = (Button) findViewById(R.id.addCalendarEvent);
        contacts.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addCalendarEvent();

                    }
                }
        );
    }

    private void addCalendarEvent() {
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", cal.getTimeInMillis());
        intent.putExtra("allDay", false);
        intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
        intent.putExtra("title", "Entrega/recogida");
        intent.putExtra("description", "Entrega recogida de lo que sea");
        intent.putExtra(CalendarContract.Attendees.ATTENDEE_EMAIL, "trevor@example.com");
        event_id = getNewEventId(getApplicationContext().getContentResolver());
        startActivityForResult(intent, REQUEST_CALENDAR_EVENT);
    }

    private void addCalendarEventNoFeedback () {

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

    private void addViewCalendarListener() {
        Button contacts = (Button) findViewById(R.id.viewCalendarEvent);
        contacts.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewCalendarEvent();

                    }
                }
        );
    }

    private void viewCalendarEvent(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri.Builder uri = CalendarContract.Events.CONTENT_URI.buildUpon();
        uri.appendPath(Long.toString(event_id));
        intent.setData(uri.build());
        startActivity(intent);
    }


    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
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
//            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//            return rootView;
//        }
//    }

    public static long getNewEventId(ContentResolver cr) {
        Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI, new String [] {"MAX(_id) as max_id"}, null, null, "_id");
        cursor.moveToFirst();
        long max_val = cursor.getLong(cursor.getColumnIndex("max_id"));
        return max_val+1;
    }

    public static long getLastEventId(ContentResolver cr) {
        Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI, new String [] {"MAX(_id) as max_id"}, null, null, "_id");
        cursor.moveToFirst();
        long max_val = cursor.getLong(cursor.getColumnIndex("max_id"));
        return max_val;
    }


//    btnGenerate_QR_Code = (Button) findViewById(R.id.button1);
//    edQR_Field = (EditText) findViewById(R.id.editText1);
//
//    btnGenerate_QR_Code.setOnClickListener(new OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//
//            String input = edQR_Field.getText().toString();
//
//            Intent intent = new Intent(
//                    "com.google.zxing.client.android.ENCODE");
//
//            intent.putExtra("ENCODE_TYPE", "TEXT_TYPE");
//            intent.putExtra("ENCODE_DATA", input);
//            intent.putExtra("ENCODE_FORMAT", "QR_CODE");
//            intent.putExtra("ENCODE_SHOW_CONTENTS", false);
//            startActivityForResult(intent, 0);
//
//            Toast.makeText(MainActivity.this, input, Toast.LENGTH_SHORT)
//                    .show();
//
//        }
//    });

}
