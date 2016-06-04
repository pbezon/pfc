package es.uc3m.manager.dao;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import es.uc3m.manager.pojo.Product;
import es.uc3m.manager.pojo.Status;

/**
 * Created by Snapster on 5/1/2016.
 */
public class RestfulDao {

    private final String url = "http://192.168.1.81:8082";
    private final String getRest = "/";
    private final String newRest = "/new";
    private final String updateRest = "/update/";
    private final String deleteRest = "/delete/";
    private final String returnRest = "/return/";
    private final String withdrawRest = "/withdraw/";
    private GetTask getTask;
    private ObjectMapper productMapper;


    public List<Product> getProduct(String id) {
        Product p = new Product();
        p.set_id("123456");
        p.setName("fakeProductName");
        p.setDescription("fakeProductDescription");
        Status nowStatus = new Status();
        p.setCurrentStatus(nowStatus);
        p.getCurrentStatus().setCalendarEventId("4771");
        p.getCurrentStatus().setContactUri("content://com.android.contacts/data/36");
        ArrayList<Product> result = new ArrayList<Product>();
        result.add(p);
        result.add(p);
        return result;

    }

    public List<Product> realGetProduct(String id) {
        getTask = new GetTask();

        String urlString = url + getRest + id; // URL to call
        AsyncTask<String, Void, Product[]> response = getTask.execute(urlString);

        List<Product> result = new ArrayList<Product>();
        try {
            Product[] products = response.get();
            for (Product p : products) {
                result.add(p);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;

    }

    public Product updateProduct(Product product) {
        String urlString = url + updateRest; // URL to call
        // HTTP post
        try {
            RestTemplate template = new RestTemplate();
            template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            return template.postForObject(urlString, product, Product.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new Product();
    }

    public Boolean deleteProduct(String productId) {
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

    public Boolean returnProduct(Product product) {
        return true;
    }

    public Product withdrawProduct(String id) {
        Product p = new Product();
        p.set_id("123456");
        p.setDescription("fakeProduct");
//        p.setDiscontinued(false);
//        p.setUnitprice(123);
        Status nowStatus = new Status();
//        nowStatus.setOutDate(new Date().toString());
        nowStatus.setStatus("Taken");
        nowStatus.setContactUri("Dude");
        p.setCurrentStatus(nowStatus);
        return p;
    }

    public Boolean add(Product p) {
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
