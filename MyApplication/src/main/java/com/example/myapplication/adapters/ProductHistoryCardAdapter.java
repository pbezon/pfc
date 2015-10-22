package com.example.myapplication.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.pojo.History;

import java.util.List;

/**
 * Created by Snapster on 9/16/2015.
 */
public class ProductHistoryCardAdapter extends RecyclerView.Adapter<ProductHistoryCardAdapter.HistoryViewHolder> {

    private List<History> historyList;
    private LayoutInflater layoutInflater;

    public ProductHistoryCardAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void setHistoryList(List<History> historyList) {
        this.historyList = historyList;
        notifyDataSetChanged();
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = layoutInflater.inflate(R.layout.histroy_card, viewGroup, false);
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

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        protected CardView cv;
        protected TextView historyItemName;
        protected TextView historyItemStatus;
        protected TextView historyItemInDate;
        protected TextView historyItemOutDate;
        protected TextView historyItemWho;


        public HistoryViewHolder(View v) {
            super(v);
            cv = (CardView) itemView.findViewById(R.id.cardview);
            historyItemName = (TextView) v.findViewById(R.id.historyItemName);
            historyItemStatus = (TextView) v.findViewById(R.id.historyItemStatus);
            historyItemInDate = (TextView) v.findViewById(R.id.historyItemInDate);
            historyItemOutDate = (TextView) v.findViewById(R.id.historyItemOutDate);
            historyItemWho = (TextView) v.findViewById(R.id.historyItemWho);
        }
    }

}
