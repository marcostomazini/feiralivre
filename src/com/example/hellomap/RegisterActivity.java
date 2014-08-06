package com.example.hellomap;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import com.arquitetaweb.feira.dto.FeiraModel;
import com.com.arquitetaweb.feira.enummodel.PeriodEnum;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by publisoft on 31/07/2014.
 */
public class RegisterActivity extends Activity {
    private FeiraModel feira;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        feira = (FeiraModel) getIntent().getSerializableExtra("feira");


        Button salvar = (Button)findViewById(R.id.btnsalvar);
        salvar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText title = (EditText)findViewById(R.id.editTitle);
                feira.setDescription(title.getText().toString());

                EditText information = (EditText)findViewById(R.id.editInfo);
                feira.setInformation(information.getText().toString());

                RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radiogroup);
                int radioButtonID = radioGroup.getCheckedRadioButtonId();
                View radioButton = radioGroup.findViewById(radioButtonID);
                int idx = radioGroup.indexOfChild(radioButton);

                switch (idx) {
                    case 0:
                        feira.setPeriod(PeriodEnum.Manha);
                        break;
                    case 1:
                        feira.setPeriod(PeriodEnum.Tarde);
                        break;
                    case 2:
                        feira.setPeriod(PeriodEnum.Noite);
                        break;
                    case 3:
                        feira.setPeriod(PeriodEnum.Madrugada);
                        break;
                    default:
                        feira.setPeriod(PeriodEnum.Manha);
                        break;
                }

                AsyncTask<Object, Void, Boolean> task = new RestFeiraAddTask().execute("https://feiralivre.herokuapp.com/api/feira", feira);
                finish();
            }
        });

        setTitle("Cadastrar");
    }
}
