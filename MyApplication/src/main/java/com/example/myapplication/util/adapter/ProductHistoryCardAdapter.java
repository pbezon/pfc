package com.example.myapplication.util.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.dao.History;

import java.util.List;

/**
 * Created by Snapster on 9/16/2015.
 */
public class ProductHistoryCardAdapter extends RecyclerView.Adapter<ProductHistoryCardAdapter.HistoryViewHolder> {



    private List<History> historyList;

    public ProductHistoryCardAdapter(List<History> contactList) {
        this.historyList = contactList;
    }


    @Override
    public int getItemCount() {
        return historyList.size();
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.fragment_card_history_item, viewGroup, false);

        HistoryViewHolder viewHolder = new HistoryViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder contactViewHolder, int i) {

        int x = historyList.size();
        Log.v("array size", x + "");

        contactViewHolder.historyItemName.setText(historyList.get(i).getName());
        contactViewHolder.historyItemStatus.setText(String.valueOf(historyList.get(i).getStatus()));
        contactViewHolder.historyItemInDate.setText(historyList.get(i).getInDate());
        contactViewHolder.historyItemOutDate.setText(historyList.get(i).getOutDate());
        contactViewHolder.historyItemWho.setText(historyList.get(i).getWho());
    }


    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        protected TextView historyItemName;
        protected TextView historyItemStatus;
        protected TextView historyItemInDate;
        protected TextView historyItemOutDate;
        protected TextView historyItemWho;


        public HistoryViewHolder(View v) {
            super(v);
            historyItemName = (TextView) v.findViewById(R.id.historyItemName);
            historyItemStatus = (TextView) v.findViewById(R.id.historyItemStatus);
            historyItemInDate = (TextView) v.findViewById(R.id.historyItemInDate);
            historyItemOutDate = (TextView) v.findViewById(R.id.historyItemOutDate);
            historyItemWho = (TextView) v.findViewById(R.id.historyItemWho);
        }
    }

}
