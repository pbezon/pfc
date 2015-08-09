package com.example.myapplication.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.display.DisplayResults;
import com.example.myapplication.activities.scan.ScanMenuActivity;
import com.example.myapplication.nfc.NFCForegroundUtil;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity {

    private static final int REQUEST_SCAN_CODE = 0;
    private static final int REQUEST_CONTACTPICKER = 1;

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
        setContentView(R.layout.activity_main);
        this.addScanListener();
        this.addContactsListener();
        this.addCalendarListener();
        this.addGetAllFromServerListener();
        //nfcForegroundUtil = new NFCForegroundUtil(this);
        //resolveIntent(getIntent());
    }

    private void addGetAllFromServerListener() {
        Button getAll = (Button) findViewById(R.id.getAllFromServer);
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
        Button scan = (Button) findViewById(R.id.scan);
        //comportamiento SCAN
        scan.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //lanzamos intent
                        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                        if (MainActivity.isIntentAvailable(getApplicationContext(), intent)) {
                            //esperamos resultado
                            startActivityForResult(intent, REQUEST_SCAN_CODE);
                        } else {
                            Toast.makeText(getApplicationContext(), "Install zxing application for camera scanning", Toast.LENGTH_LONG).show();
                            intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("market://details?id=com.google.zxing.client.android"));
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


}
