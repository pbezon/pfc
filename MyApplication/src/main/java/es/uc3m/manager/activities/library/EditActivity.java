package es.uc3m.manager.activities.library;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import es.uc3m.manager.R;
import es.uc3m.manager.activities.settings.SettingsActivity;
import es.uc3m.manager.pojo.Product;
import es.uc3m.manager.service.ProductService;
import es.uc3m.manager.util.CalendarUtils;
import es.uc3m.manager.util.ContactUtils;
import es.uc3m.manager.util.PhotoUtils;

/**
 * Created by Snapster on 15/06/2015.
 */
public class EditActivity extends Activity {

    private String scannedId;
    private Uri calendarEvent;
    private TextView nameEdit;
    private TextView descriptionEdit;
    private ImageView imageViewEdit;
    private TextView editCalendarReminder;
    private Spinner typeEdit;
    private TextView editStatusDescription;
    private TextView editContactName;
    private TextView editContactPhone;
    private TextView editCalendarDescription;
    private Uri imageUri;
    private final int ACTIVITY_TAKE_PHOTO = 1;
    private final int ACTIVITY_ADD_CALENDAR = 2;
    private final int ACTIVITY_ADD_CONTACT = 3;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        overridePendingTransition(R.animator.slide_in, R.animator.slide_out);

        scannedId = this.getIntent().getStringExtra("ID");

        nameEdit = (TextView) findViewById(R.id.nameEdit);
        nameEdit.setEnabled(false);
        descriptionEdit = (TextView) findViewById(R.id.descriptionEdit);
        descriptionEdit.setEnabled(false);
        imageViewEdit = (ImageView) findViewById(R.id.imageViewEdit);
        imageViewEdit.setEnabled(false);
        editCalendarReminder = (TextView) findViewById(R.id.editCalendarReminder);
        editCalendarReminder.setEnabled(false);
        typeEdit = (Spinner) findViewById(R.id.typeEdit);
        typeEdit.setEnabled(false);
        editStatusDescription = (TextView) findViewById(R.id.editStatusDescription);
        editStatusDescription.setEnabled(false);
        editContactName = (TextView) findViewById(R.id.editContactName);
        editContactName.setEnabled(false);
        editContactPhone = (TextView) findViewById(R.id.editContactPhone);
        editContactPhone.setEnabled(false);
        editCalendarDescription = (TextView) findViewById(R.id.editCalendarDescription);
        editCalendarDescription.setEnabled(false);
        findViewById(R.id.editViewCalendarEvent).setEnabled(false);
        findViewById(R.id.editAddViewContact).setEnabled(false);

        addCameraButtonListener();
        addViewCalendarEventListener();
        addSaveButtonListener();
        addCancelButtonListener();
        addContactsListener();
        fillForm();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        nameEdit.setEnabled(true);
        descriptionEdit.setEnabled(true);
        imageViewEdit.setEnabled(true);
        editCalendarReminder.setEnabled(true);
        typeEdit.setEnabled(true);
        editStatusDescription.setEnabled(true);
        editContactName.setEnabled(true);
        editContactPhone.setEnabled(true);
        editCalendarDescription.setEnabled(true);
        findViewById(R.id.editViewCalendarEvent).setEnabled(true);
        findViewById(R.id.editAddViewContact).setEnabled(true);
        findViewById(R.id.editOk).setVisibility(View.VISIBLE);
        findViewById(R.id.editCancel).setVisibility(View.VISIBLE);
        return true;
    }

    //TODO cambiar la cosa esta porque no termina de funcionar bien
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.slide_right_in, 0);
    }

    private void addViewCalendarEventListener() {
        if (calendarEvent != null && !calendarEvent.toString().isEmpty()) {
            ImageView calendarImage = (ImageView) findViewById(R.id.editViewCalendarEvent);
            calendarImage.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            viewCalendarEvent();

                        }
                    }
            );
        } else {
            ImageView calendarImage = (ImageView) findViewById(R.id.editViewCalendarEvent);
            calendarImage.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addCalendarEvent();

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
            case ACTIVITY_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    drawPhoto();
                }
                break;
            case ACTIVITY_ADD_CONTACT:
                if (resultCode == RESULT_OK) {
                    Uri contentUri = data.getData();
                    product.getCurrentStatus().setContactUri(contentUri.toString());
                    fillForm();
                }
        }
    }

    private void fillForm() {
        List<Product> items;
        if (product != null) {
            items = Collections.singletonList(product);
        } else {
            items = ProductService.getInstance().getProduct(scannedId);
        }
        if (items == null || items.isEmpty()){
            Toast.makeText(getApplicationContext(), "The item does not exist...", Toast.LENGTH_LONG).show();
            finish();
        } else {
            product = items.get(0);
            nameEdit.setText(product.getName());
            descriptionEdit.setText(product.getDescription());
            editStatusDescription.setText(product.getCurrentStatus().getStatus());
            editContactName.setText(ContactUtils.retrieveContactName(product.getCurrentStatus().getContactUri(), getContentResolver()));
            editContactPhone.setText(ContactUtils.retrieveContactNumber(product.getCurrentStatus().getContactUri(), getContentResolver()));
            File photo = new File(Environment.getExternalStorageDirectory() + SettingsActivity.PATH, product.get_id());
            if (photo != null) {
                imageUri = Uri.fromFile(photo);
                PhotoUtils.drawPhoto(imageUri, getContentResolver(), (ImageView) findViewById(R.id.imageViewEdit), getApplicationContext());
            }
            if (product.getCurrentStatus().getCalendarEventId() != null) {
                Uri.Builder uri = CalendarContract.Events.CONTENT_URI.buildUpon();
                uri.appendPath(product.getCurrentStatus().getCalendarEventId());
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

    private void addSaveButtonListener() {
        ImageButton save = (ImageButton) findViewById(R.id.editOk);
        save.setVisibility(View.INVISIBLE);
        save.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean response = ProductService.getInstance().edit(product);
                        if (response) {
                            Toast.makeText(getApplicationContext(), "UPDATED OK!!", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "ERROR!!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    private void addCancelButtonListener() {
        ImageButton cancel = (ImageButton) findViewById(R.id.editCancel);
        cancel.setVisibility(View.INVISIBLE);
        cancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                }
        );
    }

    private void drawPhoto() {
        Uri selectedImage = imageUri;
        getContentResolver().notifyChange(selectedImage, null);
        ImageView imageView = (ImageView) findViewById(R.id.imageViewEdit);

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            AssetFileDescriptor fileDescriptor;
            fileDescriptor = getContentResolver().openAssetFileDescriptor(imageUri, "r");

            //cogemos la foto pero no la pintamos por no romper cosas en memoria
            BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);

            options.inSampleSize = PhotoUtils.calculateInSampleSize(options);
            options.inJustDecodeBounds = false;
            Bitmap actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);

            imageView.setImageBitmap(actuallyUsableBitmap);
            imageView.setRotation(90);

            Toast.makeText(getApplicationContext(), selectedImage.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Failed to load", Toast.LENGTH_SHORT).show();
            Log.e("Camera", e.toString());
        }

    }

    private void addCameraButtonListener() {
        try {
            imageViewEdit.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //lanzamos intent
                            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                            File photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                            imageUri = Uri.fromFile(photo);
                            startActivityForResult(intent, ACTIVITY_TAKE_PHOTO);
                        }
                    }
            );
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
            e.printStackTrace();
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
                        startActivityForResult(intent, ACTIVITY_ADD_CONTACT);
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
        long event_id = CalendarUtils.getNewEventId(getApplicationContext().getContentResolver());
        startActivityForResult(intent, 99);
    }

}
