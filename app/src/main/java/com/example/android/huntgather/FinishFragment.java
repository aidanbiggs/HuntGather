package com.example.android.huntgather;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Aidan on 27/01/2018.
 */

public class FinishFragment  extends Fragment {

    int counter = 0;
    ArrayList<String> allHuntCodesPassed = new ArrayList<String>();
    ArrayList<String> allIdsPassed = new ArrayList<String>();
    ArrayList<String> allQsPassed = new ArrayList<String>();
    ArrayList<String> allAnswersPassed = new ArrayList<String>();
    String placeHolderAnswer = "";
    String placeHolderQ = "";

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

        final Button returnButton = view.findViewById(R.id.returnButton);
        final TextView mTimeDifference = view.findViewById(R.id.timeDifference);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("returnClicked", "clicked return");
                FragmentManager fm = getFragmentManager(); // or 'getSupportFragmentManager();'
                int count = fm.getBackStackEntryCount();
                for(int i = 0; i < count; ++i) {
                    fm.popBackStack();
                }
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


}
