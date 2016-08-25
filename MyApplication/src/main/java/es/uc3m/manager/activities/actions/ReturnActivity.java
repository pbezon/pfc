package es.uc3m.manager.activities.actions;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;

import es.uc3m.manager.R;
import es.uc3m.manager.activities.settings.SettingsActivity;
import es.uc3m.manager.pojo.Item;
import es.uc3m.manager.util.CalendarUtils;
import es.uc3m.manager.util.ContactUtils;
import es.uc3m.manager.util.PhotoUtils;
import es.uc3m.manager.util.SpinnerUtils;

/**
 * Created by Snapster on 15/06/2015.
 */
public class ReturnActivity extends Activity {

    private static final int REQUEST_CALENDAR_EVENT = 2;
    private static final int REQUEST_CONTACTPICKER = 3;
    private long max_event_id;
    private TextView nameEditText;
    private TextView descriptionEditText;
    private TextView editCalendarReminderText;
    private TextView editStatusDescriptionText;
    private TextView editContactName;
    private TextView editContactPhone;
    private TextView editCalendarDescription;
    private ImageView photoView;
    private Item item;
    private Spinner typeEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_item);
        //animation
        overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
        // item from previous activity
        item = (Item) this.getIntent().getSerializableExtra("ITEM");
        // get Buttons
        nameEditText = (TextView) findViewById(R.id.nameEdit);
        descriptionEditText = (TextView) findViewById(R.id.descriptionEdit);
        editCalendarReminderText = (TextView) findViewById(R.id.editCalendarReminder);
        typeEdit = (Spinner) findViewById(R.id.typeEdit);
        editStatusDescriptionText = (TextView) findViewById(R.id.editStatusDescription);
        editContactName = (TextView) findViewById(R.id.editContactName);
        editContactPhone = (TextView) findViewById(R.id.editContactPhone);
        editCalendarDescription = (TextView) findViewById(R.id.editCalendarDescription);
        photoView = (ImageView) findViewById(R.id.imageViewEdit);
        // fill layout
        fillForm();
        // button listeners
        addCalendarListener();
        addContactsListener();
    }

    //TODO cambiar la cosa esta porque no termina de funcionar bien
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.slide_right_in, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                break;
            case REQUEST_CALENDAR_EVENT:
                if (resultCode == 0) {
                    long lastEvent = CalendarUtils.getCalendarMaxEventId(getContentResolver());
                    if (lastEvent > max_event_id) {
                        if (CalendarUtils.isEvent(getContentResolver(), lastEvent)) {
                            Uri.Builder uri = CalendarContract.Events.CONTENT_URI.buildUpon();
                            uri.appendPath(Long.toString(lastEvent));
                            item.getCurrentStatus().setCalendarEventId(uri.build().toString());
                            // como lo hemos añadido, ahora al tocar no se tiene que crear un evento nuevo
                            // hay que mostrar el evento que acabamos de crear
                            this.addCalendarListener();
                        }
                    }
                } else {
                    max_event_id = -1;
                }
                break;
            case REQUEST_CONTACTPICKER:
                if (resultCode == RESULT_OK) {
                    Uri contentUri = data.getData();
                    item.getCurrentStatus().setContactUri(contentUri.toString());
                    this.addContactsListener();
                }
                break;
            default:

        }
        fillForm();
    }

    private void fillForm() {
        // asumimos que en este punto ya tenemos el item y no habría que pedirlo al servicio
        if (item == null) {
            Toast.makeText(getApplicationContext(), "The item does not exist...", Toast.LENGTH_LONG).show();
            finish();
        } else {
            nameEditText.setText(item.getName());
            descriptionEditText.setText(item.getDescription());
            editStatusDescriptionText.setText(item.getCurrentStatus().getStatus());
            editContactName.setText(ContactUtils.getContactName(item.getCurrentStatus().getContactUri(), getContentResolver()));
            editContactPhone.setText(ContactUtils.getContactNumber(item.getCurrentStatus().getContactUri(), getContentResolver()));
            typeEdit.setSelection(SpinnerUtils.getIndex(typeEdit, item.getType()));
            String calendarUri = item.getCurrentStatus().getCalendarEventId();
            if (calendarUri != null && !calendarUri.isEmpty()) {
                Cursor query = getContentResolver().query(Uri.parse(calendarUri), new String[]{CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND}, null, null, null);
                if (query != null && query.getCount() > 0 && query.moveToFirst()) {
                    editCalendarDescription.setText(query.getString(0));
                    editCalendarReminderText.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(Long.valueOf(query.getString(1))) + " - " + DateFormat.getDateInstance(DateFormat.SHORT).format(Long.valueOf(query.getString(2))));
                    query.close();
                }
            }
            if (item.getPhoto() != null && !item.getPhoto().isEmpty()) {
                File photo = new File(Environment.getExternalStorageDirectory() + SettingsActivity.PATH, item.getPhoto());
                PhotoUtils.drawPhoto(Uri.fromFile(photo), getContentResolver(), photoView, getApplicationContext());
            }
        }
    }

    private void addContactsListener() {
        ImageView contacts = (ImageView) findViewById(R.id.editAddViewContact);
        contacts.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // le han dado a añadir
                        if (item.getCurrentStatus().getContactUri() == null || item.getCurrentStatus().getContactUri().isEmpty() ) {
                            Intent intent = new Intent(Intent.ACTION_PICK,
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                            startActivityForResult(intent, REQUEST_CONTACTPICKER);
                            editContactName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    viewContactEvent();
                                }
                            });
                            editContactPhone.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    viewContactEvent();
                                }
                            });
                        } else {
                            // acaban de darle a borrar
                            item.getCurrentStatus().setContactUri(null);

                            fillForm();
                        }
                    }
                }
        );
    }

    private void addCalendarListener() {
        ImageView calendarImage = (ImageView) findViewById(R.id.editViewCalendarEvent);
        String calendarUri = item.getCurrentStatus().getCalendarEventId();

        if (calendarUri != null && !calendarUri.isEmpty()) {
            // TODO cambiar a imagen botón de borrar
            int id = getResources().getIdentifier("android:drawable/ic_menu_delete", null, null);
            calendarImage.setImageResource(id);
            calendarImage.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteCalendarEvent();
                        }
                    }
            );
            editCalendarDescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewCalendarEvent();
                }
            });
            editCalendarReminderText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewCalendarEvent();
                }
            });
        } else {
            int id = getResources().getIdentifier("android:drawable/ic_input_add", null, null);
            calendarImage.setImageResource(id);
            calendarImage.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteCalendarEvent();
                        }
                    }
            );
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
                            max_event_id = CalendarUtils.getCalendarMaxEventId(getApplicationContext().getContentResolver());
                            startActivityForResult(intent, REQUEST_CALENDAR_EVENT);
                        }
                    }
            );
        }
    }

    private void viewCalendarEvent() {
        if (item.getCurrentStatus().getCalendarEventId() != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(item.getCurrentStatus().getCalendarEventId()));
            startActivity(intent);
        }
    }

    private void deleteCalendarEvent() {
        item.getCurrentStatus().setCalendarEventId(null);
        editCalendarDescription.setText("");
        editCalendarReminderText.setText("");
        addCalendarListener();
    }

    private void viewContactEvent() {
        if (item.getCurrentStatus().getContactUri() != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getCurrentStatus().getContactUri()));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

}
