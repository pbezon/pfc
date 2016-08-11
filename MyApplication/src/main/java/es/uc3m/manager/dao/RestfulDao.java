package es.uc3m.manager.dao;

import android.os.AsyncTask;

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
class RestfulDao {

    private final String url = "http://192.168.1.81:8082";
    private final String returnRest = "/return/";
    private final String withdrawRest = "/withdraw/";
    private ObjectMapper productMapper;


    public List<Item> getProduct(String id) {
        Item p = new Item();
        p.set_id("123456");
        p.setName("fakeProductName");
        p.setDescription("fakeProductDescription");
        Status nowStatus = new Status();
        p.setCurrentStatus(nowStatus);
        p.getCurrentStatus().setCalendarEventId("4771");
        p.getCurrentStatus().setContactUri("content://com.android.contacts/data/36");
        ArrayList<Item> result = new ArrayList<Item>();
        result.add(p);
        result.add(p);
        return result;

    }

    public List<Item> realGetProduct(String id) {
        GetTask getTask = new GetTask();

        String getRest = "/";
        String urlString = url + getRest + id; // URL to call
        AsyncTask<String, Void, Item[]> response = getTask.execute(urlString);

        List<Item> result = new ArrayList<Item>();
        try {
            Item[] items = response.get();
            Collections.addAll(result, items);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;

    }

    public Item updateProduct(Item item) {
        String updateRest = "/update/";
        String urlString = url + updateRest; // URL to call
        // HTTP post
        try {
            RestTemplate template = new RestTemplate();
            template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            return template.postForObject(urlString, item, Item.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new Item();
    }

    public Boolean deleteProduct(String productId) {
        String deleteRest = "/delete/";
        String urlString = url + deleteRest + productId; // URL to call
        // HTTP post
        try {
            RestTemplate template = new RestTemplate();
//            template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            return template.postForObject(urlString, null, Boolean.class);
        } catch (Exception e) {
//            System.out.println(e.getMessage());
        }
        return false;
    }

    public Boolean returnProduct(Item item) {
        return true;
    }

    public Item withdrawProduct(String id) {
        Item p = new Item();
        p.set_id("123456");
        p.setDescription("fakeProduct");
        Status nowStatus = new Status();
//        nowStatus.setOutDate(new Date().toString());
        nowStatus.setStatus("Taken");
        nowStatus.setContactUri("Dude");
        p.setCurrentStatus(nowStatus);
        return p;
    }

    public Boolean add(Item p) {
        String newRest = "/new";
        String urlString = url + newRest; // URL to call
        try {
            return new PutTask().execute(urlString, p).get();
        } catch (InterruptedException e) {
            return false;
        } catch (ExecutionException e) {
            return false;
        }
    }
}
