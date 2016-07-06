package es.uc3m.manager.activities.library;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import es.uc3m.manager.R;
import es.uc3m.manager.pojo.Product;
import es.uc3m.manager.service.ProductService;
import es.uc3m.manager.util.CalendarUtils;
import es.uc3m.manager.util.ContactUtils;

/**
 * Created by Snapster on 15/06/2015.
 */
public class ReturnActivity extends Activity {


    private static final int REQUEST_CALENDAR_EVENT = 2;
    private static final int REQUEST_CONTACTPICKER = 3;
    private String scannedId;
    private Uri calendarEvent;
    private long event_id;
    private TextView nameEdit;
    private TextView descriptionEdit;
    private TextView editCalendarReminder;
    private TextView editStatusDescription;
    private TextView editContactName;
    private TextView editContactPhone;
    private TextView editCalendarDescription;
    private Uri imageUri;
    private Product item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_item);

        overridePendingTransition(R.animator.slide_in, R.animator.slide_out);

        scannedId = this.getIntent().getStringExtra("ID");

        nameEdit = (TextView) findViewById(R.id.nameEdit);
        descriptionEdit = (TextView) findViewById(R.id.descriptionEdit);
        ImageView imageViewEdit = (ImageView) findViewById(R.id.imageViewEdit);
        editCalendarReminder = (TextView) findViewById(R.id.editCalendarReminder);
        Spinner typeEdit = (Spinner) findViewById(R.id.typeEdit);
        editStatusDescription = (TextView) findViewById(R.id.editStatusDescription);
        editContactName = (TextView) findViewById(R.id.editContactName);
        editContactPhone = (TextView) findViewById(R.id.editContactPhone);
        editCalendarDescription = (TextView) findViewById(R.id.editCalendarDescription);


        fillForm();
        addCalendarListener();
        addContactsListener();
    }

    //TODO cambiar la cosa esta porque no termina de funcionar bien
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.slide_right_in, 0);
    }

    private void addCalendarListener() {
        ImageView calendarImage = (ImageView) findViewById(R.id.editViewCalendarEvent);
        String calendarUri = item.getCurrentStatus().getCalendarEventId();

        //TODO quitar!!!
//        calendarUri = null;


        if (calendarUri != null && !calendarUri.isEmpty())
            calendarImage.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            viewCalendarEvent();

                        }
                    }
            );
        else {
            calendarImage.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Calendar cal = Calendar.getInstance();

                            Intent intent = new Intent(Intent.ACTION_INSERT);
                            intent.setData(CalendarContract.Events.CONTENT_URI);
                            intent.setType("vnd.android.cursor.item/event");
                            intent.putExtra("beginTime", cal.getTimeInMillis());
                            intent.putExtra("allDay", false);
                            intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
                            intent.putExtra("title", "Entrega/recogida");
                            intent.putExtra("description", "Entrega recogida de lo que sea");
                            intent.putExtra(CalendarContract.Attendees.ATTENDEE_EMAIL, "trevor@example.com");
                            event_id = CalendarUtils.getNewEventId(getApplicationContext().getContentResolver());
                            startActivityForResult(intent, REQUEST_CALENDAR_EVENT);
                        }
                    }
            );
        }

    }

    private void viewCalendarEvent() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(calendarEvent);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                break;
            case REQUEST_CALENDAR_EVENT:
                if (resultCode == 0) {
                    if (CalendarUtils.isEvent(getContentResolver(), event_id)) {
                        String a = "";

                        Uri.Builder uri = CalendarContract.Events.CONTENT_URI.buildUpon();
                        uri.appendPath(Long.toString(event_id));
                        item.getCurrentStatus().setCalendarEventId(uri.build().toString());
                    }
                } else {
                    event_id = -1;
                }
                break;
            case REQUEST_CONTACTPICKER:
                if (resultCode == RESULT_OK) {
                    Uri contentUri = data.getData();
                    item.getCurrentStatus().setContactUri(contentUri.toString());
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
                    fillForm();
                }
                break;
            default:
                fillForm();
        }
    }

    private void fillForm() {

        List<Product> product = ProductService.getInstance().getProduct(scannedId);
        if (product == null || product.isEmpty()) {
            Toast.makeText(getApplicationContext(), "The item does not exist...", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Product p = product.get(0);
            item = p;
            nameEdit.setText(p.getName());
            descriptionEdit.setText(p.getDescription());
            editStatusDescription.setText(p.getCurrentStatus().getStatus());
            editContactName.setText(ContactUtils.retrieveContactName(p.getCurrentStatus().getContactUri(), getContentResolver()));
            editContactPhone.setText(ContactUtils.retrieveContactNumber(p.getCurrentStatus().getContactUri(), getContentResolver()));
            String calendarUri = p.getCurrentStatus().getCalendarEventId();
            if (calendarUri != null && !calendarUri.isEmpty()) {
                Uri.Builder uri = CalendarContract.Events.CONTENT_URI.buildUpon();
                uri.appendPath(calendarUri);
                calendarEvent = uri.build();
                Cursor query = getContentResolver().query(uri.build(), new String[]{CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND}, null, null, null);
                if (query != null && query.getCount() > 0 && query.moveToFirst()) {
                    editCalendarDescription.setText(query.getString(0));
                    editCalendarReminder.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(Long.valueOf(query.getString(1))) + " - " + DateFormat.getDateInstance(DateFormat.SHORT).format(Long.valueOf(query.getString(2))));
                }
                query.close();
            }
        }
    }

    private void addContactsListener() {
        ImageView contacts = (ImageView) findViewById(R.id.editAddViewContact);
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

}
