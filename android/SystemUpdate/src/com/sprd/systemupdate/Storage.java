package com.sprd.systemupdate;

import android.content.SharedPreferences;
import android.content.Context;
public class Storage {
    interface State {
	int NIL = -1;
	int DOWNLOADING = 0;
	int DOWNLOADED = 1;

    }
    
    private static Storage sPreference;
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private Storage(Context context) {
	mContext=context;
	mSharedPreferences=mContext.getSharedPreferences("pref",Context.MODE_PRIVATE);
    }
    synchronized public static Storage get(Context context) {
	if (sPreference==null) {
	    sPreference=new Storage(context.getApplicationContext());
	}
	return sPreference;
    }

    public String getDeviceId() {
	return mSharedPreferences.getString("device_id",null);
    }

    public void setDeviceId(String deviceId) {
	mSharedPreferences.edit().putString("device_id",deviceId).commit();
    }

    public VersionInfo getLatestVersion () {
	String json=mSharedPreferences.getString("latest_version",null);
	return VersionInfo.fromJson(json);
    }

    public void setLatestVersion(String json) {
	mSharedPreferences.edit().putString("latest_version",json).commit();
    }

    public int getState() {
	return mSharedPreferences.getInt("state",State.NIL);
    }

    public void setState(int state) {
	mSharedPreferences.edit().putInt("state",state).commit();	
    }

}