package com.example.myapplication.service;

import android.os.AsyncTask;

import com.example.myapplication.pojo.Product;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * Created by Snapster on 30/09/2014.
 */
public class ApiProxy extends AsyncTask<String, Void, String> {


    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0]; // URL to call
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
            return e.getMessage();
        }

        return resultToDisplay;
    }

    protected String getASCIIContent(BufferedInputStream in) throws IllegalStateException, IOException {
        StringBuffer out = new StringBuffer();
        int n = 1;
        while (n > 0) {
            byte[] b = new byte[4096];
            n = in.read(b);
            if (n > 0) out.append(new String(b, 0, n));
        }
        return out.toString();
    }
}
