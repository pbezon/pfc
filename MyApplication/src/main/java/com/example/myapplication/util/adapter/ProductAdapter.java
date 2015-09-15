package com.example.myapplication.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.dao.Product;

/**
 * Created by Snapster on 03/05/2015.
 */
public class ProductAdapter extends ArrayAdapter<Product> {

    private final Context context;
    private final Product[] values;

    public ProductAdapter(Context context, Product[] values) {
        super(context, R.layout.product_row, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;
        if (position % 2 == 0)
            rowView = inflater.inflate(R.layout.product_row_even, parent, false);
        else
            rowView = inflater.inflate(R.layout.product_row, parent, false);
        TextView nameText = (TextView) rowView.findViewById(R.id.toptext);
        TextView descText = (TextView) rowView.findViewById(R.id.bottomtext);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        nameText.setText(values[position].getName());
        descText.setText(values[position].getDescription());
        imageView.setImageResource(R.drawable.ic_action_add);
        //http://www.vogella.com/tutorials/AndroidListView/article.html
        return rowView;

    }

}
