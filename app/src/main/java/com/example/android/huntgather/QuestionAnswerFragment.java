package com.example.android.huntgather;
import android.graphics.Paint;
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


public class QuestionAnswerFragment extends Fragment {

    int counter = 0;
    ArrayList<String> allHuntCodesPassed = new ArrayList<String>();
    ArrayList<String> allIdsPassed = new ArrayList<String>();
    ArrayList<String> allQsPassed = new ArrayList<String>();
    ArrayList<String> allAnswersPassed = new ArrayList<String>();
    String placeHolderAnswer = "";
    String placeHolderQ = "";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.dialog_question_answer,container,false);
        final JoinHuntMap mainActivity = (JoinHuntMap) getActivity(); //https://stackoverflow.com/questions/13067033/how-to-access-activity-variables-from-a-fragment-android

        allAnswersPassed = (ArrayList<String>) mainActivity.allAnswers.clone();
        allQsPassed = (ArrayList<String>) mainActivity.allQs.clone();

        Log.d("QAFRAG AllA's", " = " +  allAnswersPassed.toString());
        Log.d("QAFRAG AllQ's", " = " +  allQsPassed.toString());
        Log.v("QAFRAG first ele", String.valueOf(allAnswersPassed.size()));



        if(mainActivity.counter == 0) {
            try{
                placeHolderAnswer = allAnswersPassed.get(0);
                placeHolderQ = allQsPassed.get(0);

            }catch(Exception e){
                Log.d("Cant get (0)", "Cant get allAnswersPassed(0)");

            }
        }



        Log.v("QAFRAG first ele", placeHolderAnswer);
        final TextView mUserQ = (TextView) view.findViewById(R.id.user_question_textView);
        final EditText mUserA = (EditText) view.findViewById(R.id.answer_editText);
        final Button mButtonFin = (Button) view.findViewById(R.id.button_submit_answer);

        mUserQ.setText(placeHolderQ);


        mButtonFin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("mUserA ==" , mUserA.getText().toString());
                Log.d("placeHolderA ==" , placeHolderAnswer);

                if(mUserA.getText().toString().equals(placeHolderAnswer)){


                    mainActivity.counter++;
                    Toast.makeText(getActivity(),"Answer is correct", Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStack();

                }// end if check
            }
        });




        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


}
