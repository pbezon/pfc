package es.uc3m.manager.dao;

import android.os.AsyncTask;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import es.uc3m.manager.pojo.Item;

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
            Item response = template.postForObject(urlString, params[1], Item.class);
            return response != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
