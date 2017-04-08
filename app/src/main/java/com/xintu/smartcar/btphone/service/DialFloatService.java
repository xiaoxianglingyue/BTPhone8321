package com.xintu.smartcar.btphone.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xintu.smartcar.btphone.MainActivity;
import com.xintu.smartcar.btphone.R;
import com.xintu.smartcar.btphone.application.BTPhoneApplication;
import com.xintu.smartcar.btphone.biz.BizMain;
import com.xintu.smartcar.btphone.db.dao.ContactsDao;
import com.xintu.smartcar.btphone.receiver.PhoneReceiver;
import com.xintu.smartcar.btphone.utils.LEIDAUtil;
import com.xintu.smartcar.btphone.utils.SharedPreferencesUtil;

public class DialFloatService extends Service {

	WindowManager wm = null;
	WindowManager.LayoutParams wmParams = null;
	static View view;
	private float mTouchStartX;
	private float mTouchStartY;
	private float x;
	private float y;
	int state;
	int delaytime=1000;
	private final static String TAG = "BTPhone";
	private int m_iCounter = 0;
	private Thread m_thread = null;
	private boolean m_isActive = false; //是否已接�?
	private TextView tv_calling_name,tv_calling_number, tv_calling_state;
	private Chronometer chronometer;
	private ImageView iv_back_btphone_main;
	private Handler mMsgHandler;
	private BizMain m_bizMain;
	private boolean m_bIncoming = true;  //来电接听的情�?
	private long mlCount = 0;
	private String number="";
	private boolean incoming=false;
	private ConnService myService;
	private ServiceConnection serviceConnection;
	public static final int BTMSG_ACTIVE_CALL = 1009;   //接�?电话
	public static final int BTMSG_HUNGUP_CALL = 1007;   //挂断电话
	public static final int TIMER_MSG = 2000;			//计时器消�?
	private boolean isConn=false;
	public static Handler mainhandler;
	//private ContactsDao m_contact;
	ContactsDao m_contactsDao;
	String strNumber ="";
	String strName="";
	private ImageView iv_float_hangup;

	public Handler dialHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==1006){
				BTPhoneApplication.getInstance().flag=false;
				m_bizMain.reqTerminateCall();
				if(null!=mainhandler){
					Message message=mainhandler.obtainMessage();
					message.what=10020;
					message.obj=true;
					mainhandler.sendMessage(message);
					tv_calling_state.setText("正在挂断");
					m_isActive=false;
				}
				LEIDAUtil.setAudioFileEnabled(false);
				stopService();
			}
		}
	};

	private Handler createMsgHandler() {
		Handler handler=new Handler(){
			@Override
			public void handleMessage(final Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case BTMSG_ACTIVE_CALL: //接听来电
					BTPhoneApplication.getInstance().flag=true;
					//m_bizMain.reqSendSpeakerVolume("0f");
					//m_bizMain.reqSendMICVolume("09");
					m_iCounter = 0;
					tv_calling_state.setText("已经接通");
					m_isActive = true;
					BTPhoneApplication.getInstance().isActionCall=true;
					SharedPreferencesUtil.saveStringData(BTPhoneApplication.getInstance(), "spnumber", number);
					break;
				case BTMSG_HUNGUP_CALL://挂断电话
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
						chronometer.setBase(SystemClock.elapsedRealtime());
						stopService();
					}
					break;
				}
			}
		};	
		return handler;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		view = LayoutInflater.from(this).inflate(R.layout.float_dial, null);
		if(null!=BTPhoneApplication.getInstance().incomingFloatActivity&&!BTPhoneApplication.getInstance().incomingFloatActivity.isFinishing()){
			BTPhoneApplication.getInstance().incomingFloatActivity.finish();
		}
		m_contactsDao= new ContactsDao(DialFloatService.this);
		tv_calling_name = (TextView) view.findViewById(R.id.tv_float_calling_name);
		tv_calling_number = (TextView) view.findViewById(R.id.tv_float_calling_number);
		tv_calling_state = (TextView) view.findViewById(R.id.float_dialing);    
		chronometer = (Chronometer)view.findViewById(R.id.chronometer);
		iv_float_hangup=(ImageView) view.findViewById(R.id.iv_float_hangup);
		iv_back_btphone_main=(ImageView) view.findViewById(R.id.iv_back_btphone_main);
		String in_name=BTPhoneApplication.getInstance().come_name;
		number=BTPhoneApplication.getInstance().come_number;
		mMsgHandler = createMsgHandler();
		if(null==number&&"".equals(number)){
			Toast.makeText(DialFloatService.this, number+"A", Toast.LENGTH_LONG).show();
			stopService();
		}
		if(!"".equals(in_name)&&null!=in_name){
			tv_calling_name.setText(in_name.trim());
			tv_calling_number.setText(number.trim());
		}else{
			tv_calling_name.setText("新来电");
			tv_calling_number.setText(number.trim());
		}
		createView();
		//挂断电话
		iv_float_hangup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				m_bizMain.reqTerminateCall();
				BTPhoneApplication.getInstance().flag=false;
				BTPhoneApplication.getInstance().ishungup=true;
				BTPhoneApplication.getInstance().ismobile=false;
				BTPhoneApplication.getInstance().isComing=false;
				BTPhoneApplication.getInstance().isbackcall=false;
				BTPhoneApplication.getInstance().isActionCall=false;
				tv_calling_state.setText("正在挂断");
				m_isActive=false;
				mlCount=0;
				Timer timer=new Timer();
				TimerTask task=new TimerTask(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						stopService();
						LEIDAUtil.setAudioFileEnabled(false);
					}
				};
				timer.schedule(task, 2000);
				String strConnStatus =SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "isBtConn", "false");
				if("true".equals(strConnStatus)){
					//if(m_contactsDao.findAllCount()==0)
					//startThread();
				}
			}
		});
		iv_back_btphone_main.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(DialFloatService.this,MainActivity.class);
				//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				view.setVisibility(ViewGroup.INVISIBLE);
			}
		});
	}


	private void createView() {
		SharedPreferences shared = getSharedPreferences("float_flag",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = shared.edit();
		editor.putInt("float", 1);
		editor.commit();
		// 获取WindowManager
		wm = (WindowManager) getApplicationContext().getSystemService("window");
		// 设置LayoutParams(全局变量）相关参数
		wmParams = ((BTPhoneApplication) getApplication()).getMywmParams();
		wmParams.type = 2002;
		wmParams.flags |= 8;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
		// 以屏幕左上角为原点，设置x、y初始值
		wmParams.x = BTPhoneApplication.getInstance().widthloc;
		wmParams.y = BTPhoneApplication.getInstance().heightloc;
		// 设置悬浮窗口长宽数据
		wmParams.width =550;//WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height =140;//WindowManager.LayoutParams.WRAP_CONTENT;
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
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		BTPhoneApplication.getInstance().m_isDialOpened = true;
		PhoneReceiver.dialHandler=dialHandler;
		if(isConn==true){
			tv_calling_state.setText("正在挂断");
			isConn=false;
		}
		BTPhoneApplication.getInstance().flag=true;
		BTPhoneApplication.getInstance().iscall=true;
		serviceConnection = new ServiceConnection() {
			/** 获取服务对象时的操作 */
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				myService = ((ConnService.ServiceBinder) service).getService();
				m_bizMain = myService.m_bizMain;
				myService.m_listener.handlerDial = mMsgHandler;
			}

			/** 无法获取到服务对象时的操做*/
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				myService = null;
				m_bizMain = null;
			}
		};			
		//bind service
		Intent intent1 = new Intent(DialFloatService.this, ConnService.class);
		bindService(intent1, serviceConnection, Context.BIND_AUTO_CREATE);    
		if(incoming == true){

		}
		else{
			//发送广播
			Intent sendbroad = new Intent();
			sendbroad.setAction("com.xintu.btphone.willcall");
			sendBroadcast(sendbroad);
		}
		if (m_bIncoming) {
			tv_calling_state.setText("已经接通");
			m_isActive = true;
		}
		Log.e(TAG, "end of resume of dial activity ~~~~");
		chronometer.start();
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
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindService(serviceConnection);
		BTPhoneApplication.getInstance().m_isDialOpened = false;
		BTPhoneApplication.getInstance().ismobile=false;
		BTPhoneApplication.getInstance().isBTTop=true;
		BTPhoneApplication.getInstance().widthloc=150;
		BTPhoneApplication.getInstance().heightloc=25;
		m_iCounter = 0;
		BTPhoneApplication.getInstance().come_name="";
		BTPhoneApplication.getInstance().come_number="";
		if(null!=wm)
			wm.removeView(view);
	}

	public void stopService(){
		Intent serviceStop = new Intent();
		serviceStop.setClass(DialFloatService.this, DialFloatService.class);
		stopService(serviceStop);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Handler dialFloathandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==2156){
				view.setVisibility(ViewGroup.VISIBLE);
			}else if(msg.what==2166){
				view.setVisibility(ViewGroup.INVISIBLE);
			}
		}

	};	
}
