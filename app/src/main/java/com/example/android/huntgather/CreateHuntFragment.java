package com.example.android.huntgather;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by Aidan on 25/11/2017.
 */

public class CreateHuntFragment extends Fragment{
    private static final String ALLOWED_CHARACTERS ="0123456789QWERTYUIOPASDFGHJKLZXCVBNM";
    private static final String TAG = CreateHuntFragment.class.getName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.navbar_create_hunt_fragment,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView mHuntCodeTextView = (TextView)view.findViewById(R.id.unique_hunt_create);
        mHuntCodeTextView.setText(getRandomString(4));

        /*
        When add marker button is pressed this will go to a blank map screen (MapAddMarkerActivity.class)
        where tapping will add a marker
         */
        view.findViewById(R.id.add_marker_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"You are inside create hunt fragment", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(),"Finished Clicked", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack();
            }
        });

        view.findViewById(R.id.view_marker_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"View Markers clicked", Toast.LENGTH_SHORT).show();
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





}


