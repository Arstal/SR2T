package com.qihaocai.scrapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class TougeRun extends AppCompatActivity {

    private static final String TAG = "TougeRun";

    private GoogleMap mMap;

    private FusedLocationProviderClient client;
    LocationRequest locationRequest;
    LocationCallback locationCallback;



    private boolean LocationPermissionGranted = false;


    PolylineOptions lineOptions = null;


    ArrayList<LatLng> List = new ArrayList<LatLng>();
    ArrayList<Marker> markerList = new ArrayList<Marker>();

    TextView latlngcoords, Speed, Timer;
    Button Start;


    int counter;

    //for timer
    Handler handler;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    int Seconds, Minutes, MilliSeconds ;
    boolean ready;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touge_run);
        ready = false;

        counter = 0;

        latlngcoords = (TextView) findViewById(R.id.latlngcoords);
        Timer = (TextView) findViewById(R.id.timer);
        Speed = (TextView) findViewById(R.id.Speed);
        Start = (Button) findViewById(R.id.startbutton);

        client = LocationServices.getFusedLocationProviderClient(TougeRun.this);

        List.clear();

        Intent i = getIntent();
        String name = i.getStringExtra("<StringName>");

        StartMap();

        makeList(name);
        Log.d(TAG, "onCreate: ----------------------------------------------------" + List.get(0).latitude + List.get(0).longitude);

        handler = new Handler();

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(100);
//        locationRequest.setFastestInterval(1000);
//        locationRequest.setMaxWaitTime(0);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();

//                Toast.makeText(getApplicationContext(),
//                        "Lat: "+Double.toString(location.getLatitude()) + '\n' +
//                                "Long: " + Double.toString(location.getLongitude()), Toast.LENGTH_LONG).show();

               Log.d(TAG, "onLocationResult: " + "lat: " + location.getLatitude() + " lng: " + location.getLongitude());
//
                Tracker(location);
                updateValue(location);


            }
        };

        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // gpsUpdates();
                //timer();
                Log.d(TAG, "onClick: Started");
                StartTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);
                Start.setVisibility(view.INVISIBLE);
                ready = true;
                //updatesAtLocation();
            }
        });

        updatesAtLocation();
    }

    private void Tracker(Location location){
//        Lat = 364,000 feet
//                Long = 288,200 feet

        double latdiff = 1/364000;
        double longdiff = 1/288200;

        String str1 = String.valueOf(location.getLatitude());
        String str2 = String.valueOf(location.getLongitude());
        String str3 = "lat: " + str1 + " lng " + str2;
        Log.d(TAG, "\npassed coords: " + str3);

        double radius = 2;
        double radiusInDegrees = radius / 111000f;

//        double latrange = List.get(counter).latitude + (2 * latdiff);
//        double longrange = List.get(counter).longitude + (2 * longdiff);

        if((counter) < List.size() && ready == true) {
            if (location.getLatitude() <= List.get(counter).latitude + radiusInDegrees && location.getLongitude() <= List.get(counter).longitude + radiusInDegrees) {
                Log.d(TAG, "\n Tracker: got " + counter);
                Toast.makeText(this, "Tracker: got " + counter, Toast.LENGTH_SHORT).show();

               // Marker marker = (Marker) markerList.get(counter);
                Log.d(TAG, "\nTracker: i am here wooooooooooo");
                if((counter) == List.size() && ready == true){
                    markerList.get(counter).remove();
                    markerList.remove(counter);
                    //Log.d(TAG, "\nTracker: i am here wooooooooooo");
                    counter++;
                }
                else{

                        Toast.makeText(this, "counter: " + counter, Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, "Tracker: finished ", Toast.LENGTH_SHORT).show();
                        EndofTrack();

                }

            }
        }
    }

    private void EndofTrack() {
        TimeBuff += MillisecondTime;
        handler.removeCallbacks(runnable);
        Log.d(TAG, "EndofTrack: " + TimeBuff);

        client.removeLocationUpdates(locationCallback);
        client.removeLocationUpdates(locationCallback);
        Intent Results = new Intent(TougeRun.this, Results.class);
        startActivity(Results);



    }

    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Minutes = Seconds / 60;

            Seconds = Seconds % 60;

            MilliSeconds = (int) (UpdateTime % 1000);

            Timer.setText("" + Minutes + ":"
                    + String.format("%02d", Seconds) + ":"
                    + String.format("%03d", MilliSeconds));

            handler.postDelayed(this, 0);
        }

    };

    private void updateValue(Location location){
        String str1 = String.valueOf(location.getLatitude());
        String str2 = String.valueOf(location.getLongitude());
        String str3 = "lat: " + str1 + " lng " + str2;
        latlngcoords.setText("lat: " + str1 + " lng " + str2);
        //Log.d(TAG, "\n onLocationResult: --------------------------------------" + str3);

        if (location.hasSpeed()) {
            Speed.setText("Speed " + String.valueOf(location.getSpeed()));
        }
        else{
            Speed.setText("Speed: 0.00");
        }


    }
////under construction--------------------------------------------------------------------------------------
    private void updatesAtLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        client.requestLocationUpdates(locationRequest, locationCallback, null/*Looper.getMainLooper()*/);
        //gpsUpdates();

    }
//
//    private void gpsUpdates () {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        //Log.d(TAG, "gpsUpdates: we got something at gps");
//        client = LocationServices.getFusedLocationProviderClient(TougeRun.this);
//        client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                updatesAtLocation();
////                Tracker(location);
////                updateValue(location);
//
//            }
//        });
//    }


//--------------------------------------------------------------------------------------------------------------------------------------
    private void drawPrimaryLinePath()
    {
//        if ( mMap == null )
//        {
//            return;
//        }

//        if ( listLocsToDraw.size() < 2 )
//        {
//            return;
//        }

        PolylineOptions options = new PolylineOptions();

        options.color( Color.parseColor( "#CC0000FF" ) );
        options.width( 15 );
        options.visible( true );

        for (int i = 0; i < List.size(); i++)
        {
            double lng = List.get(i).longitude;
            double lat = List.get(i).latitude;
            options.add( new LatLng( lat, lng));
        }

        mMap.addPolyline( options );

    }
// this is the probelms only reading one line --------------------------
    private void makeList(String name){
        //Get the text file
        File file = new File("/data/user/0/com.qihaocai.scrapp/files/Tracks/" + name);

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                Log.d(TAG, "makeList: line---------------------------------------------------" + line +"\n\n");

                    String[] parts = line.split(" ");

                    String lat = parts[0];
                    double lat2 = Double.parseDouble(lat);
                    String lng = parts[1];
                    double lng2 = Double.parseDouble(lng);

                    LatLng latlng = new LatLng(lat2, lng2);

                    List.add(latlng);

            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }






//        File OpenFile = new File("/data/user/0/com.qihaocai.scrapp/files/Tracks/" + name);
//
//        try {
//            FileInputStream fis = new FileInputStream(OpenFile);
//            DataInputStream in = new DataInputStream(fis);
//            BufferedReader br = new BufferedReader(new InputStreamReader(in));
//
//            String Line;
//
//            try {
//                while((Line = br.readLine()) != null){
//
//                    Log.d(TAG, "makeList: line---------------------------------------------------" + Line +"\n\n");
//
//                    String[] parts = Line.split(" ");
//
//                    String lat = parts[0];
//                    double lat2 = Double.parseDouble(lat);
//                    String lng = parts[1];
//                    double lng2 = Double.parseDouble(lng);
//
//                    LatLng latlng = new LatLng(lat2, lng2);
//
//                    List.add(latlng);
//
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            //not sure if need to close this
////            br.close();
////            in.close();
////            fis.close();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//

    }

    private void DrawMap(){

        for(int i = 0; i < List.size(); i++){
            MarkerOptions Marker = new MarkerOptions()
                    .position(List.get(i))
                    .title("Test Point");

            Marker newMarker = mMap.addMarker(Marker);
            markerList.add(newMarker);
            mMap.addMarker(Marker);

        }

    }


    private void StartMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Toast.makeText(TougeRun.this, "Are you ready?", Toast.LENGTH_SHORT).show();
                mMap = googleMap;

                moveCam(List.get(0),20);
                //Log.d(TAG, "startmap : lat: " + List.get(0).latitude + " " + "lng: " + List.get(0).longitude);
                mMap.setOnCameraChangeListener(getCameraChangeListener());

                DrawMap();
                drawPrimaryLinePath();

            }
        });
    }

    public GoogleMap.OnCameraChangeListener getCameraChangeListener()
    {
        return new GoogleMap.OnCameraChangeListener()
        {
            @Override
            public void onCameraChange(CameraPosition position)
            {
                int mCameraTilt = (position.zoom < 15) ? 0 : 60;
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder()
                                .target(position.target)
                                .tilt(mCameraTilt)
                                .zoom(position.zoom)
                                .build()));
            }
        };
    }

    private void moveCam(LatLng latlng, float zoom) {
        //Log.d(TAG, "Moving the camera");
        //Log.d(TAG, "moveCamera: moving the camera to: lat: " + latlng.latitude + ", lng: " + latlng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
    }
}