package com.rafiulhassan.qrcodebasedevent.AllEvents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rafiulhassan.qrcodebasedevent.R;

import java.util.ArrayList;

public class ListViewCustomAdapterMyEvents extends BaseAdapter{
    private Context context;
    private ArrayList<EventModel> eventModels = new ArrayList<>();


    public ListViewCustomAdapterMyEvents(Context context, ArrayList<EventModel> eventModels) {
        this.context = context;
        this.eventModels = eventModels;

    }

    @Override
    public int getCount() {
        return eventModels.size();
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
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.listview_res_my_events,null);

        TextView textView_my_event_title=(TextView)convertView.findViewById(R.id.textView_my_event_title) ;
        TextView textView_my_event_desc=(TextView)convertView.findViewById(R.id.textView_my_event_desc) ;
        TextView textView_my_event_code=(TextView)convertView.findViewById(R.id.textView_my_event_code) ;
        TextView textView_my_created_on=(TextView)convertView.findViewById(R.id.textView_my_created_on) ;


        textView_my_event_title.setText(eventModels.get(position).getTitle());
        textView_my_event_desc.setText(eventModels.get(position).getDesc());
        textView_my_created_on.setText("Status (Currently): "+eventModels.get(position).getType());

        if(eventModels.get(position).getCode().equals("true")) {
            textView_my_event_code.setText("Approval: " + "Granted");
        }else{
            textView_my_event_code.setText("Approval: " + "Pending");
        }

        return convertView;
    }
}
