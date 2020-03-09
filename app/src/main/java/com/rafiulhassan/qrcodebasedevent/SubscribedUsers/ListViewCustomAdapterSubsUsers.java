package com.rafiulhassan.qrcodebasedevent.SubscribedUsers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rafiulhassan.qrcodebasedevent.R;

import java.util.ArrayList;

public class ListViewCustomAdapterSubsUsers extends BaseAdapter {
    private Context context;
    private ArrayList<SubsUserModel> subsUserModels = new ArrayList<>();

    public ListViewCustomAdapterSubsUsers(Context context, ArrayList<SubsUserModel> subsUserModels) {
        this.context = context;
        this.subsUserModels = subsUserModels;
    }

    @Override
    public int getCount() {
        return subsUserModels.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.listview_res_subs_user, null);

        TextView textView_subs_user_name = (TextView) convertView.findViewById(R.id.textView_subs_user_name);
        TextView textView_subs_user_email = (TextView) convertView.findViewById(R.id.textView_subs_user_email);
        TextView textView_subs_user_accepted = (TextView) convertView.findViewById(R.id.textView_subs_user_accepted);


        textView_subs_user_name.setText(subsUserModels.get(position).getUserName());
        textView_subs_user_email.setText("Email: " + subsUserModels.get(position).getUserEmail());
        String acceptedValue;
        if (subsUserModels.get(position).getUserAccepted().equals("true")) {
            acceptedValue = "Yes";
        } else {
            acceptedValue = "No";
        }
        textView_subs_user_accepted.setText("Accepted: " + acceptedValue);

        return convertView;
    }
}
