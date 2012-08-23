package com.sprd.systemupdate;

import android.util.Log;
import android.net.NetworkInfo;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.net.ConnectivityManager;
public class PushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent outerIntent) {
	String action=outerIntent.getAction();
	Log.e ("sunway","onReceive:"+action);
	if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
	    ConnectivityManager cm=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo info=cm.getActiveNetworkInfo();
	    Log.e ("sunway","info:"+info);
	    if (info==null || (info!=null && info.getType()!=ConnectivityManager.TYPE_WIFI)) {
		Log.e ("sunway","push down");
		Intent intent=new Intent(context,PushService.class);
		intent.putExtra(PushService.KEY_MODE, PushService.MODE_NETWORK_DOWN);
		context.startService(intent);
	    } else if (info!=null && info.isConnected()) {
		Log.e ("sunway","push up");
		Intent intent=new Intent(context,PushService.class);
		intent.putExtra(PushService.KEY_MODE, PushService.MODE_NETWORK_UP);
		context.startService(intent);
	    } 
	} 
    }
}