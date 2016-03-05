package com.example.myapplication.service;

import android.os.AsyncTask;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * Created by Snapster on 30/09/2014.
 */
public class putTask extends AsyncTask<Object, Void, Boolean> {


    @Override
    protected Boolean doInBackground(Object... params) {
        String urlString = (String) params[0]; // URL to call
        try {
            RestTemplate template = new RestTemplate();
            template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            return template.postForObject(urlString, params[1], Boolean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
