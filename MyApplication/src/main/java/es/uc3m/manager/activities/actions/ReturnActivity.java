package es.uc3m.manager.activities.actions;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import es.uc3m.manager.R;
import es.uc3m.manager.pojo.Item;
import es.uc3m.manager.service.ItemService;

/**
 * Created by Snapster on 15/06/2015.
 */
public class ReturnActivity extends AbstractDetailsActivity {


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_return_item;
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
        Button button = (Button) findViewById(R.id.returnButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item returnItem = ItemService.getInstance().returnItem(item);
                if (returnItem != null) {
                    item = returnItem;
                    Toast.makeText(getApplicationContext(), "Welcome back "+item.getName()+"!!",Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Error returning "+item.getName(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
