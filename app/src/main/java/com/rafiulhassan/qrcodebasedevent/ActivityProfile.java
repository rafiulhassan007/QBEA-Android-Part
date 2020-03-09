package com.rafiulhassan.qrcodebasedevent;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ActivityProfile extends AppCompatActivity {

    TextView textView_profile_user_name, textView_profile_user_email, textView_profile_user_phone, textView_profile_user_gender,
            textView_profile_user_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initView();
        setValueToView(getIntent().getStringExtra("name"),
                getIntent().getStringExtra("email"),
                getIntent().getStringExtra("phone"),
                getIntent().getStringExtra("gender"),
                getIntent().getStringExtra("address"));
    }

    private void setValueToView(String name, String email, String phone, String gender, String address) {
        textView_profile_user_name.setText(name);
        textView_profile_user_email.setText(email);
        textView_profile_user_phone.setText(phone);
        textView_profile_user_gender.setText(gender);
        textView_profile_user_address.setText(address);
    }

    private void initView() {
        textView_profile_user_name = (TextView) findViewById(R.id.textView_profile_user_name);
        textView_profile_user_email = (TextView) findViewById(R.id.textView_profile_user_email);
        textView_profile_user_phone = (TextView) findViewById(R.id.textView_profile_user_phone);
        textView_profile_user_gender = (TextView) findViewById(R.id.textView_profile_user_gender);
        textView_profile_user_address = (TextView) findViewById(R.id.textView_profile_user_address);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ActivityProfile.this, ActivityDashBoard.class);
        Bundle bundleAnimation =
                ActivityOptions.makeCustomAnimation(ActivityProfile.this,
                        R.anim.activity_from_left_to_middle, R.anim.activity_from_middle_to_right).toBundle();
        startActivity(intent, bundleAnimation);
        finish();
        //super.onBackPressed();
    }
}
