package com.rafiulhassan.qrcodebasedevent.Core;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;


public class UserPreference {
    private SharedPreferences sharedPreferences;
    private Context context;

    public UserPreference(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("USERDATA",Context.MODE_PRIVATE);
    }

    public void toSave(User user){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Gson gson=new Gson();
        String json=gson.toJson(user);
        editor.putString("user",json);
        editor.commit();
    }

    public User getUser(){
        Gson gson=new Gson();
        String json=sharedPreferences.getString("user","empty");
        if(json.equals("empty")){
            return null;
        }else {
            User user = gson.fromJson(json, User.class);
            return user;
        }
    }

    public void logOut(){
        context.getSharedPreferences("USERDATA",Context.MODE_PRIVATE).edit().clear().commit();
    }
}