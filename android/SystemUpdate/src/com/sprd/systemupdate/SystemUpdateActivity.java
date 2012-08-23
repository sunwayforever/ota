package com.sprd.systemupdate;

import android.content.Context;
import android.util.Log;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.content.Intent;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.content.SharedPreferences;
import android.preference.PreferenceScreen;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.app.Activity;
import android.os.Bundle;

public class SystemUpdateActivity extends PreferenceActivity {
    private Preference mCheckUpdatePreference;
    private Preference mLatestUpdatePreference;

    public static final int CHECK_UPDATE_HAS_UPDATE=0;
    public static final int CHECK_UPDATE_NO_UPDATE=2;
    public static final int CHECK_UPDATE_FAILED=-1;

    private Context mContext;
    private Storage mStorage;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	mContext=this;
	mStorage=Storage.get(mContext);

	PreferenceScreen screen=getPreferenceManager().createPreferenceScreen(this);
	setPreferenceScreen(screen);
	
	mCheckUpdatePreference=new Preference(this);
	mCheckUpdatePreference.setOrder(1);
	mLatestUpdatePreference=new Preference(this);
	mLatestUpdatePreference.setOrder(2);
    }

    @Override
    public void onStart() {
	super.onStart();
	showCheckUpdate();
	showLatestUpdate();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferences, Preference preference) {
        if (preference == mCheckUpdatePreference) {
	    new CheckUpdateTask().execute();
        } else if (preference == mLatestUpdatePreference) {
	    startActivity(new Intent(SystemUpdateActivity.this,LatestUpdateActivity.class));
	} 
        return true;
    }

    class CheckUpdateTask extends AsyncTask<Void,Integer,Integer> {
	public void onPreExecute() {
	    mCheckUpdatePreference.setSummary(R.string.checking_update);
	    mCheckUpdatePreference.setEnabled(false);
	    getPreferenceScreen().removePreference(mLatestUpdatePreference);
	    
	}
	public Integer doInBackground(Void ... v) {
	    String jid=mStorage.getDeviceId();
	    if (jid==null) {
		mStorage.setLatestVersion(null);
		return CHECK_UPDATE_NO_UPDATE;
	    }
	    String json="";
	    try {
		DefaultHttpClient client=new DefaultHttpClient();
		HttpGet get=new HttpGet("/cellulars/query_deltum/"+jid);
		HttpResponse response=client.execute(new HttpHost(PushService.SERVER_ADDR,3000),get);
		if (response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK) {
		    mStorage.setLatestVersion(null);
		    return CHECK_UPDATE_NO_UPDATE;
		}
		BufferedReader reader=new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		json=reader.readLine();
		Log.e ("sunway","got update:"+json);
	    } catch (Exception e) {
		e.printStackTrace();
		mStorage.setLatestVersion(null);
		return CHECK_UPDATE_NO_UPDATE;
	    }
	    VersionInfo olderVersion=mStorage.getLatestVersion();
	    VersionInfo newVersion=VersionInfo.fromJson(json);

	    if (newVersion!=null && !newVersion.equals(olderVersion)) {
		mStorage.setLatestVersion(json);
		mStorage.setState(Storage.State.NIL);
	    } 

	    return CHECK_UPDATE_HAS_UPDATE;		
	}

	public void onPostExecute(Integer i) {
	    mCheckUpdatePreference.setEnabled(true);
	    if (i==CHECK_UPDATE_FAILED) {
		mCheckUpdatePreference.setSummary(R.string.check_update_failed);
	    } else {
		mCheckUpdatePreference.setSummary(R.string.click_to_update);
	    }
	    showLatestUpdate();
	}
    }

    private void showLatestUpdate() {
	if (mStorage.getLatestVersion()!=null && mStorage.getDeviceId()!=null) {
	    mLatestUpdatePreference.setTitle(R.string.latest_update);
	    getPreferenceScreen().addPreference(mLatestUpdatePreference);
	} else {
	    getPreferenceScreen().removePreference(mLatestUpdatePreference);
	}
    }

    private void showCheckUpdate() {
	int state=mStorage.getState();
	String jid=mStorage.getDeviceId();

	mCheckUpdatePreference.setTitle(R.string.check_update);
	mCheckUpdatePreference.setSummary(R.string.click_to_update);
	getPreferenceScreen().addPreference(mCheckUpdatePreference);

	if (state==Storage.State.DOWNLOADING) {
	    mCheckUpdatePreference.setEnabled(false);
	} else {
	    mCheckUpdatePreference.setEnabled(true);
	}
    }
}
