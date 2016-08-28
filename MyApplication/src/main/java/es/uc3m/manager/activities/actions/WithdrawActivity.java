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
public class WithdrawActivity extends AbstractDetailsActivity {


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_withdraw_item;
    }

    @Override
    protected void setupDisabledButtons() {
        super.enableCalendar();
        super.enableContact();
        super.disablePhoto();
        super.disableSpinner();
    }


    @Override
    protected void setupSpecificButtonListener() {
        Button button = (Button) findViewById(R.id.withdrawButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item returnItem = ItemService.getInstance().withdrawItem(ItemService.getInstance().withdrawItem(item));
                if (returnItem != null) {
                    item = returnItem;
                    Toast.makeText(getApplicationContext(), "See you soon "+item.getName()+"!!",Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Error withdrawing "+item.getName(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
