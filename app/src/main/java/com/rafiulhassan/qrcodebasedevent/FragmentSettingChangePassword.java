package com.rafiulhassan.qrcodebasedevent;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rafiulhassan.qrcodebasedevent.Core.UserPreference;
import com.rafiulhassan.qrcodebasedevent.UtilSetup.CustomDialog;
import com.rafiulhassan.qrcodebasedevent.UtilSetup.ErrorFilterAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class FragmentSettingChangePassword extends Fragment {

    private View view;
    private TextInputEditText textInput_setting_change_pass_old_pass, textInput_setting_change_pass_new_pass, textInput_setting_change_pass_re_pass;
    private Button btn_setting_change_pass_change_pass;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fragment_setting_change_password, container, false);
        initView();

        btn_setting_change_pass_change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textInput_setting_change_pass_old_pass.getText().toString().length() < 6 ||
                        textInput_setting_change_pass_new_pass.getText().toString().length() < 6 ||
                        textInput_setting_change_pass_re_pass.getText().toString().length() < 6) {
                    CustomDialog customDialog=new CustomDialog(getActivity(),"Minimum password length 6 characters","Ok");
                    customDialog.show();
                } else if (!textInput_setting_change_pass_new_pass.getText().toString().
                        equals(textInput_setting_change_pass_re_pass.getText().toString())) {
                    CustomDialog customDialog=new CustomDialog(getActivity(),"Password mismatched. Please check again","Ok");
                    customDialog.show();
                }else {
                    passwordChangeRequest();
                }
            }
        });

        return view;
    }

    private void passwordChangeRequest() {
        final CustomDialog customDialog = new CustomDialog(getActivity());
        customDialog.show();
        final UserPreference userPreference = new UserPreference(getActivity());
        String token="";
        if(userPreference.getUser()!=null){
            token=userPreference.getUser().getuToken();
        }
        final String finalToken = token;
        try {
            final JSONObject param = new JSONObject();
            param.put("password", textInput_setting_change_pass_old_pass.getText().toString().trim());
            param.put("newpassword", textInput_setting_change_pass_new_pass.getText().toString().trim());
            final String requestBody = param.toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    "https://api.1card.com.bd/rafiulhassan/password/change", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    //Toast.makeText(getActivity(), ""+response, Toast.LENGTH_SHORT).show();
                    try {
                        if (response.getString("message").equals("success")) {
                            onSuccessChange();
                        } else {
                            Toast.makeText(getActivity(), "Please try different password.", Toast.LENGTH_SHORT).show();
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
                    headers.put("Authorization", finalToken);
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

    private void onSuccessChange() {
        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(getActivity(), R.style.TDA_2);
        View mView = getLayoutInflater().inflate(R.layout.alert_dialog_body_and_btn, null);
        TextView textView_alert_dialog_body = (TextView) mView.findViewById(R.id.textView_alert_dialog_body);
        Button btn_alert_dialog_btn = (Button) mView.findViewById(R.id.btn_alert_dialog_btn);

        textView_alert_dialog_body.setText("Password successfully changed . Now log in again using your new password");
        btn_alert_dialog_btn.setText("Ok");

        loadingDialog.setView(mView);
        final AlertDialog dialog = loadingDialog.create();

        btn_alert_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                UserPreference userPreference=new UserPreference(getActivity());
                userPreference.logOut();
                getActivity().finish();
                Intent intent =new Intent(getActivity(),StartingActivity.class);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    private void initView() {
        textInput_setting_change_pass_old_pass = (TextInputEditText) view.findViewById(R.id.textInput_setting_change_pass_old_pass);
        textInput_setting_change_pass_new_pass = (TextInputEditText) view.findViewById(R.id.textInput_setting_change_pass_new_pass);
        textInput_setting_change_pass_re_pass = (TextInputEditText) view.findViewById(R.id.textInput_setting_change_pass_re_pass);
        btn_setting_change_pass_change_pass = (Button) view.findViewById(R.id.btn_setting_change_pass_change_pass);

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
                passwordChangeRequest();
            }
        });
        dialog.show();
    }
}
