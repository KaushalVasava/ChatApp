package com.kmv.chatapp.Network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try{
           if(isConnected(context)){
               Toast.makeText(context,"Internet Connected",Toast.LENGTH_SHORT).show();
           }else {
               Toast.makeText(context,"No internet Connection",Toast.LENGTH_SHORT).show();
           }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }
    boolean isConnected(Context context){
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo!=null || networkInfo.isConnected();
        }
        catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }
}