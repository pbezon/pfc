package com.example.myapplication.activities.library;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.util.adapter.TabsPagerAdapter;

/**
 * Created by Snapster on 17/04/2015.
 */
public class ProductManagementActivity extends FragmentActivity implements ActionBar.TabListener {

    private ViewPager viewPager;
    private ActionBar actionBar;
    private TabsPagerAdapter mAdapter;

    public enum PROD_TABS {
        Add ("Add"), Remove ("Remove"), Withdraw ("Withdraw"), Return ("Return");

        String value;
        private PROD_TABS (String s) {
            value = s;
        }

        public boolean equalsName(String otherName) {
            return (otherName == null) ? false : value.equals(otherName);
        }

        public String toString() {
            return this.value;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);


        Bundle extras = getIntent().getExtras();
        String id = extras.getString("ID");
        int tab = extras.getInt("PRODUCT_ACTION");

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        String[] tabs = {"ADD", "REM", "WithDraw", "RETURN"};

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }

//        ((EditText)findViewById(R.id.editText)).setText(id);

        Toast.makeText(getApplicationContext(), "ID: " + id + ", tab: " + tab, Toast.LENGTH_LONG);

        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

    }

}
