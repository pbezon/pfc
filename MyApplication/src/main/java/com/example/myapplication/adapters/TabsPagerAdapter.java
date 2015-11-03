package com.example.myapplication.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.myapplication.activities.library.AddFragment;
import com.example.myapplication.activities.library.RemoveFragment;
import com.example.myapplication.activities.library.ReturnFragment;
import com.example.myapplication.activities.library.WithdrawFragment;


public class TabsPagerAdapter extends FragmentPagerAdapter {

    public static final int PRODUCT_MNGMNT_TAB_COUNT = 4;
    private String scannedCode;

    public TabsPagerAdapter(FragmentManager fm, String scannedCode) {
        super(fm);
        this.scannedCode = scannedCode;
    }

    @Override
    public Fragment getItem(int index) {
        Fragment f = null;
        Bundle bundle = new Bundle();
        bundle.putString("scannedCode", scannedCode);
        switch (index) {

            case AddFragment.FRAGMENT_ID:
                // Add fragment activity
                f = new AddFragment();
                f.setArguments(bundle);
                break;
            case RemoveFragment.FRAGMENT_ID:
                // Remove fragment activity
                f = new RemoveFragment();
                f.setArguments(bundle);
                break;
            case WithdrawFragment.FRAGMENT_ID:
                // Withdraw fragment activity
                f = new WithdrawFragment();
                f.setArguments(bundle);
                break;
            case ReturnFragment.FRAGMENT_ID:
                // Return fragment activity
                f = new ReturnFragment();
                f.setArguments(bundle);
                break;
        }
        return f;
    }

    @Override
    public int getCount() {
        return PRODUCT_MNGMNT_TAB_COUNT;
    }
}
