package es.uc3m.manager.activities.library;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.List;

import es.uc3m.manager.R;
import es.uc3m.manager.pojo.Product;
import es.uc3m.manager.service.ProductService;
import es.uc3m.manager.util.ContactUtils;

/**
 * Created by Snapster on 15/06/2015.
 */
public class RemoveActivity extends Activity {

    private String scannedId;
    private TextView nameEdit;
    private TextView descriptionEdit;
    private TextView editCalendarReminder;
    private TextView editStatusDescription;
    private TextView editContactName;
    private TextView editContactPhone;
    private TextView editCalendarDescription;
    private Uri imageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_item);
        scannedId = this.getIntent().getStringExtra("ID");

        nameEdit = (TextView) findViewById(R.id.nameEdit);
        descriptionEdit = (TextView) findViewById(R.id.descriptionEdit);
        ImageView imageViewEdit = (ImageView) findViewById(R.id.imageViewEdit);
        editCalendarReminder = (TextView) findViewById(R.id.editCalendarReminder);
        Spinner typeEdit = (Spinner) findViewById(R.id.typeEdit);
        editStatusDescription = (TextView) findViewById(R.id.editStatusDescription);
        editContactName = (TextView) findViewById(R.id.editContactName);
        editContactPhone = (TextView) findViewById(R.id.editContactPhone);
        editCalendarDescription = (TextView) findViewById(R.id.editCalendarDescription);

        findViewById(R.id.editViewCalendarEvent).setVisibility(View.INVISIBLE);
        findViewById(R.id.editAddViewContact).setVisibility(View.INVISIBLE);

        fillForm();
        addDeleteListener();
    }

    private void addDeleteListener() {
        Button cancel = (Button) findViewById(R.id.deleteDelete);
        cancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ProductService.getInstance().deleteProduct(scannedId);
                        finish();
                    }
                }
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                break;
        }
    }


    private void fillForm() {
        List<Product> product = ProductService.getInstance().getProduct(scannedId);
        if (product == null || product.isEmpty()){
            Toast.makeText(getApplicationContext(), "The item does not exist...", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Product p = product.get(0);
            nameEdit.setText(p.getName());
            descriptionEdit.setText(p.getDescription());
            editStatusDescription.setText(p.getCurrentStatus().getStatus());
            editContactName.setText(ContactUtils.retrieveContactName(p.getCurrentStatus().getContactUri(), getContentResolver()));
            editContactPhone.setText(ContactUtils.retrieveContactNumber(p.getCurrentStatus().getContactUri(), getContentResolver()));
            String calendarUri = p.getCurrentStatus().getCalendarEventId();
            if (calendarUri != null && !calendarUri.isEmpty()) {
                Uri.Builder uri = CalendarContract.Events.CONTENT_URI.buildUpon();
                uri.appendPath(calendarUri);
                Uri calendarEvent = uri.build();
                Cursor query = getContentResolver().query(uri.build(), new String[]{CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND}, null, null, null);
                if (query != null && query.getCount() > 0 && query.moveToFirst()) {
                    editCalendarDescription.setText(query.getString(0));
                    editCalendarReminder.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(Long.valueOf(query.getString(1))) + " - " + DateFormat.getDateInstance(DateFormat.SHORT).format(Long.valueOf(query.getString(2))));
                }
                query.close();
            }
        }
    }

}
