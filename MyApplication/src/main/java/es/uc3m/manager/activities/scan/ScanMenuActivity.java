package es.uc3m.manager.activities.scan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import es.uc3m.manager.activities.library.AddActivity;
import es.uc3m.manager.activities.library.EditActivity;
import es.uc3m.manager.activities.library.RemoveActivity;
import es.uc3m.manager.activities.library.ReturnActivity;
import es.uc3m.manager.activities.library.WithdrawActivity;
import es.uc3m.manager.pojo.Product;
import es.uc3m.manager.service.ProductService;
import es.uc3m.manager.util.ImageButtonUtils;

public class ScanMenuActivity extends Activity {

    public static final String TAG = "ScanMenuActivity";
    private static final int REQUEST_SCAN_CODE = 0;
    private Product product;

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
        setUpButtonDisabler(getItemScanned(product.get_id()));
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                            returnActivity.putExtra("ID", product.get_id());
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
                            returnActivity.putExtra("ID", product.get_id());
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
            findViewById(R.id.menuWithdraw).setEnabled(false);
            findViewById(R.id.menuReturn).setEnabled(false);
            findViewById(R.id.menuAdd).setEnabled(true);
            findViewById(R.id.removeButton).setEnabled(false);
        } else {
            if (product.getCurrentStatus().getStatus().equalsIgnoreCase("Taken")) {
                findViewById(R.id.menuFind).setEnabled(true);
                findViewById(R.id.menuWithdraw).setEnabled(false);
                findViewById(R.id.menuReturn).setEnabled(true);
                findViewById(R.id.menuAdd).setEnabled(false);
                findViewById(R.id.removeButton).setEnabled(false);
            }

            if (product.getCurrentStatus().getStatus().equalsIgnoreCase("Available")) {
                findViewById(R.id.menuFind).setEnabled(true);
                findViewById(R.id.menuWithdraw).setEnabled(true);
                findViewById(R.id.menuReturn).setEnabled(false);
                findViewById(R.id.menuAdd).setEnabled(false);
                findViewById(R.id.removeButton).setEnabled(true);
            }
        }
    }

    private boolean getItemScanned(String scannedCode) {
        List<Product> items = ProductService.getInstance().getProduct(scannedCode);
        if(!items.isEmpty()){
            product = items.get(0);
            return true;
        } else {
            product = new Product();
            product.set_id(scannedCode);
            return false;
        }
    }


}
