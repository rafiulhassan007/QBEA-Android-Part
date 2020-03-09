package com.rafiulhassan.qrcodebasedevent.SubscribedUsers;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rafiulhassan.qrcodebasedevent.AttendanceReport.ActivityAttendanceReport;
import com.rafiulhassan.qrcodebasedevent.Core.UserPreference;
import com.rafiulhassan.qrcodebasedevent.R;
import com.rafiulhassan.qrcodebasedevent.SingleTonRequestQueue;
import com.rafiulhassan.qrcodebasedevent.UtilSetup.CustomDialog;
import com.rafiulhassan.qrcodebasedevent.UtilSetup.ErrorFilterAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivitySubscribedUsers extends AppCompatActivity {

    private TextView textView_subs_user_event_title;
    private ListView listView_subs_user;
    private ArrayList<SubsUserModel> subsUsers = new ArrayList<>();
    private ListViewCustomAdapterSubsUsers adapter;
    private String eventId = "";
    private String eventTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribed_users);

        eventId = getIntent().getStringExtra("event_id");
        eventTitle = getIntent().getStringExtra("event_title");
        initView();
        configureListAdaptor();
    }

    private void configureListAdaptor() {
        adapter = new ListViewCustomAdapterSubsUsers(ActivitySubscribedUsers.this, subsUsers);
        listView_subs_user.setAdapter(adapter);

        listView_subs_user.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                optionsDialog(eventId, subsUsers.get(i).getUserId(),subsUsers.get(i).getUserName());
            }
        });

        subsUserDataRequest();

    }

    private void optionsDialog(final String eventId, final String userId, final String userName) {

        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(ActivitySubscribedUsers.this, R.style.TDA_2);
        View mView = getLayoutInflater().inflate(R.layout.alert_dialog_body_and_two_btns, null);
        TextView textView_alert_dialog_body = (TextView) mView.findViewById(R.id.textView_alert_dialog_body);
        Button btn_alert_dialog_btn = (Button) mView.findViewById(R.id.btn_alert_dialog_btn);
        Button btn_alert_dialog_btn2 = (Button) mView.findViewById(R.id.btn_alert_dialog_btn2);

        textView_alert_dialog_body.setVisibility(View.GONE);
        btn_alert_dialog_btn2.setText("Attendance Report");
        btn_alert_dialog_btn.setText("Change Approval");

        loadingDialog.setView(mView);
        final AlertDialog dialog = loadingDialog.create();

        btn_alert_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subscriberAuthorizationChangeRequest(eventId, userId);
                dialog.dismiss();
            }
        });
        btn_alert_dialog_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toAttendanceReportActivity(eventId, userId,userName);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void toAttendanceReportActivity(String eventId, String userId,String userName) {
        Intent intent = new Intent(ActivitySubscribedUsers.this, ActivityAttendanceReport.class);
        intent.putExtra("subs_name",userName);
        intent.putExtra("event_id",eventId);
        intent.putExtra("subs_id",userId);
        Bundle bundleAnimation =
                ActivityOptions.makeCustomAnimation(ActivitySubscribedUsers.this,
                        R.anim.activity_from_right_to_middle, R.anim.activity_from_middle_to_left).toBundle();
        startActivity(intent, bundleAnimation);
    }

    private void subscriberAuthorizationChangeRequest(String eventId, String userId) {
        final CustomDialog customDialog = new CustomDialog(ActivitySubscribedUsers.this);
        customDialog.show();

        final UserPreference userPreference = new UserPreference(ActivitySubscribedUsers.this);
        final String token;
        if (userPreference.getUser() != null) {
            token = userPreference.getUser().getuToken();
        } else token = "";
        try {

            final JSONObject param = new JSONObject();
            param.put("event_id", eventId);
            param.put("sud_user_id", userId);
            final String requestBody = param.toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    ErrorFilterAgent.DOMAIN + "subscription/authorize", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    try {
                        if (response.getString("message").equals("success")) {

                            alertDialogForRefresh("Subscriber Approval Successfully Changed");
                        } else {
                            CustomDialog customDialog = new CustomDialog(ActivitySubscribedUsers.this, "No Data found ", "Ok");
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
                    headers.put("Authorization", "Bearer " + token);
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
            SingleTonRequestQueue.getInstance(ActivitySubscribedUsers.this).addToRequestQueue(jsonObjectRequest);
        } catch (Exception e) {
            Toast.makeText(ActivitySubscribedUsers.this, "Unexpected Error", Toast.LENGTH_LONG).show();
        }
    }

    private void subsUserDataRequest() {
        subsUsers.removeAll(subsUsers);
        final CustomDialog customDialog = new CustomDialog(ActivitySubscribedUsers.this);
        customDialog.show();

        final UserPreference userPreference = new UserPreference(ActivitySubscribedUsers.this);
        final String token;
        if (userPreference.getUser() != null) {
            token = userPreference.getUser().getuToken();
        } else token = "";
        try {

            final JSONObject param = new JSONObject();
            param.put("event_id", eventId);
            final String requestBody = param.toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    ErrorFilterAgent.DOMAIN + "subscribed/user", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    try {
                        if (response.getString("message").equals("success")) {
                            JSONArray subsUsersArray = response.getJSONArray("enrolled");
                            if (subsUsersArray.length() > 0) {
                                for (int i = 0; i < subsUsersArray.length(); i++) {
                                    JSONObject perEvent = subsUsersArray.getJSONObject(i);
                                    subsUsers.add(new SubsUserModel(perEvent.getJSONObject("user").getString("id"),
                                            perEvent.getJSONObject("user").getString("name"),
                                            perEvent.getJSONObject("user").getString("email"),
                                            perEvent.getString("accepted")));
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                CustomDialog customDialog = new CustomDialog(ActivitySubscribedUsers.this, "No Data found ", "Ok");
                                customDialog.show();
                            }

                        } else {
                            CustomDialog customDialog = new CustomDialog(ActivitySubscribedUsers.this, "No Data found ", "Ok");
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
                    headers.put("Authorization", "Bearer " + token);
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
            SingleTonRequestQueue.getInstance(ActivitySubscribedUsers.this).addToRequestQueue(jsonObjectRequest);
        } catch (Exception e) {
            Toast.makeText(ActivitySubscribedUsers.this, "Unexpected Error", Toast.LENGTH_LONG).show();
        }
    }

    private void initView() {
        textView_subs_user_event_title = (TextView) findViewById(R.id.textView_subs_user_event_title);
        listView_subs_user = (ListView) findViewById(R.id.listView_subs_user);

        textView_subs_user_event_title.setText(eventTitle);
    }

    private void alertDialogRetry(String msg) {
        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(ActivitySubscribedUsers.this, R.style.TDA_2);
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
                subsUserDataRequest();
            }
        });
        dialog.show();
    }

    private void alertDialogForRefresh(String msg) {
        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(ActivitySubscribedUsers.this, R.style.TDA_2);
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
                subsUserDataRequest();
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
