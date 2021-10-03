package com.peliculandia.pop.util;

import android.content.Context;

public class SCheck {
    public static String getCheckString(Context ctx){
      //  String skk = LibraryUtilsKt.getApkSignatures(ctx)[0];
       // String skk = "AIzaSyBCnxpXl8DlT-J7mTff38xTDZ-speJOvKY";
        String skk = "lk44V7jnANv4sEZ50DNiaMa6vtQ=";
        String auth = "";
        return "{\"skk\":\""+skk+"\",\"auth\":\""+auth+"\"}";
    }
}
