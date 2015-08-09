package com.example.myapplication.activities.display;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.service.ApiProxy;
import com.example.myapplication.service.ProductService;

import java.util.concurrent.ExecutionException;

public class DisplayResults extends Activity {

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_results);

        Bundle extras = getIntent().getExtras();
        id = extras.getString("ID");

        TextView textView = new TextView(this);
        textView.setTextSize(12);
//        try {
            //textView.setText(new ApiProxy().execute("http://192.168.1.42:8081/53fe0690999387e508b476ad").get());
            textView.setText(new ProductService().getProduct("53fe0690999387e508b476ad").toString());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }


        setContentView(textView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_results, menu);
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
}
