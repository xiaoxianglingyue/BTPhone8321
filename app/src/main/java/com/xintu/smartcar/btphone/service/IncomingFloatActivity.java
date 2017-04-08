package com.xintu.smartcar.btphone.service;


import com.xintu.smartcar.btphone.R;
import com.xintu.smartcar.btphone.application.BTPhoneApplication;
import com.xintu.smartcar.btphone.biz.BizMain;
import com.xintu.smartcar.btphone.db.dao.ContactsDao;
import com.xintu.smartcar.btphone.receiver.PhoneReceiver;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class IncomingFloatActivity extends Activity{
	WindowManager wm = null;
	WindowManager.LayoutParams wmParams = null;
	View view;
	private float mTouchStartX;
	private float mTouchStartY;
	private float x;
	private float y;
	int state;
	int delaytime=1000;
	public static final String TAG = "BTPhone";
	private int m_iCounter = 0;
	private Thread m_thread = null;
	private boolean m_bIsWaiting = true; //正在等待接听电话
	public static final int BTMSG_INCOMING_CALL = 1011;  //当前电话
	public static final int BTMSG_ACTIVE_CALL = 1009;   //接通电话
	public static final int BTMSG_INCOMMING_CALLNAME = 10018;//显示电话号码
	public static final int TIMER_MSG = 2000;
	protected static final int BTMSG_HUNGUP_CALL = 1007;
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
	public static Handler mainhandler;
	private Handler mMsgHandler;
	private String strInCNumber="";
	private String strName1="";
	//-----------------------------------来电未接通start---------------------------------------------------------
	public  Handler inComingHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==1005){
				m_bIsWaiting = false; 
				//BTPhoneApplication.getInstance().isComing=false;
				//置handlerIncomging为空以后，就不会发�?消息过来
				BTPhoneApplication.getInstance().iscall=true;
				BTPhoneApplication.getInstance().flag=true;
				myService.m_listener.handlerIncoming = null;
				m_bizMain.reqAnswerIncommingCall();//来电(蓝牙右键接通电话)
				String number= tv_calling_number.getText().toString().trim();
				String name = tv_calling_name.getText().toString().trim();
				if(null!=number&&!"".equals(number)){
					Intent intent=new Intent(IncomingFloatActivity.this,DialFloatService.class);
					if("".equals(strName)){
						BTPhoneApplication.getInstance().come_name="新来电";
					}else{
						BTPhoneApplication.getInstance().come_name=name;
					}
					BTPhoneApplication.getInstance().come_number=number;
					Log.e(TAG, "before start dial activity ~~~~");
					SharedPreferencesUtil.saveStringData(BTPhoneApplication.getInstance(), "spnumber", number);
					startService(intent);
					wm.removeView(view);
				}
				finish(); //接听以后，当前应该也finish�?

			}
		}
	};
	//来电界面出来以后后，如果手机上面直接挂掉
	//则应该根据是否还有BTMSG_CURRENT_CALL消息，不确定是否�?��finish当前界面
	private Handler createMsgHandler() {
		Handler handler=new Handler(){
			@Override
			public void handleMessage(final Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case BTMSG_INCOMING_CALL:
					m_iCounter = 0;
					//m_bizMain.reqSendMICVolume("09");//33
					//第一个current 消息
					if (m_thread == null) {
						m_thread = new Thread(new CounterThread());
						m_thread.start();
						String strNumber = (String)msg.obj;
						if (strNumber != null||!"".equals(strNumber)) {
							strName1=inComingName(strNumber);
						}else{
							finish();
						}						
					}
					break;
				case BTMSG_INCOMMING_CALLNAME://来电显示
					String strIncomName=(String) msg.obj;
					tv_calling_name.setText(strIncomName);
					break;
				case BTMSG_ACTIVE_CALL: //接通来电
					BTPhoneApplication.getInstance().isComing=false;
					BTPhoneApplication.getInstance().iscall=true;
					BTPhoneApplication.getInstance().flag=true;
					BTPhoneApplication.getInstance().isActionCall=true;
					//LEIDAUtil.setFlashlightEnabled(true);
					//如果来电时，由用户在手机端接听，则要启动dial界面
					String number = tv_calling_number.getText().toString();
					String name = tv_calling_name.getText().toString();
					if(null!=number&&!"".equals(number)&&null!=name&&!"".equals(name)){
						Intent intent=new Intent(IncomingFloatActivity.this,DialFloatService.class);
						BTPhoneApplication.getInstance().come_name=name;
						BTPhoneApplication.getInstance().come_number=number;
						SharedPreferencesUtil.saveStringData(BTPhoneApplication.getInstance(), "spnumber", number);	
						//m_bizMain.reqSendSpeakerVolume("0f");//32
						//m_bizMain.reqSendMICVolume("09");//33
						startService(intent);


					}
					if(null!=wm&&!isFinishing())
						wm.removeView(view);
					finish();//接听以后，当前应该也finish�?
					break;
				case BTMSG_HUNGUP_CALL:
					BTPhoneApplication.getInstance().flag=false;
					BTPhoneApplication.getInstance().isComing=false;
					BTPhoneApplication.getInstance().isbackcall=false;
					BTPhoneApplication.getInstance().isActionCall=false;
					if(null!=wm&&!isFinishing())
						wm.removeView(view);
					finish();
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
				if (m_iCounter == 6) {
					BTPhoneApplication.getInstance().flag=false;
					BTPhoneApplication.getInstance().isComing=false;
					BTPhoneApplication.getInstance().isbackcall=false;
					LEIDAUtil.setFlashlightEnabled(false);
					LEIDAUtil.setAudioFileEnabled(false);
					Intent sendbroad=new Intent();
					sendbroad.setAction("com.xintu.btphone.OutgoingCallEnd");
					sendBroadcast(sendbroad);
					BTPhoneApplication.getInstance().widthloc=500;
					BTPhoneApplication.getInstance().heightloc=25;
					/*if(null!=wm){
						wm.removeView(view);
					}*/
					//finish();
					break;
				}
			}
		}
	}


	//----------------------------------------来电未接通end--------------------------------------------------------
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("FloatService", "onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setContentView(view);
		view = LayoutInflater.from(this).inflate(R.layout.float_incoming, null);
		BTPhoneApplication.getInstance().incomingFloatActivity=IncomingFloatActivity.this;
		importAssetsUtil =new ImportAssetsUtil(this);
		m_contactsDao = new ContactsDao(IncomingFloatActivity.this);
		Intent intent=getIntent();
		strInCNumber=intent.getStringExtra("strInCNumber");
		strName1 = inComingName(strInCNumber);
		tv_calling_name = (TextView) view.findViewById(R.id.tv_calling_name);
		tv_calling_number = (TextView) view.findViewById(R.id.tv_calling_number);
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
		createView();
	}

	private void createView() {
		SharedPreferences shared = getSharedPreferences("float_flag",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = shared.edit();
		editor.putInt("float", 1);
		editor.commit();
		// 获取WindowManager
		wm = (WindowManager) getApplicationContext().getSystemService("window");
		// 设置LayoutParams(全局变量）相关参�?
		wmParams = ((BTPhoneApplication) getApplication()).getMywmParams();
		wmParams.type = 2002;
		wmParams.flags |= 8;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
		// 以屏幕左上角为原点，设置x、y初始�?
		if(1280==screen()){
			wmParams.x = 500;
			wmParams.y = 25;
		}else{
			wmParams.x = 150;
			wmParams.y = 25;
		}
		// 设置悬浮窗口长宽数据
		wmParams.width = 550;//WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height =140;// WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.format = 1;

		wm.addView(view, wmParams);

		view.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				// 获取相对屏幕的坐标，即以屏幕左上角为原点
				x = event.getRawX();
				y = event.getRawY() - 25; // 25是系统状态栏的高�?
				//Log.i("currP", "currX" + x + "====currY" + y);// 调试信息
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					state = MotionEvent.ACTION_DOWN;
					// 获取相对View的坐标，即以此View左上角为原点
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					Log.i("startP", "startX" + mTouchStartX + "====startY"
							+ mTouchStartY);// 调试信息
					break;
				case MotionEvent.ACTION_MOVE:
					state = MotionEvent.ACTION_MOVE;
					updateViewPosition();
					break;

				case MotionEvent.ACTION_UP:
					state = MotionEvent.ACTION_UP;
					updateViewPosition();
					mTouchStartX = mTouchStartY = 0;
					break;
				}
				return true;
			}
		});
	}

	private void updateViewPosition() {
		// 更新浮动窗口位置参数
		wmParams.x = (int) (x - mTouchStartX);
		wmParams.y = (int) (y - mTouchStartY);
		wm.updateViewLayout(view, wmParams);
		BTPhoneApplication.getInstance().widthloc=wmParams.x;
		BTPhoneApplication.getInstance().heightloc=wmParams.y;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// TODO Auto-generated method stub
		//LEIDAUtil.setFlashlightEnabled(true);
		//bind service
		Intent intents = new Intent(IncomingFloatActivity.this, ConnService.class);
		bindService(intents, serviceConnection, Context.BIND_AUTO_CREATE);    
		PhoneReceiver.inComingHandler=inComingHandler;
		//发�?广播
		Intent sendbroad = new Intent();
		sendbroad.setAction("com.xintu.btphone.willcall");
		sendbroad.putExtra("BTMSG_INCOMING_CALL", true);
		sendBroadcast(sendbroad);

		new Thread(){
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
							//wm.removeView(view);
							break;
						}
					}
				}
			}
		}.start();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//unbind service
		unbindService(serviceConnection);
	}

	@Override
	public void onDestroy() {
		//handler.removeCallbacks(task);
		Log.d("FloatService", "onDestroy");
		//wm.removeView(view);
		strNumber="";
		super.onDestroy();
	}

	public void click(View v){
		switch(v.getId()){
		case R.id.iv_answer_service:
			m_bizMain.reqAnswerIncommingCall();//按浮动窗上接听键接通电话
			BTPhoneApplication.getInstance().isActionCall=true;
			if(null!=wm)
				wm.removeView(view);
			BTPhoneApplication.getInstance().flag=true;
			BTPhoneApplication.getInstance().iscall=true;
			BTPhoneApplication.getInstance().isComing=false;
			//	m_bizMain.reqSendMICVolume("09");
			m_bIsWaiting = false; 
			//置handlerIncomging为空以后，就不会发�?消息过来
			myService.m_listener.handlerIncoming = null;
			String number = tv_calling_number.getText().toString().trim();
			String name = tv_calling_name.getText().toString().trim();
			if(null!=number&&!"".equals(number)&&null!=name&&!"".equals(name)){
				Intent intent=new Intent(IncomingFloatActivity.this,DialFloatService.class);
				BTPhoneApplication.getInstance().come_name=name;
				BTPhoneApplication.getInstance().come_number=number;
				SharedPreferencesUtil.saveStringData(BTPhoneApplication.getInstance(), "spnumber", number);
				startService(intent);
				finish();
			}
			break;
		case R.id.iv_hangup_service:
			m_bizMain.reqRejectIncommingCall();//拒接电话
			BTPhoneApplication.getInstance().flag=false;
			BTPhoneApplication.getInstance().isComing=false;
			BTPhoneApplication.getInstance().isbackcall=false;
			BTPhoneApplication.getInstance().isActionCall=false;
			BTPhoneApplication.getInstance().isBTTop=true;
			BTPhoneApplication.getInstance().widthloc=150;
			BTPhoneApplication.getInstance().heightloc=25;
			finish();
			LEIDAUtil.setAudioFileEnabled(false);
			if(null!=wm)
				wm.removeView(view);
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		return;
	}

	public int screen(){
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int widthPixels= dm.widthPixels;
		//int heightPixels= dm.heightPixels;
		return widthPixels;
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
