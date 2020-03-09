package com.rafiulhassan.qrcodebasedevent.AllEvents;

import android.app.ActivityOptions;
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
import com.rafiulhassan.qrcodebasedevent.ActivityQrGenerator;
import com.rafiulhassan.qrcodebasedevent.Core.UserPreference;
import com.rafiulhassan.qrcodebasedevent.SubscribedUsers.ActivitySubscribedUsers;
import com.rafiulhassan.qrcodebasedevent.UtilSetup.ErrorFilterAgent;
import com.rafiulhassan.qrcodebasedevent.UtilSetup.CustomDialog;
import com.rafiulhassan.qrcodebasedevent.R;
import com.rafiulhassan.qrcodebasedevent.SingleTonRequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityEvents extends AppCompatActivity {

    private ListView listView_my_events;
    private TextView textView_events_header;
    private ListViewCustomAdapterHostedEvents adapter;
    private ListViewCustomAdapterMyEvents adapter2;
    private ArrayList<EventModel> eventModels = new ArrayList<>();
    private String type = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        type = getIntent().getStringExtra("type");
        initView();

        if (type.equals("hosted")) {
            configureHostedEventList();
        } else {
            configureMyEventList();
        }
    }

    private void configureMyEventList() {
        adapter2 = new ListViewCustomAdapterMyEvents(ActivityEvents.this, eventModels);
        listView_my_events.setAdapter(adapter2);
        myEventListRequest();
    }

    private void myEventListRequest() {

        final CustomDialog customDialog = new CustomDialog(ActivityEvents.this);
        customDialog.show();

        final UserPreference userPreference = new UserPreference(ActivityEvents.this);
        final String token;
        if (userPreference.getUser() != null) {
            token = userPreference.getUser().getuToken();
        } else token = "";
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    ErrorFilterAgent.DOMAIN + "events/myEvents", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    try {
                        if (response.getString("message").equals("success")) {
                            JSONArray enrolled = response.getJSONArray("enrolled");
                            if (enrolled.length() > 0) {
                                for (int i = 0; i < enrolled.length(); i++) {
                                    JSONObject perEvent = enrolled.getJSONObject(i);
                                    eventModels.add(new EventModel(
                                            perEvent.getJSONObject("event").getString("id"),
                                            perEvent.getJSONObject("event").getString("title"),
                                            perEvent.getJSONObject("event").getString("description"),
                                            perEvent.getJSONObject("event").getString("status"),
                                            perEvent.getJSONObject("event").getString("created_at"),
                                            perEvent.getString("accepted")));
                                }
                                adapter2.notifyDataSetChanged();
                            } else {
                                CustomDialog customDialog = new CustomDialog(ActivityEvents.this, "No events found ", "Ok");
                                customDialog.show();
                            }

                        } else {
                            CustomDialog customDialog = new CustomDialog(ActivityEvents.this, "No events found ", "Ok");
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

            };
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(3 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
            SingleTonRequestQueue.getInstance(ActivityEvents.this).addToRequestQueue(jsonObjectRequest);
        } catch (Exception e) {
            Toast.makeText(ActivityEvents.this, "Unexpected Error", Toast.LENGTH_LONG).show();
        }

    }

    private void configureHostedEventList() {
        adapter = new ListViewCustomAdapterHostedEvents(ActivityEvents.this, eventModels);
        listView_my_events.setAdapter(adapter);

        listView_my_events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (type.equals("hosted")) {
                    eventOptionsDialog(eventModels.get(i).getId(), eventModels.get(i).getTitle());
                }
            }
        });
        hostedEventListRequest();
    }

    private void initView() {
        listView_my_events = (ListView) findViewById(R.id.listView_my_events);
        textView_events_header = (TextView) findViewById(R.id.textView_events_header);

        if (type.equals("hosted")) {
            textView_events_header.setText("My Hosted Events");
        } else {
            textView_events_header.setText("My Events");
        }

    }

    private void eventOptionsDialog(final String id, final String title) {
        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(ActivityEvents.this, R.style.TDA_2);
        View mView = getLayoutInflater().inflate(R.layout.alert_dialog_event_options, null);

        Button btn_event_option_subscriber_list = (Button) mView.findViewById(R.id.btn_event_option_subscriber_list);
        Button btn_event_option_attendance_record = (Button) mView.findViewById(R.id.btn_event_option_attendance_record);
        Button btn_event_option_update_event = (Button) mView.findViewById(R.id.btn_event_option_update_event);

        loadingDialog.setView(mView);
        final AlertDialog dialog = loadingDialog.create();

        btn_event_option_subscriber_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                toSubscribedUsersActivity(id, title);
            }
        });

        btn_event_option_attendance_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                toQRGenerateActivity(id);
            }
        });

        btn_event_option_update_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        dialog.show();

    }

    private void toSubscribedUsersActivity(String id, String title) {
        Intent intent = new Intent(ActivityEvents.this, ActivitySubscribedUsers.class);
        intent.putExtra("event_id", id);
        intent.putExtra("event_title", title);
        Bundle bundleAnimation =
                ActivityOptions.makeCustomAnimation(ActivityEvents.this,
                        R.anim.activity_from_right_to_middle, R.anim.activity_from_middle_to_left).toBundle();
        startActivity(intent, bundleAnimation);
    }

    private void toQRGenerateActivity(String id) {
        Intent intent = new Intent(ActivityEvents.this, ActivityQrGenerator.class);
        intent.putExtra("event_id", id);
        Bundle bundleAnimation =
                ActivityOptions.makeCustomAnimation(ActivityEvents.this,
                        R.anim.activity_from_right_to_middle, R.anim.activity_from_middle_to_left).toBundle();
        startActivity(intent, bundleAnimation);
    }

    private void hostedEventListRequest() {

        final CustomDialog customDialog = new CustomDialog(ActivityEvents.this);
        customDialog.show();

        final UserPreference userPreference = new UserPreference(ActivityEvents.this);
        final String token;
        if (userPreference.getUser() != null) {
            token = userPreference.getUser().getuToken();
        } else token = "";
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    ErrorFilterAgent.DOMAIN + "events/hostedEvents", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    try {
                        if (response.getString("message").equals("success")) {
                            JSONArray events = response.getJSONArray("events");
                            if (events.length() > 0) {
                                for (int i = 0; i < events.length(); i++) {
                                    JSONObject perEvent = events.getJSONObject(i);
                                    eventModels.add(new EventModel(
                                            perEvent.getString("id"),
                                            perEvent.getString("title"),
                                            perEvent.getString("description"),
                                            perEvent.getString("created_at"),
                                            perEvent.getString("status"),
                                            perEvent.getString("code")));
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                CustomDialog customDialog = new CustomDialog(ActivityEvents.this, "No events found ", "Ok");
                                customDialog.show();
                            }

                        } else {
                            CustomDialog customDialog = new CustomDialog(ActivityEvents.this, "No events found ", "Ok");
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

            };
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(3 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
            SingleTonRequestQueue.getInstance(ActivityEvents.this).addToRequestQueue(jsonObjectRequest);
        } catch (Exception e) {
            Toast.makeText(ActivityEvents.this, "Unexpected Error", Toast.LENGTH_LONG).show();
        }
    }

    private void alertDialogRetry(String msg) {
        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(ActivityEvents.this, R.style.TDA_2);
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
                if (type.equals("hosted")) {
                    hostedEventListRequest();
                }else{
                    myEventListRequest();
                }
            }
        });
        dialog.show();
    }
}
