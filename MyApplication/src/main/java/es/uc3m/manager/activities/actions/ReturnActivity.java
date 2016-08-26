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
import android.widget.Button;
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
import es.uc3m.manager.service.ItemService;
import es.uc3m.manager.util.CalendarUtils;
import es.uc3m.manager.util.ContactUtils;
import es.uc3m.manager.util.PhotoUtils;
import es.uc3m.manager.util.SpinnerUtils;

/**
 * Created by Snapster on 15/06/2015.
 */
public class ReturnActivity extends AbstractDetailsActivity {


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_return_item;
    }

    @Override
    protected void setupSpecificButtonListener() {

        Button button = (Button) findViewById(R.id.returnButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item returnItem = ItemService.getInstance().withdrawItem(ItemService.getInstance().returnItem(item));
                if (returnItem != null) {
                    Toast.makeText(getApplicationContext(), "Welcome back "+item.getName()+"!!",Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Error returning "+item.getName(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
