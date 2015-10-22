package com.example.myapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.pojo.History;

import java.util.List;

/**
 * Created by Snapster on 03/05/2015.
 */
public class ProductHistoryAdapter extends ArrayAdapter<History> {

    private final Context context;
    private final List<History> values;

    public ProductHistoryAdapter(Context context, List<History> values) {
        super(context, R.layout.fragment_card_history_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.fragment_card_history_item, parent, false);
        History history = values.get(position);
        ((TextView) rowView.findViewById(R.id.historyItemName)).setText(history.getName());
        ((TextView) rowView.findViewById(R.id.historyItemStatus)).setText(String.valueOf(history.getStatus()));
        ((TextView) rowView.findViewById(R.id.historyItemInDate)).setText(history.getInDate());
        ((TextView) rowView.findViewById(R.id.historyItemOutDate)).setText(history.getOutDate());
        //pendiente de hacer query a contactos
        ((TextView) rowView.findViewById(R.id.historyItemWho)).setText(history.getWho());

        return rowView;

    }

}
