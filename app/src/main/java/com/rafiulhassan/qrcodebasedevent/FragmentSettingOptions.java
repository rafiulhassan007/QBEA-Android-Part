package com.rafiulhassan.qrcodebasedevent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class FragmentSettingOptions extends Fragment implements View.OnClickListener {

    private View view;
    private Button btn_settings_change_pass, btn_settings_about;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fragment_setting_options, container, false);
        initView();

        return view;
    }

    private void initView() {
        btn_settings_change_pass = (Button) view.findViewById(R.id.btn_setting_option_change_pass);
        btn_settings_about = (Button) view.findViewById(R.id.btn_setting_option_about);

        btn_settings_change_pass.setOnClickListener(this);
        btn_settings_about.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btn_settings_change_pass) {
            toChangePasswordFragment();
        } else if (view == btn_settings_about) {
            aboutAlertDialog();
        }
    }

    private void toChangePasswordFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_from_right,
                R.anim.slide_out_to_left);
        fragmentTransaction.replace(R.id.setting_fragment_container, new FragmentSettingChangePassword());
        fragmentTransaction.commit();
        ActivitySettings.SETTING_BACK_PRESSED="1";
    }

    private void aboutAlertDialog() {
//        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(getActivity(), R.style.TDA_2);
//        View mView = getLayoutInflater().inflate(R.layout.alert_dialog_setting_about, null);
//        TextInputEditText textInput_setting_alert_d_old_pass=(TextInputEditText)mView.findViewById(R.id.textInput_setting_alert_d_old_pass);
//        TextInputEditText textInput_setting_alert_d_new_pass=(TextInputEditText)mView.findViewById(R.id.textInput_setting_alert_d_new_pass);
//        TextInputEditText textInput_setting_alert_d_re_pass=(TextInputEditText)mView.findViewById(R.id.textInput_setting_alert_d_re_pass);
//        Button btn_setting_alert_d_change_pass=(Button)mView.findViewById(R.id.btn_setting_alert_d_change_pass);
//
//        loadingDialog.setView(mView);
//        final AlertDialog dialog = loadingDialog.create();
//
//        btn_setting_alert_d_change_pass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//
//            }
//        });
//        dialog.show();
    }

}
