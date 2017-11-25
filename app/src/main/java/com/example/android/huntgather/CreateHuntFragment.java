package com.example.android.huntgather;

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

//        Random mHuntCodeRandom = new Random();
//        int mHuntCodeNumber = mHuntCodeRandom.nextInt(1000-9999);
//        String mHuntCodeString = String.valueOf(mHuntCodeNumber);
//        //Log.d(TAG,"mHuntCodeNumber is " + mHuntCodeNumber);

        TextView mHuntCodeTextView = (TextView)view.findViewById(R.id.unique_hunt_create);
        mHuntCodeTextView.setText(getRandomString(4));


        view.findViewById(R.id.add_marker_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"You are inside create hunt fragment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

}
