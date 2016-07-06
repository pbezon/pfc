package es.uc3m.manager.dao;

import android.os.AsyncTask;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.IOException;

import es.uc3m.manager.pojo.Product;

/**
 * Created by Snapster on 30/09/2014.
 */
class PutTask extends AsyncTask<Object, Void, Boolean> {


    @Override
    protected Boolean doInBackground(Object... params) {
        String urlString = (String) params[0]; // URL to call
        try {
            RestTemplate template = new RestTemplate();
            template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Product response = template.postForObject(urlString, params[1], Product.class);
            return response != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    protected String getASCIIContent(BufferedInputStream in) throws IllegalStateException, IOException {
        StringBuilder out = new StringBuilder();
        int n = 1;
        while (n > 0) {
            byte[] b = new byte[4096];
            n = in.read(b);
            if (n > 0) out.append(new String(b, 0, n));
        }
        return out.toString();
    }

}
