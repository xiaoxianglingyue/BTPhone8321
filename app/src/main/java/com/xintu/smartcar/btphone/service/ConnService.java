package com.xintu.smartcar.btphone.service;

import java.io.InputStream;
import java.io.OutputStream;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.xintu.smartcar.bluetoothphone.iface.CurrentCall;
import com.xintu.smartcar.btphone.DialActivity;
import com.xintu.smartcar.btphone.MyListener;
import com.xintu.smartcar.btphone.application.BTPhoneApplication;
import com.xintu.smartcar.btphone.bean.PBItem;
import com.xintu.smartcar.btphone.biz.BizMain;
import com.xintu.smartcar.btphone.db.dao.CallRecordsDao;
import com.xintu.smartcar.btphone.db.dao.ContactsDao;
import com.xintu.smartcar.btphone.receiver.PhoneReceiver;
import com.xintu.smartcar.btphone.serialport.PrintClass;
import com.xintu.smartcar.btphone.utils.LEIDAUtil;
import com.xintu.smartcar.btphone.utils.SharedPreferencesUtil;
import com.xintu.smartcar.btphone.utils.Util;




public class ConnService extends Service{
	private static final String TAG = "BTPhone";
	public ServiceBinder sBinder;
	public MyListener m_listener;
	public BizMain m_bizMain; 
	private PrintClass printClass = null;
	private BTAutoConn m_btAutoConn = null;
	private HanyuPinyinOutputFormat format;
	// 存放拼音使用的字符串数组
	private String[] pinyin;
	private ContactsDao m_contactsDao;
	private Handler handlerService = null;
	private int type=-1;
	CurrentCall currentCall;
	private int mlCount=0;
	private TimerThread timerThread=null;
	public static CallRecordsDao m_recordDao;
	private Util util;
	private boolean isflag=false;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return sBinder;
	}	

	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==1111){
				//收到进入休眠状态的广播，断开蓝牙连接
				BTPhoneApplication.getInstance().isConnDevices=false;
				m_bizMain.reqDisconnectHFP();
				m_btAutoConn.blockThread(); //阻塞自动连接线程
				//BizMain.getInstance(getApplicationContext()).reqReboot();
				//BizMain.getInstance(BTPhoneApplication.getInstance()).reqCodec();
			}else if(msg.what==1112){
				BTPhoneApplication.getInstance().isConnDevices=true;
				m_btAutoConn.wakeThread(); //唤醒自动连接线程
			}else if(msg.what==1201){
				String timeCount=(String) msg.obj;

			}
		}

	};
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "BtPhone  ConnService onCreate");
		sBinder = new ServiceBinder();
		PhoneReceiver.serviceHandler=handler;
		AIDLService.doCallbackCheckBTStatus(false);
		util=new Util();
		//启动蓝牙系统
		startBTSystem();
		format = new HanyuPinyinOutputFormat();
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		m_contactsDao = new ContactsDao(this);
		m_recordDao=new CallRecordsDao(this);
		//让listener 发回incoming_call消息
		handlerService = createMsgHandler();
		m_listener.handlerService = handlerService;
		m_contactsDao.clearAll();
		currentCall=new CurrentCall();
		//Intent intent=new Intent(ConnService.this,AIDLService.class);
		//startService(intent);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.e(TAG, "service onStart");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "BtPhone  service onDestroy");
	}


	//此方法是为了可以在Acitity中获得服务的实例   
	public class ServiceBinder extends Binder {
		public ConnService getService() {
			return ConnService.this;
		}
	}

	//启动蓝牙系统
	private void startBTSystem() {
		Log.e(TAG, "service startBTSystem");
		//启动蓝牙系统
		m_bizMain = BizMain.getInstance(this);

		if (m_listener == null) {
			m_listener = new MyListener();
		}

		if (printClass == null) {
			printClass = PrintClass.getInstance(this);
			printClass.openSerialPort();
			InputStream  is= printClass.getInputStream();
			OutputStream os = printClass.getOutputStream();		

			m_bizMain.startMainBiz(is, os, m_listener);
			m_bizMain.reqReboot();
			//m_bizMain.reqCodec();
		}
		if (m_btAutoConn == null) {// TODO Auto-generated catch block
			m_btAutoConn = new BTAutoConn();
			m_btAutoConn.start(BTPhoneApplication.getInstance(), m_bizMain, m_listener);
		}
	}



	//给activity 的接口
	public BizMain getBizMain() {
		return m_bizMain;
	}

	public MyListener getListener() {
		return m_listener;
	}

	//当mainactivity打开的时候调用
	public void notifyForeGround() {
		m_btAutoConn.m_isBackground = false;
	}

	//当mainactivity关闭的时候调用
	public void notifyBackground() {
		m_btAutoConn.m_isBackground = true;
	}

	private Handler createMsgHandler() {
		Handler handler = new Handler(){
			@SuppressWarnings("static-access")
			@Override
			public void handleMessage(final Message msg) {

				switch(msg.what){
				case MyListener.BTMSG_INCOMING_CALL://来电
					//Log.e(TAG, "service incoming call");
					//Log.d("YL", "进入服务");
					//当在后台运行的时候，才会处理
					if (m_btAutoConn.m_isBackground == true) {
						String strInCNumber=(String) msg.obj;
						BTPhoneApplication.getInstance().isComing=true;
						BTPhoneApplication.getInstance().isBTTop=false;
						BTPhoneApplication.getInstance().isbackcall=true;
						//Intent intent = new Intent(ConnService.this, IncomingFloatActivity.class);
						//intent.putExtra("strInCNumber", strInCNumber);
						//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
						//startActivity(intent);
						currentCall.callName=util.inComingName(strInCNumber);
						currentCall.callNumber=strInCNumber;
					}
					AIDLService.doCallbackCommingCall(currentCall);//来电
					break;
				case MyListener.BTMSG_OUTGOING_CALL://正在拨号
					String strDiaNumber = (String)msg.obj;
					//BTPhoneApplication.getInstance().isOutgoingCall=true;
					Log.e(TAG, "Service MyListener.BTMSG_OUTGOING_CAL....4444........" + strDiaNumber + ", " +
							m_btAutoConn.m_isBackground + "," + BTPhoneApplication.getInstance().m_isDialOpened) ;

					if (m_btAutoConn.m_isBackground == true&&BTPhoneApplication.getInstance().m_isDialOpened == false) {


						//用户在手机上拨打，则要启动拨打界面
						//BTPhoneApplication.getInstance().m_isDialOpened = true;
						String strName = "";
						if (strDiaNumber != null) {
							//query database
							ContactsDao m_contactsDao = new ContactsDao(ConnService.this);
							strName = m_contactsDao.findName(strDiaNumber);
							/*if(!LEIDAUtil.getSystemVersion().equals("")&&Integer.parseInt(LEIDAUtil.getSystemVersion())>=2001)
								LEIDAUtil.setFMSendPower(false);*/
							//Intent intent = new Intent(ConnService.this, DialActivity.class);
							//if("".equals(strName)){
							//	intent.putExtra("name", "未知号码");
							//}else{
							//	intent.putExtra("name", strName);
							//}
							//intent.putExtra("number", strDiaNumber);
							//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
							//startActivity(intent);
							BTPhoneApplication.getInstance().isbackcall=true;
							BTPhoneApplication.getInstance().ismobile=true;
							currentCall.callName=strName;
							currentCall.callNumber=strDiaNumber;
							AIDLService.doCallbackOutGoingCall(currentCall);//来电
						}
					}

					break;
				case MyListener.BTMSG_DISCONNECTED://断开连接

					SharedPreferencesUtil.saveStringData(BTPhoneApplication.getInstance(), "isBtConn", "false");
					if(null!=m_contactsDao&&!"".equals(m_contactsDao)){
						m_contactsDao.clearAll();
					}
					break;
				case MyListener.BTMSG_CONNECTED://连接成功
					//Log.e("MM", "service:连接成功");
					AIDLService.doCallbackCheckBTStatus(true);
					LEIDAUtil.setAudioFileEnabled(true);//连接成功打开功放
					SharedPreferencesUtil.saveStringData(BTPhoneApplication.getInstance(), "isBtConn", "true");
					if(BTPhoneApplication.getInstance().isActivityTop==false)
						new Thread() {
						public void run() {
							try {
								Thread.sleep(8000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							m_bizMain.reqPhoneBook();//同步电话本
						}
					}.start();
					break;
				case MyListener.BTMSG_PHONEBOOK_DATA://正在同步电话本
					String strName="";
					String strNumber="";
					PBItem pbItem = (PBItem)msg.obj;
					type=pbItem.m_iCallType;
					if (pbItem.m_strName == null) {
						pbItem.m_strName = "";
					}
					else if (pbItem.m_strName.equalsIgnoreCase("null")) {
						pbItem.m_strName = "";
					}

					if (pbItem.m_strNumber == null) {
						pbItem.m_strNumber = "";
					}
					else if (pbItem.m_strNumber.equalsIgnoreCase("null")) {
						pbItem.m_strNumber = "";
					}

					if (pbItem.m_strNumber == "" && pbItem.m_strName == "") {
						break;
					}
					strName = pbItem.m_strName;
					strNumber = pbItem.m_strNumber;
					String strnamepinyin=getStringPinYin(strName);
					if(BTPhoneApplication.getInstance().isActivityTop==false){
						if(!"".equals(strNumber)&&null!=strNumber){
							if(m_contactsDao.isExitNumber(strNumber.trim())==false)
								m_contactsDao.save(strNumber.trim(),strName.trim(),strnamepinyin.trim());
						}
					}

				case MyListener.BTMSG_PHONEBOOK_COMPLETE://电话本同步完成
					/*if(type==0){
						m_bizMain.reqPhoneBook_missed();//未接电话

					}else if(type==1){
						m_bizMain.reqPhoneBook_answered();//已接电话

					}else if(type==2){
						m_bizMain.reqPhoneBook_dialed();//已拨电话
					}*/
					break;
				case MyListener.BTMSG_SEND_CONNBTNAME://已经连接的蓝牙设备的名称
					String strDevicesName=(String) msg.obj;
					SharedPreferencesUtil.saveStringData(BTPhoneApplication.getInstance(), "strDevicesName", strDevicesName);
					break;
				case MyListener.BTMSG_INCOMMING_CALLNAME://来电号码显示
					String strIncomName=(String) msg.obj;
					break;
				case MyListener.BTMSG_ACTIVE_CALL://接通电话
					AIDLService.doCallbackConnectedCall();
					isflag=true;
					//开始计时
					if(null==timerThread){
						timerThread=new TimerThread();
						timerThread.start();
					}
					break;
				case MyListener.BTMSG_HUNGUP_CALL://挂断电话
					if(null!=timerThread){
						isflag=false;
						timerThread.interrupt();
						mlCount=0;
						timerThread=null;
					}
					AIDLService.doCallbackHungup();
					AIDLService.doCallbackRecordCall(m_recordDao.findAll());
					break;
				}	
				super.handleMessage(msg);
			}
		};		

		return handler;
	}

	/**
	 * 字符转拼音
	 */
	public String getStringPinYin(String str)

	{
		StringBuilder sb = new StringBuilder();
		String tempPinyin = null;
		for (int i = 0; i < str.length(); ++i)
		{
			tempPinyin = getCharacterPinYin(str.charAt(i));
			if (tempPinyin == null)
			{
				sb.append(str.charAt(i));
			}
			else
			{
				sb.append(tempPinyin);
			}
		}
		return sb.toString();
	}
	public String getCharacterPinYin(char c)
	{
		try
		{
			pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
		}
		catch (BadHanyuPinyinOutputFormatCombination e)
		{
			e.printStackTrace();
		}
		if (pinyin == null)
			return null;
		return pinyin[0];
	}

	private class TimerThread extends Thread{
		public void run() {
			while(isflag){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					isflag = false;
					break;
				}
				mlCount ++;  
				int totalSec = 0;  
				totalSec = (int)(mlCount);  
				final int min = (totalSec / 60);  
				final int sec = (totalSec % 60); 
				AIDLService.doCallbackCallTimer(String.format("%1$02d:%2$02d", min, sec));
			}
		}
	}
}

