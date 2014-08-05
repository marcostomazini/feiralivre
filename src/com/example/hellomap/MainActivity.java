package com.example.hellomap;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.arquitetaweb.feira.dto.FeiraModel;
import com.com.arquitetaweb.feira.enummodel.PeriodEnum;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;

import javax.xml.parsers.FactoryConfigurationError;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends FragmentActivity {
    private GoogleMap mMap;
    private Marker marker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setUpMapIfNeeded();
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        mMap.setOnMapLongClickListener(myMapLongClickListener);
        mMap.setOnInfoWindowClickListener(myInfoWindowClickListener);
        mMap.setInfoWindowAdapter(myInfoWindowAdapter);
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
        AsyncTask<String, Void, FeiraModel[]> task = new RestFeiraTask().execute("https://feiralivre.herokuapp.com/api/feira");
        try {
            FeiraModel[] feiras = task.get();
            for (FeiraModel feira : feiras) {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(feira.getLatitude(), feira.getLongitude()))
                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marco_veio))
                        .icon(BitmapDescriptorFactory.defaultMarker(getColor(feira.getPeriod())))
                        .snippet(feira.getInformation())
                        .title(feira.getDescription()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private float getColor(PeriodEnum period) {
        switch (period) {
            case Madrugada:
                return BitmapDescriptorFactory.HUE_AZURE;
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

}
