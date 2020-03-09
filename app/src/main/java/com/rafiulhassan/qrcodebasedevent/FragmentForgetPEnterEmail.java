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

public class FragmentForgetPEnterEmail extends Fragment {

    private View view;
    private Button btn_forget_password_get_code;
    private TextInputEditText textInput_email_get_security_code;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fragment_forget_penter_email, container, false);

        textInput_email_get_security_code = (TextInputEditText) view.findViewById(R.id.textInput_email_get_security_code);
        btn_forget_password_get_code = (Button) view.findViewById(R.id.btn_forget_password_get_code);

        btn_forget_password_get_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textInput_email_get_security_code.getWindowToken(), 0);

                if (textInput_email_get_security_code.getText().toString().length() < 1 ||
                        !textInput_email_get_security_code.getText().toString().trim().contains("@") || !textInput_email_get_security_code.getText().toString().trim().contains(".")) {
                    CustomDialog customDialog=new CustomDialog(getActivity()," Please, enter a valid email address","Ok");
                    customDialog.show();
                } else {
                    sendSecurityCodeRequest();
                }
            }
        });
        return view;
    }

    private void sendSecurityCodeRequest() {

        final CustomDialog customDialog = new CustomDialog(getActivity());
        customDialog.show();
        try {
            final JSONObject param = new JSONObject();
            param.put("email", textInput_email_get_security_code.getText().toString().trim());
            final String requestBody = param.toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    "https://api.1card.com.bd/rafiulhassan/forget", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    //Toast.makeText(getActivity(), ""+response, Toast.LENGTH_SHORT).show();
                    try {
                        if (response.getString("message").equals("success")) {

                            securityCodeSentOnSuccess(response.getString("token"));

                        } else {

                            CustomDialog customDialog=new CustomDialog(getActivity(),"Unauthorized email address. please contact with the Administrator.",
                                    "Ok");
                            customDialog.show();

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
                    alertDialogRetry(ErrorFilterAgent.errorMsgShow(err));

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap();
                    headers.put("Accept", "application/json");
                    headers.put("Authorization", "Bearer fXOkrwppgzkHcgXeV8XmAz3UYReT68Xnns1IFBBL");
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


//        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(getContext(), R.style.TDA_2);
//        View mView = getLayoutInflater().inflate(R.layout.alert_dialog_loading, null);
//
//        loadingDialog.setView(mView);
//        final AlertDialog dialog = loadingDialog.create();
//        //dialog.setCancelable(false);
//
//        new Handler().postDelayed(new Runnable() {
//            // Using handler with postDelayed called runnable run method
//            @Override
//            public void run() {
//                dialog.dismiss();
//                securityCodeSentOnSuccess();
//
//            }
//        }, 3200);
//
//        dialog.show();
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
                sendSecurityCodeRequest();
            }
        });
        dialog.show();
    }

    private void securityCodeSentOnSuccess(final String token) {
        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(getContext(), R.style.TDA_2);
        View mView = getLayoutInflater().inflate(R.layout.alert_dialog_security_code_sent, null);
        Button btn_security_reset_ok = (Button) mView.findViewById(R.id.btn_security_reset_ok);

        loadingDialog.setView(mView);
        final AlertDialog dialog = loadingDialog.create();
        dialog.setCancelable(false);
        btn_security_reset_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fragmentResetPassword(token);
            }
        });
        dialog.show();
    }

    private void fragmentResetPassword(String token) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_from_right,
                R.anim.slide_out_to_left);

        Bundle arg=new Bundle();
        arg.putString("token",token);
        FragmentResetPassword fragmentResetPassword=new FragmentResetPassword();
        fragmentResetPassword.setArguments(arg);

        fragmentTransaction.replace(R.id.starting_fragment_container, fragmentResetPassword);
        fragmentTransaction.commit();
        StartingActivity.superBackPressed = "3";
    }
}
