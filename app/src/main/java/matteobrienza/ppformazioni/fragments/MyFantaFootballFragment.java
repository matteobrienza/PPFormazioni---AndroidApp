package matteobrienza.ppformazioni.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import matteobrienza.ppformazioni.R;


public class MyFantaFootballFragment extends Fragment {


    public MyFantaFootballFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_myfantafootball, container, false);
    }

}
