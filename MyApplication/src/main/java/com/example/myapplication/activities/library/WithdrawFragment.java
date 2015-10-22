package com.example.myapplication.activities.library;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;

/**
 * Created by Snapster on 15/06/2015.
 */
public class WithdrawFragment extends Fragment {

    public final static int FRAGMENT_ID = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_withdraw_item, container, false);

        return rootView;
    }
}
