package es.uc3m.manager.activities.scan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

import es.uc3m.manager.R;
import es.uc3m.manager.activities.actions.AddActivity;
import es.uc3m.manager.activities.actions.EditActivity;
import es.uc3m.manager.activities.actions.RemoveActivity;
import es.uc3m.manager.activities.actions.ReturnActivity;
import es.uc3m.manager.activities.actions.WithdrawActivity;
import es.uc3m.manager.pojo.Item;
import es.uc3m.manager.service.ProductService;

public class ScanMenuActivity extends Activity {

    private static final String TAG = "ScanMenuActivity";
    private static final int REQUEST_SCAN_CODE = 0;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String scannedCode = this.getIntent().getStringExtra("SCAN_RESULT");
        setContentView(R.layout.activity_scan_menu);
        boolean existsItem = getItemScanned(scannedCode);
        setUpButtonListeners();
        setUpButtonDisabler(existsItem);
        Toast.makeText(getApplicationContext(), "ID READ: " + scannedCode, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setUpButtonDisabler(getItemScanned(item.get_id()));
    }

    /*
         * Menu bar
         */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scan_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    /*
     * Private methods
     */
    private void setUpButtonListeners() {
        this.addAddListener();
        this.addSearchListener();
        this.addRemoveListener();
        this.addReturnListener();
        this.addWithdrawListener();
    }

    private void addWithdrawListener() {
        ImageButton button = (ImageButton) findViewById(R.id.menuWithdraw);
        setupActivityListener(button, WithdrawActivity.class);
    }


    private void addReturnListener() {
        ImageButton returnButton = (ImageButton) findViewById(R.id.menuReturn);
        setupActivityListener(returnButton, ReturnActivity.class);
    }

    private void addAddListener() {
        ImageButton addButton = (ImageButton) findViewById(R.id.menuAdd);
        setupActivityListener(addButton, AddActivity.class);
    }

    private void addRemoveListener() {
        Button removeButton = (Button) findViewById(R.id.removeButton);
        try {
            removeButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //lanzamos intent
                            Intent returnActivity = new Intent(getBaseContext(), RemoveActivity.class);
                            returnActivity.putExtra("ID", item.get_id());
                            //arrancamos intent
                            startActivity(returnActivity);
                        }
                    }
            );
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void addSearchListener() {
        ImageButton searchButton = (ImageButton) findViewById(R.id.menuFind);
        setupActivityListener(searchButton, EditActivity.class);
    }

    private void setupActivityListener(ImageButton button, final Class clazz) {
        try {
            button.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //lanzamos intent
                            Intent returnActivity = new Intent(getBaseContext(), clazz);
                            returnActivity.putExtra("ID", item.get_id());
                            //arrancamos intent
                            startActivity(returnActivity);
                        }
                    }
            );
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void setUpButtonDisabler(boolean existsItem) {
        if (!existsItem) {
            findViewById(R.id.menuFind).setEnabled(false);
            findViewById(R.id.menuFind).setBackground(getResources().getDrawable(R.drawable.findedit_disabled));
            findViewById(R.id.menuWithdraw).setEnabled(false);
            findViewById(R.id.menuWithdraw).setBackground(getResources().getDrawable(R.drawable.withdraw_disabled));
            findViewById(R.id.menuReturn).setEnabled(false);
            findViewById(R.id.menuReturn).setBackground(getResources().getDrawable(R.drawable.return_disabled));
            findViewById(R.id.menuAdd).setEnabled(true);
            findViewById(R.id.menuAdd).setBackground(getResources().getDrawable(R.drawable.add));
            findViewById(R.id.removeButton).setEnabled(false);
//            findViewById(R.id.removeButton).setBackground(getResources().getDrawable(R.drawable.re));
        } else {
            if (item.getCurrentStatus().getStatus().equalsIgnoreCase("Taken")) {
                findViewById(R.id.menuFind).setEnabled(true);
                findViewById(R.id.menuFind).setBackground(getResources().getDrawable(R.drawable.findedit));
                findViewById(R.id.menuWithdraw).setEnabled(false);
                findViewById(R.id.menuWithdraw).setBackground(getResources().getDrawable(R.drawable.withdraw_disabled));
                findViewById(R.id.menuReturn).setEnabled(true);
                findViewById(R.id.menuReturn).setBackground(getResources().getDrawable(R.drawable.returnicon));
                findViewById(R.id.menuAdd).setEnabled(false);
                findViewById(R.id.menuAdd).setBackground(getResources().getDrawable(R.drawable.add_disabled));
                findViewById(R.id.removeButton).setEnabled(false);
            }

            if (item.getCurrentStatus().getStatus().equalsIgnoreCase("Available")) {
                findViewById(R.id.menuFind).setEnabled(true);
                findViewById(R.id.menuFind).setBackground(getResources().getDrawable(R.drawable.findedit));
                findViewById(R.id.menuWithdraw).setEnabled(true);
                findViewById(R.id.menuWithdraw).setBackground(getResources().getDrawable(R.drawable.withdraw));
                findViewById(R.id.menuReturn).setEnabled(false);
                findViewById(R.id.menuReturn).setBackground(getResources().getDrawable(R.drawable.return_disabled));
                findViewById(R.id.menuAdd).setEnabled(false);
                findViewById(R.id.menuAdd).setBackground(getResources().getDrawable(R.drawable.add_disabled));
                findViewById(R.id.removeButton).setEnabled(true);
            }
        }
    }

    private boolean getItemScanned(String scannedCode) {
        List<Item> items = ProductService.getInstance().getProduct(scannedCode);
        if (!items.isEmpty()) {
            item = items.get(0);
            return true;
        } else {
            item = new Item();
            item.set_id(scannedCode);
            return false;
        }
    }


}
