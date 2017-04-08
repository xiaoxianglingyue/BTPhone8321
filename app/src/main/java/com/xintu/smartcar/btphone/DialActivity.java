package com.xintu.smartcar.btphone;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import com.xintu.smartcar.btphone.application.BTPhoneApplication;
import com.xintu.smartcar.btphone.biz.BizMain;
import com.xintu.smartcar.btphone.db.dao.ContactsDao;
import com.xintu.smartcar.btphone.receiver.PhoneReceiver;
import com.xintu.smartcar.btphone.service.ConnService;
import com.xintu.smartcar.btphone.utils.ImportAssetsUtil;
import com.xintu.smartcar.btphone.utils.LEIDAUtil;
import com.xintu.smartcar.btphone.utils.SharedPreferencesUtil;

public class DialActivity extends Activity {
	private final static String TAG = "BluetoothPhone";
	private int m_iCounter = 0;
	private Thread m_thread = null;
	private boolean m_isActive = false; //是否已接�?
	private TextView tv_calling_name,tv_calling_number, tv_calling_state;
	private Chronometer chronometer;
	private Handler mMsgHandler;
	private BizMain m_bizMain;
	private boolean m_bIncoming = false;  //来电接听的情
	private boolean isMutex=false;
	private long mlCount = 0;
	private String number="";
	private boolean incoming=false;
	private ConnService myService;
	private ServiceConnection serviceConnection;
	public static final int BTMSG_ACTIVE_CALL = 1009;   //接通电话
	public static final int BTMSG_HUNGUP_CALL = 1007;   //挂断电话
	public static final int TIMER_MSG = 2000;			//计时器消�?
	private boolean isConn=false;
	public static Handler mainhandler;
	//private ContactsDao m_contact;
	ContactsDao m_contactsDao;
	String strNumber ="";
	String strName="";
	private ImportAssetsUtil importAssetsUtil;
	private Thread thread=null;

	public Handler dialHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==1006){
				BTPhoneApplication.getInstance().flag=false;
				BTPhoneApplication.getInstance().isflag=true;
				m_bizMain.reqTerminateCall();
				if(null!=mainhandler){
					Message message=mainhandler.obtainMessage();
					message.what=10020;
					message.obj=true;
					mainhandler.sendMessage(message);
					tv_calling_state.setText("正在为您挂断...");
					BTPhoneApplication.getInstance().isTopComing=false;
				}
				LEIDAUtil.setAudioFileEnabled(false);
				finish();
			}

		}
	};


	private Handler createMsgHandler() {
		Handler handler=new Handler(){
			@SuppressWarnings("static-access")
			@Override
			public void handleMessage(final Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case BTMSG_ACTIVE_CALL: //接听来电


					if(isMutex==false){
						isMutex=true;
						Log.e(TAG, "case BTMSG_ACTIVE_CALL, LEIDAUtil.setFlashlightEnabled(true);");
						BTPhoneApplication.getInstance().flag=true;
						BTPhoneApplication.getInstance().isActionCall=true;
						//m_bizMain.reqSendSpeakerVolume("0f");
						//m_bizMain.reqSendMICVolume("09");
						m_iCounter = 0;
						tv_calling_state.setText("已接通");
						m_isActive = true;
						SharedPreferencesUtil.saveStringData(BTPhoneApplication.getInstance(), "spnumber", number);

						chronometer.setBase(SystemClock.elapsedRealtime());
						if(BTPhoneApplication.getInstance().isTopComing==false){
							chronometer.start();

							thread=new Thread() {
								public void run() {
									while(m_isActive){
										try {
											Thread.sleep(1000);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										mlCount ++;  
										int totalSec = 0;  
										totalSec = (int)(mlCount);  
										final int min = (totalSec / 60);  
										final int sec = (totalSec % 60); 
										if(null!=mainhandler){
											Message message=mainhandler.obtainMessage();
											message.what=10000;
											message.obj=String.format("%1$02d:%2$02d", min, sec);
											mainhandler.sendMessage(message);
										}
									}
								}
							};thread.start();
						}
					}
					break;
				case BTMSG_HUNGUP_CALL:
					if(null!=thread)
						thread.interrupted();
					m_isActive=false;
					BTPhoneApplication.getInstance().isActionCall=false;
					BTPhoneApplication.getInstance().flag=false;
					BTPhoneApplication.getInstance().isflag=true;
					LEIDAUtil.setAudioFileEnabled(false);
					isConn=true;
					BTPhoneApplication.getInstance().isflag=true;
					if(null!=mainhandler){
						Message message=mainhandler.obtainMessage();
						message.what=10019;
						mainhandler.sendMessage(message);
						//LEIDAUtil.setFlashlightEnabled(false);
						LEIDAUtil.setAudioFileEnabled(false);
						Intent sendbroad=new Intent();
						sendbroad.setAction("com.xintu.btphone.OutgoingCallEnd");
						sendBroadcast(sendbroad);
						BTPhoneApplication.getInstance().m_isDailByMirror = false;
						BTPhoneApplication.getInstance().isTopComing=false;
						finish();
					}
					break;
				}
			}
		};	
		return handler;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dial);
		importAssetsUtil =new ImportAssetsUtil(this);
		m_contactsDao= new ContactsDao(DialActivity.this);
		tv_calling_name = (TextView) findViewById(R.id.tv_calling_name);
		tv_calling_number = (TextView) findViewById(R.id.tv_calling_number);
		tv_calling_state = (TextView) findViewById(R.id.dialing);    
		BTPhoneApplication.getInstance().dialActivity=DialActivity.this;
		chronometer = (Chronometer)findViewById(R.id.chronometer);
		Intent intent = getIntent();
		number = intent.getStringExtra("number");
		incoming=intent.getBooleanExtra("incoming", false);

		String in_name=intent.getStringExtra("in_name");
		m_bIncoming = intent.getBooleanExtra("incoming", false);
		mMsgHandler = createMsgHandler();

		if(null==number&&"".equals(number)){
			//Toast.makeText(DialActivity.this, number+"A", Toast.LENGTH_LONG).show();
			showToast(number+"A");
			finish();
		}
		if(incoming==true){
			if(!"".equals(in_name)&&null!=in_name){
				tv_calling_name.setText(in_name.trim());
				tv_calling_number.setText(number.trim());
			}else{
				tv_calling_name.setText("新来电");
				tv_calling_number.setText(number.trim());
			}
		}else{
			if(!"".equals(importAssetsUtil.importGeneralContact(number,true))){
				String[] strSplit = importAssetsUtil.importGeneralContact(number,true).split(",");
				String strname=strSplit[0];
				tv_calling_name.setText(strname);
				tv_calling_number.setText(number.trim());
			}else{
				if(null!=number&&!"".equals(number)){
					if(number.length()>=3){
						if(number.substring(0, 1).equals("0")){
							strName = m_contactsDao.findName(number);//查询到人�?
							if(null!=strName&&!"".equals(strName)){
								tv_calling_name.setText(strName);
								tv_calling_number.setText(number.trim());
							}else{
								String strNumber1=number.substring(3, number.length());
								strName = m_contactsDao.findName(strNumber1);//查询到人�?
								if(null!=strName&&!"".equals(strName)){
									tv_calling_name.setText(strName);
									tv_calling_number.setText(strNumber1);
								}else{
									String strNumber2=number.substring(4, number.length());
									strName = m_contactsDao.findName(strNumber2);//查询到人�?
									if(null!=strName&&!"".equals(strName)){
										tv_calling_name.setText(strName);
										tv_calling_number.setText(strNumber2);
									}else{
										tv_calling_name.setText("未知号码");
										tv_calling_number.setText(number.trim());
									}
								}
							}

						}else{
							String strN=number.substring(2, number.length());
							strName = m_contactsDao.findName(strN);
							if(null!=strName&&!"".equals(strName)){
								tv_calling_name.setText(strName);
								tv_calling_number.setText(number.trim());
							}else{
								strName = m_contactsDao.findName(number);
								if(null!=strName&&!"".equals(strName)){
									tv_calling_name.setText(strName);
									tv_calling_number.setText(number.trim());
								}else{
									String strNumber1="0086"+number;
									strName = m_contactsDao.findName(strNumber1);
									if(null!=strName&&!"".equals(strName)){
										tv_calling_name.setText(strName);	
										tv_calling_number.setText(strNumber1);
									}else{
										String strNumber2="86"+number;
										strName = m_contactsDao.findName(strNumber2);
										if(null!=strName&&!"".equals(strName)){
											tv_calling_name.setText(strName);	
											tv_calling_number.setText(strNumber2);
										}else{
											String strNumber3="00"+number;
											strName = m_contactsDao.findName(strNumber3);
											if(null!=strName&&!"".equals(strName)){
												tv_calling_name.setText(strName);	
												tv_calling_number.setText(strNumber3);
											}else{
												tv_calling_name.setText("未知号码");
												tv_calling_number.setText(number.trim());
											}
										}
									}
								}
							}
						}
					}else{
						tv_calling_name.setText("未知号码");
						tv_calling_number.setText(number.trim());
					}
				}
			}
		}
		//Log.e(TAG, "end of dial activity create~~~~");


	}

	public void hangup(View v) {//挂断电话
		m_isActive=false;
		m_bizMain.reqTerminateCall();
		BTPhoneApplication.getInstance().isActionCall=false;
		BTPhoneApplication.getInstance().isTopComing=false;
		BTPhoneApplication.getInstance().flag=false;
		BTPhoneApplication.getInstance().ishungup=true;
		BTPhoneApplication.getInstance().ismobile=false;
		BTPhoneApplication.getInstance().isComing=false;
		BTPhoneApplication.getInstance().isbackcall=false;
		BTPhoneApplication.getInstance().isflag=true;
		tv_calling_state.setText("正在为您挂断...");
		Timer timer=new Timer();
		TimerTask task=new TimerTask(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				finish();
				LEIDAUtil.setAudioFileEnabled(false);
			}
		};
		timer.schedule(task, 2000);
	}


	public void click(View v){
		Intent intent=new Intent(DialActivity.this,MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	@Override
	protected void onResume() {

		BTPhoneApplication.getInstance().m_isDialOpened = true;
		PhoneReceiver.dialHandler=dialHandler;
		// TODO Auto-generated method stub
		super.onResume();
		if(isConn==true){
			tv_calling_state.setText("正在为您挂断...");
			isConn=false;
		}
		BTPhoneApplication.getInstance().flag=true;
		BTPhoneApplication.getInstance().iscall=true;
		serviceConnection = new ServiceConnection() {
			/** 获取服务对象时的操作 */
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				myService = ((ConnService.ServiceBinder) service).getService();
				//myService.notifyForeGround(); //通知进入前台工作状�?
				m_bizMain = myService.m_bizMain;
				myService.m_listener.handlerDial = mMsgHandler;
			}

			/** 无法获取到服务对象时的操�?*/
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				myService = null;
				m_bizMain = null;
			}
		};			
		//bind service
		Intent intent = new Intent(DialActivity.this, ConnService.class);
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);    
		if(incoming == true){

		}
		else{
			//发�?广播
			Intent sendbroad = new Intent();
			sendbroad.setAction("com.xintu.btphone.willcall");
			sendBroadcast(sendbroad);
		}
		if (m_bIncoming) {
			tv_calling_state.setText("已接通");
			m_isActive = true;
		}


		if( BTPhoneApplication.getInstance().isTopComing==true){	
			new Thread() {
				public void run() {
					while(m_isActive){
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mlCount ++;  
						int totalSec = 0;  
						totalSec = (int)(mlCount);  
						final int min = (totalSec / 60);  
						final int sec = (totalSec % 60); 
						if(null!=mainhandler){
							Message message=mainhandler.obtainMessage();
							message.what=10000;
							message.obj=String.format("%1$02d:%2$02d", min, sec);
							mainhandler.sendMessage(message);
						}
					}
				}
			}.start();
			chronometer.start();
		}
	}


	@Override
	protected void onPause() {
		super.onPause();
		//unbind service
		unbindService(serviceConnection);
		Log.e(TAG, "end of ONPAUSE of dial activity ~~~~");
		//BTPhoneApplication.getInstance().isTop=false;
	}
	@SuppressWarnings("static-access")
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();	
		BTPhoneApplication.getInstance().m_isDialOpened = false;
		BTPhoneApplication.getInstance().ismobile=false;
		BTPhoneApplication.getInstance().isBTTop=true;
		if(null!=thread)
			thread.interrupted();
		m_iCounter = 0;
		mlCount=0;
		//Log.e(TAG, "end of onDestroy of dial activity ~~~~");
		// 将计时器清零  
		chronometer.setBase(SystemClock.elapsedRealtime());  
	}

	public int screen(){
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int widthPixels= dm.widthPixels;
		//int heightPixels= dm.heightPixels;
		return widthPixels;
	}

	public void showToast(String str){
		if(1280==screen()){
			Toast toast=Toast.makeText(DialActivity.this, str, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER|Gravity.BOTTOM,280, 50);
			toast.show();
		}else{
			Toast.makeText(DialActivity.this, str, Toast.LENGTH_LONG).show();
		}
	}
}
