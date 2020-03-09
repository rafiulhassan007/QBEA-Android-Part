package com.rafiulhassan.qrcodebasedevent;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FragmentChooseLogType extends Fragment implements View.OnClickListener {

    private Button btn_merchant_Log_in,btn_new_registration;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fragment_choose_log_type, container, false);

        btn_merchant_Log_in=(Button)view.findViewById(R.id.btn_merchant_Log_in);
        btn_new_registration=(Button)view.findViewById(R.id.btn_my_card_offer);

        btn_merchant_Log_in.setOnClickListener(this);
        btn_new_registration.setOnClickListener(this);
        return view;
    }

    private void fragmentLogIn() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_from_right,
                R.anim.slide_out_to_left);
        fragmentTransaction.replace(R.id.starting_fragment_container, new FragmentLogIn());
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        if(v==btn_merchant_Log_in){
            StartingActivity.superBackPressed="1";
            fragmentLogIn();
        }else if(v==btn_new_registration){
            StartingActivity.superBackPressed="1";
            fragmentRegistration();

//            Intent intent = new Intent(getActivity(),ActivityQRScanner.class);
//            Bundle bundleAnimation =
//                    ActivityOptions.makeCustomAnimation(getActivity(),
//                            R.anim.activity_from_right_to_middle,R.anim.activity_from_middle_to_left).toBundle();
//
//            startActivity(intent,bundleAnimation);
        }
    }

    private void fragmentRegistration() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_from_right,
                R.anim.slide_out_to_left);
        fragmentTransaction.replace(R.id.starting_fragment_container, new FragmentNewRegistration());
        fragmentTransaction.commit();
    }
}
