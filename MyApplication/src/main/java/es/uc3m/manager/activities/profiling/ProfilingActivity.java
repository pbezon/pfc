package es.uc3m.manager.activities.profiling;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import es.uc3m.manager.R;
import es.uc3m.manager.pojo.Item;
import es.uc3m.manager.pojo.Status;

public class ProfilingActivity extends Activity {

    TextView profilingResults;
    TextView inserts;
    Button launch;
    Button launchGet;
    Button clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.performance);

        launch = (Button) findViewById(R.id.launchPerformance);
        launchGet = (Button) findViewById(R.id.launchGet);
        clear = (Button) findViewById(R.id.clearConsole);
        inserts = (TextView) findViewById(R.id.performanceInput);
        profilingResults = (TextView) findViewById(R.id.logResultPerformance);
        profilingResults.setTextColor(Color.BLACK);


        launch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long id = System.currentTimeMillis();
                for (int i = 0; i<Integer.valueOf(inserts.getText().toString()); i++) {
                    Item item = new Item();
                    item.set_id(String.valueOf(id++));
                    item.setName("name " + id);
                    item.setDescription("name " + id);
                    Status currentStatus = new Status();
                    currentStatus.setStatus("AVAILABLE");
                    item.setCurrentStatus(currentStatus);
                    String text = "";
                    try {
                        text = new PutInsertTask().execute("http://192.168.1.81:8082/new", item).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    profilingResults.setText(profilingResults.getText()+"\n"+text);
                }
                Toast.makeText(getApplicationContext(), "Done" , Toast.LENGTH_SHORT).show();
            }
        });

        launchGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long id = System.currentTimeMillis();
                for (int i = 0; i<Integer.valueOf(inserts.getText().toString()); i++) {
                    String text = "";
                    try {
                        text = new GetAllTask().execute("http://192.168.1.81:8082/").get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    profilingResults.setText(profilingResults.getText()+"\n"+text);

                }
                Toast.makeText(getApplicationContext(), "Done" , Toast.LENGTH_SHORT).show();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profilingResults.setText("");
                Toast.makeText(getApplicationContext(), "Done" , Toast.LENGTH_SHORT).show();
            }
        });
    }

    class PutInsertTask extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... params) {
            String urlString = (String) params[0]; // URL to call
            try {
                long init = System.currentTimeMillis();
                RestTemplate template = new RestTemplate();
                MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
                List<MediaType> supportedMediaTypes = new ArrayList<MediaType>(converter.getSupportedMediaTypes());
                supportedMediaTypes.add(MediaType.TEXT_HTML);
                converter.setSupportedMediaTypes(supportedMediaTypes);
                template.getMessageConverters().add(converter);
                template.postForObject(urlString, params[1], Boolean.class);
                long end = System.currentTimeMillis();
                return ("Profiling Insert: " + (end - init));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

    }

    class GetAllTask extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... params) {
            String urlString = (String) params[0]; // URL to call
            try {
                long init = System.currentTimeMillis();
                Item[] p;
                RestTemplate template = new RestTemplate();
                template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                p = template.getForObject(urlString, Item[].class);
                long end = System.currentTimeMillis();
                return ("Profiling Get ALL: " + (end - init) + ", size: "+p.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
    }
}

