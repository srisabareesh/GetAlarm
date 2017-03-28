package com.example.sabarish.googlemaps;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Routes extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {
    public GoogleMap mMap;


    Button button, button2, button3;
    EditText from, to;
    String fromAddress, toAddress;
    ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();
    MarkerOptions options = new MarkerOptions();
    RadioGroup group;
    Context c;
    RadioButton drive, walk, cycle;
    int mMode, node;
    List<Address> address1;
    List<Address> address2;
    Marker mark=null;
  LatLng destination;
    String url;
    GoogleApiClient googleApiClient;
    LocationRequest request;
    Location mlocation;
    ProgressDialog progress;
    Location ll2;
    double a, b;
  //  Locations loc=new Locations();

    //16.9929,81.66667
    //17.0005,81.80403
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        button = (Button) findViewById(R.id.button1);
       // from = (EditText) findViewById(R.id.origin);
        to = (EditText) findViewById(R.id.destination);
        button2 = (Button) findViewById(R.id.alter);
        button3 = (Button) findViewById(R.id.start);
        //button4=(Button)findViewById(R.id.place);
        group = (RadioGroup) findViewById(R.id.group);
        drive = (RadioButton) findViewById(R.id.driving);
        walk = (RadioButton) findViewById(R.id.walking);
        cycle = (RadioButton) findViewById(R.id.bicycling);
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        //button4.setOnClickListener(this);
        //group.setOnCheckedChangeListener(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        button3.setVisibility(View.INVISIBLE);
        setup();


    }
    public void setup(){
        googleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .build();
        request=new LocationRequest();
        request.setInterval(5*1000);
        request.setSmallestDisplacement(1);
        request.setFastestInterval(1000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        // mMap.setOnMapClickListener(this);
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.d("message", "onStart fired ..............");
        googleApiClient.connect();
    }


    public void hide() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
        }
    }

   /* public Address from(String fromAddress) {
        Geocoder geo = new Geocoder(this);
        if (fromAddress != null) {
            try {
                address1 = geo.getFromLocationName(fromAddress, 1);
                //add1=address1.get(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return address1.get(0);
        }
        return null;
    }*/

    public Address to(String toAddress) {
        Geocoder geo = new Geocoder(this);
        if (toAddress != null) {
            try {
                address2 = geo.getFromLocationName(toAddress, 1);
                Address add2 = address2.get(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return address2.get(0);
        }
        return null;
    }


    @Override
    public void onClick(View view) {
        if (view == button) {
            if (options != null) {
                mMap.clear();
            }
            node = 1;
           // fromAddress = from.getText().toString();
            toAddress = to.getText().toString();
       //     Address add1 = from(fromAddress);
            Address add2 = to(toAddress);
            LatLng origin = new LatLng(ll2.getLatitude(), ll2.getLongitude());
            Toast.makeText(getApplicationContext(), "" + origin.latitude + "  " + origin.longitude, Toast.LENGTH_SHORT).show();
            options = new MarkerOptions().position(origin).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 10));
            destination = new LatLng(add2.getLatitude(), add2.getLongitude());
            Toast.makeText(getApplicationContext(), "" + destination.latitude + "" + destination.longitude, Toast.LENGTH_SHORT).show();
            options = new MarkerOptions().position(destination).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mMap.addMarker(options);

            Toast.makeText(getApplicationContext(), "button is clicked", Toast.LENGTH_SHORT).show();
            hide();

          /*  LatLng or = new LatLng(16.9929, 81.66667);
            LatLng des = new LatLng(17.0005, 81.80403);*/
            options = new MarkerOptions().position(origin).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(options);
            options = new MarkerOptions().position(destination).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mMap.addMarker(options);

            String url = getDirectionUrl(origin, destination);
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
        }
        if (view == button2) {
            if (url == null) {
                button2.setVisibility(View.INVISIBLE);
            }
            url = url + "&" + "alternatives=true";
            DownloadTask downloadTask1 = new DownloadTask();
            downloadTask1.execute(url);
        }
        if (view == button3) {
            updateUI();

        }

    }
    @Override
    public void onStop() {
        super.onStop();
        Log.d("message", "onStop fired ..............");
        googleApiClient.disconnect();
        Log.d("message", "isConnected ...............: " + googleApiClient.isConnected());
    }



    private void animateMarker(final Marker marker, final LatLng toPosition,
                               final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1000;

        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }


    private String getDirectionUrl(LatLng origin, LatLng destination) {
        // String str_origin=origin;
        //String str_destination=destination;
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_destination = "destination=" + destination.latitude + "," + destination.longitude;
        String sensor = "sensor=true";
        String mode = "mode=driving";
        String units = "units=metric";
        int check = group.getCheckedRadioButtonId();
        if (check == R.id.driving) {
            mode = "mode=driving";
            mMode = 1;
        } else if (check == R.id.bicycling) {
            mode = "mode=bicycling";
            mMode = 2;
        } else {
            mode = "mode=walking";
            mMode = 3;
        }
        String parameters = str_origin + "&" + str_destination + "&" + sensor + "&" + units;
        String output = "json";
        if (node == 1) {
            url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&" + mode;
        }
        return url;
    }

    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... url) {
            String data="";

            try {
                data=downloadUrl(url[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
             progress.dismiss();
            button3.setVisibility(View.VISIBLE);
            ParserTask parserTask=new ParserTask();
            parserTask.execute(result);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
         progress=new ProgressDialog(Routes.this);
                progress.setMessage("downloading....");
                progress.show();

        }
        public String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream input = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(strUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                input = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(input));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                br.close();
            } catch (Exception e) {
            } finally {
                input.close();
                connection.disconnect();
            }
            return data;
        }

    }
    public class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>> > {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            String distance = "";
            String time = "";
            PolylineOptions lineOptions = new PolylineOptions();
            MarkerOptions markerOptions = new MarkerOptions();
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                // lineOptions=new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    if (j == 0) {
                        distance = (String) point.get("distance");
                        continue;
                    }
                    if (j == 1) {
                        time = (String) point.get("duration");
                        continue;
                    }
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                lineOptions.addAll(points)
                        .width(5);
                if (mMode == 1) {
                    lineOptions.color(Color.RED);
                }
                if (mMode == 2) {
                    lineOptions.color(Color.BLUE);
                }
                if (mMode == 3) {
                    lineOptions.color(Color.GREEN);
                }
            }
            Toast.makeText(getApplicationContext(), "distance is" + distance + " time is" + time, Toast.LENGTH_SHORT).show();
            mMap.addPolyline(lineOptions);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("message", "onConnected - isConnected ...............: " + googleApiClient.isConnected());
        Toast.makeText(this, "onconnected", Toast.LENGTH_SHORT).show();
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
         ll2=LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, request, this);
        Log.d("message", "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("message", "Firing onLocationChanged..............................................");
        Toast.makeText(this, "onLocationchanged", Toast.LENGTH_SHORT).show();
        mlocation = location;
        LatLng lg=new LatLng(mlocation.getLatitude(),mlocation.getLongitude());
        if(lg==destination){
            AlarmManager alarm=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent in=new Intent(this,MyBroadcast.class);
            PendingIntent pend=PendingIntent.getBroadcast(this,0,in,0);
            alarm.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+1000,pend);
            Toast.makeText(Routes.this, "trigger alarm...", Toast.LENGTH_SHORT).show();
        }
        String mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    private void updateUI() {
        Log.d("message", "UI update initiated .............");
        if (null != mlocation) {
            a=mlocation.getLatitude();
            b=mlocation.getLongitude();
            String lat = String.valueOf(a);
            String lng = String.valueOf(b);
            Log.d("locations","lattitude"+lat+" longitude"+lng);
           /* tvLocation.setText("At Time: " + mLastUpdateTime + "\n" +
                    "Latitude: " + lat + "\n" +
                    "Longitude: " + lng + "\n" +
                    "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
                    "Provider: " + mCurrentLocation.getProvider());*/
        }
        else {
            Log.d("message", "location is null ...............");
        }
        LatLng ll=new LatLng(a,b);
        if(mark==null){
            mark=mMap.addMarker(new MarkerOptions().position(ll).title("your currrent location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll,15));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
        animateMarker(mark,ll,false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        googleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(googleApiClient.isConnected()){
            googleApiClient.disconnect();
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        }
    }


    //AIzaSyBAMGR9E4lIkCI9IAzFGZ6TVLM1Bl1Qtcc

}
