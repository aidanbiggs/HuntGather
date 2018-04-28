package com.example.android.huntgather;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MapAddMarkerActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private ArrayList<Marker> arrMarkerList;
    private ArrayList<String> questionList = new ArrayList<>();
    private ArrayList<String> answerList = new ArrayList<>();
    private ArrayList<Polyline> arrPolylineList;
    private boolean finishClicked = false;
    JSONObject jsonMarkerList = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    private boolean dialogOpen;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        arrMarkerList = new ArrayList<>();
        arrPolylineList = new ArrayList<>();
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }


        }
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);


    }

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private String passedHuntCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                passedHuntCode = null;
            } else {
                passedHuntCode = extras.getString("userHuntCode");
            }
        } else {
            passedHuntCode = (String) savedInstanceState.getSerializable("userHuntCode");
        }


        setContentView(R.layout.activity_add_marker_map);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.navBarDrawerLayout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.action_open, R.string.action_close);
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getLocationPermission();
        final Button finishedButton = (Button) findViewById(R.id.add_marker_finished_button);

        finishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CallAPI().execute("http://mi-linux.wlv.ac.uk/~1429967/setValues.php");
                finishClicked = true;
                MapAddMarkerActivity.this.onBackPressed();
            }
        });

        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                Fragment navBarFragment = null;
                int id = item.getItemId();

                if (id == R.id.nav_item_create) {

                    //Toast.makeText(MapAddMarkerActivity.this, "Create Hunt Selected", Toast.LENGTH_SHORT).show();
                    mDrawerLayout.closeDrawers();
                    navBarFragment = new CreateHuntFragment();
                } else if (id == R.id.nav_item_join) {
                    //Toast.makeText(MapAddMarkerActivity.this, "Join Hunt Selected", Toast.LENGTH_SHORT).show();

                } else if (id == R.id.nav_item_explore) {
                    //Toast.makeText(MapAddMarkerActivity.this, "Friends Selected", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_item_settings) {
                    //Toast.makeText(MapAddMarkerActivity.this, "Settings Selected", Toast.LENGTH_SHORT).show();
                }


                if (navBarFragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.navBarDrawerLayout, navBarFragment).addToBackStack(null).commit();


                }


                return true;
            }
        });
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            // mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("Your Position"));
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapAddMarkerActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapAddMarkerActivity.this);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
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
    public boolean onOptionsItemSelected(MenuItem item) {

        return mActionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);


    }// end onOptionsItemSelected

    @Override
    public void onMapClick(LatLng latLng) {

        final double lit = latLng.latitude;
        final double lon = latLng.longitude;
        String passedHuntCode;




        /*
        Code for dialog box
         */
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapAddMarkerActivity.this); // Creates new dialog box builder of activity
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_marker, null); // overlays view on top of current

        final EditText mQuestion = (EditText) mView.findViewById(R.id.question_editText); // get text stored in question edit text
        final EditText mAnswer = (EditText) mView.findViewById(R.id.answer_editText); // get text stored in answer edit text
        Button mAddMarker = (Button) mView.findViewById(R.id.add_marker_dialog); //
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show(); // inflates dialog over builder

        mAddMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mQuestion.getText().toString().isEmpty() && !mAnswer.getText().toString().isEmpty()) {

                    questionList.add(mQuestion.getText().toString());//add Question to question list
                    answerList.add(mAnswer.getText().toString()); // add answer to answer list
                    arrMarkerList.add(mMap.addMarker(new MarkerOptions().position(new LatLng(lit, lon)).title("Your Added Marker").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))));
                    dialog.dismiss(); // close dialog when "add marker" pressed


                } else {
                    //If a box is empty dont post or leave
                    Toast.makeText(MapAddMarkerActivity.this, "Please fill in any empty fields", Toast.LENGTH_SHORT).show();

                }
            }
        });


        for (Polyline polyline : arrPolylineList) {
            polyline.remove();
        }

        if (arrMarkerList.size() > 1) {
            PolylineOptions polylineOptions = new PolylineOptions();
            for (Marker marker : arrMarkerList) {
                polylineOptions.add(marker.getPosition());
            }
            arrPolylineList.add(mMap.addPolyline(polylineOptions));
        }
    }

    public class CallAPI extends AsyncTask<String, ArrayList<Marker>, String> {

        public CallAPI() {
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String markerLat;
            String markerLng;
            String huntCode = passedHuntCode;
            String userId = "1";
            String question;
            String answer;


            for (int i = 0; i < arrMarkerList.size(); i++) {

                markerLat = String.valueOf(arrMarkerList.get(i).getPosition().latitude);
                markerLng = String.valueOf(arrMarkerList.get(i).getPosition().longitude);
                question = questionList.get(i);
                answer = answerList.get(i);
                Log.d("Counter", "i = " + i);
                try {
                    jsonMarkerList = new JSONObject();
                    jsonMarkerList.put("lat", markerLat);
                    jsonMarkerList.put("lng", markerLng);
                    jsonMarkerList.put("id", userId);
                    jsonMarkerList.put("huntCode", huntCode);
                    jsonMarkerList.put("question", question);
                    jsonMarkerList.put("answer", answer);
                    Log.d("markerList", "JSONMARKERLIST = " + jsonMarkerList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(jsonMarkerList);
                Log.d("JSONARRAY FOR", "jsonArray is " + jsonArray);


                Log.d("MarkerLat", "Marker Lat is " + markerLat);
                Log.d("markerLng", "marker Lng = is " + markerLng);

            }
            Log.v("For Loop JsonArray", " = " + jsonArray);
        }


        @Override
        protected String doInBackground(String... params) {

            String urlString = params[0]; // URL to call


            OutputStream out = null;
            try {


                Log.v("params0", urlString);
                Log.v("jsonArray is equal to ", jsonArray.toString());

                URL url = new URL(urlString);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-type", "application/json");
                urlConnection.setDoOutput(true);

                out = new BufferedOutputStream(urlConnection.getOutputStream());

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                Log.v("JSONarRAY", String.valueOf(jsonArray));
                writer.write(String.valueOf(jsonArray));

                StringBuffer response = null;

                writer.flush();

                writer.close();

                out.close();
                int statusCode = urlConnection.getResponseCode();
                Log.d("STATUS", " The status code is " + statusCode);
                switch (statusCode) {
                    case 200:
                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String inputLine;
                        response = new StringBuffer();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                }

                Log.v("response", " The response is " + response);


                urlConnection.connect();
                return null;

            } catch (Exception e) {

                System.out.println(e.getMessage());


            }

            return null;
        }
    } //end call API

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //  Toast.makeText(MapActivity.this, "Join Hunt Selected", Toast.LENGTH_SHORT).show();
        if (finishClicked == false) {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MapAddMarkerActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();

        }else{

            super.onBackPressed();

        }
    }
}





