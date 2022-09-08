package com.qihaocai.scrapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.datatransport.BuildConfig;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrackMaker extends AppCompatActivity implements Dialog.DialogListener {


    private static final String TAG = "TrackMaker";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    //widgets
    private EditText mSearchText;
    private ImageView goBackButton, removeLast, removeAll, scooterNext,dropcoords;


    //varibles
    private boolean LocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient client;

    ArrayList<Marker> markerList = new ArrayList<Marker>();

    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_maker);

        // for search text
        mSearchText = (EditText) findViewById(R.id.input_search);

        //for go back gps button
        goBackButton = (ImageView) findViewById(R.id.ic_gps);

        //for my remove last and remove all button
        removeLast = (ImageView) findViewById(R.id.ic_remove_last);
        removeAll = (ImageView) findViewById(R.id.ic_remove_all);
        scooterNext = (ImageView) findViewById(R.id.ic_scooter);
        dropcoords = (ImageView) findViewById(R.id.ic_BreadCrumbs);

        client = LocationServices.getFusedLocationProviderClient(this);

        //used for the menu

        //gets and check permissions and starts the map
        getLocationPermissions(); // calls start map
        init();


        //init(); // lets the search bar work
    }

    //--------------------------------------------------------
    //adds a marker when you click the map
    private void onMapClick(GoogleMap map) {
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addMarker(latLng);

                //Log.d("ON_MAP_CLICK", "1. OnMapClick called");
                //Toast.makeText(TrackMaker.this, "Lat: " + latLng.latitude + "\n Long: " + latLng.longitude, Toast.LENGTH_SHORT).show();
                //handleOnMapClick(point);
            }
        });

    }

    //--------------------------------------------------------

    //initializes our widgets
    private void init() {
        Log.d(TAG, "init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == keyEvent.ACTION_DOWN
                        || keyEvent.getAction() == keyEvent.KEYCODE_ENTER) {
                    //execute our search function
                    geoLocate();

                }

                return false;
            }
        });

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getDeviceLocation();
            }
        });

        removeLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                removeLastMarker(markerList);
            }
        });

        removeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                removeAllMarker(markerList, mMap);

            }
        });

        scooterNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoGetterForTrack();
            }
        });

       dropcoords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLocation();
            }
        });

        // this doesn't work.
        hideKeyboard();

    }

    private void addLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                addMarker(latlng);
                Log.d(TAG, "onSuccess: " + "lat: " + location.getLatitude() + " lng: " + location.getLongitude());
            }
        });

    }

    private void addMarker(LatLng point){
        MarkerOptions Marker = new MarkerOptions()
                .position(point)
                .title("Test Point");

        Marker newMarker = mMap.addMarker(Marker);
        markerList.add(newMarker);



//        int i;
//        for(i = 0; i < markerList.size(); i++){
//            Log.d(TAG, "markerList: " + i + " " + markerList.get(i).getTitle());
//            Toast.makeText(this, "markerList: " + i + ": " + markerList.get(i), Toast.LENGTH_SHORT).show();
//        }


        //mMap.addMarker(Marker);
    }


    private void removeLastMarker(ArrayList markerList){
        if(markerList.size() > 0){

            Marker marker = (Marker) markerList.get(markerList.size()-1);
            marker.remove();

            markerList.remove(markerList.size() - 1);
        }
        else{

        }

    }

    private void removeAllMarker(ArrayList markerList, GoogleMap map){

        map.clear();
        markerList.clear();


    }

    @Override
    public void applyTexts(String Filename) {
        isDirMade(markerList, Filename);
    }


    private void isDirMade(ArrayList List, String Filename){
        File file = new File("/data/user/0/com.qihaocai.scrapp/files/Tracks");
        if(file.isDirectory() != true){
            file.mkdirs();
            createTrack(List, Filename);
        }
        else {
            createTrack(List, Filename);
        }
    }

    private void infoGetterForTrack(){
        Dialog dialog = new Dialog();
        dialog.show(getSupportFragmentManager(), "Filename");
    }

    //writes file to phone
    private void createTrack(ArrayList List, String Filename){
        double lat;
        double lng;

        File directory = new File("/data/user/0/com.qihaocai.scrapp/files/Tracks");

        File dataFile = new File(directory, Filename);

        FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(dataFile, true);
                for(int i = 0; i < List.size(); i++) {
                    try {
                        Marker marker = (Marker) markerList.get(i);
                        lat = marker.getPosition().latitude;
                        lng = marker.getPosition().longitude;

                        String text = lat + " " + lng + "\n";

                        fos.write(text.getBytes());
                        fos.flush();
                        //------------------------------------------------------------
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(this, "Saved to " + getFilesDir() + "/" + Filename, Toast.LENGTH_LONG).show();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            finally{
                if(fos != null){
                    try {
                        fos.close();
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }

    }



    //--------------------------------------------------------
    private void geoLocate(){
        Log.d(TAG, "Geo Locate method");

        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(TrackMaker.this);
        List<Address> list = new ArrayList<>();

        try{
            list = geocoder.getFromLocationName(searchString, 5);
        }
        catch (IOException e) {
            Log.d(TAG, "GeoLocate: IOException" + e.getMessage());
        }

        if(list.size() > 0){
            Address address = list.get(0);
            moveCam(new LatLng(address.getLatitude(), address.getLongitude()), 15);
           // Log.d(TAG, "geoLocate: found a location: " + address.toString());
        }


    }

    //--------------------------------------------------------

    //--------------------------------------------------------

    private void moveCam(LatLng latlng, float zoom) {
        Log.d(TAG, "Moving the camera");
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latlng.latitude + ", lng: " + latlng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
        hideKeyboard();
    }

    //--------------------------------------------------------
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location ");
        client = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (LocationPermissionGranted) {
                Task Location = client.getLastLocation();
                Location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "onComplete: found location!");
                            Location CurrentLocation = (Location) task.getResult();

                            moveCam(new LatLng(CurrentLocation.getLatitude(), CurrentLocation.getLongitude()), 15);
                        } else {
                            Log.d(TAG, "onComplete: Current location is null");
                            Toast.makeText(TrackMaker.this, "YO BITCH WHERE THE FUCK ARE YOU", Toast.LENGTH_SHORT).show();
                            Toast.makeText(TrackMaker.this, "Maybe Turn on Gps?", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }
    //--------------------------------------------------------


    //starts and gets the map
    private void StartMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Toast.makeText(TrackMaker.this, "Map is Ready", Toast.LENGTH_SHORT).show();
                mMap = googleMap;

                if (LocationPermissionGranted) {
                    getDeviceLocation();

                    if (ActivityCompat.checkSelfPermission(TrackMaker.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(TrackMaker.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    init(); // lets the search bar work

                    //for the clicks on the map
                    onMapClick(mMap);


                    //mMap.setMyLocationEnabled(true);
//                    mMap.getUiSettings().setMyLocationButtonEnabled(true);


                }
            }
        });
    }

    //--------------------------------------------------------
    //gets location permissions
    private void getLocationPermissions(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        //  checks for if we obatined permissions
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                LocationPermissionGranted = true;
                StartMap();
            }
        }
        else{
            ActivityCompat.requestPermissions(this, permissions, 369);
        }
    }

    //--------------------------------------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LocationPermissionGranted = false;
        if (requestCode == 369) {
            if (grantResults.length > 0) {
                for(int i = 0; i < grantResults.length; i++){
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        LocationPermissionGranted = false;
                        return;
                    }
                }
                LocationPermissionGranted = true;
                StartMap();
                // since we got our permissions we are good to go on launching the map
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    //--------------------------------------------------------

    private void hideKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


}