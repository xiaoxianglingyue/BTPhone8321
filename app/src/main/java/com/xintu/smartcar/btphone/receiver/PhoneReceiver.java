package com.xintu.smartcar.btphone.receiver;

import com.xintu.smartcar.btphone.application.BTPhoneApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class PhoneReceiver extends BroadcastReceiver {
	public static Handler handler;
	public static Handler inComingHandler;
	public static Handler dialHandler;
	public static Handler dHandler;
	public static Handler serviceHandler;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals("com.xintu.cloudmirror.maintophone")){
			//收到主程序发来的人名
			String m_name=intent.getStringExtra("linkman");
			if(null!=handler){
				Message msg=handler.obtainMessage();
				Bundle bundle=new Bundle();
				bundle.putString("m_name", m_name);
				msg.what=1001;
				msg.setData(bundle);
				handler.sendMessage(msg);
				m_name="";
			}
		}else if(intent.getAction().equals("com.xintu.cloudmirror.closeallprocess")){
			if(null!=handler){
				handler.sendEmptyMessage(1002);
			}
			if(null!=serviceHandler){
				serviceHandler.sendEmptyMessage(1111);
			}
		}else if(intent.getAction().equals("com.xintu.cloudmirror.main.closebtphone")){
			if(null!=handler){
				handler.sendEmptyMessage(1002);
			}
		}else if(intent.getAction().equals("com.xintu.cloudmirror.main.speechdialog")){
			String style_mode=intent.getStringExtra("style_mode");
			if(null!=handler){
				Message msg=handler.obtainMessage();
				msg.what=1008;
				msg.obj=style_mode;
				handler.sendMessage(msg);
			}
		}else if(intent.getAction().equals("com.xintu.cloudmirror.main.start_bt_req")){
			if(null!=serviceHandler){
				serviceHandler.sendEmptyMessage(1112);
				
			}
		}
		else if(intent.getAction().equals("com.xintu.cloudmirror.maintophone.selectnum")){
			if(null!=dHandler){
				int to_list_num=intent.getIntExtra("to_list_num", 0);
				if(to_list_num==0){}else{
					Message msg=dHandler.obtainMessage();
					intent.putExtra("to_list_num", to_list_num);
					msg.what=1003;
					msg.obj=to_list_num;
					dHandler.sendMessage(msg);
					to_list_num=0;
				}
			}
		}else if(intent.getAction().equals("com.xintu.cloudmirror.maintophone.selectcancel")){
			if(null!=dHandler){
				dHandler.sendEmptyMessage(10033);
			}
		}
		if(intent.getAction().equals("com.xintu.cloudmirror.leftbutton.closebtphone")){//来电
			if(BTPhoneApplication.getInstance().isComing==true){
				if(null!=inComingHandler){
					inComingHandler.sendEmptyMessage(1005);
				}
				BTPhoneApplication.getInstance().isComing=false;
			}
			else{
				if(null!=dialHandler){
					dialHandler.sendEmptyMessage(1006);
				}
				if(null!=handler){
					if(BTPhoneApplication.getInstance().flag==true){
						handler.sendEmptyMessage(1004);
					}else{
						handler.sendEmptyMessage(1009);
					}
				}
			}
			if(BTPhoneApplication.getInstance().isdialogtop==true){
				if(null!=dHandler){
					dHandler.sendEmptyMessage(10034);
				}
			}
		}
	}
}