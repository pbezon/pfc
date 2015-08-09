package com.example.myapplication.service;

import com.example.myapplication.dao.Product;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * Created by Snapster on 21/03/2015.
 */
public class ProductService {

    private ApiProxy apiProxy = new ApiProxy();
    private ObjectMapper productMapper = new ObjectMapper();
    private final String url = "http://192.168.1.42:80801";
    private final String getRest = "/";
    private final String updateRest = "/update/";
    private final String deleteRest = "/delete/";
    private final String returnRest = "/return/";
    private final String withdrawRest = "/withdraw/";

    public Product getProduct(String id) {
        try {
            String response;
            String urlString = url + getRest + id; // URL to call
            String resultToDisplay = "";
            BufferedInputStream in = null;
            // HTTP Get
            try {
                RestTemplate template = new RestTemplate();
                template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Product[] p = template.getForObject(urlString, Product[].class);
                p.toString();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            response = resultToDisplay;
            return productMapper.readValue(response, Product.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean updateProduct(Product product) {
        String urlString = url + updateRest; // URL to call
        // HTTP post
        try {
            RestTemplate template = new RestTemplate();
            template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            return template.postForObject(urlString, product, Boolean.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
        }
        return false;
    }

}
