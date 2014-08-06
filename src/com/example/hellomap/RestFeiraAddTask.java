package com.example.hellomap;

import android.os.AsyncTask;
import com.arquitetaweb.feira.dto.FeiraModel;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Marcos on 04/08/2014.
 */

public class RestFeiraAddTask extends AsyncTask<Object, Void, Boolean>{
    @Override
    protected Boolean doInBackground(Object... params) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

        try {
            restTemplate.postForObject((String) params[0], params[1], FeiraModel[].class);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}