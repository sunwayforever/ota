package com.sprd.systemupdate;

import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class VersionInfo {
    String mVersion;
    String mDate;
    int mSize;
    String mUrl;
    String mReleaseNote;

    public static VersionInfo fromJson(String json) {
	if (TextUtils.isEmpty(json)) {
	    return null;
	} 
	try {
	    JSONObject jsonObj=new JSONObject(json);
	    VersionInfo ret=new VersionInfo();
	    ret.mVersion=jsonObj.getJSONObject("b_version").getString("version");
	    ret.mDate=jsonObj.getJSONObject("b_version").getString("date");
	    ret.mSize=jsonObj.getInt("size");
	    ret.mUrl="/delta/"+jsonObj.getString("id")+"/get";
	    ret.mReleaseNote=jsonObj.getJSONObject("b_version").getString("release_note");
	    return ret;	    
	} catch (JSONException e) {
	    e.printStackTrace();
	    return null;
	} 
    }

    public boolean equals(VersionInfo info) {
	return
	    info!=null
	    && mVersion.equals(info.mVersion)
	    && mDate.equals(info.mDate)
	    && mSize==info.mSize
	    && mUrl.equals(info.mUrl)
	    && mReleaseNote.equals(info.mReleaseNote);
    }
}