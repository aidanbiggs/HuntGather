package com.example.android.huntgather;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

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
import java.util.Random;

/**
 * Created by Aidan on 25/11/2017.
 */

public class CreateHuntFragment extends Fragment{
    private static final String ALLOWED_CHARACTERS ="0123456789QWERTYUIOPASDFGHJKLZXCVBNM";
    private static final String TAG = CreateHuntFragment.class.getName();


    JSONObject jsonMarkerList = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    private String mHuntCode;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.navbar_create_hunt_fragment,null);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int count = getFragmentManager().getBackStackEntryCount();
        //Toast.makeText(getActivity(),"backStackCount is "+ count, Toast.LENGTH_SHORT).show();
        final TextView mHuntCodeTextView = (TextView)view.findViewById(R.id.unique_hunt_create);
        mHuntCode = getRandomString(4);
        mHuntCodeTextView.setText(mHuntCode);
                /*
        When add marker button is pressed this will go to a blank map screen (MapAddMarkerActivity.class)
        where tapping will add a marker
         */
        view.findViewById(R.id.add_marker_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(),"You are inside create hunt fragment", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MapAddMarkerActivity.class);
                intent.putExtra("userHuntCode",mHuntCodeTextView.getText().toString());
                startActivity(intent);
            }
        });

        /*
        When Back button is pressed this will pop back to the main activity
         */
        view.findViewById(R.id.finish_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(),"Finished Clicked", Toast.LENGTH_SHORT).show();
                new CallAPI().execute("http://mi-linux.wlv.ac.uk/~1429967/setOptionsValues.php");
                getFragmentManager().popBackStack();
            }
        });

        view.findViewById(R.id.view_marker_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(),"View Markers clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),ViewPlacedMarkers.class);
                intent.putExtra("userHuntCode",mHuntCodeTextView.getText().toString());
                startActivity(intent);
            }
        });

    }

    /*
       Generates random 4 length character for Hunt Code
     */
    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public class CallAPI extends AsyncTask<String, ArrayList<Marker>, String> {

        public CallAPI() {
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            String huntCode = mHuntCode;


            try {

                jsonMarkerList.put("huntCode", huntCode);


                final CheckBox timerCheckbox = (CheckBox) getView().findViewById(R.id.timer_check_box);
                final CheckBox multiplayerCheckbox = (CheckBox) getView().findViewById(R.id.multiplayer_check_box);

                if(timerCheckbox.isChecked()){
                    jsonMarkerList.put("huntOptionsTimeLimit", 1);
                }else{
                    jsonMarkerList.put("huntOptionsTimeLimit", 0);
                }

                if(multiplayerCheckbox.isChecked()){
                    jsonMarkerList.put("huntOptionsMultiplayer", 1);
                }else{
                    jsonMarkerList.put("huntOptionsMultiplayer", 0);
                }


                Log.d("markerList", "JSONMARKERLIST = " + jsonMarkerList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonMarkerList);
            Log.d("JSONARRAY FOR", "jsonArray is " + jsonArray);




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
                            Log.d("inputline ", "Input line is " + inputLine);
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






}


