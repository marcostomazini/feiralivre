package com.arquitetaweb.feira;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import com.arquitetaweb.feira.dto.FeiraModel;
import com.arquitetaweb.feira.enummodel.DayWeekEnum;
import com.arquitetaweb.feira.enummodel.PeriodEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

                List<DayWeekEnum> dias = getDayWeekEnums();
                feira.setDays(dias);

                RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radiogroup);
                int radioButtonID = radioGroup.getCheckedRadioButtonId();
                View radioButton = radioGroup.findViewById(radioButtonID);
                int idx = radioGroup.indexOfChild(radioButton);

                switch (idx) {
                    case 0:
                        feira.setPeriod(PeriodEnum.MANHA);
                        break;
                    case 1:
                        feira.setPeriod(PeriodEnum.TARDE);
                        break;
                    case 2:
                        feira.setPeriod(PeriodEnum.NOITE);
                        break;
                    case 3:
                        feira.setPeriod(PeriodEnum.MADRUGADA);
                        break;
                    default:
                        feira.setPeriod(PeriodEnum.MANHA);
                        break;
                }

                AsyncTask<Object, Void, Boolean> task = new RestFeiraAddTask((RegisterActivity)v.getContext()).execute("https://feiralivre.herokuapp.com/api/feira", feira);
                try {
                    if (task.get()) {
                        finish();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            private List<DayWeekEnum> getDayWeekEnums() {
                CheckBox checkDom = (CheckBox)findViewById(R.id.checkDom);
                CheckBox checkSeg = (CheckBox)findViewById(R.id.checkSeg);
                CheckBox checkTer = (CheckBox)findViewById(R.id.checkTer);
                CheckBox checkQua = (CheckBox)findViewById(R.id.checkQua);
                CheckBox checkQui = (CheckBox)findViewById(R.id.checkQui);
                CheckBox checkSex = (CheckBox)findViewById(R.id.checkSex);
                CheckBox checkSab = (CheckBox)findViewById(R.id.checkSab);
                List<DayWeekEnum> dias = new ArrayList<DayWeekEnum>();
                if (checkDom.isChecked()) {
                    dias.add(DayWeekEnum.DOMINGO);
                }
                if (checkSeg.isChecked()) {
                    dias.add(DayWeekEnum.SEGUNDA);
                }
                if (checkTer.isChecked()) {
                    dias.add(DayWeekEnum.TERCA);
                }
                if (checkQua.isChecked()) {
                    dias.add(DayWeekEnum.QUARTA);
                }
                if (checkQui.isChecked()) {
                    dias.add(DayWeekEnum.QUINTA);
                }
                if (checkSex.isChecked()) {
                    dias.add(DayWeekEnum.SEXTA);
                }
                if (checkSab.isChecked()) {
                    dias.add(DayWeekEnum.SABADO);
                }
                return dias;
            }
        });

        setTitle("Cadastrar");
    }
}
