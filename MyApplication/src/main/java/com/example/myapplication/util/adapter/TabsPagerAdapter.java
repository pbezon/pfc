package com.example.myapplication.util.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.myapplication.activities.library.AddFragment;
import com.example.myapplication.activities.library.RemoveFragment;
import com.example.myapplication.activities.library.ReturnFragment;
import com.example.myapplication.activities.library.WithdrawFragment;


public class TabsPagerAdapter extends FragmentPagerAdapter {

    public static final int PRODUCT_MNGMNT_TAB_COUNT = 4;

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case AddFragment.FRAGMENT_ID:
                // Add fragment activity
                return new AddFragment();
            case RemoveFragment.FRAGMENT_ID:
                // Remove fragment activity
                return new RemoveFragment();
            case WithdrawFragment.FRAGMENT_ID:
                // Withdraw fragment activity
                return new WithdrawFragment();
            case ReturnFragment.FRAGMENT_ID:
                // Return fragment activity
                return new ReturnFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return PRODUCT_MNGMNT_TAB_COUNT;
    }
}
