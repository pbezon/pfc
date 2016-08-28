package es.uc3m.manager.activities.collection;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import es.uc3m.manager.R;
import es.uc3m.manager.activities.actions.EditActivity;
import es.uc3m.manager.adapters.ItemAdapter;
import es.uc3m.manager.pojo.Item;
import es.uc3m.manager.service.ItemService;

public class ListCollectionActivity extends ListActivity {

    private String id;

    private ItemAdapter itemAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_results);

        itemAdapter = new ItemAdapter(this, ItemService.getInstance().getItem(""));
        setListAdapter(itemAdapter);

        addFilterListener(itemAdapter);

        this.getListView().setLongClickable(true);
        this.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                //Do your tasks here
                AlertDialog.Builder alert = new AlertDialog.Builder(ListCollectionActivity.this);
                alert.setTitle("Alert!!");
                alert.setMessage("Are you sure to delete record");

                final Item localItem = itemAdapter.getItem(position);

                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ItemService.getInstance().deleteItem(localItem.get_id());
                        itemAdapter.notifyDataSetChanged();
                        itemAdapter.remove(localItem);
                        //do your work here
                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
                return true;
            }
        });
    }

    private void addFilterListener(final ItemAdapter itemAdapter) {
        EditText inputSearch = (EditText) findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                itemAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        try {
            Item item = (Item) getListAdapter().getItem(position);
            Toast.makeText(this, item.getName() + " selected", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getBaseContext(), EditActivity.class);
            intent.putExtra("ID", item.get_id());
            intent.putExtra("ITEM", item);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(e.getMessage(), e.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        itemAdapter = new ItemAdapter(this, ItemService.getInstance().getItem(""));
        setListAdapter(itemAdapter);
        addFilterListener(itemAdapter);
    }
}
