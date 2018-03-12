package com.example.android.huntgather;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JoinHuntMap extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private String passedHuntCode;
    MyDBHandler dbHandler;
    public Location currentLocation;
    public String timeDifference = "";



    public List<LatLng> allHuntPoints = new ArrayList<LatLng>();
    public ArrayList<String> allHuntCodes = new ArrayList<String>();
    public ArrayList<String> allIds = new ArrayList<String>();
    public ArrayList<String> allQs = new ArrayList<String>();
    public ArrayList<String> allAnswers = new ArrayList<String>();
    public int counter = 0;
    public int zoomCounter = 0;



    @Override
    public void onMapReady(GoogleMap googleMap) {
       // Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }


        }
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);



    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_join);
        new JSONTASK().execute("http://mi-linux.wlv.ac.uk/~1429967/getValues.php");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.navBarDrawerLayout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.action_open,R.string.action_close);
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getLocationPermission();
        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                passedHuntCode= null;
            } else {
                passedHuntCode= extras.getString("userHuntCode");
            }
        } else {
            passedHuntCode= (String) savedInstanceState.getSerializable("userHuntCode");
        }

        Log.v("Testing V" , allQs.toString());

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                Fragment navBarFragment = null;
                int id = item.getItemId();

                if(id == R.id.nav_item_create){

                    Toast.makeText(JoinHuntMap.this, "Create Hunt Selected", Toast.LENGTH_SHORT).show();
                    mDrawerLayout.closeDrawers();
                    navBarFragment = new CreateHuntFragment();
                }else if(id == R.id.nav_item_join){
                    Toast.makeText(JoinHuntMap.this, "Join Hunt Selected", Toast.LENGTH_SHORT).show();

                }else if(id == R.id.nav_item_friends) {
                    Toast.makeText(JoinHuntMap.this, "Friends Selected", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.nav_item_settings) {
                    Toast.makeText(JoinHuntMap.this, "Settings Selected", Toast.LENGTH_SHORT).show();
                }



                if(navBarFragment != null){
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.navBarDrawerLayout, navBarFragment).addToBackStack(null).commit();


                }

                return true;
            }
        });

    }

    public void locationChecker() {

        if(counter == allQs.size()) {

            dbHandler = new MyDBHandler(getApplicationContext(), null,null,1);
            Log.d("JoinHuntMapBefore", "JoinPrintDatabase: "+ dbHandler.getTableAsString());

            dbHandler.updateHuntTimer(passedHuntCode);
            Log.d("JoinHuntMapAfter", "JoinPrintDatabase: "+ dbHandler.getTableAsString());
            timeDifference =  dbHandler.timeDifference();
            Fragment QAFragmentFinish = new FinishFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.navBarDrawerLayout, QAFragmentFinish).addToBackStack(null).commit();

        }
        addMarkerToMap();
        getDeviceLocation();// updates Current Location
        Location markerLocation = new Location("Marker Point");
        float distanceMarkerToDevice = -1 ;
        try {

           markerLocation.setLatitude(allHuntPoints.get(counter).latitude);
           markerLocation.setLongitude(allHuntPoints.get(counter).longitude);
           distanceMarkerToDevice = markerLocation.distanceTo(currentLocation);
          // Log.d(TAG, "LocationChecker:  dis" + distanceMarkerToDevice);
           Log.d(TAG, "LocationChecker: Distance is " + distanceMarkerToDevice + " markerLocation is " + markerLocation + " currentLocation is " + currentLocation);
        }catch (Exception e){

            Log.e("LocationChecker", "Unable to get variables");
        }

        if((distanceMarkerToDevice > 0 && distanceMarkerToDevice < 65)){

            //Toast.makeText(JoinHuntMap.this, "within 65m of current marker", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "LocationChecker: Near Location");
            Fragment QAFragment = new QuestionAnswerFragment();
            Fragment QAFragmentFinish = new FinishFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            Log.d("Counter and Size", " Coutner = " + counter + " Size = " + allQs.size());
            if(counter < allQs.size()) {
                ft.replace(R.id.navBarDrawerLayout, QAFragment).addToBackStack(null).commit();
            }else{
                ft.replace(R.id.navBarDrawerLayout, QAFragmentFinish).addToBackStack(null).commit();

            }

        }else{

           // Toast.makeText(JoinHuntMap.this, "TOO FAR", Toast.LENGTH_SHORT).show();
            if(counter < allQs.size()) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        locationChecker();
                    }
                }, 1000);
            }
        }
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            currentLocation = (Location) task.getResult();
                            //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("Your Position"));
                            if(zoomCounter >0) {
                                //moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),  mMap.getCameraPosition().zoom);
                            }else{
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),DEFAULT_ZOOM);
                                zoomCounter++;
                            }

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(JoinHuntMap.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(JoinHuntMap.this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        return mActionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);


    }// end onOptionsItemSelected


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }



    public  class JSONTASK extends AsyncTask<String ,String,String> {


        @Override
        protected String doInBackground(String... urls) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            allHuntCodes.clear();
            allHuntPoints.clear();
            allIds.clear();
            try {

                Log.d("reaching try", "Reaching try 0");
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while((line = reader.readLine()) != null){
                    buffer.append(line);

                }
                Log.d("reaching try", "Reaching try 0");
                String finalJson = buffer.toString();
                Log.d("finalJson", "finalJson = " + finalJson);
                JSONArray parentArray = new JSONArray(finalJson);

                StringBuffer finalBufferedData = new StringBuffer();
                for(int i = 0 ; i< parentArray.length() ; i++) {
                    JSONObject parentObject = parentArray.getJSONObject(i);

                    String jsonLat = parentObject.getString("lat");
                    String jsonLng = parentObject.getString("lng");
                    String jsonId = parentObject.getString("id");
                    String jsonHuntCode = parentObject.getString("huntCode");
                    String jsonQ = parentObject.getString("question");
                    String jsonAnswer = parentObject.getString("answer");
                    if(jsonHuntCode.equals(passedHuntCode)) {
                        allHuntPoints.add(new LatLng(Float.parseFloat(jsonLat), Float.parseFloat(jsonLng)));
                        allIds.add(jsonId);
                        allHuntCodes.add(jsonHuntCode);
                        allQs.add(jsonQ);
                        allAnswers.add(jsonAnswer);
                    }
                    Log.d("This is AllHuntCodes", "Arr:" + allHuntCodes.toString());
                    Log.d("This is q's", "Arr:" + allQs.toString());
                    Log.d("This is a's", "Arr:" + allAnswers.toString());
                    finalBufferedData.append(jsonLat + " AND " + jsonLng + " AND " + jsonId + " AND " + jsonHuntCode + "\n");

                }

                return finalBufferedData.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(connection!=null){
                    connection.disconnect();
                }
            }// end finally and catches


            return null;
        }// end do in background

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.v("JoinHuntMap allAnswers", allAnswers.toString());
            Log.v("JoinHuntMap allQ's", allQs.toString());
            setAllAnswers(allAnswers);
            setAllQs(allQs);
            locationChecker();



        }
    }// end JSONTASK

    @Override
    protected void onResume() {
        super.onResume();
        //new JSONTASK().execute("http://mi-linux.wlv.ac.uk/~1429967/getValues.php");
    }




    public ArrayList<String> getAllAnswers() {
        return allAnswers;
    }

    public void setAllAnswers(ArrayList<String> allAnswers) {
        this.allAnswers = allAnswers;
    }

    public ArrayList<String> getAllQs() {

        return allQs;
    }

    public void setAllQs(ArrayList<String> allQs) {
        this.allQs = allQs;
    }

    private void addMarkerToMap(){
        try {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(new LatLng(allHuntPoints.get(counter).latitude, allHuntPoints.get(counter).longitude)).title("Your Goal"));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            LatLng marker = new LatLng(allHuntPoints.get(counter).latitude,allHuntPoints.get(counter).longitude);
            LatLng user = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());

            builder.include(marker);
            builder.include(user);

            LatLngBounds bounds = builder.build();

            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.20); // offset from edges of the map 10% of screen

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mMap.animateCamera(cu);
        }catch(Exception e){

            Log.e("AddMarkerToMap", "Cant add marker");
        }// end try Catch

    }// End addMarkerToMap


}//End JoinHuntMap





