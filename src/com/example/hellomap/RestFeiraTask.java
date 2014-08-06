package com.example.hellomap;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import com.arquitetaweb.feira.dto.FeiraModel;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Marcos on 04/08/2014.
 */

public class RestFeiraTask extends AsyncTask<String, Void, FeiraModel[]>{
    private ProgressDialog progressDialog;

    public RestFeiraTask(Context context) {
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected FeiraModel[] doInBackground(String... params) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

        return restTemplate.getForObject(params[0], FeiraModel[].class);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Procurando Locais");
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(FeiraModel[] result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
    }
}