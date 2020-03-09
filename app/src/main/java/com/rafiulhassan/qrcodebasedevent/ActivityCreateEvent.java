package com.rafiulhassan.qrcodebasedevent;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
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

public class ActivityCreateEvent extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText textInput_create_event_name, textInput_create_event_short_desc, textInput_create_event_type, textInput_create_event_code;
    private Button btn_create_event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        initView();
        textInput_create_event_type.setOnClickListener(this);
        btn_create_event.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == textInput_create_event_type) {
            popupMenuForDropdown();
        } else if (view == btn_create_event) {
            if (textInput_create_event_name.getText().toString().length() < 1 ||
                    textInput_create_event_short_desc.getText().toString().length() < 1 ||
//                    textInput_create_event_type.getText().toString().length() < 1 ||
                    textInput_create_event_code.getText().toString().length() < 1) {
                CustomDialog customDialog = new CustomDialog(ActivityCreateEvent.this, "Some fields are empty", "Ok");
                customDialog.show();
            } else {
                eventCreateRequest();
            }
        }
    }

    private void popupMenuForDropdown() {
        Context wrapper = new ContextThemeWrapper(ActivityCreateEvent.this, R.style.popUp);
        PopupMenu amountPop = new PopupMenu(wrapper, textInput_create_event_type);
        amountPop.getMenu().add("Once");
        amountPop.getMenu().add("Regular");

        amountPop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                textInput_create_event_type.setText(item.getTitle());

                return true;
            }
        });
        amountPop.show();
    }

    private void eventCreateRequest() {
        final CustomDialog customDialog = new CustomDialog(ActivityCreateEvent.this);
        customDialog.show();
        final UserPreference userPreference = new UserPreference(ActivityCreateEvent.this);
        final String token;
        if (userPreference.getUser() != null) {
            token = userPreference.getUser().getuToken();
        }else token="";

        try {
            final JSONObject param = new JSONObject();
            param.put("title", textInput_create_event_name.getText().toString());
            param.put("description", textInput_create_event_short_desc.getText().toString());
            //param.put("type", textInput_create_event_type.getText().toString());
            param.put("code", textInput_create_event_code.getText().toString());

            final String requestBody = param.toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    ErrorFilterAgent.DOMAIN + "create/event", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    try {
                        if (response.getString("message").equals("success")) {
                            alertDialogonSuccess("New event created successfully");
                        } else {
                            CustomDialog customDialog = new CustomDialog(ActivityCreateEvent.this, "Event Creation failed", "Ok");
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
                    headers.put("Authorization", "Bearer "+token);
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
            SingleTonRequestQueue.getInstance(ActivityCreateEvent.this).addToRequestQueue(jsonObjectRequest);
        } catch (Exception e) {
            Toast.makeText(ActivityCreateEvent.this, "Unexpected Error", Toast.LENGTH_LONG).show();
        }
    }

    private void initView() {
        textInput_create_event_name = (TextInputEditText) findViewById(R.id.textInput_create_event_name);
        textInput_create_event_short_desc = (TextInputEditText) findViewById(R.id.textInput_create_event_short_desc);
        textInput_create_event_type = (TextInputEditText) findViewById(R.id.textInput_create_event_type);
        textInput_create_event_code = (TextInputEditText) findViewById(R.id.textInput_create_event_code);
        btn_create_event = (Button) findViewById(R.id.btn_create_event);
    }

    private void alertDialogRetry(String msg) {
        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(ActivityCreateEvent.this, R.style.TDA_2);
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
                eventCreateRequest();
            }
        });

        dialog.show();
    }

    private void alertDialogonSuccess(String msg) {
        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(ActivityCreateEvent.this, R.style.TDA_2);
        View mView = getLayoutInflater().inflate(R.layout.alert_dialog_body_and_btn, null);
        TextView textView_alert_dialog_body = (TextView) mView.findViewById(R.id.textView_alert_dialog_body);
        Button btn_alert_dialog_btn = (Button) mView.findViewById(R.id.btn_alert_dialog_btn);

        textView_alert_dialog_body.setText(msg);
        btn_alert_dialog_btn.setText("Ok");

        loadingDialog.setView(mView);
        final AlertDialog dialog = loadingDialog.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                finish();
            }
        });
        btn_alert_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


}
