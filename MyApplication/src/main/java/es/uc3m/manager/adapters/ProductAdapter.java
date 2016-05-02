package es.uc3m.manager.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.uc3m.manager.R;
import es.uc3m.manager.pojo.Product;

/**
 * Created by Snapster on 03/05/2015.
 */
public class ProductAdapter extends ArrayAdapter<Product> implements Filterable {

    private final Context context;
    private List<Product> values;
    private List<Product> originalValues;
    private Filter filter;

    public ProductAdapter(Context context, List<Product> values) {
        super(context, R.layout.product_list_row_even, values);
        this.context = context;
        this.values = values;
        cloneItems(values);
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;
        rowView = inflater.inflate(R.layout.product_list_row_even, parent, false);
        TextView nameText = (TextView) rowView.findViewById(R.id.toptext);
        TextView descText = (TextView) rowView.findViewById(R.id.bottomtext);
        TextView status = (TextView) rowView.findViewById(R.id.status);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        nameText.setText(values.get(position).getName());
        descText.setText(values.get(position).getDescription());
        status.setText(values.get(position).getCurrentStatus().getStatus());
        imageView.setImageResource(R.drawable.ic_action_search);
        //http://www.vogella.com/tutorials/AndroidListView/article.html
        return rowView;

    }

    @Override
    public int getCount() {
        if (values == null)
            return 0;
        return values.size();
    }

    @Override
    public Filter getFilter() {
        if (filter != null)
            return filter;

        filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                values = (ArrayList<Product>) results.values;
                notifyDataSetChanged();

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();


                if (constraint == null || constraint.length() == 0) {
                    results.values = originalValues;
                    results.count = originalValues.size();
                } else {
                    List<Product> filteredResult = new ArrayList<Product>();
                    List<Product> localCopy = new ArrayList<Product>();
                    localCopy.addAll(originalValues);
                    constraint = constraint.toString().toLowerCase();
                    for (Product p : localCopy) {
                        if (p.getName().toLowerCase().contains(constraint)
                                || p.getDescription().toLowerCase().contains(constraint)
                                || (p.getCurrentStatus().getStatus() != null
                                && p.getCurrentStatus().getStatus().toLowerCase().contains(constraint))) {
                            filteredResult.add(p);
                        }
                    }

                    results.count = filteredResult.size();
                    results.values = filteredResult;
                    Log.e("VALUES", results.values.toString());
                }
                return results;
            }
        };

        return filter;
    }

    protected void cloneItems(List<Product> items) {
        originalValues = new ArrayList<Product>();
        for (Iterator iterator = items.iterator(); iterator.hasNext(); ) {
            Product p = (Product) iterator.next();
            originalValues.add(p);
        }
    }

}
