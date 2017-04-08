package com.xintu.smartcar.btphone.service;

import java.util.ArrayList;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import com.xintu.smartcar.bluetoothphone.iface.BTCallback;
import com.xintu.smartcar.bluetoothphone.iface.CallInfo;
import com.xintu.smartcar.bluetoothphone.iface.CallRecordInfo;
import com.xintu.smartcar.bluetoothphone.iface.ContactInterface;
import com.xintu.smartcar.bluetoothphone.iface.CurrentCall;
import com.xintu.smartcar.bluetoothphone.iface.SelectContactInfo;
import com.xintu.smartcar.btphone.MyListener;
import com.xintu.smartcar.btphone.application.BTPhoneApplication;
import com.xintu.smartcar.btphone.biz.BizMain;
import com.xintu.smartcar.btphone.db.dao.CallRecordsDao;
import com.xintu.smartcar.btphone.db.dao.ContactsDao;
import com.xintu.smartcar.btphone.utils.GlobalUtil;
import com.xintu.smartcar.btphone.utils.ImportAssetsUtil;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

public class AIDLService extends Service {

	private String namepinyin="";
	private ImportAssetsUtil importAssetsUtil;
	private CallInfo contact;
	ArrayList<CallInfo> list;
	public BizMain m_bizMain; 
	private HanyuPinyinOutputFormat format;
	// 存放拼音使用的字符串数组
	private String[] pinyin;
	private ContactsDao m_contactsDao;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		m_bizMain = BizMain.getInstance(this);
		format = new HanyuPinyinOutputFormat();
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		m_contactsDao = new ContactsDao(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		//String strAction = intent.getAction();
		//if(!"".equals(strAction)&&null!=strAction&&strAction.equalsIgnoreCase("com.xintu.smartcar.btphone.service.action"))
		Log.d("MOMOMO", "laile");

		return btphoneConnect;
		//return null;

	}

	public static RemoteCallbackList<BTCallback> btContactList = new RemoteCallbackList<BTCallback>();  

	private  ContactInterface.Stub btphoneConnect = new ContactInterface.Stub() {

		@Override
		public int reqQueryContact(String strInfo) throws RemoteException {
			return Contactlist(strInfo);
		}

		@Override
		public void reqConnectCall() throws RemoteException {
			m_bizMain.reqAnswerIncommingCall();//收到接通的命令
		}

		@Override
		public void reqOutgoingCall(String strInfo) throws RemoteException {
			m_bizMain.reqDialNum(strInfo);

		}

		@Override
		public void reqHungup() throws RemoteException {
			m_bizMain.reqTerminateCall();//挂断电话

		}

		@Override
		public void registerCallback(BTCallback cb) throws RemoteException {
			new Thread(){
				public void run(){
					try {
						Thread.sleep(3000);
						AIDLService.doCallbackCheckBTStatus(MyListener.m_bConnected);
						CallRecordInfo info = new CallRecordInfo();
						CallInfo call = new CallInfo();
						call.m_strName = "xiaolu";
						call.m_strNumber = "10022";
						info.callRecords.add(call);
						AIDLService.doCallbackRecordCall(info);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}.start();

			btContactList.register(cb);

		}

		@Override
		public void unregisterCallback(BTCallback cb) throws RemoteException {
			btContactList.unregister(cb);
		}};


		private int Contactlist(String strInfo){ 
			Log.e("MOMOMO", strInfo+"++++++");
			/*SelectContactInfo selectContactInfo=new SelectContactInfo();
			if(GlobalUtil.isNumeric(GlobalUtil.ToDBC(strInfo))!=true){
				Log.e("MOMOMO", strInfo+"+++0000+++");
				//----------------------------当strInfo为名字---------------------------------------
				namepinyin=getStringPinYin(strInfo);
				String str=importAssetsUtil.importGeneralContact(strInfo,false);
				if("".equals(str)){
					Log.e("MOMOMO", strInfo+"+++1111+++");
					if(null!=namepinyin&&!"".equals(namepinyin)){
						if(null!=m_contactsDao.findNumberisExit(namepinyin)&&!"".equals(m_contactsDao.findNumberisExit(namepinyin))){
							selectContactInfo=m_contactsDao.findAllName2(namepinyin);
						}
					}

				}else if(!"".equals(str)&&str.length()>=2){
					Log.e("MOMOMO", strInfo+"+++2222+++");
					String[] strSplit = str.split(",");
					String strName = strSplit[0];
					String strNumber = strSplit[1];
					contact=new CallInfo();
					contact.m_strName=strName;
					contact.m_strNumber=strNumber;
					selectContactInfo.selectContacts.add(contact);
				}
			}else{
				Log.e("MOMOMO", strInfo+"+++3333+++");
				//--------------------------当strInfo为电话号码
				String strn=importAssetsUtil.importGeneralContact(strInfo,true);
				if("".equals(strn)){
					Log.e("MOMOMO", strInfo+"+++4444+++");
					String name=m_contactsDao.findName(strInfo);
					
					if(null!=name&&!"".equals(name)){
						contact=new CallInfo();
						contact.m_strName=name;
						contact.m_strNumber=strInfo;
						selectContactInfo.selectContacts.add(contact);
						Log.e("MOMOMO", strInfo+"+++5555+++");
					}else{
						BTPhoneApplication.getInstance().isbackcall=false;
						list=new ArrayList<CallInfo>();
						contact=new CallInfo();
						contact.m_strName="未知号码";
						contact.m_strNumber=strInfo;
						selectContactInfo.selectContacts.add(contact);
						Log.e("MOMOMO", strInfo+"+++6666+++");
					} 
				}else{
					Log.e("MOMOMO", strInfo+"+++7777+++");
					String[] strSplit = strn.split(",");
					String strName = strSplit[0];
					String strNumber = strSplit[1];
					contact=new CallInfo();
					contact.m_strName=strName;
					contact.m_strNumber=strNumber;
					selectContactInfo.selectContacts.add(contact);
				}
				Log.e("MOMOMO", strInfo+"+++8888+++");
				if(selectContactInfo.selectContacts.size()>0){
					Log.e("MOMOMO", strInfo+"+++9999+++");
					doCallbackContact(selectContactInfo);
				}
			}*/
			SelectContactInfo selectContactInfo =new SelectContactInfo();
			CallInfo call = new CallInfo();
			call.m_strName = "gx";
			call.m_strNumber = strInfo;
			selectContactInfo.selectContacts.add(call);
			doCallbackContact(selectContactInfo);
			return /*selectContactInfo.selectContacts.size()*/1;
		}

		public static void doCallbackCheckBTStatus(boolean isConn){
			int N = btContactList.beginBroadcast();  
			try {  
				for (int i = 0; i < N; i++) {  
					btContactList.getBroadcastItem(i).notifyBTCheckStatus(isConn);;  
				}  
			} catch (RemoteException e) {  
			}  	
			btContactList.finishBroadcast();  	
		}

		public static void doCallbackContact(SelectContactInfo info) {
			int N = btContactList.beginBroadcast();  
			try {  
				for (int i = 0; i < N; i++) {  
					btContactList.getBroadcastItem(i).notifyBTContact(info);  
				}  
			} catch (RemoteException e) {  
			}  	
			btContactList.finishBroadcast();  				
		}

		public static void doCallbackRecordCall(CallRecordInfo info){
			int N = btContactList.beginBroadcast();  
			try {  
				for (int i = 0; i < N; i++) {  
					btContactList.getBroadcastItem(i).notifyBTRecordCall(info);  
				}  
			} catch (RemoteException e) {  
			}  	
			btContactList.finishBroadcast();
		}

		public static void doCallbackCommingCall(CurrentCall call){//有来电
			int N=btContactList.beginBroadcast();
			try {  
				for (int i = 0; i < N; i++) {  
					btContactList.getBroadcastItem(i).notifyBTCommingCall(call);
				}  
			} catch (RemoteException e) {   
			}  	
			btContactList.finishBroadcast();
		}

		public static  void doCallbackConnectedCall(){//已接通来电
			int N=btContactList.beginBroadcast();
			try {  
				for (int i = 0; i < N; i++) {  
					btContactList.getBroadcastItem(i).notifyBTConnectedCall();;
				}  
			} catch (RemoteException e) {   
			}  	
			btContactList.finishBroadcast();
		}

		public static void doCallbackCallTimer(String time){
			try {  
				int N=btContactList.beginBroadcast();
				for (int i = 0; i < N; i++) {  
					btContactList.getBroadcastItem(i).notifyBTCallTimer(time);
				}  
				btContactList.finishBroadcast();
			} catch (Exception e) {   
			}  	
		}

		public static void doCallbackOutGoingCall(CurrentCall call){
			int N=btContactList.beginBroadcast();
			try {  
				for (int i = 0; i < N; i++) {  
					btContactList.getBroadcastItem(i).notifyBTOutgoingCall(call);;
				}  
			} catch (RemoteException e) {   
			}  	
			btContactList.finishBroadcast();
		}



		public static void doCallbackHungup(){
			int N=btContactList.beginBroadcast();
			try {  
				for (int i = 0; i < N; i++) {  
					btContactList.getBroadcastItem(i).notifyBTHungup();
				}  
			} catch (RemoteException e) {   
			}  	
			btContactList.finishBroadcast();
		}

		public void doCallbackBTisUp(boolean isflag){
			int N=btContactList.beginBroadcast();
			try {  
				for (int i = 0; i < N; i++) {  
					btContactList.getBroadcastItem(i).notifyBTisUP(isflag);
				}  
			} catch (RemoteException e) {   
			}  	
			btContactList.finishBroadcast();
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
}
