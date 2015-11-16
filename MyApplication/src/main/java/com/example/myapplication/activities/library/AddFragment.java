package com.example.myapplication.activities.library;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.pojo.Product;
import com.example.myapplication.service.ProductService;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by Snapster on 15/06/2015.
 */
public class AddFragment extends Fragment {

    public final static int FRAGMENT_ID = 0;
    public final static int REQUEST_CONTACTPICKER = 2;
    public final static String TAB_NAME = "Add";
    private final static String TAG = "AddFragment";
    private Uri imageUri;

    private TextView id;
    private TextView name;
    private TextView description;
    private ImageView photo;
    private Spinner type;

    private String contactID;
    private Uri uriContact;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_item, container, false);

        id = (TextView) rootView.findViewById(R.id.scannedCodeFragment);
        id.setText(this.getArguments().getString("scannedCode"));
//        name = (TextView) rootView.findViewById(R.id.addFragmentName);
//        description = (TextView) rootView.findViewById(R.id.addFragmentDescription);
        photo = (ImageView) rootView.findViewById(R.id.imageView);
        type = (Spinner) rootView.findViewById(R.id.addFragmentSpinner);
//        this.addSaveButtonListener(rootView);
        this.addCameraButtonListener(rootView);
        return rootView;
    }

    private void addSaveButtonListener(final View rootView) {
        Button save = (Button) rootView.findViewById(R.id.saveButton);
        save.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String inputId = id.getText().toString();
                        String inputName = name.getText().toString();
                        String inputDescription = name.getText().toString();
                        String inputType = type.getSelectedItem().toString();

                        Product p = new Product();
                        p.set_id(inputId);
                        p.setName(inputName);
                        p.setDescription(inputDescription);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        if (photo != null && photo.getDrawable() != null)
                            try {
                                ((BitmapDrawable) photo.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
                            } catch (Exception e) {
                                Log.e("tag", e.getMessage());
                            }
                        p.setPhoto(stream.toByteArray());

                        boolean response = ProductService.getInstance().add(p);
                        if (response) {
                            Toast.makeText(getActivity().getApplicationContext(), "OK!!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "ERROR!!", Toast.LENGTH_LONG).show();
                        }
                    }
                }

        );
    }

    public void addCameraButtonListener(View rootView) {
        try {
            //comportamiento SCAN
//            ImageButton scan = (ImageButton) rootView.findViewById(R.id.imageButton);
            photo.setOnClickListener(
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    drawPhoto();
                }
                break;
            case REQUEST_CONTACTPICKER:
                if(resultCode ==  Activity.RESULT_OK) {
                    String x = "x";
                    x.toString();
                    uriContact = data.getData();
//                    retrieveContactName();
//                    retrieveContactNumber();
//                    retrieveContactPhoto();
                }
                break;
        }
    }

//    private void addContactsListener(View rootView) {
//        Button contacts = (Button) rootView.findViewById(R.id.readContacts);
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

    public void drawPhoto() {
        Uri selectedImage = imageUri;
        getActivity().getContentResolver().notifyChange(selectedImage, null);
        ImageView imageView = (ImageView) getActivity().findViewById(R.id.imageView);

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            AssetFileDescriptor fileDescriptor = null;
            fileDescriptor = getActivity().getContentResolver().openAssetFileDescriptor(imageUri, "r");

            //cogemos la foto pero no la pintamos por no romper cosas en memoria
            BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);

            options.inSampleSize = calculateInSampleSize(options, 150, 150);
            options.inJustDecodeBounds = false;
            Bitmap actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);

            imageView.setImageBitmap(actuallyUsableBitmap);

            Toast.makeText(getActivity().getApplicationContext(), selectedImage.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Failed to load", Toast.LENGTH_SHORT).show();
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

//    private void retrieveContactPhoto() {
//        Bitmap photo = null;
//        try {
//            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getActivity().getContentResolver(),
//                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactID)));
//            if (inputStream != null) {
//                photo = BitmapFactory.decodeStream(inputStream);
//                ImageView imageView = (ImageView) getActivity().findViewById(R.id.img_contact);
//                imageView.setImageBitmap(photo);
//            }
//            if(inputStream != null)
//                inputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

//    private void retrieveContactNumber() {
//        String contactNumber = null;
//        // getting contacts ID
//        Cursor cursorID = getActivity().getContentResolver().query(uriContact,
//                new String[]{ContactsContract.Contacts._ID}, null, null, null);
//        if (cursorID.moveToFirst()) {
//            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
//        }
//
//        cursorID.close();
//        Log.d(TAG, "Contact ID: " + contactID);
//        // Using the contact ID now we will get contact phone number
//        Cursor cursorPhone = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
//                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
//                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
//                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
//                new String[]{contactID},
//                null);
//        if (cursorPhone.moveToFirst()) {
//            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//        }
//        cursorPhone.close();
//
//        Log.d(TAG, "Contact Phone Number: " + contactNumber);
//
//        ((TextView)getActivity().findViewById(R.id.contactPhone)).setText(contactNumber);
//    }

//    private void retrieveContactName() {
//        String contactName = null;
//        // querying contact data store
//        Cursor cursor = getActivity().getContentResolver().query(uriContact, null, null, null, null);
//        if (cursor.moveToFirst()) {
//
//            // DISPLAY_NAME = The display name for the contact.
//            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.
//
//            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//        }
//        cursor.close();
//        Log.d(TAG, "Contact Name: " + contactName);
//        ((TextView)getActivity().findViewById(R.id.contactName)).setText(contactName);
//
//    }
}
