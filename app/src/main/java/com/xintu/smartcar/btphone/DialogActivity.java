package com.xintu.smartcar.btphone;

import java.util.ArrayList;
import com.xintu.smartcar.btphone.adapter.DialogAdapter;
import com.xintu.smartcar.btphone.application.BTPhoneApplication;
import com.xintu.smartcar.btphone.bean.SelectContact;
import com.xintu.smartcar.btphone.biz.BizMain;
import com.xintu.smartcar.btphone.receiver.PhoneReceiver;
import com.xintu.smartcar.btphone.utils.SharedPreferencesUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class DialogActivity extends Activity {

	private ListView lv_dialog;
	private ArrayList<SelectContact> list;
	private DialogAdapter adapter;
	private BizMain m_bizMain;

	public Handler dhandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==1003){
				BTPhoneApplication.getInstance().flag=true;
				int	numberitem=(Integer) msg.obj;
				String name=list.get(numberitem-1).getName();
				String number=list.get(numberitem-1).getNumber();
				String strConnStatus =SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "isBtConn", "false");
				if("true".equals(strConnStatus)){
					dial(name,number);
					finish();
				}else{
					BTPhoneApplication.getInstance().flag=false;
					Intent sendbroad = new Intent();
					sendbroad.setAction("com.xintu.btphone.phonehaslist");
					sendbroad.putExtra("name_list", "none");
					sendbroad.putExtra("speech", "蓝牙未连接");
					sendBroadcast(sendbroad);
				}
			}else if(msg.what==10033){
				BTPhoneApplication.getInstance().flag=false;
				BTPhoneApplication.getInstance().iscall=true;
				BTPhoneApplication.getInstance().isdialogtop=false;
				finish();
			}else if(msg.what==10034){
				BTPhoneApplication.getInstance().flag=false;
				BTPhoneApplication.getInstance().isdialogtop=false;
				finish();
				Intent sendbroad = new Intent();
				sendbroad.setAction("com.xintu.btphone.speechhit");
				sendbroad.putExtra("speechhit", "");
				sendBroadcast(sendbroad);
			}
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(false);
		setContentView(R.layout.dialog);
		Window window = getWindow();  
		/*LayoutParams lp=window.getAttributes();
		*/

		WindowManager.LayoutParams layoutParams = window.getAttributes();  
		window.setGravity(Gravity.CENTER);
		layoutParams.x=280;
		layoutParams.y=0;
		//window.setAttributes(layoutParams);
		//设置窗口的大小及透明度
		lv_dialog=(ListView) findViewById(R.id.lv_dialog);
		//window.setGravity(Gravity.BOTTOM);
		PhoneReceiver.dHandler=dhandler;
		Intent intent=getIntent();
		list=(ArrayList<SelectContact>) intent.getSerializableExtra("list");
		if(list.size()==1){
			layoutParams.height = 120;
		}else if(list.size()==2){
			layoutParams.height=150;
		}else if(list.size()==3){
			layoutParams.height = 190;
		}else if(list.size()==4){
			layoutParams.height = 240;
		}else{
			layoutParams.height = 280;
		}
		layoutParams.width = 580;
		window.setAttributes(layoutParams); 
		//m_bizMain=BizMain.getInstance(this);
		adapter=new DialogAdapter(this,list);
		lv_dialog.setAdapter(adapter);
		lv_dialog.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				//String spStr[] = list.get(position).toString().split(":");  
				String name=list.get(position).getName();
				String number=list.get(position).getNumber();
				//Log.d("MainActivity", number);
				String strConnStatus =SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "isBtConn", "false");
				if("true".equals(strConnStatus)){
					BTPhoneApplication.getInstance().flag=true;
					dial(name,number);
					finish();

				}else{
					speech();
					finish();
				}
				Intent sendbroad = new Intent();
				sendbroad.setAction("com.xintu.btphone.closedialog");
				sendBroadcast(sendbroad);

			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		BTPhoneApplication.getInstance().isdialogtop=true;
		BTPhoneApplication.getInstance().dialogActivity=DialogActivity.this;
	}


	public void dial(String m_name,String number){

		Intent intent=new Intent(DialogActivity.this,DialActivity.class);
		//tv_phoneName.setText(m_name);
		//m_bizMain.reqDialNum(number.trim());
		doDial(number.trim());
		intent.putExtra("name",m_name);
		intent.putExtra("number",number);
		startActivity(intent);
		m_name="";
		number="";
	}
	private void doDial(String number){
		BTPhoneApplication.getInstance().m_isDailByMirror = true;
		m_bizMain.reqDialNum(number);
	}

	public void speech(){
		BTPhoneApplication.getInstance().flag=false;
		Intent sendbroad = new Intent();
		sendbroad.setAction("com.xintu.btphone.speechhit");
		sendbroad.putExtra("speechhit", "蓝牙未连接");
		sendBroadcast(sendbroad);
	}
	public void click(View v){
		BTPhoneApplication.getInstance().flag=false;
		BTPhoneApplication.getInstance().iscall=true;
		finish();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		finish();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		BTPhoneApplication.getInstance().iscall=true;
		//Intent sendbroad = new Intent();
		/*sendbroad.setAction("com.xintu.btphone.speechhit");
		sendbroad.putExtra("speechhit", "");
		sendBroadcast(sendbroad);*/
		//BTPhoneApplication.getInstance().isdialogtop=false;
		Log.v("MM", "onDestroy");
		//BTPhoneApplication.getInstance().dialogActivity=null;
	}
}
