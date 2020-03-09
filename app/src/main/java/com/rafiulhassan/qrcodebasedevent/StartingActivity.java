package com.rafiulhassan.qrcodebasedevent;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rafiulhassan.qrcodebasedevent.Core.UserPreference;
import com.rafiulhassan.qrcodebasedevent.UtilSetup.ErrorFilterAgent;

import org.json.JSONException;
import org.json.JSONObject;

public class StartingActivity extends AppCompatActivity {
    public static String superBackPressed = "x";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentStartUpcheck();

        new Handler().postDelayed(new Runnable() {
            // Using handler with postDelayed called runnable run method
            @Override
            public void run() {
//                versionCheckRequest();
                if (loggedInCheck()) {
                    dashBoardActivity();
                } else {
                    fragmentChooseLogType();
                }
            }
        }, 3500);
    }

    private void versionCheckRequest() {

        try {
            final JSONObject param = new JSONObject();
            param.put("version", BuildConfig.VERSION_NAME);

            final String requestBody = param.toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    ErrorFilterAgent.DOMAIN + "versioncheck", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getString("message").equals("success")) {
                            if (loggedInCheck()) {
                                dashBoardActivity();
                            } else {
                                fragmentChooseLogType();
                            }
                        } else {
                            alertDialogdownloadUpdate("Good news! A new version is available at the play store. please update now.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    superBackPressed = "0";
                    int err = ErrorFilterAgent.errorFiltering(error);
                    if (err == 1 || err == 5 || err == 6) {
                        alertDialogRetry(ErrorFilterAgent.errorMsgShow(err));
                    } else {
                        alertDialogRetry(ErrorFilterAgent.errorMsgShow(err));
                    }
                }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestBody.getBytes("utf-8");
                    } catch (Exception e) {
                        return null;
                    }
                }
            };
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(3 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
            SingleTonRequestQueue.getInstance(StartingActivity.this).addToRequestQueue(jsonObjectRequest);
        } catch (Exception e) {
            Toast.makeText(StartingActivity.this, "Unexpected Error", Toast.LENGTH_LONG).show();
        }
    }

    private void dashBoardActivity() {
        Intent intent = new Intent(StartingActivity.this, ActivityDashBoard.class);
        Bundle bundleAnimation =
                ActivityOptions.makeCustomAnimation(StartingActivity.this,
                        R.anim.activity_from_right_to_middle, R.anim.activity_from_middle_to_left).toBundle();
        startActivity(intent, bundleAnimation);
        finish();
    }

    private boolean loggedInCheck() {
        UserPreference userPreference = new UserPreference(StartingActivity.this);
        if (userPreference.getUser() != null) {
            return true;
        } else {
            return false;
        }
    }

    private void fragmentChooseLogType() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_from_right,
                R.anim.slide_out_to_left);
        fragmentTransaction.replace(R.id.starting_fragment_container, new FragmentChooseLogType());
        fragmentTransaction.commitAllowingStateLoss();
        superBackPressed = "0";
    }

    private void fragmentStartUpcheck() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.starting_fragment_container, new FragmentStartChecking());
        fragmentTransaction.commitAllowingStateLoss();

    }

    @Override
    public void onBackPressed() {

        if (superBackPressed.equals("0")) {
            super.onBackPressed();
        } else if (superBackPressed.equals("1")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(
                    R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            fragmentTransaction.replace(R.id.starting_fragment_container, new FragmentChooseLogType());
            fragmentTransaction.commit();
            superBackPressed = "0";
        } else if (superBackPressed.equals("2")) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(
                    R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            fragmentTransaction.replace(R.id.starting_fragment_container, new FragmentLogIn());
            fragmentTransaction.commit();
            superBackPressed = "1";

        } else if (superBackPressed.equals("3")) {
            AlertDialog.Builder loadingDialog = new AlertDialog.Builder(StartingActivity.this, R.style.TDA_2);
            View mView = getLayoutInflater().inflate(R.layout.alert_dialog_body_and_two_btns, null);
            TextView textView_alert_dialog_body = (TextView) mView.findViewById(R.id.textView_alert_dialog_body);
            Button btn_alert_dialog_btn = (Button) mView.findViewById(R.id.btn_alert_dialog_btn);
            Button btn_alert_dialog_btn2 = (Button) mView.findViewById(R.id.btn_alert_dialog_btn2);

            textView_alert_dialog_body.setText("Do you want to abort the password reset process?");
            btn_alert_dialog_btn.setText("Yes");
            btn_alert_dialog_btn2.setText("No");

            loadingDialog.setView(mView);
            final AlertDialog dialog = loadingDialog.create();

            btn_alert_dialog_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(
                            R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                    fragmentTransaction.replace(R.id.starting_fragment_container, new FragmentLogIn());
                    fragmentTransaction.commit();
                    superBackPressed = "1";
                }
            });
            btn_alert_dialog_btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();

        }
    }

    private void alertDialogRetry(String msg) {
        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(StartingActivity.this, R.style.TDA_2);
        View mView = getLayoutInflater().inflate(R.layout.alert_dialog_body_and_btn, null);
        TextView textView_alert_dialog_body = (TextView) mView.findViewById(R.id.textView_alert_dialog_body);
        Button btn_alert_dialog_btn = (Button) mView.findViewById(R.id.btn_alert_dialog_btn);

        textView_alert_dialog_body.setText(msg);
        btn_alert_dialog_btn.setText("Retry");

        loadingDialog.setView(mView);
        final AlertDialog dialog = loadingDialog.create();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        btn_alert_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                versionCheckRequest();
            }
        });
        dialog.show();
    }

    private void alertDialogdownloadUpdate(String msg) {
        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(StartingActivity.this, R.style.TDA_2);
        View mView = getLayoutInflater().inflate(R.layout.alert_dialog_body_and_btn, null);
        TextView textView_alert_dialog_body = (TextView) mView.findViewById(R.id.textView_alert_dialog_body);
        Button btn_alert_dialog_btn = (Button) mView.findViewById(R.id.btn_alert_dialog_btn);

        textView_alert_dialog_body.setText(msg);
        btn_alert_dialog_btn.setText("Update Now");

        loadingDialog.setView(mView);
        final AlertDialog dialog = loadingDialog.create();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        btn_alert_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.onecard.bd.daffodil"));
                startActivity(intent);
            }
        });
        dialog.show();
    }
}
