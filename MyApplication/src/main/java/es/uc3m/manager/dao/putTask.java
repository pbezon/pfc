package es.uc3m.manager.dao;

import android.os.AsyncTask;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Snapster on 30/09/2014.
 */
class PutTask extends AsyncTask<Object, Void, Boolean> {

    @Override
    protected Boolean doInBackground(Object... params) {
        String urlString = (String) params[0]; // URL to call
        try {
            RestTemplate template = new RestTemplate();
            MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
            List<MediaType> supportedMediaTypes = new ArrayList<MediaType>(converter.getSupportedMediaTypes());
            supportedMediaTypes.add(MediaType.TEXT_HTML);
            converter.setSupportedMediaTypes(supportedMediaTypes);
            template.getMessageConverters().add(converter);
            return template.postForObject(urlString, params[1], Boolean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
