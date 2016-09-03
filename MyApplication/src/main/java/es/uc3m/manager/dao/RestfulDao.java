package es.uc3m.manager.dao;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import es.uc3m.manager.pojo.Item;
import es.uc3m.manager.pojo.Status;

/**
 * Created by Snapster on 5/1/2016.
 */
class RestfulDao implements ItemDao {

    private String url = "http://192.168.1.81:8082";
    private final String returnRest = "/return/";
    private final String withdrawRest = "/withdraw/";
    private ObjectMapper productMapper;

    @Override
    public List<Item> getItem(String id) {
        GetTask getTask = new GetTask();

        String getRest = "/";
        String urlString = url + getRest + id; // URL to call
        AsyncTask<String, Void, Item[]> response = getTask.execute(urlString);

        List<Item> result = new ArrayList<Item>();
        try {
            Item[] items = response.get();
            Collections.addAll(result, items);
        } catch (InterruptedException e) {
            Log.e("RestfulDao", e.getMessage());
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.e("RestfulDao", e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Boolean updateItem(Item item) {
        String updateRest = "/update";
        String urlString = url + updateRest; // URL to call
        try {
            return new PutTask().execute(urlString, item).get();
        } catch (InterruptedException e) {
            Log.e("RestfulDao", e.getMessage());
            return false;
        } catch (ExecutionException e) {
            Log.e("RestfulDao", e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean deleteItem(String itemId) {
        String deleteRest = "/delete/";
        String urlString = url + deleteRest + itemId; // URL to call
        // HTTP post
        try {
            return new DeleteTask().execute(urlString, itemId).get();
        } catch (InterruptedException e) {
            Log.e("RestfulDao", e.getMessage());
            return false;
        } catch (ExecutionException e) {
            Log.e("RestfulDao", e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean returnItem(Item item) {
        String returnRest = "/return/";
        String urlString = url + returnRest + item.get_id(); // URL to call
        try {
            return new PutTask().execute(urlString, item).get();
        } catch (InterruptedException e) {
            Log.e("RestfulDao", e.getMessage());
            return false;
        } catch (ExecutionException e) {
            Log.e("RestfulDao", e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean withdrawItem(Item item) {
        String withdrawRest = "/withdraw/";
        String urlString = url + withdrawRest + item.get_id(); // URL to call
        try {
            return new PutTask().execute(urlString, item).get();
        } catch (InterruptedException e) {
            Log.e("RestfulDao", e.getMessage());
            return false;
        } catch (ExecutionException e) {
            Log.e("RestfulDao", e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean add(Item p) {
        String newRest = "/new";
        String urlString = url + newRest; // URL to call
        try {
            return new PutTask().execute(urlString, p).get();
        } catch (InterruptedException e) {
            Log.e("RestfulDao", e.getMessage());
            return false;
        } catch (ExecutionException e) {
            Log.e("RestfulDao", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean reload(String ip, String port) {
        url = "http://"+ip+":"+port;
        return true;
    }
}
