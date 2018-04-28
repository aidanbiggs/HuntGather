package com.example.android.huntgather;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import static android.view.Gravity.CENTER;


/**
 * Created by Aidan on 25/11/2017.
 */

public class ExplorerFragment extends android.support.v4.app.Fragment {


    ArrayList<String> allHuntCodes = new ArrayList<>();
    ArrayList<String> avgRatings = new ArrayList<>();
    private ArrayList<HashMap<String, String>> list;

    int counter = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        try {
            new GetValues().execute("http://mi-linux.wlv.ac.uk/~1429967/getValues.php");
        }catch (Exception e){

            //Log.e("onCreateView", "HuntCodeGet Not working" );
        }
        return inflater.inflate(R.layout.hunt_explorer,container,false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public  class GetRatings extends AsyncTask<String ,String,String> {


        @Override
        protected String doInBackground(String... urls) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {


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
                String finalJson = buffer.toString();
                JSONArray parentArray = new JSONArray(finalJson);

                StringBuffer finalBufferedData = new StringBuffer();
                for(int i = 0 ; i< parentArray.length() ; i++) {

                    JSONObject parentObject = parentArray.getJSONObject(i);
                    String jsonRatings = parentObject.getString("rating");
                    if (jsonRatings == "null") {
                        avgRatings.add("~");
                    } else {
                        avgRatings.add(jsonRatings);
                    }
                    finalBufferedData.append(jsonRatings + "\n");

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
            return "hello";
        }// end do in background

        @Override
        protected void onPostExecute(String result) {
            //Log.d("result", "Result :  " + result);
            super.onPostExecute(result);
           // Log.d("AvgRAtings", "AvgRating " + avgRatings.toString() + "huntCode" + allHuntCodes.toString());
            Log.d("counter", "counter = " + counter + " avgRatings = " + avgRatings.size());
            try {
                if(counter == (allHuntCodes.size() - 1 )) {
                    View view = getView();
                    TableLayout tableLayout = (TableLayout)view.findViewById(R.id.hunt_rating_table_layout);

                    populateList();
                    Log.d("ExplorerFragment", "HuntCodeSize: " + allHuntCodes.size());
                    Log.d("ExplorerFragment", "avgRatings size: " + avgRatings.size());
                    for(int i = 0; i <allHuntCodes.size() ; i++){

                        TableRow tr = new TableRow(getActivity());
                        TextView huntCodeText = new TextView(getActivity());
                        TextView ratingText = new TextView(getActivity());

                        ratingText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                        ratingText.setGravity(CENTER);
                        ratingText.setTextSize(18);

                        huntCodeText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                        huntCodeText.setGravity(CENTER);
                        huntCodeText.setTextSize(18);

                        huntCodeText.setText(allHuntCodes.get(i));
                        ratingText.setText(avgRatings.get(i));

                        tr.addView(huntCodeText);
                        tr.addView(ratingText);
                        tableLayout.addView(tr);
                    }
                }
                counter++;
            }catch (Exception e){


            }

        }
    }// end JSONTASK

    private void populateList() {
        // TODO Auto-generated method stub

        list = new ArrayList<HashMap<String, String>>();

        Map<String, String> hashmap = new HashMap<String, String>();
        Log.d("DEBUG", "allHuntCodes length : " + allHuntCodes.size());
        for(int i = 0; i <allHuntCodes.size() ; i++){
            hashmap.put(allHuntCodes.get(i),avgRatings.get(i));

        }

        TreeMap<String,String> sortedMap = new TreeMap<String,String>(new MyComparator(hashmap));
        sortedMap.putAll(hashmap);
        try {
            Log.d("Sorted Hash", " : " + hashmap.toString());
        }catch(Exception e ){


        }

    }

    public  class GetValues extends AsyncTask<String ,String,String> {


        @Override
        protected String doInBackground(String... urls) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            allHuntCodes.clear();
            try {


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
                String finalJson = buffer.toString();
                JSONArray parentArray = new JSONArray(finalJson);
                //Log.d("JSON", "Parent Array:" + parentArray);

                StringBuffer finalBufferedData = new StringBuffer();
                for(int i = 0 ; i< parentArray.length() ; i++) {
                    JSONObject parentObject = parentArray.getJSONObject(i);
                    String jsonHuntCode = parentObject.getString("huntCode");
                    allHuntCodes.add(jsonHuntCode);
                    //Log.d("This is AllHuntCodes", "Arr:" + allHuntCodes.toString());
                    finalBufferedData.append(jsonHuntCode + "\n");

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
            //Log.d("DEBUG", "allHuntCodes length : " + allHuntCodes.size());
            allHuntCodes = new ArrayList<String>(new LinkedHashSet<String>(allHuntCodes));
            //Log.d("DEBUG", "allHuntCodes length : " + allHuntCodes.size());
            for (int i = 0; i < allHuntCodes.size(); i++) {

               new GetRatings().execute("http://mi-linux.wlv.ac.uk/~1429967/getRating.php?huntCode=" + allHuntCodes.get(i));

            }
        }
    }// end GetValues

}
