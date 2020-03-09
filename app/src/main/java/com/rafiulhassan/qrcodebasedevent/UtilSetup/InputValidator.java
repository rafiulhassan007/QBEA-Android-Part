package com.rafiulhassan.qrcodebasedevent.UtilSetup;

import android.text.TextUtils;

public class InputValidator {

    private String textRepresent = "";

    public boolean checkEmpty(String s) {
        if (TextUtils.isEmpty(s)) {
            setTextRepresent(" Please, fill all information correctly");
            return true;
        } else return false;
    }

    public boolean checkEmailIfFalse(String s) {
        if (s.contains("@") && s.contains(".")) {
            return false;
        } else {
            setTextRepresent(" Please, enter a valid email address");
            return true;
        }
    }

    public boolean checkPasswordLengthIfFalse(String s, int length) {
        if (s.length() >= length) {
            return false;
        } else {
            setTextRepresent(" Minimum password length "+length+" characters");
            return true;
        }
    }

    public String getTextRepresent() {
        return textRepresent;
    }

    public boolean compareIfFalse(String first,String second){
        if(first.equals(second)){
            return false;
        }else{
            setTextRepresent(" Password mismatched!");
            return true;
        }
    }

    private void setTextRepresent(String textRepresent) {
        this.textRepresent = textRepresent;
    }



}
