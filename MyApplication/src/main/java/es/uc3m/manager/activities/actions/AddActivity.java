package es.uc3m.manager.activities.actions;

import android.app.Activity;
import android.content.Intent;
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

import java.io.File;
import java.util.Date;

import es.uc3m.manager.R;
import es.uc3m.manager.activities.settings.SettingsActivity;
import es.uc3m.manager.pojo.Item;
import es.uc3m.manager.service.ItemService;
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
    private final static int SCAN = 1;


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
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Button save = (Button) findViewById(R.id.saveButton);
                if (name.getText() != null && !name.getText().toString().isEmpty()) {
                    save.setEnabled(true);
                } else {
                    save.setEnabled(false);
                }
            }
        });
        description = (TextView) findViewById(R.id.descriptionInput);
        this.addCameraButtonListener();
        this.addSaveButtonListener();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCAN:
                if (resultCode == Activity.RESULT_OK) {
                    PhotoUtils.drawPhoto(imageUri, getContentResolver(), (ImageView) findViewById(R.id.imageView), getApplicationContext());
                }
                break;
        }
    }


    /*
     * Private Methods
     */

    /**
     * Method that adds the Camera Listener to the button
     */
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
                            startActivityForResult(intent, SCAN);
                        }
                    }
            );
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Method that adds the Save Listener to the button
     */
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
                        Item p = new Item();
                        if (dateCheck.isChecked()) {
                            p.setInsertDate(new Date());
                        }
                        p.set_id(inputId);
                        p.setName(inputName);
                        p.setDescription(inputDescription);
                        p.setType(inputType);

                        if (photo != null && photo.getDrawable() != null) {
                            p.setPhoto(inputId);
                        }

                        boolean response = ItemService.getInstance().add(p);
                        if (response) {
                            Toast.makeText(getApplicationContext(), "Item " + p.getName()+" was correctly added!!", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "ERROR adding item "+p.getName(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

        );
    }
}
