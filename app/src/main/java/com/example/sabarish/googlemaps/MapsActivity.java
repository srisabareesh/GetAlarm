package com.example.sabarish.googlemaps;


import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener,NavigationView.OnNavigationItemSelectedListener{
Toolbar tool;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    private GoogleMap mMap;
    private GoogleApiClient apiClient;
    private LocationRequest request=null;
   private FusedLocationProviderApi providerApi = LocationServices.FusedLocationApi;
    private Location mlocation=null;
    double a=0,b=0;
    Marker mark = null;
    SupportMapFragment mapFragment;
    FloatingActionButton zoomout, zoomin;
    List<Address> address = new ArrayList<>();
    private Double lat = 0.0, lng = 0.0;
    MarkerOptions marker=null;
    TextView lattitude,longitude;
    NavigationView navigate;
    private final int CONNECTION_FAILURE_RESOLUTION_REQUEST=9000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        tool = (Toolbar) findViewById(R.id.toolbar);
        drawer=(DrawerLayout)findViewById(R.id.drawer);
        navigate=(NavigationView)findViewById(R.id.navigate);
        toggle=new ActionBarDrawerToggle(this,drawer,tool,R.string.open,R.string.close);
        drawer.setDrawerListener(toggle);
        setSupportActionBar(tool);
        setTitle("Route");
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        navigate.setNavigationItemSelectedListener(this);
       apiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this).build();
        request=LocationRequest.create();
       //requests.setFastestInterval(5*1000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setSmallestDisplacement(2);
        request.setInterval(1000);


        zoomout = (FloatingActionButton) findViewById(R.id.zoomout);
        zoomin = (FloatingActionButton) findViewById(R.id.zoomin);
        zoomout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                      /*  googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(37.4233438, -122.0728817))
                                .title("LinkedIn")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(37.4629101,-122.2449094))
                                .title("Facebook")
                                .snippet("Facebook HQ: Menlo Park"));

                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(37.3092293, -122.1136845))
                                .title("Apple"));*/
                        googleMap.animateCamera(CameraUpdateFactory.zoomIn());


                                            }
                });
            }
        });
        zoomin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
        toggle.syncState();

    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        MenuItem Searchitem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(Searchitem);
//       String data=(CharSequence) searchView.getQuery();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("message", query);
                locate(query);
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return true;
    }

    public void locate(String query) {
        Log.d("message", query);
        if (query != "" || query != null) {
            Geocoder geo = new Geocoder(this);
            try {
                address = geo.getFromLocationName(query, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Address addr = address.get(0);
                LatLng latlng = new LatLng(addr.getLatitude(), addr.getLongitude());
                mark = mMap.addMarker(new MarkerOptions().position(latlng).title("your required location"));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));

            } catch (Exception e) {
            }
        }

      }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
       // mMap.setMyLocationEnabled(true);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(true);

        // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng), 10));


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if(connectionResult.hasResolution()){
            try{
                connectionResult.startResolutionForResult(this,CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
        else{
            Log.d("message","connection problem is "+connectionResult.getErrorMessage()+" code is"+connectionResult.getErrorCode());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("message","onConnected");
        if(mlocation==null) {
            mlocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
            handleLocation();
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        else {
            try {
                PendingResult<Status> pending=  LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, request, this);
                Toast.makeText(MapsActivity.this, "onconnected method", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
            }
        }
        /*if(mlocation==null) {
            Log.d("message", "null values");
            LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, request, this);
            handleLocation(mlocation);
        }
        if(mlocation!=null){
            Log.d("message",String.valueOf(mlocation.getLatitude()));
            Log.d("message",String.valueOf(mlocation.getLongitude()));
            handleLocation(mlocation);

        }*/
    }


    @Override
    public void onConnectionSuspended(int i) {
        apiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mlocation=location;
        handleLocation();
        Toast.makeText(MapsActivity.this, "location changed", Toast.LENGTH_SHORT).show();
    }
public void handleLocation() {
    if (mlocation != null) {
        a = mlocation.getLatitude();
        b = mlocation.getLongitude();
       // lattitude.setText(String.valueOf(a));
        //longitude.setText(String.valueOf(b));
    } else {
        Toast.makeText(MapsActivity.this, "values are null", Toast.LENGTH_SHORT).show();
    }
    LatLng latlng = new LatLng(a, b);
    if (mark == null) {
        mark = mMap.addMarker(new MarkerOptions().position(latlng).title("your location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,15));

    }
    mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
    animateMarker(mark, latlng, false);

    //animateMarker(mark,ll,false);
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

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        apiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(apiClient.isConnected()){
            apiClient.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.normal){
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        else if(id==R.id.satellite){
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        else if(id==R.id.hybrid){
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
        else if(id==R.id.territon){
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
        else if (id==R.id.route){
            Intent in=new Intent(this,Routes.class);
            startActivity(in);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
