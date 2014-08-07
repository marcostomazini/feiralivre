package com.arquitetaweb.feira;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.arquitetaweb.feira.dto.FeiraModel;
import com.arquitetaweb.feira.enummodel.PeriodEnum;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;

public class MainActivity extends FragmentActivity {
    private GoogleMap mMap;
    private Marker marker;
    private FeiraModel[] feiras;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setUpMapIfNeeded();
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        mMap.setOnMapLongClickListener(myMapLongClickListener);
        mMap.setOnInfoWindowClickListener(myInfoWindowClickListener);
        //mMap.setInfoWindowAdapter(myInfoWindowAdapter);
        actionsCheckBox();
    }

    private void actionsCheckBox() {
        final CheckBox checkManha = (CheckBox)findViewById(R.id.checkManha);
        final CheckBox checkTarde = (CheckBox)findViewById(R.id.checkTarde);
        final CheckBox checkNoite = (CheckBox)findViewById(R.id.checkNoite);
        final CheckBox checkMadrugada = (CheckBox)findViewById(R.id.checkMadrugada);

        checkManha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    carregarListaDeFeiras(PeriodEnum.Manha);
                } else {
                    mMap.clear();
                    if (checkTarde.isChecked()) carregarListaDeFeiras(PeriodEnum.Tarde);
                    if (checkNoite.isChecked()) carregarListaDeFeiras(PeriodEnum.Noite);
                    if (checkMadrugada.isChecked()) carregarListaDeFeiras(PeriodEnum.Madrugada);
                }
            }
        });

        checkTarde.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    carregarListaDeFeiras(PeriodEnum.Tarde);
                } else {
                    mMap.clear();
                    if (checkManha.isChecked()) carregarListaDeFeiras(PeriodEnum.Manha);
                    if (checkNoite.isChecked()) carregarListaDeFeiras(PeriodEnum.Noite);
                    if (checkMadrugada.isChecked())  carregarListaDeFeiras(PeriodEnum.Madrugada);
                }
            }
        });


        checkNoite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    carregarListaDeFeiras(PeriodEnum.Noite);
                } else {
                    mMap.clear();
                    if (checkManha.isChecked()) carregarListaDeFeiras(PeriodEnum.Manha);
                    if (checkTarde.isChecked()) carregarListaDeFeiras(PeriodEnum.Tarde);
                    if (checkMadrugada.isChecked()) carregarListaDeFeiras(PeriodEnum.Madrugada);
                }
            }
        });


        checkMadrugada.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    carregarListaDeFeiras(PeriodEnum.Madrugada);
                } else {
                    mMap.clear();
                    if (checkTarde.isChecked()) carregarListaDeFeiras(PeriodEnum.Tarde);
                    if (checkNoite.isChecked()) carregarListaDeFeiras(PeriodEnum.Noite);
                    if (checkManha.isChecked()) carregarListaDeFeiras(PeriodEnum.Manha);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap != null) {
            return;
        }
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);

        if (mMap == null) {
            return;
        }
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-23.4209995, -51.9330558), 10));
        carregarListaDeFeiras();
    }

    private void carregarListaDeFeiras() {
        if (marker != null) { // remove marcador de inserção
            marker.remove();
        }

        AsyncTask<String, Void, FeiraModel[]> task = new RestFeiraTask(this).execute("https://feiralivre.herokuapp.com/api/feira");
        try {
            feiras = task.get();
            for (FeiraModel feira : feiras) {
                MarkerOptions mkO = new MarkerOptions()
                        .position(new LatLng(feira.getLatitude(), feira.getLongitude()))
                                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marco_veio))
                        .icon(BitmapDescriptorFactory.defaultMarker(getColor(feira.getPeriod())))
                        .snippet(feira.getInformation())
                        .title(feira.getDescription());

                mMap.addMarker(mkO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void carregarPatrocinadores() {
        AsyncTask<String, Void, FeiraModel[]> task = new RestFeiraTask(this).execute("https://feiralivre.herokuapp.com/api/patrocinadores");
        try {
            feiras = task.get();
            for (FeiraModel patrocinio : feiras) {
                MarkerOptions mkO = new MarkerOptions()
                        .position(new LatLng(patrocinio.getLatitude(), patrocinio.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marco_veio))
                        .snippet(patrocinio.getInformation())
                        .title(patrocinio.getDescription());

                mMap.addMarker(mkO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void carregarListaDeFeiras(PeriodEnum period) {
        for (FeiraModel feira : feiras) {
            if (feira.getPeriod().equals(period)) {
                MarkerOptions mkO = new MarkerOptions()
                        .position(new LatLng(feira.getLatitude(), feira.getLongitude()))
                                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marco_veio))
                        //.icon(BitmapDescriptorFactory.defaultMarker(getColor(feira.getPeriod())))
                        .icon(BitmapDescriptorFactory.defaultMarker(getColor(feira.getPeriod())))
                        .snippet(feira.getInformation())
                        .title(feira.getDescription());

                mMap.addMarker(mkO);
            }
        }
    }

    private float getColor(PeriodEnum period) {
        switch (period) {
            case Madrugada:
                return BitmapDescriptorFactory.HUE_ORANGE;
            case Manha:
                return BitmapDescriptorFactory.HUE_BLUE;
            case Tarde:
                return BitmapDescriptorFactory.HUE_GREEN;
            case Noite:
                return BitmapDescriptorFactory.HUE_ROSE;
        }
        return BitmapDescriptorFactory.HUE_BLUE;
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            //mMap.addMarker(new MarkerOptions().position(loc).title("Teste" + counter++));
            if(mMap != null){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 14.0f));
                mMap.setOnMyLocationChangeListener(null);
            }
        }
    };

    private GoogleMap.OnMapLongClickListener myMapLongClickListener = new GoogleMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(LatLng latLng) {
            if (marker != null) {
                marker.remove();
            }
            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.adicionar_descricao))
                    .draggable(true)
                    .visible(true));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));

            marker.showInfoWindow();
        }
    };

    private GoogleMap.OnInfoWindowClickListener myInfoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            if (marker.getTitle().equals(getString(R.string.adicionar_descricao))) {
                Intent intent = new Intent(getApplicationContext(),
                        RegisterActivity.class);

                FeiraModel feiraObj = new FeiraModel();
                feiraObj.setLatitude(marker.getPosition().latitude);
                feiraObj.setLongitude(marker.getPosition().longitude);
                feiraObj.setConfirmed(false);

                intent.putExtra("feira", feiraObj);
                startActivityForResult(intent, 100);
            }
        }
    };

    private GoogleMap.InfoWindowAdapter myInfoWindowAdapter = new GoogleMap.InfoWindowAdapter() {
        // Use default InfoWindow frame
        @Override
        public View getInfoWindow(Marker marker) {
            View v = getLayoutInflater().inflate(R.layout.maps_info, null);

            TextView note = (TextView) v.findViewById(R.id.note);
            //note.setTextColor(Color.parseColor("#4C0000"));
            note.setText(marker.getTitle());

            TextView info = (TextView) v.findViewById(R.id.info);
            //info.setTextColor(Color.parseColor("#00007F"));
            info.setText(marker.getSnippet());

            // Returning the view containing InfoWindow contents
            return v;
        }

        // Defines the contents of the InfoWindow
        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        carregarListaDeFeiras();
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.pgtaSair)
                    .setCancelable(false)
                    .setPositiveButton("Sim",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    MainActivity.super.onBackPressed();
                                }
                            })
                    .setNegativeButton("Não",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            super.onBackPressed();
        }
    }
}
