package com.example.lenovopc.mapnew;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import android.location.LocationListener;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    GoogleMap mMap;

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final double BAHRAIN_LAT = 26.218511, BAHRAIN_LNG = 50.518343;
    private LocationListener mListener;
    ViewPager viewPager;
    customswipe adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        if (servicesOK()) {


            setContentView(R.layout.activity_map);
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }


            if (initMap()) {

                //Toast.makeText(this, "Ready to map", Toast.LENGTH_SHORT).show();
                gotoLocation(BAHRAIN_LAT, BAHRAIN_LNG, 15);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
               mListener = new LocationListener() {
                   @Override
                   public void onLocationChanged(Location location) {
                       gotoLocation(location.getLatitude(),location.getLongitude(),15);

                   }

                   @Override
                   public void onStatusChanged(String provider, int status, Bundle extras) {

                   }

                   @Override
                   public void onProviderEnabled(String provider) {

                   }

                   @Override
                   public void onProviderDisabled(String provider) {

                   }
               };
               LocationRequest request=LocationRequest.create();
               request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
               request.setInterval(5000);
               request.setFastestInterval(1000);







           }else {
              Toast.makeText(this,"Map not connected",Toast.LENGTH_SHORT).show();
          }


        } else {
            setContentView(R.layout.activity_main);
       }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean servicesOK(){
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(isAvailable == ConnectionResult.SUCCESS){
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)){
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable,this,ERROR_DIALOG_REQUEST);
            dialog.show();
        } else
        {
            Toast.makeText(this,"cant connect to mapping service",Toast.LENGTH_SHORT).show();
        }
        return false;

    }
    private boolean initMap(){
        if(mMap==null){
            SupportMapFragment mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mMap=mapFragment.getMap();
            if(mMap!=null){
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        View v=getLayoutInflater().inflate(R.layout.info_window,null);
                        return v;
                    }

                });
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        setContentView(R.layout.flatrent1);
                        viewPager = (ViewPager) findViewById(R.id.view_pager);
                        adapter = new customswipe(getApplicationContext());
                        viewPager.setAdapter(adapter);
                    }
                });
            }
        }
        return (mMap!=null);
    }

    private void gotoLocation (double Lat,double Lng,float zoom){
        LatLng latlng = new LatLng(Lat,Lng);
        CameraUpdate update= CameraUpdateFactory.newLatLngZoom(latlng, zoom);
        mMap.moveCamera(update);
    }

    private void hideSoftKeyboard(View v){
        InputMethodManager imm= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromInputMethod(v.getWindowToken(), 0);
    }
    public void geoLocate(View v) throws IOException {
        hideSoftKeyboard(v);
        TextView tv =(TextView)findViewById(R.id.editText);

        String searchString = tv.getText().toString();
        Toast.makeText(this,"Searching for "+searchString,Toast.LENGTH_SHORT).show();
        Geocoder gc=new Geocoder(this);
        List<Address> list=gc.getFromLocationName(searchString, 1);
        if(list.size()>0){
           Address add=list.get(0);
            String locality=add.getLocality();
           // Toast.makeText(this,"Found Locality "+locality,Toast.LENGTH_LONG).show();
            double lat=add.getLatitude();
            double lng=add.getLongitude();
            gotoLocation(lat, lng, 15);
            addMarker(add,lat,lng);

        }
    }

    private void addMarker(Address add,double lat,double lng){

        MarkerOptions options=new MarkerOptions().title(add.getLocality())
                .position(new LatLng(lat, lng))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_toggle_radio_button_on));
        mMap.addMarker(options);
    }
}
