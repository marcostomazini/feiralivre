package com.arquitetaweb.feira;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import com.arquitetaweb.feira.dto.FeiraModel;
import com.arquitetaweb.feira.enummodel.PeriodEnum;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;

public class MainActivity extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, AsyncTaskListener {
    private GoogleMap mMap;
    private LocationClient mLocationClient;
    private Marker marker;
    private FeiraModel[] feiras;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mLocationClient = new LocationClient(this, this, this);

        setUpMapIfNeeded();
        //mMap.setOnMyLocationChangeListener(myLocationChangeListener);
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
                    if (checkMadrugada.isChecked()) carregarListaDeFeiras(PeriodEnum.Madrugada);
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
    }

    private void carregarListaDeFeiras() {
        if (marker != null) { // remove marcador de inserção
            marker.remove();
        }
        AsyncTask<String, Void, FeiraModel[]> task = new RestFeiraTask(this).execute("https://feiralivre.herokuapp.com/api/feira");
    }

    private void carregarPatrocinadores() {
        AsyncTask<String, Void, FeiraModel[]> task = new RestFeiraTask(this).execute("https://feiralivre.herokuapp.com/api/patrocinadores");
        try {
            /*feiras = task.get();
            for (FeiraModel patrocinio : feiras) {
                MarkerOptions mkO = new MarkerOptions()
                        .position(new LatLng(patrocinio.getLatitude(), patrocinio.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marco_veio))
                        .snippet(patrocinio.getInformation())
                        .title(patrocinio.getDescription());

                mMap.addMarker(mkO);
            }*/
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
                carregarListaDeFeiras();
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

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode =  GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
                    this,
                    9000);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
            }
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        if(isGooglePlayServicesAvailable()){
            mLocationClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Display the connection status
        //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        Location location = mLocationClient.getLastLocation();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14.0f);
        mMap.animateCamera(cameraUpdate);
        carregarListaDeFeiras();
        mLocationClient.disconnect();
    }

    @Override
    public void onDisconnected() {
    // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        9000);
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTaskComplete(FeiraModel[] result) {
        feiras = result;
        for (FeiraModel feira : feiras) {
            MarkerOptions mkO = new MarkerOptions()
                    .position(new LatLng(feira.getLatitude(), feira.getLongitude()))
                    .icon(BitmapDescriptorFactory.defaultMarker(getColor(feira.getPeriod())))
                    .snippet(feira.getInformation())
                    .title(feira.getDescription());

            mMap.addMarker(mkO);
        }
    }
}
