package com.example.myapplication.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.pojo.Product;
import com.example.myapplication.adapters.ProductHistoryCardAdapter;

/**
 * Created by Snapster on 10/19/2015.
 */
public class FragmentDetails extends Fragment{


    ProductHistoryCardAdapter productHistoryCardAdapter;
    RecyclerView recyclerViewCardList;
    Product product;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        product = (Product) getArguments().getSerializable("item");
        recyclerViewCardList = (RecyclerView) view.findViewById(R.id.listDails);
        recyclerViewCardList.setLayoutManager(new LinearLayoutManager(getActivity()));
        productHistoryCardAdapter = new ProductHistoryCardAdapter(getActivity());
        productHistoryCardAdapter.setHistoryList(product.getProductHistory());
        recyclerViewCardList.setAdapter(productHistoryCardAdapter);
        return view;
    }
}
