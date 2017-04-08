package com.xintu.smartcar.btphone.listener;

import android.content.Context;

import com.xintu.smartcar.btphone.bean.PBItem;


public abstract class GeneralListener {
	
	public abstract void onConnecting(); //正在连接
	public abstract void onConnected(); //连接完成 
	public abstract void onConnectedDeviceName(String strName);//已连接的设备名
	public abstract void onOutgoingCall(String strNumber); //正在拨打电话;
	public abstract void onActiveCall() ;//接通电话
	public abstract void onHangup();	//挂机
	public abstract void onIncomingCall(String strNumber);//来电
	public abstract void onIncomingCallName(String strName);//来电名字
	public abstract void onDisconnected();//已断开连接
	public abstract void getLocationDeviceName(String strLocDeviceName);//本地蓝牙设备的名称
	public abstract void onA2DPStreamState(Context context, boolean bIsOn);//A2DP的状态
	//电话本
	public abstract void onPhoneBookData(PBItem pbItem); //来了一个数据
	public abstract void onPhoneBookComplete();//结束下载
	
	
}
