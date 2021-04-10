package com.example.proj1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    protected LocationManager locationManager;
    private boolean loc_gps;
    private boolean loc_network;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

        Toast.makeText(this, "hello map", Toast.LENGTH_SHORT).show();
        // Add a marker in EC and move the camera
        LatLng ec = new LatLng(24.7869954, 120.997482);
        mMap.addMarker(new MarkerOptions().position(ec).title("初始位置"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ec,9));


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "require permission 0", Toast.LENGTH_SHORT).show();
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            enableMyLocation();
//            return;
        }
        try{
            Location gpslocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (gpslocation != null){
                loc_gps = true;
            }
        } catch(Exception e){
            Log.d("##", "gps location fail");
            loc_gps = false;
        }
        try{
            Location networklocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (networklocation != null){
                loc_network = true;
            }
        } catch(Exception e){
            Log.d("##", "gps location fail");
            loc_network = false;
        }



        Location location = null;
        if (loc_gps){
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else if (loc_network){
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }


        if (location == null) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "require permission", Toast.LENGTH_SHORT).show();
                Log.d("##","null location");
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location == null) {
                Toast.makeText(this, "null location!", Toast.LENGTH_SHORT).show();
                Log.d("##","null location");
            }
        }else{
            Log.d("##",location.toString());
            Toast.makeText(this, "show place", Toast.LENGTH_SHORT).show();
            MarkerOptions markerOpt = new MarkerOptions();
            final LatLng mylocation = new LatLng(location.getLatitude(), location.getLongitude());
            markerOpt.position(mylocation);
            markerOpt.title(" 現 在 位 置 ");
            markerOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)); mMap.addMarker(markerOpt).showInfoWindow();
            PolylineOptions polylineOpt = new PolylineOptions();
            polylineOpt.add(mylocation); polylineOpt.add(ec);
            polylineOpt.color(Color.BLUE);

            Polyline polyline = mMap.addPolyline(polylineOpt);
            polyline.setWidth(5);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation,9));

            LatLng markLocation = new LatLng(location.getLatitude(), location.getLongitude());
            // label per 10 secs
            new Timer().scheduleAtFixedRate(new TimerTask(){
                @Override
                public void run(){
                    Log.d("##", "A Kiss every 10 seconds");



                }
            },0,10000);
        }


    }
/*
    private Location getNowLocation(){
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (loc_gps){
                try{
                    return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                } catch{

                }

            }
        } else{
            return;
        }

    }*/

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }


    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

}
