package com.example.myapplication.service;

import android.os.AsyncTask;

import com.example.myapplication.pojo.Product;
import com.fasterxml.jackson.databind.JavaType;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Snapster on 30/09/2014.
 */
public class getTask extends AsyncTask<String, Void, Product[]> {


    @Override
    protected Product[] doInBackground(String... params) {
        String urlString = params[0]; // URL to call
        BufferedInputStream in = null;
        Product[] p;

        // HTTP Get

        RestTemplate template = new RestTemplate();
//        JavaType listType = productMapper.getTypeFactory().constructCollectionType(List.class, Product.class);
        template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        p = template.getForObject(urlString, Product[].class);
        return p;
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
