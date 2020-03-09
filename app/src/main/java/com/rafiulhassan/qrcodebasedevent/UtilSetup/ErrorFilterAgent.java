package com.rafiulhassan.qrcodebasedevent.UtilSetup;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

public class ErrorFilterAgent {

//    public static String DOMAIN="http://192.168.137.1:3000/qbea/public/api/";
    public final static String DOMAIN="http://192.168.137.1/auth_imp/public/api/";

    public static int errorFiltering(VolleyError error) {
        int type=0;
        if (error instanceof NetworkError) {
            type=1;
        } else if (error instanceof ServerError) {
            type=2;
        } else if (error instanceof AuthFailureError) {
            type=3;
        } else if (error instanceof ParseError) {
            type=4;
        } else if (error instanceof NoConnectionError) {
            type=5;
        } else if (error instanceof TimeoutError) {
            type=6;
        }
        return type;
    }
    public static String errorMsgShow(int type){
        String message = null;
        if (type==1) {
            message = "Please Check Your Internet Connection!";
        } else if (type==2) {
            message = "Server Maintenance Running, Please try again after some time!!";
        } else if (type==3) {
            message = "Request Can Not Be Proceed!";
        } else if (type==4) {
            message = "Parsing error! Please try again after some time!!";
        } else if (type==5) {
            message = "Please Check Your Internet Connection!";
        } else if (type==6) {
            message = "Connection TimeOut! Please check your internet connection.";
        }else if (type==0) {
            message = "Please Contact With The Card Provider!";
        }
        return message;
    }
}
