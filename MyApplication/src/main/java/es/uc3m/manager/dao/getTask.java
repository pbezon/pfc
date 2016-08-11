package es.uc3m.manager.dao;

import android.os.AsyncTask;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import es.uc3m.manager.pojo.Item;

/**
 * Created by Snapster on 30/09/2014.
 */
class GetTask extends AsyncTask<String, Void, Item[]> {


    @Override
    protected Item[] doInBackground(String... params) {
        String urlString = params[0]; // URL to call
        Item[] p;
        // HTTP Get
        RestTemplate template = new RestTemplate();
//        JavaType listType = productMapper.getTypeFactory().constructCollectionType(List.class, Item.class);
        template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        p = template.getForObject(urlString, Item[].class);
        return p;
    }
}
