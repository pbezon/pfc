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
 * Created by Snapster on 25-Aug-16.
 */
public abstract class AbstractDetailsActivity extends Activity{

    private static final int REQUEST_CALENDAR_EVENT = 2;
    private static final int REQUEST_CONTACTPICKER = 3;
    protected long max_event_id;
    protected TextView nameEditText;
    protected TextView descriptionEditText;
    protected TextView editCalendarReminderText;
    protected TextView editStatusDescriptionText;
    protected TextView editContactName;
    protected TextView editContactPhone;
    protected TextView editCalendarDescription;
    protected ImageView photoView;
    protected Item item;
    protected Spinner typeEdit;
    protected ImageView calendarImage;
    protected ImageView contactAddIcon;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(getLayoutResourceId());

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
        calendarImage = (ImageView) findViewById(R.id.editViewCalendarEvent);
        contactAddIcon = ((ImageView)findViewById(R.id.editAddViewContact));

        // fill layout
        fillForm();
        // button listeners
        addCalendarListener();
        addContactsListener();

        setupSpecificButtonListener();
        setupDisabledButtons();
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

                    int id = getResources().getIdentifier("android:drawable/ic_menu_delete", null, null);
                    contactAddIcon.setImageResource(id);
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
            String calendarUri = item.getCurrentStatus().getCalendarEventId();
            typeEdit.setSelection(SpinnerUtils.getIndex(typeEdit, item.getType()));
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
        if (item.getCurrentStatus().getContactUri() != null && !item.getCurrentStatus().getContactUri().isEmpty()) {
            int id = getResources().getIdentifier("android:drawable/ic_menu_delete", null, null);
            contactAddIcon.setImageResource(id);
        }
        contactAddIcon.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // le han dado a añadir
                        if (item.getCurrentStatus().getContactUri() == null || item.getCurrentStatus().getContactUri().isEmpty() ) {
                            Intent intent = new Intent(Intent.ACTION_PICK,
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                            startActivityForResult(intent, REQUEST_CONTACTPICKER);

                        } else {
                            // acaban de darle a borrar
                            item.getCurrentStatus().setContactUri(null);
                            int id = getResources().getIdentifier("android:drawable/ic_input_add", null, null);
                            contactAddIcon.setImageResource(id);
                            fillForm();

                        }
                    }
                }
        );
    }

    private void addCalendarListener() {
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

    protected abstract int getLayoutResourceId();

    protected abstract void setupDisabledButtons();

    protected abstract void setupSpecificButtonListener();

    protected void disableCalendar() {
        calendarImage.setVisibility(View.INVISIBLE);
        calendarImage.setClickable(false);
        calendarImage.setEnabled(false);
        calendarImage.setFocusable(false);
    }

    protected void enableCalendar() {
        calendarImage.setVisibility(View.VISIBLE);
        calendarImage.setClickable(true);
        calendarImage.setEnabled(true);
        calendarImage.setFocusable(true);
    }

    protected void disableContact () {
        contactAddIcon.setVisibility(View.INVISIBLE);
        contactAddIcon.setClickable(false);
        contactAddIcon.setEnabled(false);
        contactAddIcon.setFocusable(false);
    }

    protected void enableContact() {
        contactAddIcon.setVisibility(View.VISIBLE);
        contactAddIcon.setClickable(true);
        contactAddIcon.setEnabled(true);
        contactAddIcon.setFocusable(true);
    }

    protected void disableSpinner () {
        typeEdit.setClickable(false);
        typeEdit.setEnabled(false);
        typeEdit.setFocusable(false);
    }

    protected void enableSpinner () {
        typeEdit.setClickable(true);
        typeEdit.setEnabled(true);
        typeEdit.setFocusable(true);
    }

    protected void disablePhoto () {
        photoView.setClickable(false);
        photoView.setEnabled(false);
        photoView.setFocusable(false);
    }

    protected void enablePhoto () {
        photoView.setClickable(true);
        photoView.setEnabled(true);
        photoView.setFocusable(true);
    }

}
