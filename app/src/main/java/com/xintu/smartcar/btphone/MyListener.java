package com.xintu.smartcar.btphone;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xintu.smartcar.btphone.application.BTPhoneApplication;
import com.xintu.smartcar.btphone.bean.CallRecord;
import com.xintu.smartcar.btphone.bean.PBItem;
import com.xintu.smartcar.btphone.db.dao.CallRecordsDao;
import com.xintu.smartcar.btphone.listener.GeneralListener;
import com.xintu.smartcar.btphone.service.AIDLService;
import com.xintu.smartcar.btphone.service.ConnService;
import com.xintu.smartcar.btphone.utils.LEIDAUtil;
import com.xintu.smartcar.btphone.utils.Util;

public class MyListener extends GeneralListener{
	private static final String TAG = "BTPhone";
	public Handler handlerDial;
	public Handler handlerMain;
	public Handler handlerIncoming;
	public Handler handlerAutoConn;
	public Handler handlerService;

	public static boolean m_bStreamOn = false;
	public static boolean m_bActiveCall = false;
	public static boolean m_bConnected = false;
	Util util=new Util();

	//正在连接
	public static final int BTMSG_CONNECTING = 1005;
	//连接完成
	public static final int BTMSG_CONNECTED = 1006;
	//正在拨打电话
	public static final int BTMSG_OUTGOING_CALL = 1008;  
	//接通来电
	public static final int BTMSG_ACTIVE_CALL = 1009; 
	//挂机
	public static final int BTMSG_HUNGUP_CALL = 1007;
	//有来电
	public static final int BTMSG_INCOMING_CALL = 1011;
	//电话本数据
	public static final int BTMSG_PHONEBOOK_DATA = 1013; 
	//电话本同步完成
	public static final int BTMSG_PHONEBOOK_COMPLETE = 1014; 
	//发送蓝牙名称
	public static final int BTMSG_SEND_CONNBTNAME=10015;
	//已断开连接
	public static final int BTMSG_DISCONNECTED=10016;
	//本地蓝牙名称
	public static final int BTMSG_LOC_DEVICENAME=10017;
	//来电名字显示
	public static final int BTMSG_INCOMMING_CALLNAME=10018;
	@Override
	public void onConnecting() {
		// TODO Auto-generated method stub
		//正在连接蓝牙
		System.out.println("connecting....");
		if (null != handlerAutoConn) {
			handlerAutoConn.sendEmptyMessage(BTMSG_CONNECTING);//将正在连接的信号发送给自动连接的服务
		}
		if(null != handlerMain){
			handlerMain.sendEmptyMessage(BTMSG_CONNECTING);	//将正在连接的信号发送给蓝牙主页面
		}
	}

	@Override
	public void onConnected() {
		// TODO Auto-generated method stub
		//蓝牙连接成功
		m_bConnected=true;
		AIDLService.doCallbackCheckBTStatus(m_bConnected);
		System.out.println("connected");
		//将连接完成的信号发送给蓝牙主页面
		if(null != handlerMain){
			Message msg = handlerMain.obtainMessage();
			msg.what = BTMSG_CONNECTED;
			handlerMain.sendMessage(msg);
		}
		//将连接完成的信号发送给蓝牙服务
		if (null != handlerService) {
			Message msg = handlerService.obtainMessage();
			msg.what = BTMSG_CONNECTED;
			handlerService.sendMessage(msg);
		}
	}

	@Override
	public void onActiveCall() {
		// TODO Auto-generated method stub
		System.out.println("接通电话");
		//BTPhoneApplication.getInstance().isTopComing=true;
		m_bActiveCall=true;
		if(null != handlerDial){
			handlerDial.sendEmptyMessage(BTMSG_ACTIVE_CALL);
		}
		//如果手户直接在手机上接听的话，就应该走如下的流程
		if (null != handlerIncoming) {
			handlerIncoming.sendEmptyMessage(BTMSG_ACTIVE_CALL);
		}
		if(null!=handlerMain){
			handlerMain.sendEmptyMessage(BTMSG_ACTIVE_CALL);
		}
		if(null!=handlerService){
			handlerService.sendEmptyMessage(BTMSG_ACTIVE_CALL);
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public void onIncomingCall(String strNumber) {
		// TODO Auto-generated method stub
		//有来电
		System.out.println("有来电.....");
		//将来电信息发到蓝牙主页面
		if(null != handlerMain){
			Message msg = handlerMain.obtainMessage();
			msg.what =BTMSG_INCOMING_CALL;
			msg.obj=strNumber;
			handlerMain.sendMessage(msg);
		}
		//当在后台运行的时候，有来电将来电信息通知蓝牙服务
		if (null != handlerService) {
			Message msg=handlerService.obtainMessage();
			msg.what=BTMSG_INCOMING_CALL;
			msg.obj=strNumber;
			handlerService.sendMessage(msg);
		}

		if(null!=handlerIncoming){
			Message msg=handlerIncoming.obtainMessage();
			msg.what=BTMSG_INCOMING_CALL;
			msg.obj=strNumber;
			handlerIncoming.sendMessage(msg);
		}
		if(ConnService.m_recordDao!=null && !ConnService.m_recordDao.findNumber(strNumber)){
			ConnService.m_recordDao.saveCallRecord(util.inComingName(strNumber),strNumber,System.currentTimeMillis()+"");
		}

	}

	@Override
	public void onPhoneBookData(PBItem pbItem) {
		// TODO Auto-generated method stub
		//电话本数据
		System.out.println("电话本数据");
		//m_strName;    姓名
		//m_strNumber;  电话号码
		//m_iCallType;  类型，未接、已接....

		//同步电话本(包含通话记录),蓝牙在前台时将电话号码发送给蓝牙主页面
		if(null != handlerMain){
			Message msg = handlerMain.obtainMessage();
			msg.what = BTMSG_PHONEBOOK_DATA;
			//String strBookPhone=pbItem.m_iCallType+","+pbItem.m_strName+","+pbItem.m_strNumber;
			msg.obj = pbItem;
			handlerMain.sendMessage(msg);
		}

		//同步电话本(包含通话记录),蓝牙在后台时将电话号码发送给蓝牙服务
		if(null!=handlerService){
			Message msg = handlerService.obtainMessage();
			msg.what = BTMSG_PHONEBOOK_DATA;
			//String strBookPhone=pbItem.m_iCallType+","+pbItem.m_strName+","+pbItem.m_strNumber;
			msg.obj = pbItem;
			handlerService.sendMessage(msg);
		}
	}
	@Override
	public void onPhoneBookComplete() {
		// TODO Auto-generated method stub
		//完成下载电话本
		System.out.println("完成都下载电话本");
		//将电话本同步完成的信息发送到蓝牙主页面
		if(null!=handlerMain){
			handlerMain.sendEmptyMessage(BTMSG_PHONEBOOK_COMPLETE);
		}
		//将电话本同步完成的信息发送到蓝牙服务中
		if (null != handlerService) {
			handlerService.sendEmptyMessage(BTMSG_PHONEBOOK_COMPLETE);
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public void onOutgoingCall(String strNumber) {
		// TODO Auto-generated method stub
		//正在拨打电话
		//将正在拨打电话的信号以及电话号码发送给蓝牙主页面

		Log.e("YL", "onOutgoingCall.............. ");
		if(null != handlerMain){
			Log.e(TAG, "onOutgoingCall.............. 222222222222");
			Message msg = handlerMain.obtainMessage();
			msg.what = BTMSG_OUTGOING_CALL;
			msg.obj=strNumber;
			handlerMain.sendMessage(msg);
		}

		//将正在拨打电话的信号以及电话号码发送给蓝牙服务(手机上拨打，通过服务来启动蓝牙拨号页面)
		if (null != handlerService) {
			Log.e(TAG, "onOutgoingCall.............. 3333333333");
			Message msg = handlerService.obtainMessage();
			msg.what = BTMSG_OUTGOING_CALL;
			msg.obj=strNumber;
			handlerService.sendMessage(msg);//将挂机信号发送给蓝牙服务	
		}
		if(ConnService.m_recordDao!=null && !ConnService.m_recordDao.findNumber(strNumber)){
			ConnService.m_recordDao.saveCallRecord(util.inComingName(strNumber),strNumber,System.currentTimeMillis()+"");
		}
	}

	@Override
	public void onHangup() {
		// TODO Auto-generated method stub
		//挂机
		m_bActiveCall=false;
		LEIDAUtil.setFlashlightEnabled(true);
		LEIDAUtil.setAudioFileEnabled(false);
		if (null != handlerMain) {
			handlerMain.sendEmptyMessage(BTMSG_HUNGUP_CALL);//将挂机信号发送给蓝牙主页面
		}
		if(null != handlerDial){
			handlerDial.sendEmptyMessage(BTMSG_HUNGUP_CALL);//将挂机信号发送给打电话页
		}
		if (null != handlerIncoming) {
			handlerIncoming.sendEmptyMessage(BTMSG_HUNGUP_CALL);//将挂机信号发送给来电页面
		}
		if (null != handlerService) {
			handlerService.sendEmptyMessage(BTMSG_HUNGUP_CALL);//将挂机信号发送给蓝牙服务	
		}

	}

	@Override
	public void onConnectedDeviceName(String strName) {
		// TODO Auto-generated method stub
		//已链接的蓝牙设备的名称
		//蓝牙在前台时将以链接的蓝牙设备名发送给蓝牙主页面
		if(null != handlerMain){
			Message msg = handlerMain.obtainMessage();
			msg.what = BTMSG_SEND_CONNBTNAME;
			msg.obj = strName;
			handlerMain.sendMessage(msg);
		}
		//蓝牙在后台时将已连接的蓝牙名称发送给蓝牙服务
		if(null!=handlerService){
			Message msg = handlerService.obtainMessage();
			msg.what = BTMSG_SEND_CONNBTNAME;
			msg.obj = strName;
			handlerService.sendMessage(msg);
		}
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		m_bConnected=false;
		AIDLService.doCallbackCheckBTStatus(m_bConnected);
		//已断开连接
		//将蓝牙断开的信号发送到蓝牙主页面（清除数据库以及数据列表中的电话本）
		if (null != handlerMain) {
			handlerMain.sendEmptyMessage(BTMSG_DISCONNECTED);
		}
		//将蓝牙断开的信号发送到蓝牙后台服务中（清除数据库以及数据列表中的电话本）
		if (null != handlerService) {
			handlerService.sendEmptyMessage(BTMSG_DISCONNECTED);
		}
	}

	@Override
	public void getLocationDeviceName(String strLocDeviceName) {
		// TODO Auto-generated method stub
		//获取到本地蓝牙的名称
		if(null != handlerMain){
			Message msg = handlerMain.obtainMessage();
			msg.what = BTMSG_LOC_DEVICENAME;
			msg.obj=strLocDeviceName;
			handlerMain.sendMessage(msg);
		}	
	}

	@Override
	public void onA2DPStreamState(Context context, boolean bIsOn) {
		// TODO Auto-generated method stub
		//当前A2DP的状态
		m_bStreamOn=bIsOn;
		LEIDAUtil.setFlashlightEnabled(m_bStreamOn);
		if(null != handlerMain){
			if (m_bStreamOn) {
				Intent castIntent = new Intent();
				castIntent.setAction("com.xintu.btphone.audioswitch");
				castIntent.putExtra("operate", "pause");
				context.sendBroadcast(castIntent);                                                                                                                                                                         
			}
			else {
				LEIDAUtil.setAudioFileEnabled(false);
				Intent castIntent = new Intent();
				castIntent.setAction("com.xintu.btphone.audioswitch");
				castIntent.putExtra("operate", "resume");
				context.sendBroadcast(castIntent);
			}
		}
	}

	@Override
	public void onIncomingCallName(String strName) {
		// TODO Auto-generated method stub
		//来电名字显示
		if(null != handlerMain){
			Message msg = handlerMain.obtainMessage();
			msg.what =BTMSG_INCOMMING_CALLNAME;
			msg.obj=strName;
			handlerMain.sendMessage(msg);
		}
		//当在后台运行的时候，有来电将来电信息通知蓝牙服务
		if (null != handlerService) {
			Message msg=handlerService.obtainMessage();
			msg.what=BTMSG_INCOMMING_CALLNAME;
			msg.obj=strName;
			handlerService.sendMessage(msg);
		}

		if(null!=handlerIncoming){
			Message msg=handlerIncoming.obtainMessage();
			msg.what=BTMSG_INCOMMING_CALLNAME;
			msg.obj=strName;
			handlerIncoming.sendMessage(msg);
		}


	}
}
