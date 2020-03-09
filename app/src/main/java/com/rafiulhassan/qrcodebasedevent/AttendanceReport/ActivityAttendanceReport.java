package com.rafiulhassan.qrcodebasedevent.AttendanceReport;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class ActivityAttendanceReport extends AppCompatActivity {

    private TextView textView_attendance_report_of, textView_attendance_report_total_found;
    private ListView listView_attendance_report;
    private String subscriberName = "";
    private String eventId = "";
    private String subscriberId = "";
    private ListViewCustomAdapterAttendanceReport adapter;
    private ArrayList<ReportModel> reports = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_report);

        subscriberName = getIntent().getStringExtra("subs_name");
        eventId = getIntent().getStringExtra("event_id");
        subscriberId = getIntent().getStringExtra("subs_id");
        initView();

        configureListView();
    }

    private void configureListView() {
        adapter = new ListViewCustomAdapterAttendanceReport(ActivityAttendanceReport.this, reports);
        listView_attendance_report.setAdapter(adapter);

        reportDataRequest();
    }

    private void reportDataRequest() {
        final CustomDialog customDialog = new CustomDialog(ActivityAttendanceReport.this);
        customDialog.show();
        final UserPreference userPreference = new UserPreference(ActivityAttendanceReport.this);
        final String token;
        if (userPreference.getUser() != null) {
            token = userPreference.getUser().getuToken();
        } else token = "";

        try {
            final JSONObject param = new JSONObject();
            param.put("event_id", eventId);
            param.put("user_id", subscriberId);


            final String requestBody = param.toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    ErrorFilterAgent.DOMAIN + "attendance/report", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    try {
                        if (response.getString("message").equals("success")) {
                            JSONArray dateArray = response.getJSONArray("attendance");
                            if (dateArray.length() > 0) {
                                textView_attendance_report_total_found.setText(String.valueOf(dateArray.length()));
                                for (int i = 0; i < dateArray.length(); i++) {
                                    JSONObject perData = dateArray.getJSONObject(i);
                                    reports.add(new ReportModel(perData.getString("id"),
                                            perData.getString("datetime")));
                                }
                                adapter.notifyDataSetChanged();
                            }else{
                                alertDialogToFinish("No Attendance History Found For This Subscriber!");
                            }
                        } else {
                            CustomDialog customDialog = new CustomDialog(ActivityAttendanceReport.this, "Fetching Data Failed failed", "Ok");
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
            SingleTonRequestQueue.getInstance(ActivityAttendanceReport.this).addToRequestQueue(jsonObjectRequest);
        } catch (Exception e) {
            Toast.makeText(ActivityAttendanceReport.this, "Unexpected Error", Toast.LENGTH_LONG).show();
        }
    }

    private void initView() {
        textView_attendance_report_of = (TextView) findViewById(R.id.textView_attendance_report_of);
        textView_attendance_report_total_found = (TextView) findViewById(R.id.textView_attendance_report_total_found);
        listView_attendance_report = (ListView) findViewById(R.id.listView_attendance_report);
        textView_attendance_report_of.setText("Attendance Report Of " + subscriberName);

    }

    private void alertDialogRetry(String msg) {
        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(ActivityAttendanceReport.this, R.style.TDA_2);
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
                reportDataRequest();
            }
        });

        dialog.show();
    }

    private void alertDialogToFinish(String msg) {
        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(ActivityAttendanceReport.this, R.style.TDA_2);
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
