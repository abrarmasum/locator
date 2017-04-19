package com.example.mohammadfaisal.locator;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {


    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    Marker marker;
    LocationRequest mLocationRequest;
    private String name_of_user = "unknown";
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Toast.makeText(this,"happenning ... " , Toast.LENGTH_LONG);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //this is the funcion for accessing current location
    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //code for accesing and updating current location
        buildGoogleApiClient();
        //if i want to add the current location button
        addCurrentLocationButton();
    }

    private void addCurrentLocationButton() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }


    public void name_provided(View view) throws IOException {
        Toast.makeText(this,"happenning ... " , Toast.LENGTH_LONG);

        EditText et = (EditText) findViewById(R.id.search_map);
        name_of_user = et.getText().toString();

        System.out.println(name_of_user);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LatLng destination = new LatLng( location.getLatitude(),location.getLongitude() );


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(name_of_user).setValue(destination);

        setMarker( name_of_user+" is here in " ,destination);

    }




    public void geoLocate(View view) throws IOException {
        EditText et = (EditText) findViewById(R.id.search_map);
        String location = et.getText().toString();

        Geocoder gc = new Geocoder(this);

        List<Address> list = gc.getFromLocationName(location, 1);
        Address address = list.get(0);
        String locality = address.getLocality();
        Toast.makeText(this, locality, Toast.LENGTH_LONG).show();
        double lat = address.getLatitude();
        double lng = address.getLongitude();
        LatLng destination = new LatLng(lat, lng);
        goToLocationZoom(lat, lng, 17);
        setMarker(locality, destination);
    }

    private void setMarker(String locality, LatLng destination) {
        if(marker!=null)
        {
            marker.remove();
        }
        marker = mMap.addMarker(new MarkerOptions()
                .position(destination)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                .snippet(locality)
                .title(locality));
    }

    private void goToLocationZoom(double lat, double lng, float zoom) {

        LatLng ll = new LatLng(lat, lng);
       //CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.moveCamera(update);
    }



    public String bal = "1" ;

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //mLocationRequest.setInterval(10000);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LatLng destination = new LatLng( location.getLatitude(),location.getLongitude() );
        //setMarker(name_of_user , destination);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(name_of_user).setValue(destination);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"happenning ... " , Toast.LENGTH_LONG);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location ==null){
            Toast.makeText(this,"cant get current location" , Toast.LENGTH_LONG);
        }
        else{
            float zoom = 17;
            LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
            CameraUpdate update =  CameraUpdateFactory.newLatLngZoom(ll, zoom);

            mMap.animateCamera(update);

            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child(name_of_user).setValue(ll);

            bal = bal +1;
            marker = mMap.addMarker(new MarkerOptions()
                    .position(ll)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    .snippet(bal)
                    .title("new location"));
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Error", Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_type_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map_type_normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.map_type_sattelite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.map_type_terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.map_type_hybreed:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
