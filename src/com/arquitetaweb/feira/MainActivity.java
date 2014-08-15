package com.arquitetaweb.feira;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.*;
import android.widget.*;
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
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class MainActivity extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, AsyncTaskListener {
    private GoogleMap mMap;
    private LocationClient mLocationClient;
    private Marker marker;
    private FeiraModel[] feiras;
    private LatLng locationSelected;
    private Polyline polyline;
    private List<Marker> patrocinios;
    private Map<Marker, PeriodEnum> feirasPontos;
    private ShareActionProvider mShareActionProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mLocationClient = new LocationClient(this, this, this);
        patrocinios = new ArrayList<Marker>();
        feirasPontos = new HashMap<Marker, PeriodEnum>();

        setUpMapIfNeeded();
        //mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        mMap.setOnMapLongClickListener(myMapLongClickListener);
        mMap.setOnInfoWindowClickListener(myInfoWindowClickListener);
        mMap.setOnMarkerClickListener(myMarkerClickListener);
        mMap.setOnCameraChangeListener(myCameraChangeListener);
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
                mostrarListaDeFeiras(PeriodEnum.MANHA, isChecked);
            }
        });

        checkTarde.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mostrarListaDeFeiras(PeriodEnum.TARDE, isChecked);
            }
        });

        checkNoite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mostrarListaDeFeiras(PeriodEnum.NOITE, isChecked);
            }
        });

        checkMadrugada.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mostrarListaDeFeiras(PeriodEnum.MADRUGADA, isChecked);
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
        new RestFeiraTask(this).execute("https://feiralivre.herokuapp.com/api/feira");
    }

    private void carregarPatrocinadores() {
        new RestFeiraTask(this).execute("https://feiralivre.herokuapp.com/api/patrocinadores");
    }

    private void mostrarListaDeFeiras(PeriodEnum period, Boolean isChecked) {
        Set set = feirasPontos.entrySet();
        Iterator i = set.iterator();
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            Marker mk = (Marker) me.getKey();
            if (me.getValue().equals(period)) {
                mk.setVisible(isChecked ? true : false);
            }
        }
    }

    private float getColor(PeriodEnum period) {
        switch (period) {
            case MADRUGADA:
                return BitmapDescriptorFactory.HUE_ORANGE;
            case MANHA:
                return BitmapDescriptorFactory.HUE_BLUE;
            case TARDE:
                return BitmapDescriptorFactory.HUE_GREEN;
            case NOITE:
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

    private GoogleMap.OnCameraChangeListener myCameraChangeListener = new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            if (cameraPosition.zoom > 15.0f) {
                for (Marker mk: patrocinios) {
                    mk.setVisible(true);
                }
            } else {
                for (Marker mk: patrocinios) {
                    mk.setVisible(false);
                }
            }
        }
    };

    private GoogleMap.OnMarkerClickListener myMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            locationSelected = marker.getPosition();
            return false;
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
                feiraObj.setAds(false);

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
        carregarPatrocinadores();
        //mLocationClient.disconnect();
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

            if (feira.getAds().equals(false)) {
                MarkerOptions mkO = new MarkerOptions()
                        .position(new LatLng(feira.getLatitude(), feira.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(getColor(feira.getPeriod())))
                        .snippet(feira.getInformation())
                        .title(feira.getDescription());

                feirasPontos.put(mMap.addMarker(mkO), feira.getPeriod());
            } else {

                /*Bitmap bmp = null;
                try {
                    URL url = new URL("http://marcostomazini.me/images/myphoto.jpg");
                    try {
                        bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }*/

                MarkerOptions mkO = new MarkerOptions()
                        .position(new LatLng(feira.getLatitude(), feira.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                        //.icon(BitmapDescriptorFactory.fromBitmap(bmp))
                        .snippet(feira.getInformation())
                        .title(feira.getDescription());

                patrocinios.add(mMap.addMarker(mkO));

                for (Marker patrocinio : patrocinios) {
                    patrocinio.showInfoWindow();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.route:
                Location location = mLocationClient.getLastLocation();

                LatLng curre = new LatLng(location.getLatitude(), location.getLongitude());
                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(curre, locationSelected);
                DownloadTask downloadTask = new DownloadTask();
                // Start downloading json data from Google Directions API
                downloadTask.execute(url);

                return true;
            case R.id.routeapi:
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + locationSelected.latitude + "," + locationSelected.longitude));
                startActivity(intent);
                //for (Marker mk: feirasPontos) {
                //mk.setVisible(false);
                //}
                return true;
            case R.id.help:
                initShareIntent("face");
                //initShareIntent("twi");
                /*initShareIntent("twi");
                if facebook:

            initShareIntent("face");
                if mail:

            initShareIntent("mail"); // or "gmail"
                */
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(4);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            if (polyline != null) {
                polyline.remove();
            }
            polyline = mMap.addPolyline(lineOptions);
        }
    }

    private void initShareIntent(String type) {
        boolean found = false;
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("image/jpeg");

        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(share, 0);
        if (!resInfo.isEmpty()){
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains(type) ||
                        info.activityInfo.name.toLowerCase().contains(type) ) {
                    share.putExtra(Intent.EXTRA_SUBJECT,  "subject");
                    share.putExtra(Intent.EXTRA_TEXT,     "your text");
                    //share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(myPath)) ); // Optional, just if you wanna share an image.
                    share.setPackage(info.activityInfo.packageName);
                    found = true;
                    break;
                }
            }
            if (!found)
                return;

            startActivity(Intent.createChooser(share, "Select"));
        }
    }
}
