package com.sprd.systemupdate;

import java.util.Random;
import android.content.Context;
import android.app.NotificationManager;
import android.content.Intent;
import android.app.PendingIntent;
import android.app.Notification;
import org.apache.http.client.HttpClient;
import android.net.http.AndroidHttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import java.util.List;
import java.io.IOException;
import org.apache.http.HttpStatus;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import java.util.UUID;
import android.content.SharedPreferences;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.content.Intent;
import android.os.IBinder;
import android.os.Handler;
import android.os.HandlerThread;
import android.app.Service;
public class PushService extends Service {
    public static final String SERVER_ADDR="192.168.0.100";
    public static final String KEY_MODE="mode";
    public static final int MODE_NETWORK_DOWN=0;
    public static final int MODE_NETWORK_UP=1;

    public static final int TIME_SLICE=2000;
    public static final int MAX_RETRY_COUNT=12;
    private int mRetryCount=0;
    
    private HandlerThread mThread;
    private Handler mHandler;

    private XMPPConnection mConnection;

    private Context mContext;
    private Storage mStorage;

    @Override
    public IBinder onBind(Intent intent) {
	return null;
    }

    private boolean tryRegister() {
	Log.e ("sunway","network is up");
	boolean succ=registerIfNeeded();
	if (!succ) {
	    Log.e ("sunway","cellular registration failed");
	    return false;
	}
	succ=startXmppConnection();
	if (!succ) {
	    Log.e ("sunway","xmpp registration failed");
	    return false;
	}
	Log.e ("sunway","registration succeed");
	return true;
    }
    
    public void onCreate() {
	mContext=this;
	mStorage=Storage.get(mContext);
	HandlerThread thread=new HandlerThread("push_receiver");
	thread.start();
	mHandler=new Handler(thread.getLooper()) {
		@Override
		public void handleMessage(android.os.Message msg) {
		    if (msg.what==MODE_NETWORK_DOWN) {
			Log.e ("sunway","network is down");
			removeMessages(MODE_NETWORK_UP);
			stopXmppConnection();
		    } else if (msg.what==MODE_NETWORK_UP) {
			removeMessages(MODE_NETWORK_UP);
			if (!tryRegister() && mRetryCount<MAX_RETRY_COUNT) {
			    // reschedule a retry
			    android.os.Message retryMsg=new android.os.Message();
			    retryMsg.what=MODE_NETWORK_UP;
			    int wakeUpTime=new Random(System.currentTimeMillis()).nextInt(2<<(++mRetryCount))*TIME_SLICE;
			    Log.e ("sunway","registration: retry in "+wakeUpTime);
			    sendMessageDelayed(retryMsg,wakeUpTime);
			} else {
			    mRetryCount=0;
			}
		    } else {
			Log.e ("sunway","unknow mode:"+msg.what);
		    }
		}
	    };
    }

    private void stopXmppConnection() {
	
    }
    
    private boolean startXmppConnection() {
	final ConnectionConfiguration connectionConfig = new ConnectionConfiguration(
	    SERVER_ADDR, 5222, "ota");
	String jid=getDeviceId();
	
	mConnection = new XMPPConnection(connectionConfig);
	try {
	    mConnection.connect();
	    mConnection.login(jid,jid);
	    Presence presence=new Presence(Presence.Type.available);
	    mConnection.sendPacket(presence);
	    mConnection.addPacketListener(
		new PacketListener() {
		    public void processPacket(Packet pkt) {
			Message msg=(Message)pkt;
			Log.e ("sunway","got push:"+msg.getBody());
			String json=msg.getBody();
			VersionInfo info=VersionInfo.fromJson(json);
			if (info!=null) {
			    VersionInfo olderVersion=mStorage.getLatestVersion();
			    if (info.equals(olderVersion)) {
				return;
			    } 
			    
			    mStorage.setLatestVersion(json);
			    mStorage.setState(Storage.State.NIL);

			    PendingIntent pendingIntent=PendingIntent.getActivity(
				PushService.this,
				0,
				new Intent(PushService.this, LatestUpdateActivity.class), 0);

			    String title=getResources().getString(R.string.latest_update);
			    Notification notification=new Notification.Builder(PushService.this).setAutoCancel(true)
				.setContentTitle(title)
				.setContentText(info.mVersion)
				.setWhen(System.currentTimeMillis())
				.setTicker(title)
				.setSmallIcon(android.R.drawable.stat_sys_download)
				.setContentIntent(pendingIntent)
				.setOngoing(true).getNotification();
			    NotificationManager nm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			    nm.notify(1, notification);
			} 
		    }
		}, new PacketTypeFilter(Message.class)
		);
	    return true;
	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	} 

    }
    
    private String getDeviceId() {
	String storedId=mStorage.getDeviceId();
	if (storedId!=null) {
	    return storedId;
	} 
	TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
	String deviceId=tm.getDeviceId();
	if (deviceId==null) {
	    deviceId=UUID.randomUUID().toString();
	}
	mStorage.setDeviceId(deviceId);
	return deviceId;
    }
    
    private boolean registerIfNeeded() {

	String deviceId=getDeviceId();
	Log.e ("sunway","getDeviceId:"+deviceId);

	String version="sprdroid";
	String product="U788";

	List<NameValuePair> pairs=new ArrayList<NameValuePair>();
	pairs.add(new BasicNameValuePair("version",version));
	pairs.add(new BasicNameValuePair("product",product));
	pairs.add(new BasicNameValuePair("jid",deviceId));
	
	
	DefaultHttpClient client=new DefaultHttpClient();
	try {
	    HttpPost post=new HttpPost("/cellulars/register");
	    post.setEntity(new UrlEncodedFormEntity(pairs));
	    HttpResponse response=client.execute(new HttpHost(SERVER_ADDR,3000),post);
	    return response.getStatusLine().getStatusCode()==HttpStatus.SC_OK;
	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	} 
    }
    
    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
	if (intent==null) {
	    return START_NOT_STICKY;
	}

	int mode=intent.getIntExtra(KEY_MODE,-1);
	mHandler.obtainMessage(mode).sendToTarget();
	
	return START_STICKY;
    }

}
//  LocalWords:  xmpp
