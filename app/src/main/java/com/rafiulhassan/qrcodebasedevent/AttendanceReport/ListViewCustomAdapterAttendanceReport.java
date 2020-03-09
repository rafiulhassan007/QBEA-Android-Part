package com.rafiulhassan.qrcodebasedevent.AttendanceReport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rafiulhassan.qrcodebasedevent.R;

import java.util.ArrayList;

public class ListViewCustomAdapterAttendanceReport extends BaseAdapter{
    private Context context;
    private ArrayList<ReportModel> reports=new ArrayList<>();

    public ListViewCustomAdapterAttendanceReport(Context context, ArrayList<ReportModel> reports) {
        this.context = context;
        this.reports = reports;
    }

    @Override
    public int getCount() {
        return reports.size();
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
        convertView = inflater.inflate(R.layout.listview_res_reports,null);

        TextView textView_attendance_report_date=(TextView)convertView.findViewById(R.id.textView_attendance_report_date) ;

        textView_attendance_report_date.setText(reports.get(position).getDate());

        return convertView;
    }
}
