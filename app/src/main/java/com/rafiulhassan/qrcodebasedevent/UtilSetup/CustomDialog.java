package com.rafiulhassan.qrcodebasedevent.UtilSetup;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rafiulhassan.qrcodebasedevent.R;

public class CustomDialog {
    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    public CustomDialog(Context context){
        builder = new AlertDialog.Builder(context, R.style.TDA_2);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = layoutInflater.inflate(R.layout.alert_dialog_loading, null);

        builder.setView(mView);
        dialog = builder.create();
        dialog.setCancelable(false);
    }

    public CustomDialog(Context context, String msg,String btn){
        builder = new AlertDialog.Builder(context, R.style.TDA_2);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = layoutInflater.inflate(R.layout.alert_dialog_body_and_btn, null);
        TextView textView_alert_dialog_body=(TextView)mView.findViewById(R.id.textView_alert_dialog_body);
        Button btn_alert_dialog_btn=(Button)mView.findViewById(R.id.btn_alert_dialog_btn);

        textView_alert_dialog_body.setText(msg);
        btn_alert_dialog_btn.setText(btn);

        builder.setView(mView);
        dialog = builder.create();


        btn_alert_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
    }

    public void show(){
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }

}
