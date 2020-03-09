package com.rafiulhassan.qrcodebasedevent;

import android.content.Context;
import android.content.DialogInterface;
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
import com.rafiulhassan.qrcodebasedevent.UtilSetup.InputValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class FragmentNewRegistration extends Fragment implements View.OnClickListener {

    private View mView;
    private TextInputEditText textInput_new_reg_name, textInput_new_reg_email, textInput_new_reg_password,
            textInput_new_reg_password_conf;
    private Button btn_new_reg_register;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_fragment_new_registration, container, false);
        initView();

        return mView;
    }

    private void initView() {
        textInput_new_reg_name = (TextInputEditText) mView.findViewById(R.id.textInput_new_reg_name);
        textInput_new_reg_email = (TextInputEditText) mView.findViewById(R.id.textInput_new_reg_email);
        textInput_new_reg_password = (TextInputEditText) mView.findViewById(R.id.textInput_new_reg_password);
        textInput_new_reg_password_conf = (TextInputEditText) mView.findViewById(R.id.textInput_new_reg_password_conf);
        btn_new_reg_register = (Button) mView.findViewById(R.id.btn_new_reg_register);

        btn_new_reg_register.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == btn_new_reg_register) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(textInput_new_reg_password.getWindowToken(), 0);
            validateInput();
        }
    }

    private void validateInput() {
        CustomDialog customDialog;
        InputValidator inputValidator = new InputValidator();
        if (inputValidator.checkEmpty(textInput_new_reg_name.getText().toString())
                || inputValidator.checkEmpty(textInput_new_reg_email.getText().toString())
                || inputValidator.checkEmpty(textInput_new_reg_password.getText().toString())
                || inputValidator.checkEmpty(textInput_new_reg_password_conf.getText().toString())
                || inputValidator.checkEmailIfFalse(textInput_new_reg_email.getText().toString())
                || inputValidator.checkPasswordLengthIfFalse(textInput_new_reg_password.getText().toString(), 8)
                || inputValidator.compareIfFalse(textInput_new_reg_password.getText().toString(),
                textInput_new_reg_password_conf.getText().toString())
                ) {
            customDialog = new CustomDialog(getActivity(), inputValidator.getTextRepresent(), "Ok");
            customDialog.show();
            return;
        } else {
            registrationRequest();
        }
    }

    private void registrationRequest() {
        final CustomDialog loadingDialog = new CustomDialog(getActivity());
        loadingDialog.show();
        try {
            final JSONObject param = new JSONObject();
            param.put("name", textInput_new_reg_name.getText().toString());
            param.put("email", textInput_new_reg_email.getText().toString());
            param.put("password", textInput_new_reg_password.getText().toString());
            param.put("password_confirmation", textInput_new_reg_password_conf.getText().toString());

            final String requestBody = param.toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    ErrorFilterAgent.DOMAIN + "register", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    loadingDialog.dismiss();

                    try {
                        if (response.getString("message").equals("success")) {
                            alertDialogOnSuccess("Registration successful. Now you can login using your email and password");
                        } else {
                            CustomDialog customDialog = new CustomDialog(getActivity(), "Registration failed. Try with different email address. ", "Ok");
                            customDialog.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loadingDialog.dismiss();
                    if(error.networkResponse!=null && error.networkResponse.statusCode==422){
                        CustomDialog customDialog = new CustomDialog(getActivity(), "Email Address Already Taken", "Ok");
                        customDialog.show();
                        return;
                    }
                   // Toast.makeText(getActivity(), ""+error.networkResponse.statusCode, Toast.LENGTH_SHORT).show();
                    int err = ErrorFilterAgent.errorFiltering(error);
                    if (err == 1 || err == 5 || err == 6) {
                        alertDialogRetry(ErrorFilterAgent.errorMsgShow(err));
                    } else {
                        alertDialogRetry(ErrorFilterAgent.errorMsgShow(err));
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
            public void onClick(View view) {
                dialog.dismiss();
                registrationRequest();
            }
        });

        dialog.show();
    }

    private void alertDialogOnSuccess(String msg) {
        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(getActivity(), R.style.TDA_2);
        View mView = getLayoutInflater().inflate(R.layout.alert_dialog_body_and_btn, null);
        TextView textView_alert_dialog_body = (TextView) mView.findViewById(R.id.textView_alert_dialog_body);
        Button btn_alert_dialog_btn = (Button) mView.findViewById(R.id.btn_alert_dialog_btn);

        textView_alert_dialog_body.setText(msg);
        btn_alert_dialog_btn.setText("Ok");

        loadingDialog.setView(mView);
        final AlertDialog dialog = loadingDialog.create();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                toLoginFragment();
            }
        });
        btn_alert_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                toLoginFragment();
            }
        });

        dialog.show();
    }

    private void toLoginFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        fragmentTransaction.replace(R.id.starting_fragment_container, new FragmentLogIn());
        fragmentTransaction.commit();
        StartingActivity.superBackPressed = "1";
    }
}
