package es.uc3m.manager.activities.actions;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import es.uc3m.manager.R;
import es.uc3m.manager.activities.settings.SettingsActivity;
import es.uc3m.manager.pojo.Item;
import es.uc3m.manager.service.ItemService;
import es.uc3m.manager.util.Constants;
import es.uc3m.manager.util.PhotoUtils;

/**
 * Created by Snapster on 15/06/2015.
 */
public class EditActivity extends AbstractDetailsActivity {

    private Item initialItem;

    private final static int PHOTO = 1;

    private Uri imageUri;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        nameEditText.setEnabled(true);
        descriptionEditText.setEnabled(true);
        photoView.setEnabled(true);
        editCalendarReminderText.setEnabled(true);
        enableSpinner();
        editStatusDescriptionText.setEnabled(true);
        editContactName.setEnabled(true);
        editContactPhone.setEnabled(true);
        editCalendarDescription.setEnabled(true);

        if (Constants.STATUS_TAKEN.equals(initialItem.getCurrentStatus().getStatus())) {
            enableCalendar();
            enableContact();
        }
        enablePhoto();

        findViewById(R.id.editOk).setVisibility(View.VISIBLE);
        findViewById(R.id.editCancel).setVisibility(View.VISIBLE);
        return true;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_edit_item;
    }

    @Override
    protected void setupDisabledButtons() {
        super.disableCalendar();
        super.disableContact();
        super.disablePhoto();
        super.disableSpinner();
    }

    @Override
    protected void setupSpecificButtonListener() {
        initialItem = new Item(super.item);
        addSaveButtonListener();
        addCancelButtonListener();
        addCameraButtonListener();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    PhotoUtils.drawPhoto(imageUri, getContentResolver(), (ImageView) findViewById(R.id.imageView), getApplicationContext());
                    item.setPhoto(item.get_id());
                }
                break;
        }
    }

    /**
     * Method that adds the Camera Listener to the button
     */
    private void addCameraButtonListener() {
        try {
            photoView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //lanzamos intent
                            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                            File photo = new File(Environment.getExternalStorageDirectory() + SettingsActivity.PATH, item.get_id());
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                            imageUri = Uri.fromFile(photo);
                            startActivityForResult(intent, PHOTO);
                        }
                    }
            );
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
            e.printStackTrace();
        }
    }

    private void addSaveButtonListener() {
        ImageButton save = (ImageButton) findViewById(R.id.editOk);
        save.setVisibility(View.INVISIBLE);
        save.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String inputName = nameEditText.getText().toString();
                        String inputDescription = descriptionEditText.getText().toString();
                        String inputType = typeEdit.getSelectedItem().toString();

                        item.setName(inputName);
                        item.setDescription(inputDescription);
                        item.setType(inputType);

                        if (photoView != null && photoView.getDrawable() != null) {
                            item.setPhoto(item.get_id());
                        }
                        boolean response = ItemService.getInstance().updateItem(item);
                        if (response) {
                            Toast.makeText(getApplicationContext(), "Item "+item.getName()+" correctly updated!", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "ERROR updating "+item.getName(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    private void addCancelButtonListener() {
        ImageButton cancel = (ImageButton) findViewById(R.id.editCancel);
        cancel.setVisibility(View.INVISIBLE);
        this.item = initialItem;
        cancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                }
        );
    }


}
