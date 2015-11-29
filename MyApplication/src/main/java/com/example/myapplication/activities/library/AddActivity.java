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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.scan.ScanMenuActivity;
import com.example.myapplication.pojo.Product;
import com.example.myapplication.service.ProductService;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by Snapster on 15/06/2015.
 */
public class AddActivity extends Activity {

    private TextView id;
    private TextView name;
    private TextView description;
    private ImageView photo;
    private Spinner type;
    private Uri imageUri;
    String scannedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        id = (TextView) findViewById(R.id.scannedCodeFragment);
        scannedId = this.getIntent().getStringExtra("ID");
        id.setText(scannedId);
        photo = (ImageView) findViewById(R.id.imageView);
        type = (Spinner) findViewById(R.id.addFragmentSpinner);
        name = (TextView) findViewById(R.id.nameInput);
        description = (TextView) findViewById(R.id.descriptionInput);
        this.addCameraButtonListener();
        this.addSaveButtonListener();
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
        }
    }

    public void addCameraButtonListener() {
        try {
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


    private void addSaveButtonListener() {
        Button save = (Button) findViewById(R.id.saveButton);
        save.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String inputId = id.getText().toString();
                        String inputName = name.getText().toString();
                        String inputDescription = name.getText().toString();
                        String inputType = type.getSelectedItem().toString();
                        CheckBox dateCheck = (CheckBox) findViewById(R.id.dateCheckBox);
                        boolean addDate = dateCheck.isChecked();

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
                            Toast.makeText(getApplicationContext(), "OK!!", Toast.LENGTH_LONG).show();
                            finish();
//                            Intent scanMenu = new Intent(getBaseContext(), ScanMenuActivity.class);
//                            scanMenu.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                            scanMenu.putExtra("SCAN_RESULT", scannedId);
//                            //arrancamos intent
//                            startActivity(scanMenu);
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
        ImageView imageView = (ImageView) findViewById(R.id.imageView);

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

}
