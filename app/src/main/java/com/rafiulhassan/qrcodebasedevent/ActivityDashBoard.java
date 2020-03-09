package com.rafiulhassan.qrcodebasedevent;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rafiulhassan.qrcodebasedevent.Core.UserPreference;
import com.rafiulhassan.qrcodebasedevent.AllEvents.ActivityEvents;
import com.rafiulhassan.qrcodebasedevent.UtilSetup.ErrorFilterAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivityDashBoard extends AppCompatActivity implements View.OnClickListener {

    private CardView cardView_dashboard_log_out;
    private ImageView imageView_dashboard_user_profile, imageView_dashboard_settings, imageView_dashboard_create_event,
            imageView_dashboard_my_events, imageView_dashboard_subscribe_to_events, imageView_dashboard_hosted_events;
    private TextView textView_user_name1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        initView();

    }

    private void initView() {
        cardView_dashboard_log_out = (CardView) findViewById(R.id.cardView_dashboard_log_out);
        imageView_dashboard_user_profile = (ImageView) findViewById(R.id.imageView_dashboard_user_profile);
        imageView_dashboard_settings = (ImageView) findViewById(R.id.imageView_dashboard_settings);
        imageView_dashboard_create_event = (ImageView) findViewById(R.id.imageView_dashboard_create_event);
        imageView_dashboard_my_events = (ImageView) findViewById(R.id.imageView_dashboard_my_events);
        imageView_dashboard_hosted_events = (ImageView) findViewById(R.id.imageView_dashboard_hosted_events);
        imageView_dashboard_subscribe_to_events = (ImageView) findViewById(R.id.imageView_dashboard_subscribe_to_events);
        textView_user_name1 = (TextView) findViewById(R.id.textView_user_name1);

        cardView_dashboard_log_out.setOnClickListener(this);
        imageView_dashboard_user_profile.setOnClickListener(this);
        imageView_dashboard_settings.setOnClickListener(this);
        imageView_dashboard_create_event.setOnClickListener(this);
        imageView_dashboard_hosted_events.setOnClickListener(this);
        imageView_dashboard_subscribe_to_events.setOnClickListener(this);
        imageView_dashboard_my_events.setOnClickListener(this);

        UserPreference userPreference = new UserPreference(ActivityDashBoard.this);
        final String name;
        if (userPreference.getUser() != null) {
            name = userPreference.getUser().getuName();
        } else name = "";
        textView_user_name1.setText("User: " + name + " ");
    }

    @Override
    public void onClick(View v) {
        if (v == cardView_dashboard_log_out) {
            logOutAlertDialog();
        } else if (v == imageView_dashboard_user_profile) {
            //profileDataRequest();
            toQrScannerActivity();
        } else if (v == imageView_dashboard_settings) {
            toSettingsActivity();
        } else if (v == imageView_dashboard_create_event) {
            toAddEventActivity();
        } else if (v == imageView_dashboard_hosted_events) {
            toEventsActivity("hosted");
        } else if (v == imageView_dashboard_subscribe_to_events) {
            toSubscribeEventActivity();
        } else if (v == imageView_dashboard_my_events) {
            toEventsActivity("my");
        }
    }

    private void toEventsActivity(String type) {
        Intent intent = new Intent(ActivityDashBoard.this, ActivityEvents.class);
        intent.putExtra("type", type);
        Bundle bundleAnimation =
                ActivityOptions.makeCustomAnimation(ActivityDashBoard.this,
                        R.anim.activity_from_right_to_middle, R.anim.activity_from_middle_to_left).toBundle();
        startActivity(intent, bundleAnimation);
    }

    private void toQrScannerActivity() {
        Intent intent = new Intent(ActivityDashBoard.this, ActivityQRScanner.class);
        Bundle bundleAnimation =
                ActivityOptions.makeCustomAnimation(ActivityDashBoard.this,
                        R.anim.activity_from_right_to_middle, R.anim.activity_from_middle_to_left).toBundle();
        startActivity(intent, bundleAnimation);
    }

    private void toSubscribeEventActivity() {
        Intent intent = new Intent(ActivityDashBoard.this, ActivitySubscribeEvent.class);
        Bundle bundleAnimation =
                ActivityOptions.makeCustomAnimation(ActivityDashBoard.this,
                        R.anim.activity_from_right_to_middle, R.anim.activity_from_middle_to_left).toBundle();
        startActivity(intent, bundleAnimation);
    }

    private void toAddEventActivity() {
        Intent intent = new Intent(ActivityDashBoard.this, ActivityCreateEvent.class);
        Bundle bundleAnimation =
                ActivityOptions.makeCustomAnimation(ActivityDashBoard.this,
                        R.anim.activity_from_right_to_middle, R.anim.activity_from_middle_to_left).toBundle();
        startActivity(intent, bundleAnimation);
    }

    private void toSettingsActivity() {
        Intent intent = new Intent(ActivityDashBoard.this, ActivitySettings.class);
        Bundle bundleAnimation =
                ActivityOptions.makeCustomAnimation(ActivityDashBoard.this,
                        R.anim.activity_from_right_to_middle, R.anim.activity_from_middle_to_left).toBundle();
        startActivity(intent, bundleAnimation);
        finish();
    }

    private void profileDataRequest() {
        final UserPreference userPreference = new UserPreference(ActivityDashBoard.this);
        String token = "";
        if (userPreference.getUser() != null) {
            token = userPreference.getUser().getuToken();
        }
        final String finalToken = token;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://api.1card.com.bd/rafiulhassan/profile", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response.getString("message").equals("success")) {

                        toProfileActivity(response.getString("name"),
                                response.getString("email"),
                                response.getString("phone"),
                                response.getString("gender"),
                                response.getString("address"));
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int err = ErrorFilterAgent.errorFiltering(error);
                alertDialogRetry(ErrorFilterAgent.errorMsgShow(err));

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap();
                headers.put("Accept", "application/json");
                headers.put("Authorization", finalToken);
                return headers;
            }

        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(3 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        SingleTonRequestQueue.getInstance(ActivityDashBoard.this).addToRequestQueue(jsonObjectRequest);
    }

    private void toProfileActivity(String name, String email, String phone, String gender, String address) {
        Intent intent = new Intent(ActivityDashBoard.this, ActivityProfile.class);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("phone", phone);
        intent.putExtra("gender", gender);
        intent.putExtra("address", address);
        Bundle bundleAnimation =
                ActivityOptions.makeCustomAnimation(ActivityDashBoard.this,
                        R.anim.activity_from_right_to_middle, R.anim.activity_from_middle_to_left).toBundle();
        startActivity(intent, bundleAnimation);
        finish();
    }

    private void logOutAlertDialog() {
        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(ActivityDashBoard.this, R.style.TDA_2);
        View mView = getLayoutInflater().inflate(R.layout.alert_dialog_body_and_btn, null);
        TextView textView_alert_dialog_body = (TextView) mView.findViewById(R.id.textView_alert_dialog_body);
        Button btn_alert_dialog_btn = (Button) mView.findViewById(R.id.btn_alert_dialog_btn);

        textView_alert_dialog_body.setText("Do you want to log out from QRBAM app?");
        btn_alert_dialog_btn.setText("Yes, Log Out");

        loadingDialog.setView(mView);
        final AlertDialog dialog = loadingDialog.create();

        btn_alert_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                logOutRequest();
            }
        });
        dialog.show();
    }

    private void logOutRequest() {
        final UserPreference userPreference = new UserPreference(ActivityDashBoard.this);
        userPreference.logOut();
        toStartingActivity();
//
//        String token = "";
//        if (userPreference.getUser() != null) {
//            token = userPreference.getUser().getuToken();
//        }
//        final String finalToken = token;
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
//                "", null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//                try {
//                    if (response.getString("message").equals("success")) {
//                        userPreference.logOut();
//                        toStartingActivity();
//                    } else {
//                        reLogin();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                int err = ErrorFilterAgent.errorFiltering(error);
//                alertDialogRetry(ErrorFilterAgent.errorMsgShow(err));
//
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap();
//                headers.put("Accept", "application/json");
//                headers.put("Authorization", finalToken);
//                return headers;
//            }
//
//        };
//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(3 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
//        SingleTonRequestQueue.getInstance(ActivityDashBoard.this).addToRequestQueue(jsonObjectRequest);
    }

    private void toStartingActivity() {
        Intent intent = new Intent(ActivityDashBoard.this, StartingActivity.class);
        Bundle bundleAnimation =
                ActivityOptions.makeCustomAnimation(ActivityDashBoard.this,
                        R.anim.activity_from_left_to_middle, R.anim.activity_from_middle_to_right).toBundle();
        startActivity(intent, bundleAnimation);
        finish();
    }

    private void reLogin() {
        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(ActivityDashBoard.this, R.style.TDA_2);
        View mView = getLayoutInflater().inflate(R.layout.alert_dialog_body_and_btn, null);
        TextView textView_alert_dialog_body = (TextView) mView.findViewById(R.id.textView_alert_dialog_body);
        Button btn_alert_dialog_btn = (Button) mView.findViewById(R.id.btn_alert_dialog_btn);

        textView_alert_dialog_body.setText("Something unusual occurred. Please try re-login");
        btn_alert_dialog_btn.setText("Ok");

        loadingDialog.setView(mView);
        final AlertDialog dialog = loadingDialog.create();

        btn_alert_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                UserPreference userPreference = new UserPreference(ActivityDashBoard.this);
                userPreference.logOut();
            }
        });
        dialog.show();
    }

    private void alertDialogRetry(String msg) {
        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(ActivityDashBoard.this, R.style.TDA_2);
        View mView = getLayoutInflater().inflate(R.layout.alert_dialog_body_and_btn, null);
        TextView textView_alert_dialog_body = (TextView) mView.findViewById(R.id.textView_alert_dialog_body);
        Button btn_alert_dialog_btn = (Button) mView.findViewById(R.id.btn_alert_dialog_btn);

        textView_alert_dialog_body.setText(msg);
        btn_alert_dialog_btn.setText("Retry");

        loadingDialog.setView(mView);
        final AlertDialog dialog = loadingDialog.create();

        btn_alert_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                logOutRequest();
            }
        });
        dialog.show();
    }
}
