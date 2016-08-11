package es.uc3m.manager.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import es.uc3m.manager.R;
import es.uc3m.manager.pojo.Item;

/**
 * Created by Snapster on 16/04/2015.
 */
class ItemAdapter extends ArrayAdapter<Item> {

    private final ArrayList<Item> items;

    public ItemAdapter(Context context, int textViewResourceId, ArrayList<Item> items) {
        super(context, textViewResourceId, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //        if (v == null) {
//            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            v = vi.inflate(R.layout.product_row, null);
//        }
        Item o = items.get(position);
        if (o != null) {
            TextView tt = (TextView) convertView.findViewById(R.id.toptext);
            TextView bt = (TextView) convertView.findViewById(R.id.bottomtext);
            if (tt != null) {
                tt.setText("Name: " + o.getName());
            }
            if (bt != null) {
                bt.setText("Description: " + o.getDescription());
            }
        }
        return convertView;
    }

}
