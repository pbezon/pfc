package es.uc3m.manager.activities.actions;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import es.uc3m.manager.R;
import es.uc3m.manager.activities.settings.SettingsActivity;
import es.uc3m.manager.pojo.Item;
import es.uc3m.manager.service.ItemService;
import es.uc3m.manager.util.PhotoUtils;
import es.uc3m.manager.util.SpinnerUtils;

/**
 * Created by Snapster on 15/06/2015.
 */
public class RemoveActivity extends Activity {

    private TextView nameEdit;
    private TextView descriptionEdit;
    private ImageView photoView;
    private Spinner typeEdit;
    private TextView itemIdView;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_item);

        nameEdit = (TextView) findViewById(R.id.nameInput);
        descriptionEdit = (TextView) findViewById(R.id.descriptionInput);
        typeEdit = (Spinner) findViewById(R.id.addFragmentSpinner);
        photoView = (ImageView) findViewById(R.id.imageView);
        itemIdView = (TextView) findViewById(R.id.scannedCodeFragment);
        item = (Item) this.getIntent().getSerializableExtra("ITEM");

        fillForm();
        addDeleteListener();
    }

    private void addDeleteListener() {
        Button cancel = (Button) findViewById(R.id.deleteDelete);
        cancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ItemService.getInstance().deleteItem(item.get_id());
                        Toast.makeText(getApplicationContext(), "Item deleted!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
        );
    }

    private void fillForm() {
        if (item == null) {
            Toast.makeText(getApplicationContext(), "The item does not exist...", Toast.LENGTH_LONG).show();
            finish();
        } else {
            nameEdit.setText(item.getName());
            descriptionEdit.setText(item.getDescription());
            itemIdView.setText(item.get_id());
            typeEdit.setSelection(SpinnerUtils.getIndex(typeEdit, item.getType()));
            if (item.getPhoto() != null && !item.getPhoto().isEmpty()) {
                File photo = new File(Environment.getExternalStorageDirectory() + SettingsActivity.PATH, item.getPhoto());
                PhotoUtils.drawPhoto(Uri.fromFile(photo), getContentResolver(), photoView, getApplicationContext());
            }
        }
    }
}
