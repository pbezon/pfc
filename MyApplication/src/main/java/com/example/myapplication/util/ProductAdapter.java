package com.example.myapplication.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.pojo.Product;

import java.util.ArrayList;

/**
 * Created by Snapster on 16/04/2015.
 */
public class ProductAdapter extends ArrayAdapter<Product> {

    private ArrayList<Product> items;

    public ProductAdapter(Context context, int textViewResourceId, ArrayList<Product> items) {
        super(context, textViewResourceId, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
//        if (v == null) {
//            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            v = vi.inflate(R.layout.product_row, null);
//        }
        Product o = items.get(position);
        if (o != null) {
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
            if (tt != null) {
                tt.setText("Name: " + o.getName());
            }
            if (bt != null) {
                bt.setText("Description: " + o.getDescription());
            }
        }
        return v;
    }

}
