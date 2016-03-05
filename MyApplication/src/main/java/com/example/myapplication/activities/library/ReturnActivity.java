package com.example.myapplication.activities.library;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.pojo.Product;
import com.example.myapplication.service.ProductService;
import com.example.myapplication.util.CalendarUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by Snapster on 15/06/2015.
 */
public class ReturnActivity extends Activity {


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
    String scannedId;

    Uri calendarEvent;

    private Product item;

    private static final int REQUEST_CALENDAR_EVENT = 2;
    long event_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_item);

        overridePendingTransition(R.animator.slide_in, R.animator.slide_out);

        scannedId = this.getIntent().getStringExtra("ID");

        //nos traemos el producto
        item = ProductService.getInstance().getProduct(scannedId).get(0);

        nameEdit = (TextView) findViewById(R.id.nameEdit);
        descriptionEdit = (TextView) findViewById(R.id.descriptionEdit);
        imageViewEdit = (ImageView) findViewById(R.id.imageViewEdit);
        editCalendarReminder = (TextView) findViewById(R.id.editCalendarReminder);
        typeEdit = (Spinner) findViewById(R.id.typeEdit);
        editStatusDescription = (TextView) findViewById(R.id.editStatusDescription);
        editContactName = (TextView) findViewById(R.id.editContactName);
        editContactPhone = (TextView) findViewById(R.id.editContactPhone);
        editCalendarDescription =  (TextView) findViewById(R.id.editCalendarDescription);

        addCameraButtonListener();
        addCalendarListener();
        fillForm();
    }

    //TODO cambiar la cosa esta porque no termina de funcionar bien
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.slide_right_in,0);
    }

    private void addCalendarListener() {
        ImageView calendarImage = (ImageView) findViewById(R.id.editViewCalendarEvent);
        String calendarUri = item.getCurrentStatus().getCalendarEventId();

        //TODO quitar!!!
        calendarUri = null;


        if ( calendarUri != null && !calendarUri.isEmpty())
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

    private void viewCalendarEvent(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(calendarEvent);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    drawPhoto();
                }
                break;
            case REQUEST_CALENDAR_EVENT:
                if (resultCode == 0) {
                    if (CalendarUtils.isEvent(getContentResolver(), event_id)) {
                        String a = "";
                        a.toString();

                        Uri.Builder uri = CalendarContract.Events.CONTENT_URI.buildUpon();
                        uri.appendPath(Long.toString(event_id));
                        item.getCurrentStatus().setCalendarEventId(uri.build().toString());
                    }
                } else {
                    event_id = -1;
                }
                break;
        }
    }

    private void fillForm(){

        nameEdit.setText(item.getName());
        descriptionEdit.setText(item.getDescription());
        editStatusDescription.setText(item.getCurrentStatus().getStatus());
        editContactName.setText(this.retrieveContactName(item.getCurrentStatus().getContactUri()));
        editContactPhone.setText(this.retrieveContactNumber(item.getCurrentStatus().getContactUri()));
        Uri.Builder uri = CalendarContract.Events.CONTENT_URI.buildUpon();
        uri.appendPath(item.getCurrentStatus().getCalendarEventId());
        calendarEvent = uri.build();

        Cursor query = getContentResolver().query(uri.build(), new String[]{CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND}, null, null, null);
        if (query!= null && query.getCount()>0 && query.moveToFirst()) {
            editCalendarDescription.setText(query.getString(0));
            editCalendarReminder.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(new Long(query.getString(1))) + " - " + DateFormat.getDateInstance(DateFormat.SHORT).format(new Long(query.getString(2))));
        }
        query.close();
    }

    private void addSaveButtonListener() {
        Button save = (Button) findViewById(R.id.saveButton);
        save.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String inputId = scannedId;
                        String inputName = nameEdit.getText().toString();
                        String inputDescription = descriptionEdit.getText().toString();
                        String inputType = typeEdit.getSelectedItem().toString();

                        Product p = new Product();
                        p.set_id(inputId);
                        p.setName(inputName);
                        p.setDescription(inputDescription);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        if (imageViewEdit != null && imageViewEdit.getDrawable() != null)
                            try {
                                ((BitmapDrawable) imageViewEdit.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
                            } catch (Exception e) {
                                Log.e("tag", e.getMessage());
                            }
                        p.setPhoto(stream.toByteArray());

                        boolean response = ProductService.getInstance().add(p);
                        if (response) {
                            Toast.makeText(getApplicationContext(), "UPDATED OK!!", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "ERROR!!", Toast.LENGTH_LONG).show();
                        }
                    }
                }

        );
    }


    public void drawPhoto() {
        Uri selectedImage = imageUri;
        getContentResolver().notifyChange(selectedImage, null);
        ImageView imageView = (ImageView) findViewById(R.id.imageViewEdit);

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            AssetFileDescriptor fileDescriptor = null;
            fileDescriptor = getContentResolver().openAssetFileDescriptor(imageUri, "r");

            //cogemos la foto pero no la pintamos por no romper cosas en memoria
            BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);

            options.inSampleSize = calculateInSampleSize(options, 150, 150);
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

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    private String retrieveContactNumber(String uriContact) {
        String contactNumber = null;
        String contactID = null;
        // getting contacts ID
        Cursor cursorID = getContentResolver().query(Uri.parse(uriContact),
                new String[]{ContactsContract.Contacts._ID}, null, null, null);
        if (cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();
        Log.d("TAG", "Contact ID: " + contactID);
        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                new String[]{contactID},
                null);
        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        cursorPhone.close();

        Log.d("TAG", "Contact Phone Number: " + contactNumber);

        return contactNumber;
    }

    private String retrieveContactName(String uriContact) {
        String contactName = null;
        // querying contact data store
        Cursor cursor = getContentResolver().query(Uri.parse(uriContact), null, null, null, null);
        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
        cursor.close();
        Log.d("TAG", "Contact Name: " + contactName);
        return contactName;

    }

    public void addCameraButtonListener() {
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
                            startActivityForResult(intent, 1);
                        }
                    }
            );
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
            e.printStackTrace();
        }
    }

}
