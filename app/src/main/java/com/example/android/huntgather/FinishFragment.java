package com.example.android.huntgather;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
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

/**
 * Created by Aidan on 27/01/2018.
 */

public class FinishFragment  extends Fragment {

    String rating;
    String huntCode;
    JSONObject jsonRating = new JSONObject();
    JSONArray jsonArray = new JSONArray();


    @Override
    public void onDetach() {
        super.onDetach();
        final JoinHuntMap mainActivity = (JoinHuntMap) getActivity(); //https://stackoverflow.com/questions/13067033/how-to-access-activity-variables-from-a-fragment-android
        mainActivity.locationChecker();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.finish_screen,container,false);
        final JoinHuntMap mainActivity = (JoinHuntMap) getActivity(); //https://stackoverflow.com/questions/13067033/how-to-access-activity-variables-from-a-fragment-android
        huntCode = mainActivity.getPassedHuntCode();
        final Button returnButton = view.findViewById(R.id.returnButton);
        final TextView mTimeDifference = view.findViewById(R.id.timeDifference);
        final RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("returnClicked", "clicked return");
                FragmentManager fm = getFragmentManager(); // or 'getSupportFragmentManager();'
                int count = fm.getBackStackEntryCount();
                for(int i = 0; i < count; ++i) {
                    fm.popBackStack();
                }



                rating = String.valueOf(ratingBar.getRating());
                new PostRating().execute("http://mi-linux.wlv.ac.uk/~1429967/setRating.php");
                Log.d("StarRating is", "Star rating is" + rating);
                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
            }
        });


        mTimeDifference.setText(mainActivity.timeDifference);


        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    public class PostRating extends AsyncTask<String, String, String> {

        public PostRating() {
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                jsonRating.put("huntCode", huntCode);
                jsonRating.put("rating", rating);

                Log.d("markerList", "JSONMARKERLIST = " + jsonRating);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonRating);
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
    }

}
