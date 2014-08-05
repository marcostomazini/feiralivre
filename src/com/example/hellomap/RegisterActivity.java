package com.example.hellomap;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import com.arquitetaweb.feira.dto.FeiraModel;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by publisoft on 31/07/2014.
 */
public class RegisterActivity extends Activity {
    private FeiraModel feira;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        feira = (FeiraModel) getIntent().getSerializableExtra("feira");

        setTitle("Cadastrar");
    }
}
