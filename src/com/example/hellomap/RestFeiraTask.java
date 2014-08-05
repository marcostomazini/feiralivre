package com.example.hellomap;

import android.os.AsyncTask;
import com.arquitetaweb.feira.dto.FeiraModel;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Marcos on 04/08/2014.
 */

public class RestFeiraTask extends AsyncTask<String, Void, FeiraModel[]>{
    @Override
    protected FeiraModel[] doInBackground(String... params) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

        return restTemplate.getForObject(params[0], FeiraModel[].class);
    }
}