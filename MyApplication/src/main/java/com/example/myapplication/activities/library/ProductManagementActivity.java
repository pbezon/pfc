package com.example.myapplication.activities.library;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapters.TabsPagerAdapter;

/**
 * Created by Snapster on 17/04/2015.
 */
public class ProductManagementActivity extends FragmentActivity implements ActionBar.TabListener {

    private ViewPager viewPager;
    private ActionBar actionBar;
    private TabsPagerAdapter mAdapter;

//    public static enum PROD_TABS {
//        Add ("Add" ,AddFragment.FRAGMENT_ID ), Remove ("Remove", RemoveFragment.FRAGMENT_ID), Withdraw ("Withdraw", WithdrawFragment.FRAGMENT_ID ), Return ("Return", ReturnFragment.FRAGMENT_ID );
//
//        String value;
//        int position;
//        private PROD_TABS (String s, int position) {
//            value = s;
//            this.position = position;
//        }
//        public boolean equalsName(String otherName) {
//            return (otherName == null) ? false : value.equals(otherName);
//        }
//
//        public int getPosition() {
//            return position;
//        }
//
//        public String toString() {
//            return this.value;
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);


        Bundle extras = getIntent().getExtras();
        String scannedCode = extras.getString("ID");
        int tab = extras.getInt("PRODUCT_ACTION");

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), scannedCode);

        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(4);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        String[] tabs = {AddFragment.TAB_NAME, RemoveFragment.TAB_NAME, WithdrawFragment.TAB_NAME, ReturnFragment.TAB_NAME};

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }
        actionBar.setSelectedNavigationItem(tab);

//        ((EditText)findViewById(R.id.editText)).setText(id);

        Toast.makeText(getApplicationContext(), "ID: " + scannedCode + ", tab: " + tab, Toast.LENGTH_LONG);

        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make tab selected
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
