package es.uc3m.manager.activities.library;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;

import es.uc3m.manager.R;
import es.uc3m.manager.activities.settings.SettingsActivity;
import es.uc3m.manager.pojo.Product;
import es.uc3m.manager.service.ProductService;
import es.uc3m.manager.util.PhotoUtils;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        id = (TextView) findViewById(R.id.scannedCodeFragment);
        String scannedId = this.getIntent().getStringExtra("ID");
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
                    PhotoUtils.
                    drawPhoto(imageUri, getContentResolver(),(ImageView) findViewById(R.id.imageView), getApplicationContext() );
                }
                break;
        }
    }

    private void addCameraButtonListener() {
        try {
            photo.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //lanzamos intent
                            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                            File photo = new File(Environment.getExternalStorageDirectory() + SettingsActivity.PATH, id.getText().toString());
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
                        String inputDescription = description.getText().toString();
                        String inputType = type.getSelectedItem().toString();
                        CheckBox dateCheck = (CheckBox) findViewById(R.id.dateCheckBox);
                        Product p = new Product();
                        if(dateCheck.isChecked()) {
                            p.setInsertDate(new Date());
                        }
                        p.set_id(inputId);
                        p.setName(inputName);
                        p.setDescription(inputDescription);
                        p.setType(inputType);

                        if (photo != null && photo.getDrawable() != null) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            try {
                                ((BitmapDrawable) photo.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
                            } catch (Exception e) {
                                Log.e("tag", e.getMessage());
                            }
                            p.setPhoto(stream.toByteArray());
                        }

                        boolean response = ProductService.getInstance().add(p);
                        if (response) {
                            Toast.makeText(getApplicationContext(), "OK!!", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "ERROR!!", Toast.LENGTH_LONG).show();
                        }
                    }
                }

        );
    }
}
