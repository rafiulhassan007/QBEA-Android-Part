package com.rafiulhassan.qrcodebasedevent;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ActivitySettings extends AppCompatActivity {

    public static String SETTING_BACK_PRESSED = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        settingOptionsFragment();

    }

    private void settingOptionsFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.setting_fragment_container, new FragmentSettingOptions());
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (SETTING_BACK_PRESSED.equals("1")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(
                    R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            fragmentTransaction.replace(R.id.setting_fragment_container, new FragmentSettingOptions());
            fragmentTransaction.commit();
            SETTING_BACK_PRESSED = "0";
        } else {
            Intent intent = new Intent(ActivitySettings.this, ActivityDashBoard.class);
            Bundle bundleAnimation =
                    ActivityOptions.makeCustomAnimation(ActivitySettings.this,
                            R.anim.activity_from_left_to_middle, R.anim.activity_from_middle_to_right).toBundle();
            startActivity(intent, bundleAnimation);
            finish();
        }
    }
}
