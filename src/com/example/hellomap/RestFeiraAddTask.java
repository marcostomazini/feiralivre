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

public class RestFeiraAddTask extends AsyncTask<Object, Void, Boolean>{

    private ProgressDialog progressDialog;

    public RestFeiraAddTask(Context context) {
        progressDialog = new ProgressDialog(context);
    }

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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Salvando Local");
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
    }
}