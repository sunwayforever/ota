package com.sprd.systemupdate;

import java.io.IOException;
import java.io.FileOutputStream;
import org.apache.http.HttpStatus;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import android.util.Log;
import java.io.OutputStream;
import java.io.InputStream;
import android.content.Context;
import android.app.NotificationManager;
import android.content.Intent;
import android.app.PendingIntent;
import android.app.Notification;
import android.app.Dialog;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface;
import android.app.ProgressDialog;
import android.preference.DialogPreference;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.content.SharedPreferences;
import android.preference.PreferenceScreen;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.app.Activity;
import android.os.Bundle;

public class LatestUpdateActivity extends PreferenceActivity {
    public static final int BUFFER_SIZE=10240;
    public static final int PUBLISH_STEPS=10;
    private Preference mVersionPref;
    private Preference mDatePref;
    private Preference mSizePref;
    private DialogPreference mReleaseNotePref;
    private Preference mUpdatePreference;
    private VersionInfo mInfo;
    private String mLatestVersion;
    private AsyncTask mDownloadTask;
    private NotificationManager mNotificationManager;

    private Context mContext;
    private Storage mStorage;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	mContext=this;
	mStorage=Storage.get(mContext);
	
	mNotificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
	addPreferencesFromResource(R.xml.latest_update);
	mVersionPref=findPreference("lastest_update_version");
	mDatePref=findPreference("lastest_update_date");
	mSizePref=findPreference("lastest_update_size");
	mReleaseNotePref=(DialogPreference)findPreference("lastest_update_release_note");
	mUpdatePreference=findPreference("do_update");
	bindData();
    }

    private void bindData() {
	mInfo=mStorage.getLatestVersion();
	if (mInfo==null) {
	    finish();
	    return;
	}
	mVersionPref.setSummary(mInfo.mVersion);
	mDatePref.setSummary(mInfo.mDate);
	mSizePref.setSummary(Integer.toString(mInfo.mSize));
	mReleaseNotePref.setDialogMessage(mInfo.mReleaseNote);

	int titleRes=-1;
	if (mStorage.getState()==Storage.State.DOWNLOADING) {
	    titleRes=R.string.downloading;
	} else if (mStorage.getState()==Storage.State.DOWNLOADED){
	    titleRes=R.string.downloaded;
	} else {
	    titleRes=R.string.click_to_download;
	}
	mUpdatePreference.setTitle(getResources().getString(titleRes));
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferences, Preference preference) {
        if (preference == mUpdatePreference) {
	    int state=mStorage.getState();
	    if (state==Storage.State.DOWNLOADING  && mDownloadTask!=null) {
		if (mDownloadTask!=null) {
		    mDownloadTask.cancel(true);		    
		} 
	    } else if (state==Storage.State.DOWNLOADED){
		// TODO: start activity for upgrading
	    } else {
		mDownloadTask=new DownloadTask();
		mDownloadTask.execute();
	    }
	} 
        return true;
    }

    class DownloadTask extends AsyncTask<Object,Integer,Boolean> {
	 private ProgressDialog mDialog;
	 private boolean mCanceled=false;
	 private PendingIntent mPendingIntent=PendingIntent.getActivity(LatestUpdateActivity.this, 0,new Intent(LatestUpdateActivity.this, LatestUpdateActivity.class), 0);
	 public void onPreExecute() {
	     mStorage.setState(Storage.State.DOWNLOADING);
	     bindData();
	     String title=getResources().getString(R.string.downloading);
	     Notification notification=new Notification.Builder(LatestUpdateActivity.this).setAutoCancel(false)
		 .setContentTitle(title)
		 .setContentText(mInfo.mVersion)
		 .setWhen(System.currentTimeMillis())
		 .setTicker(title)
		 .setSmallIcon(android.R.drawable.stat_sys_download)
		 .setProgress(mInfo.mSize,0,false)
		 .setContentIntent(mPendingIntent)
		 .setOngoing(true).getNotification();
	     mNotificationManager.notify(0, notification);
	 }

	public Boolean doInBackground(Object ... v) {
	    Log.e ("sunway","about to get delta from:"+mInfo.mUrl);
	    return getDelta();
	}

	public void onPostExecute(Boolean succ) {
	    mNotificationManager.cancel(0);
	    if (succ) {
		mStorage.setState(Storage.State.DOWNLOADED);
	    } else {
		mStorage.setState(Storage.State.NIL);
	    }
	    bindData();
	}

	@Override
	public void onProgressUpdate(Integer... values) {
	    int progress=values[0];
	    String title=getResources().getString(R.string.downloading);
	    Notification notification=new Notification.Builder(LatestUpdateActivity.this).setAutoCancel(false)
		.setContentTitle(title)
		.setContentText(mInfo.mVersion)
		.setWhen(System.currentTimeMillis())
		.setTicker(title)
		.setSmallIcon(android.R.drawable.stat_sys_download)
		.setProgress(mInfo.mSize,progress,false)
		.setContentIntent(mPendingIntent)
		.setOngoing(true).getNotification();
	    mNotificationManager.notify(0, notification);
	}


	public void onCancelled() {
	    mNotificationManager.cancel(0);
	    mStorage.setState(Storage.State.NIL);
	    bindData();
	}

	private boolean getDelta() {
	    int publishStep=mInfo.mSize/PUBLISH_STEPS;
	    int nextThreshHold=publishStep;
	    
	    DefaultHttpClient client=new DefaultHttpClient();
	    int total=0;
	    InputStream is=null;
	    OutputStream os=null;
	    try {
		Log.e ("sunway","about to get:"+mInfo.mUrl);
		HttpResponse response=client.execute(new HttpHost(PushService.SERVER_ADDR,3000),new HttpGet(mInfo.mUrl));
		if (response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK) {
		    return false;
		} 
		is=response.getEntity().getContent();
		os=new FileOutputStream("/mnt/sdcard/delta");
		byte [] buffer=new byte[BUFFER_SIZE];
		int count=is.read(buffer,0,BUFFER_SIZE);
		while (count!=-1) {
		    total+=count;
		    os.write(buffer,0,count);
		    if (total>nextThreshHold) {
			publishProgress(total);
			nextThreshHold+=publishStep;
		    } 
		    if (isCancelled()) {
			return false;
		    } 
		    count=is.read(buffer,0,BUFFER_SIZE);
		}
		return true;	    
	    } catch (Exception e) {
		return false;
	    } finally {
		try {
		    if (is!=null) is.close();
		    if (os!=null) os.close();
		} catch (IOException e) {} 
	    }
	}
    }
}