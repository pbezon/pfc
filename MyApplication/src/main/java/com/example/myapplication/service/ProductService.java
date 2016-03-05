package com.example.myapplication.service;

import android.os.AsyncTask;

import com.example.myapplication.pojo.Product;
import com.example.myapplication.pojo.Status;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Snapster on 21/03/2015.
 */
public class ProductService {

    private getTask getTask;
    private ObjectMapper productMapper;
    private final String url = "http://192.168.1.81:8081";
    private final String getRest = "/";
    private final String newRest = "/new";
    private final String updateRest = "/update/";
    private final String deleteRest = "/delete/";
    private final String returnRest = "/return/";
    private final String withdrawRest = "/withdraw/";

    private static ProductService instance;

    private List<Product> dummyList = new ArrayList<Product>();

    private ProductService() {
        getTask = new getTask();
        productMapper = new ObjectMapper();
    }

    public static ProductService getInstance() {
        if (instance == null)
            instance = new ProductService();
        return instance;
    }


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

    public Boolean updateProduct(Product product) {
        String urlString = url + updateRest; // URL to call
        // HTTP post
        try {
            RestTemplate template = new RestTemplate();
            template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            return template.postForObject(urlString, product, Boolean.class);
        } catch (Exception e) {
//            System.out.println(e.getMessage());
        }
        return false;
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
        String urlString = url+newRest; // URL to call
        try {
            return new putTask().execute(urlString, p).get();
        } catch (InterruptedException e) {
            return false;
        } catch (ExecutionException e) {
            return false;
        }
    }
}
