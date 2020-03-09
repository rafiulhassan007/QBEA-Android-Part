package com.rafiulhassan.qrcodebasedevent;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rafiulhassan.qrcodebasedevent.Core.User;
import com.rafiulhassan.qrcodebasedevent.Core.UserPreference;
import com.rafiulhassan.qrcodebasedevent.UtilSetup.CustomDialog;
import com.rafiulhassan.qrcodebasedevent.UtilSetup.ErrorFilterAgent;
import com.rafiulhassan.qrcodebasedevent.UtilSetup.InputValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class FragmentLogIn extends Fragment implements View.OnClickListener {

    private View view;
    private TextView textView_forget_password;
    private Button btn_log_in;
    private TextInputEditText textInput_login_email, textInput_login_password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fragment_log_in, container, false);
        initView();

        return view;
    }

    private void initView() {
        textView_forget_password = (TextView) view.findViewById(R.id.textView_forget_password);
        btn_log_in = (Button) view.findViewById(R.id.btn_log_in);
        textInput_login_email = (TextInputEditText) view.findViewById(R.id.textInput_login_email);
        textInput_login_password = (TextInputEditText) view.findViewById(R.id.textInput_login_password);

        textView_forget_password.setOnClickListener(this);
        btn_log_in.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == textView_forget_password) {
            forgetPasswordFragment();
        } else if (v == btn_log_in) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(textInput_login_password.getWindowToken(), 0);

            validateInput();


//                DummyData
//                User user=new User("name","email", "",
//                        "token", "", "true", "");
//
//                UserPreference userPreference=new UserPreference(getActivity());
//                userPreference.toSave(user);
//                getActivity().finish();
//                dashBoardActivity();
//            }

//            final CustomDialog customLoading=new CustomDialog(getActivity());
//            customLoading.show();
//            new Handler().postDelayed(new Runnable() {
//                // Using handler with postDelayed called runnable run method
//                @Override
//                public void run() {
//                    customLoading.dismiss();
//                    dashBoardActivity();
//                }
//            }, 3200);
        }
    }

    private void validateInput() {
        CustomDialog customDialog;
        InputValidator inputValidator = new InputValidator();
        if (inputValidator.checkEmpty(textInput_login_email.getText().toString())
                || inputValidator.checkEmpty(textInput_login_password.getText().toString())
                || inputValidator.checkEmailIfFalse(textInput_login_email.getText().toString())) {
            customDialog = new CustomDialog(getActivity(), inputValidator.getTextRepresent(), "Ok");
            customDialog.show();
        } else {
            logInRequest();
        }
    }

    private void dashBoardActivity() {

        Intent intent = new Intent(getActivity(), ActivityDashBoard.class);
        Bundle bundleAnimation =
                ActivityOptions.makeCustomAnimation(getActivity(),
                        R.anim.activity_from_right_to_middle, R.anim.activity_from_middle_to_left).toBundle();
        startActivity(intent, bundleAnimation);
    }

    private void forgetPasswordFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_from_right,
                R.anim.slide_out_to_left);
        fragmentTransaction.replace(R.id.starting_fragment_container, new FragmentForgetPEnterEmail());
        fragmentTransaction.commit();
        StartingActivity.superBackPressed = "2";
    }

    private void logInRequest() {
        final CustomDialog customDialog = new CustomDialog(getActivity());
        customDialog.show();

        try {
            final JSONObject param = new JSONObject();
            param.put("email", textInput_login_email.getText().toString().trim());
            param.put("password", textInput_login_password.getText().toString().trim());
            final String requestBody = param.toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    ErrorFilterAgent.DOMAIN + "login", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();

                    //Toast.makeText(getActivity(), ""+response, Toast.LENGTH_SHORT).show();
                    try {
                        if (response.getString("message").equals("success")) {
                            User user = new User(response.getJSONObject("user").getString("name"),
                                    response.getJSONObject("user").getString("email"), "",
                                    response.getString("access_token"), "", "true", "");

                            UserPreference userPreference = new UserPreference(getActivity());
                            userPreference.toSave(user);
                            getActivity().finish();
                            dashBoardActivity();
                        } else {
                            onFailearDialogShow();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.dismiss();

                    int err = ErrorFilterAgent.errorFiltering(error);
                    if (err == 1 || err == 5 || err == 6) {
                        alertDialogRetry(ErrorFilterAgent.errorMsgShow(err));
                    } else {
                        alertDialogFinishActivity(ErrorFilterAgent.errorMsgShow(err));
                    }
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap();
                    headers.put("Accept", "application/json");
                    headers.put("Authorization", "Bearer 7CBVRegoSP7DTwJGDxyjV4mjaeoFMcm3pNubgVpy");
                    return headers;
                }

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
            SingleTonRequestQueue.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Unexpected Error", Toast.LENGTH_LONG).show();
        }
    }

    private void onFailearDialogShow() {
        CustomDialog customDialog = new CustomDialog(getActivity(), "Incorrect email or password. Please enter correctly.",
                "Ok");
        customDialog.show();
    }

    private void alertDialogRetry(String msg) {
        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(getActivity(), R.style.TDA_2);
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
                logInRequest();
            }
        });
        dialog.show();
    }

    private void alertDialogFinishActivity(String msg) {
        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(getActivity(), R.style.TDA_2);
        View mView = getLayoutInflater().inflate(R.layout.alert_dialog_body_and_btn, null);
        TextView textView_alert_dialog_body = (TextView) mView.findViewById(R.id.textView_alert_dialog_body);
        Button btn_alert_dialog_btn = (Button) mView.findViewById(R.id.btn_alert_dialog_btn);

        textView_alert_dialog_body.setText(msg);
        btn_alert_dialog_btn.setText("Ok");

        loadingDialog.setView(mView);
        final AlertDialog dialog = loadingDialog.create();

        dialog.setCancelable(false);
        btn_alert_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getActivity().finish();
            }
        });
        dialog.show();
    }
}
