package com.xintu.smartcar.btphone.application;

import java.util.List;

import com.xintu.smartcar.btphone.DialActivity;
import com.xintu.smartcar.btphone.DialogActivity;
import com.xintu.smartcar.btphone.MainActivity;
import com.xintu.smartcar.btphone.service.ConnService;
import com.xintu.smartcar.btphone.service.IncomingFloatActivity;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;


public class BTPhoneApplication extends Application{
	private static final String TAG = "BTPhone";
	private	static BTPhoneApplication instance;
	//DialActiviy是否已启动
	public boolean m_isDialOpened = false;
	public static int mlCount=0;
	//if active by 
	public boolean m_isDailByMirror = false;
	public boolean flag=false;
	public boolean ishungup=false;//机器上拨打后未接通挂�?
	public boolean ismobile=false;
	public boolean isComing = false;//前台来电
	public boolean isconn=false;
	public boolean iscall=false;
	public boolean isdialogtop=false;
	public DialogActivity dialogActivity;
	public boolean isbackcall=false;
	public boolean isActivityTop=false;
	public boolean isConnDevices=true;//判断蓝牙是否连接(用于acc断开和连接上来处理自动链接)
	public boolean isBTTop=true;//判断接电话时蓝牙界面是否在最上面，如果是则不显示蓝牙页面，否则显示
	public MainActivity mainActivity;
	public DialActivity dialActivity;
	public int widthloc=500;
	public int heightloc=25;
	public String come_name="";
	public String come_number="";
	public boolean isflag=false;
	public IncomingFloatActivity incomingFloatActivity;
	public String tempNumber="";
	public boolean isPlayMusic=false;
	public boolean btIsConn=false;//判断蓝牙是否连接。连接为true,断开为false
	public boolean isActionCall=false;
	public boolean isTopComing=false;
	
	private WindowManager.LayoutParams wmParams=new WindowManager.LayoutParams();

	public WindowManager.LayoutParams getMywmParams(){
		return wmParams;
	}
	public static BTPhoneApplication getInstance() {

		return instance;
	}	

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		/*CrashHandler crashHandler = CrashHandler.getInstance();  
		crashHandler.init(this);*/
		Context context = getApplicationContext();
		
		if(isServiceRunning(context,"com.xintu.smartcar.btphone.service.ConnService")==true){
		}else{
			Intent intent = new Intent(context, ConnService.class);
			context.startService(intent);
		}
	}


	/**
	 * 用来判断服务是否运行.
	 * @param context
	 * @param className 判断的服务名�?
	 * @return true 在运�?false 不在运行
	 */
	public static boolean isServiceRunning(Context mContext,String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager)
				mContext.getSystemService(Context.ACTIVITY_SERVICE); 
		List<ActivityManager.RunningServiceInfo> serviceList= activityManager.getRunningServices(30);
		if (!(serviceList.size()>0)) {
			return false;
		}
		for (int i=0; i<serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
	     		break;
			}
		}
		return isRunning;
	}
}
