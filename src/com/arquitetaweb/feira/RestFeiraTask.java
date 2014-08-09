package com.arquitetaweb.feira;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import com.arquitetaweb.feira.dto.FeiraModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by Marcos on 04/08/2014.
 */

public class RestFeiraTask extends AsyncTask<String, Void, FeiraModel[]>{
    private AsyncTaskListener callback;
    private ProgressDialog progressDialog;

    public RestFeiraTask(Context context) {
        this.callback = (AsyncTaskListener) context;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected FeiraModel[] doInBackground(String... params) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

        FeiraModel[] feiras =  restTemplate.getForObject(params[0], FeiraModel[].class);
        return feiras;
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
        callback.onTaskComplete(result);
        progressDialog.dismiss();
    }
}
