package com.rafiulhassan.qrcodebasedevent;

import android.content.Context;
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
import com.rafiulhassan.qrcodebasedevent.UtilSetup.CustomDialog;
import com.rafiulhassan.qrcodebasedevent.UtilSetup.ErrorFilterAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class FragmentResetPassword extends Fragment {

    private View view;
    private TextInputEditText textInput_reset_pass_security_code, textInput_reset_pass_security_n_pass,
            textInput_reset_pass_security_re_pass;
    private Button btn_reset_pass_reset;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fragment_reset_password, container, false);
        initView();

        btn_reset_pass_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textInput_reset_pass_security_re_pass.getWindowToken(), 0);

                if (textInput_reset_pass_security_code.getText().toString().length() < 1
                        || textInput_reset_pass_security_n_pass.getText().toString().length() < 1
                        || textInput_reset_pass_security_re_pass.getText().toString().length() < 1) {
                    CustomDialog customDialog = new CustomDialog(getActivity(), "Please fill all empty fields and try again.", "Ok");
                    customDialog.show();
                    return;
                }else if (textInput_reset_pass_security_n_pass.getText().toString().length()<6
                        || textInput_reset_pass_security_re_pass.getText().toString().length()<6) {
                    CustomDialog customDialog = new CustomDialog(getActivity(), "Minimum password length 6 characters", "Ok");
                    customDialog.show();
                    return;
                }else if (!textInput_reset_pass_security_n_pass.getText().toString()
                        .equals(textInput_reset_pass_security_re_pass.getText().toString())) {
                    CustomDialog customDialog = new CustomDialog(getActivity(), "Password mismatched. Please check again", "Ok");
                    customDialog.show();
                    return;
                } else {
                    resetPassRequest();
                }

            }
        });

        return view;
    }

    private void resetPassRequest() {

        final CustomDialog customDialog = new CustomDialog(getActivity());
        customDialog.show();
        try {
            final JSONObject param = new JSONObject();
            param.put("code", textInput_reset_pass_security_code.getText().toString());
            param.put("password", textInput_reset_pass_security_n_pass.getText().toString());
            final String requestBody = param.toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    "https://api.1card.com.bd/rafiulhassan/password/reset", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    //Toast.makeText(getActivity(), ""+response, Toast.LENGTH_SHORT).show();
                    try {
                        alertDialogOnResponse(response.getString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.dismiss();
                    int err = ErrorFilterAgent.errorFiltering(error);
                    alertDialogRetry(ErrorFilterAgent.errorMsgShow(err));

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap();
                    headers.put("Accept", "application/json");
                    headers.put("Authorization", getArguments().getString("token"));
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

    private void initView() {
        textInput_reset_pass_security_code = (TextInputEditText) view.findViewById(R.id.textInput_reset_pass_security_code);
        textInput_reset_pass_security_n_pass = (TextInputEditText) view.findViewById(R.id.textInput_reset_pass_security_n_pass);
        textInput_reset_pass_security_re_pass = (TextInputEditText) view.findViewById(R.id.textInput_reset_pass_security_re_pass);
        btn_reset_pass_reset = (Button) view.findViewById(R.id.btn_reset_pass_reset);
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
                resetPassRequest();
            }
        });
        dialog.show();
    }

    private void alertDialogOnResponse(String responseType) {
        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(getActivity(), R.style.TDA_2);
        View mView = getLayoutInflater().inflate(R.layout.alert_dialog_body_and_btn, null);
        TextView textView_alert_dialog_body = (TextView) mView.findViewById(R.id.textView_alert_dialog_body);
        Button btn_alert_dialog_btn = (Button) mView.findViewById(R.id.btn_alert_dialog_btn);

        if (responseType.equals("success")) {
            textView_alert_dialog_body.setText("Password reset successful. Now you can log in using your new password");
        } else {
            textView_alert_dialog_body.setText("Something went wrong! Please try again after sometime.");
        }
        btn_alert_dialog_btn.setText("Ok");

        loadingDialog.setView(mView);
        final AlertDialog dialog = loadingDialog.create();

        if (responseType.equals("success")) {
            dialog.setCancelable(false);
        }

        btn_alert_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                toLogInFragment();
            }
        });
        dialog.show();
    }

    private void toLogInFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        fragmentTransaction.replace(R.id.starting_fragment_container, new FragmentLogIn());
        fragmentTransaction.commit();
        StartingActivity.superBackPressed = "1";
    }
}
