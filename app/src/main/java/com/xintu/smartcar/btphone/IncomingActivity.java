package com.xintu.smartcar.btphone;

import com.xintu.smartcar.btphone.application.BTPhoneApplication;
import com.xintu.smartcar.btphone.biz.BizMain;
import com.xintu.smartcar.btphone.db.dao.ContactsDao;
import com.xintu.smartcar.btphone.receiver.PhoneReceiver;
import com.xintu.smartcar.btphone.service.ConnService;
import com.xintu.smartcar.btphone.utils.ImportAssetsUtil;
import com.xintu.smartcar.btphone.utils.LEIDAUtil;
import com.xintu.smartcar.btphone.utils.SharedPreferencesUtil;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;


public class IncomingActivity extends Activity {
	public static final String TAG = "BTPhone";
	private int m_iCounter = 0;
	private Thread m_thread = null;
	private boolean m_bIsWaiting = true; //正在等待接听电话

	public static final int BTMSG_INCOMING_CALL = 1011;  //当前电话
	public static final int BTMSG_ACTIVE_CALL = 1009;   //接通电话
	public static final int BTMSG_INCOMMING_CALLNAME = 10018;//显示来电号码
	protected static final int BTMSG_HUNGUP_CALL = 1007;//挂断
	//public static final int TIMER_MSG = 2000;	
	private Handler mMsgHandler;
	private BizMain m_bizMain;
	private static TextView tv_calling_name;
	private static TextView tv_calling_number;
	private ServiceConnection serviceConnection;
	private ConnService myService;
	PhoneReceiver receiver;
	String strNumber ="";
	String strName="";
	private ContactsDao m_contactsDao;
	private ImportAssetsUtil importAssetsUtil;
	String strName1="";
	String strInCNumber="";
	public  Handler inComingHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==1005){
				m_bIsWaiting = false; 
				//BTPhoneApplication.getInstance().isComing=false;
				//置handlerIncomging为空以后，就不会发过来消息过来
				BTPhoneApplication.getInstance().iscall=true;
				BTPhoneApplication.getInstance().flag=true;
				myService.m_listener.handlerIncoming = null;
				m_bizMain.reqAnswerIncommingCall();//来电(按蓝牙左边的键接听)

				String number= tv_calling_number.getText().toString().trim();
				String name = tv_calling_name.getText().toString().trim();
				if(null!=number&&!"".equals(number)){
					Intent intent=new Intent(IncomingActivity.this,DialActivity.class);
					if("".equals(strName)){
						intent.putExtra("in_name", "新来电");
					}else{
						intent.putExtra("in_name", name);
					}
					intent.putExtra("number", number);
					intent.putExtra("incoming", true); //是否由来电启动
					//缓存电话号码
					SharedPreferencesUtil.saveStringData(BTPhoneApplication.getInstance(), "spnumber", number);
					startActivity(intent);
				}
				finish(); //接听以后，当前应该也finish�?				
			}
		}
	};
	//来电界面出来以后后，如果手机上面直接挂掉
	//则应该根据是否还有BTMSG_CURRENT_CALL消息，不确定是否finish当前界面
	private Handler createMsgHandler() {
		Handler handler=new Handler(){
			@Override
			public void handleMessage(final Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case BTMSG_INCOMING_CALL:
					//m_bizMain.reqSendMICVolume("09");//33
					//第一个current 消息
					if (m_thread == null) {
						m_thread = new Thread(new CounterThread());
						m_thread.start();
						String strNumber=(String) msg.obj;
						if (strNumber != null||!"".equals(strNumber)) {
							strName1=inComingName(strNumber);
						}else{
							finish();
						}						
					}
					m_iCounter = 0;
					break;
				case BTMSG_INCOMMING_CALLNAME://来电显示
					String strIncomName=(String) msg.obj;
					tv_calling_name.setText(strIncomName);
					break;
				case BTMSG_ACTIVE_CALL: //接通来电
					BTPhoneApplication.getInstance().isActionCall=true;
					BTPhoneApplication.getInstance().isComing=false;
					BTPhoneApplication.getInstance().iscall=true;
					BTPhoneApplication.getInstance().flag=true;
					BTPhoneApplication.getInstance().isTopComing=true;
					//LEIDAUtil.setFlashlightEnabled(true);
					//如果来电时，由用户在手机端接听，则要启动dial界面
					String number = tv_calling_number.getText().toString();
					String name = tv_calling_name.getText().toString();
					if(null==BTPhoneApplication.getInstance().dialActivity){
						if(null!=number&&!"".equals(number)&&null!=name&&!"".equals(name)){
							Intent intent=new Intent(IncomingActivity.this,DialActivity.class);
							intent.putExtra("in_name", name);
							intent.putExtra("number", number);
							intent.putExtra("incoming", true); //是否由来电启�?
							SharedPreferencesUtil.saveStringData(BTPhoneApplication.getInstance(), "spnumber", number);
							//m_bizMain.reqSendSpeakerVolume("0f");//32
							//m_bizMain.reqSendMICVolume("09");//33
							startActivity(intent);
						}
					}
					finish(); //接听以后，当前应该也finish�?				
				break;
				case BTMSG_HUNGUP_CALL:
					//BTPhoneApplication.getInstance().isOutgoingCall=false;
					BTPhoneApplication.getInstance().isActionCall=false;
					BTPhoneApplication.getInstance().flag=false;
					BTPhoneApplication.getInstance().isComing=false;
					finish();
					BTPhoneApplication.getInstance().isbackcall=false;
					 BTPhoneApplication.getInstance().isTopComing=false;
					break;
				}
			}
		};	

		return handler;
	}	
	public void speech(String name,String number){
		Intent sendbroad = new Intent();
		sendbroad.setAction("com.xintu.btphone.speechhit");
		sendbroad.putExtra("speechhit", "新来电"+name+number);
		sendBroadcast(sendbroad);
	}
	private class CounterThread implements Runnable {
		public void run() {
			while(m_bIsWaiting) { // 在等待接听的状�?
				try {
					Thread.sleep(1000);
					//Message msg = Message.obtain();
					//msg.what = TIMER_MSG;
					//mMsgHandler.sendMessage(msg);
				} 
				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				m_iCounter += 1;
				if (m_iCounter == 5) {
					BTPhoneApplication.getInstance().flag=false;
					BTPhoneApplication.getInstance().isComing=false;
					BTPhoneApplication.getInstance().isbackcall=false;
					 BTPhoneApplication.getInstance().isTopComing=false;
					LEIDAUtil.setFlashlightEnabled(false);
					LEIDAUtil.setAudioFileEnabled(false);
					Intent sendbroad=new Intent();
					sendbroad.setAction("com.xintu.btphone.OutgoingCallEnd");
					sendBroadcast(sendbroad);
					//finish();
					break;
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_incoming);
		importAssetsUtil =new ImportAssetsUtil(this);
		m_contactsDao = new ContactsDao(IncomingActivity.this);
		Intent intent=getIntent();
		strInCNumber=intent.getStringExtra("strInCNumber");
		strName1 = inComingName(strInCNumber);
		tv_calling_name = (TextView) findViewById(R.id.tv_calling_name);
		tv_calling_number = (TextView) findViewById(R.id.tv_calling_number);
		tv_calling_number.setText(strInCNumber);
		tv_calling_name.setText(strName1);
		mMsgHandler = createMsgHandler();
		serviceConnection = new ServiceConnection() {
			/** 获取服务对象时的操作 */
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				myService = ((ConnService.ServiceBinder) service).getService();
				//myService.notifyForeGround(); //通知进入前台工作状�?

				m_bizMain = myService.m_bizMain;
				myService.m_listener.handlerIncoming = mMsgHandler;
			}

			/** 无法获取到服务对象时的操�?*/
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				myService = null;
				m_bizMain = null;
			}
		};
		PhoneReceiver.inComingHandler = inComingHandler;
	}
	public void click(View v){
		switch(v.getId()){
		case R.id.iv_answer:
			m_bizMain.reqAnswerIncommingCall();//接通来电
			BTPhoneApplication.getInstance().flag=true;
			BTPhoneApplication.getInstance().iscall=true;
			BTPhoneApplication.getInstance().isComing=false;
			BTPhoneApplication.getInstance().isActionCall=true;
			BTPhoneApplication.getInstance().isTopComing=true;
			//m_bizMain.reqSendMICVolume("09");
			m_bIsWaiting = false; 
			//置handlerIncomging为空以后，就不会发�?消息过来
			myService.m_listener.handlerIncoming = null;
			String number = tv_calling_number.getText().toString().trim();
			String name = tv_calling_name.getText().toString().trim();
			if(null!=number&&!"".equals(number)&&null!=name&&!"".equals(name)){
				Intent intent=new Intent(IncomingActivity.this,DialActivity.class);
				intent.putExtra("in_name", strName1);
				intent.putExtra("number", strInCNumber);
				intent.putExtra("incoming", true); //是否由来电启动
				SharedPreferencesUtil.saveStringData(BTPhoneApplication.getInstance(), "spnumber", number);
				startActivity(intent);
			}
			finish(); //接听以后，当前应该也finish�?
			break;
		case R.id.iv_hangup:
			m_bizMain.reqRejectIncommingCall();//拒接来电
			BTPhoneApplication.getInstance().flag=false;
			BTPhoneApplication.getInstance().isComing=false;
			BTPhoneApplication.getInstance().isActionCall=false;
			 BTPhoneApplication.getInstance().isTopComing=false;
			finish();
			BTPhoneApplication.getInstance().isbackcall=false;
			/*String strConnStatus =SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "isBtConn", "false");
			if("true".equals(strConnStatus)){
				if(m_contactsDao.findAllCount()==0)
					startThread();
			}*/
			LEIDAUtil.setAudioFileEnabled(false);
			BTPhoneApplication.getInstance().isBTTop=true;
			break;
		}
	}


	@Override
	protected void onResume(){
		// TODO Auto-generated method stub
		super.onResume();
		//LEIDAUtil.setFlashlightEnabled(true);
		//bind service
		Intent intent = new Intent(IncomingActivity.this, ConnService.class);
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);    
		//PhoneReceiver.inComingHandler=inComingHandler;
		//发�?广播
		Intent sendbroad = new Intent();
		sendbroad.setAction("com.xintu.btphone.willcall");
		sendbroad.putExtra("BTMSG_INCOMING_CALL", true);
		sendBroadcast(sendbroad);
       // BTPhoneApplication.getInstance().isTopComing=true;
		/*new Thread(){
			public void run(){
				int i=0;
				while(true){
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					i++;
					if(i==5){
						if("".equals(strNumber)){
							BTPhoneApplication.getInstance().flag=false;
							BTPhoneApplication.getInstance().isComing=false;
							BTPhoneApplication.getInstance().isbackcall=false;
							i=0;
							break;
						}
					}
				}
			}
		}.start();*/
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//unbind service
		unbindService(serviceConnection);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		strNumber="";
	}

	public String inComingName(String strNumber){

		if(!"".equals(importAssetsUtil.importGeneralContact(strNumber,true))){
			String[] strSplit = importAssetsUtil.importGeneralContact(strNumber,true).split(",");
			strName=strSplit[0];
			//tv_calling_name.setText(name);
			//tv_calling_number.setText(strNumber);
			speech(strName,strNumber);
		}else{
			if(strNumber.substring(0, 1).equals("0")){
				strName = m_contactsDao.findName(strNumber);//查询到人�?
				if(null!=strName&&!"".equals(strName)){
					speech(strName,strNumber);
					//tv_calling_name.setText(strName);
					//tv_calling_number.setText(strNumber);
				}else{
					String strNumber1=strNumber.substring(3, strNumber.length());
					strName = m_contactsDao.findName(strNumber1);//查询到人�?
					if(null!=strName&&!"".equals(strName)){
						speech(strName,strNumber);
						//tv_calling_name.setText(strName);
						//tv_calling_number.setText(strNumber1);
					}else{
						String strNumber2=strNumber.substring(4, strNumber.length());
						strName = m_contactsDao.findName(strNumber2);//查询到人�?
						if(null!=strName&&!"".equals(strName)){
							speech(strName,strNumber);
							//tv_calling_name.setText(strName);
							//tv_calling_number.setText(strNumber2);
						}else{
							speech("",strNumber);
							//tv_calling_name.setText("新来电");
							//tv_calling_number.setText(strNumber);
						}
					}
				}
			}else{
				strName = m_contactsDao.findName(strNumber);
				if(null!=strName&&!"".equals(strName)){
					speech(strName,strNumber);
					///tv_calling_name.setText(strName);
					//tv_calling_number.setText(strNumber);
				}else{
					String strNumber1="0086"+strNumber;
					strName = m_contactsDao.findName(strNumber1);
					if(null!=strName&&!"".equals(strName)){
						speech(strName,strNumber);
						//tv_calling_name.setText(strName);	
						//tv_calling_number.setText(strNumber1);
					}else{
						String strNumber2="86"+strNumber;
						strName = m_contactsDao.findName(strNumber2);
						if(null!=strName&&!"".equals(strName)){
							speech(strName,strNumber);
							//tv_calling_name.setText(strName);	
							//tv_calling_number.setText(strNumber2);
						}else{
							String strNumber3="00"+strNumber;
							strName = m_contactsDao.findName(strNumber3);
							if(null!=strName&&!"".equals(strName)){
								speech(strName,strNumber);
								//tv_calling_name.setText(strName);	
								//tv_calling_number.setText(strNumber3);	
							}else{
								speech("",strNumber);
								//tv_calling_name.setText("新来电");
								//tv_calling_number.setText(strNumber.trim());
							}
						}
					}
				}
			}
		}

		return strName;

	}
}
