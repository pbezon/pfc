package com.example.myapplication.activities.display;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ListActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.dao.Product;
import com.example.myapplication.service.ProductService;
import com.example.myapplication.util.adapter.ProductAdapter;
import com.example.myapplication.util.adapter.ProductHistoryCardAdapter;

public class DisplayResults extends ListActivity {

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_results);

        ProductAdapter productAdapter = new ProductAdapter(this, new ProductService().getProduct(""));
        setListAdapter(productAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Product item = (Product) getListAdapter().getItem(position);
        Toast.makeText(this, item.getName() + " selected", Toast.LENGTH_LONG).show();
        this.flipCard(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void flipCard(Integer position) {

        CardBackFragment fragment = new CardBackFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        fragment.setArguments(bundle);
        // Create and commit a new fragment transaction that adds the fragment for the back of
        // the card, uses custom animations, and is part of the fragment manager's back stack.
        getFragmentManager()
                .beginTransaction()
                        // Replace the default fragment animations with animator resources representing
                        // rotations when switching to the back of the card, as well as animator
                        // resources representing rotations when flipping back to the front (e.g. when
                        // the system Back button is pressed).
                .setCustomAnimations(
                        R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                        // Replace any fragments currently in the container view with a fragment
                        // representing the next page (indicated by the just-incremented currentPage
                        // variable).
                .replace(R.id.flipContainer, fragment)
                        // Add this transaction to the back stack, allowing users to press Back
                        // to get to the front of the card.
                .addToBackStack(null)
                        // Commit the transaction.
                .commit();
    }


    /**
     * A fragment representing the back of the card.
     */
    @SuppressLint("ValidFragment")
    private class CardBackFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_display_results_detail, container, false);
//
//            Product product = (Product) getListAdapter().getItem(getArguments().getInt("position"));
//            TextView nameText = (TextView) view.findViewById(R.id.detailedName);
//            TextView descText = (TextView) view.findViewById(R.id.detailedDescription);
//            nameText.setText(product.getName());
//            descText.setText(product.getDescription());
//            ListView listView = (ListView) view.findViewById(R.id.listPastHistory);
//            listView.setAdapter(new ProductHistoryAdapter(getApplicationContext(), product.getProductHistory()));
            return view;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
//            RecyclerView recList = (RecyclerView) view.findViewById(R.id.cardList);
//            Product product = (Product) getListAdapter().getItem(getArguments().getInt("position"));
//            LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
//            llm.setOrientation(LinearLayoutManager.VERTICAL);
//            recList.setLayoutManager(llm);
//
//            ProductHistoryCardAdapter cardAdapter = new ProductHistoryCardAdapter(product.getProductHistory());
//            recList.setAdapter(cardAdapter);
        }

    }



}
