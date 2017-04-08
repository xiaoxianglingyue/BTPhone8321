package com.xintu.smartcar.btphone.biz;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.util.Log;

import com.xintu.smartcar.btphone.bean.InputFrame;
import com.xintu.smartcar.btphone.bean.InputStruc;
import com.xintu.smartcar.btphone.bean.OutputFrame;
import com.xintu.smartcar.btphone.bean.PBItem;
import com.xintu.smartcar.btphone.listener.FrameListener;
import com.xintu.smartcar.btphone.listener.GeneralListener;



//主的业务模块
//应包括，可以发送同步命令，也可以发送异步命令，异步命令应包含量有回调函数
public class BizMain{
	private static final String TAG = "BTPhone";
	public final static int BIZ_STATE_INIT = 0;		//初始状态
	public final static int BIZ_STATE_CODEC = 0;	//codec状态
	//public final static int 

	private BizBase m_bizBase;

	private static BizMain instance = null;

	private AsynReq m_AsynReq = new AsynReq();
	private GeneralListener m_generallListener;
	static Context context;
	public BizMain(Context context){
		instance = this;
		this.context=context;
	}

	public static BizMain getInstance(Context context){
		if(instance==null){
			new BizMain(context);
		}
		return instance;
	}

	//全部是异步请求
	//1. 《异步请求》请求重新引导
	public void reqReboot() {
		OutputFrame outFrame = BizProtocol.reqReboot();
		m_AsynReq.send(outFrame);
	}
	//2. 《异步请求》设置上电自动连接
	public void reqSetAutoConn() {
		OutputFrame outFrame = BizProtocol.reqSetAutoConn();
		m_AsynReq.send(outFrame);
	}
	//3. 《异步请求》取消上电自动连接
	public void reqCancelAutoConn() {
		OutputFrame outFrame = BizProtocol.reqCancelAutoConn();
		m_AsynReq.send(outFrame);
	}
	//4. 《异步请求》进入配对模式
	public void reqEnterPairingMode() {
		OutputFrame outFrame = BizProtocol.reqEnterPairingMode();
		m_AsynReq.send(outFrame);
	}
	//5. 《异步请求》取消配对模式, #####暂时不用
	public void reqCancelPairingMode() {
		OutputFrame outFrame = BizProtocol.reqCancelPairingMode();
		m_AsynReq.send(outFrame);
	}
	//6. 《异步请求》连接HFP
	public void reqConnectHFP(int iIndex) {
		OutputFrame outFrame = BizProtocol.reqConnectHFP(iIndex);
		m_AsynReq.send(outFrame);
	}

	//7. 断开HFP，#####暂时不用
	public void reqDisconnectHFP() {
		OutputFrame outFrame = BizProtocol.reqDisconnectHFP();
		m_AsynReq.send(outFrame);
	}

	//8. 接听来电
	public void reqAnswerIncommingCall() {
		OutputFrame outFrame = BizProtocol.reqAnswerIncommingCall();
		m_AsynReq.send(outFrame);
	}

	//9. 拒听来电
	public void reqRejectIncommingCall() {
		OutputFrame outFrame = BizProtocol.reqRejectIncommingCall();
		m_AsynReq.send(outFrame);
	}

	//10. 挂断电话
	public void reqTerminateCall() {
		OutputFrame outFrame = BizProtocol.reqTerminateCall();
		m_AsynReq.send(outFrame);
	}

	//11. 拨打电话
	public void reqDialNum(String strNumber) {
		OutputFrame outFrame = BizProtocol.reqDialNum(strNumber);
		m_AsynReq.send(outFrame);
	}

	//12. 重拨
	public void reqRedial() {
		OutputFrame outFrame = BizProtocol.reqRedial();
		m_AsynReq.send(outFrame);
	}

	//13. 电话本
	public void reqPhoneBook() {
		OutputFrame outFrame = BizProtocol.reqPhoneBook();
		m_AsynReq.send(outFrame);
	}

	//14. 已拨电话
	public void reqPhoneBook_dialed() {
		OutputFrame outFrame = BizProtocol.reqPhoneBook_dialed();
		m_AsynReq.send(outFrame);
	}

	//15. 已接
	public void reqPhoneBook_answered() {
		OutputFrame outFrame = BizProtocol.reqPhoneBook_answered();
		m_AsynReq.send(outFrame);
	}

	//16. 未接
	public void reqPhoneBook_missed() {
		OutputFrame outFrame = BizProtocol.reqPhoneBook_missed();
		m_AsynReq.send(outFrame);
	}


	//17. 设置设备名
	public void reqSetDeviceName(String strName) {
		OutputFrame outFrame = BizProtocol.reqSetDeviceName(strName);
		m_AsynReq.send(outFrame);
	}

	//18. 设置Pincode
	public void reqSetPinCode(String strPinCode) {
		OutputFrame outFrame = BizProtocol.reqSetPinCode(strPinCode);
		m_AsynReq.send(outFrame);
	}

	//19. 拨分机号
	public void reqSendDTMF(String strNumber) {
		OutputFrame outFrame = BizProtocol.reqSendDTMF(strNumber);
		m_AsynReq.send(outFrame);
	}

	//20. 发送获取蓝牙名称的命令
	public void reqDeviceName(){
		OutputFrame outFrame = BizProtocol.reqDeviceName();
		m_AsynReq.send(outFrame);
	}
	//21. 连接最后一个AV设备
	public void reqConnectLastUsed(){
		return;
		//OutputFrame outFrame = BizProtocol.reqConnectLastUsed();
		//m_AsynReq.send(outFrame);
	}
	//22. 查看HFP的状态
	public void reqCheckHFPStatus(){
		OutputFrame outFrame = BizProtocol.reqCheckHFPStatus();
		m_AsynReq.send(outFrame);

	}
	//23. 查看配对的记录
	public void reqCheckPariRecord(){
		OutputFrame outFrame = BizProtocol.reqCheckPariRecord();
		m_AsynReq.send(outFrame);
	}
	
	public void reqConnMobile(){
		OutputFrame outFrame = BizProtocol.reqCheckPariRecord();
		m_AsynReq.send(outFrame);
	}
	public void startMainBiz(InputStream is, OutputStream os, GeneralListener listener) {
		m_generallListener = listener;
		m_bizBase = BizBase.getInstance();
		MyFrameListener myFrameListener = new MyFrameListener(); 
		m_bizBase.startBizBase(is, os, myFrameListener);
	}

	//结束主业务模块
	public void stopMainBiz() {
		m_bizBase.stopBizBase();
	}



	private class MyFrameListener implements FrameListener {
		private boolean strEqul(String str1, String str2) {
			if (str1.equalsIgnoreCase(str2)) {
				return true;
			}
			else {
				return false;
			}
		}
		@Override
		public void onReceiveFrame(InputFrame inputFrame) {
			// TODO Auto-generated method stub
			InputStruc inputStruc = FrameParse.doParse(inputFrame);
			//回调通知应用
			String strResult = inputStruc.m_strResult;
			String strAttach1 = inputStruc.m_strAttach1;
			String strAttach2 = inputStruc.m_strAttach2;
			//String strAttach3 = inputStruc.m_strAttach3;

			if (strEqul(strResult, FrameParse.IND_CONNECTING)) {//连接中
				Log.e(TAG, "连接. 中...........");
				m_generallListener.onConnecting();
			}
			else if (strEqul(strResult, FrameParse.IND_CONNECTED)) {//已连接
				Log.e(TAG, "已连接............");
				m_generallListener.onConnected();
			}else if(strEqul(strResult,FrameParse.IND_DISCONNECTED)){//断开
				m_generallListener.onDisconnected();
			}else if (strResult.startsWith(FrameParse.IND_CONNECTED_PHONENAME)) {//已连接的手机名
				m_generallListener.onConnectedDeviceName(strAttach1);
			}
			else if (strResult.startsWith(FrameParse.IND_DIAL)) { //正在拨打电话

				m_generallListener.onOutgoingCall(strAttach1);
			}
			else if (strEqul(strResult, FrameParse.IND_ACTIVE_CALL)) {//接通电话
				m_generallListener.onActiveCall();
			}
			else if (strEqul(strResult, FrameParse.IND_HANGUP_CALL)) { //挂机
				m_generallListener.onHangup();
			}
			else if (strResult.startsWith(FrameParse.IND_INCOMMING_CALL)) { //来电
				m_generallListener.onIncomingCall(strAttach1);
			}
			else if (strResult.startsWith(FrameParse.IND_INCOMMING_CALLNAME)) {//来电名字显示
				m_generallListener.onIncomingCallName(strAttach1);
			}	
			else if (strResult.startsWith(FrameParse.IND_PB_ITEM)) { //电话本数据
				PBItem pbItem = new PBItem();
				pbItem.m_strName = strAttach1;
				pbItem.m_strNumber = strAttach2;
				if(null!=strAttach1&&null!=strAttach2)
					m_generallListener.onPhoneBookData(pbItem);
			}else if(strResult.startsWith(FrameParse.IND_PD_ITEM)){
				
				if(null!=strResult&&null!=strResult){
					Log.d("MM", strResult.substring(2));
					PBItem pbItem = new PBItem();
					pbItem.m_strName = "未知";
					pbItem.m_strNumber = strResult.substring(2);
					m_generallListener.onPhoneBookData(pbItem);
				}
			}	
			else if (strEqul(strResult, FrameParse.IND_PB_COMPLETED)) { //完成下载电话本
				m_generallListener.onPhoneBookComplete();
			}else if(strResult.startsWith(FrameParse.IND_DEVICE_NAME)) {//蓝牙本地名字
				m_generallListener.getLocationDeviceName(strAttach1);
			}else if(strEqul(strResult, FrameParse.IND_A2DP_CONN))	{//当前A2DP的状态已连接
				m_generallListener.onA2DPStreamState(context,true);
			}else if(strEqul(strResult, FrameParse.IND_A2DP_DISCONN)) {//当前A2DP的状态已断开
				m_generallListener.onA2DPStreamState(context,false);
			}else if(strResult.startsWith(FrameParse.IND_PARI_LIST)){	
			}	
		}
	}
	
	
	public static String decode(String bytes) {
		String hexString="0123456789ABCDEF"; 
		ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
		//将每2位16进制整数组装成一个字节
		for (int i = 0; i < bytes.length(); i += 2)
		baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
		return new String(baos.toByteArray());
		}
	
/*	public static String decode(String bytes) 
	{ 
    String hexString="0123456789ABCDEF"; 
	ByteArrayOutputStream baos=new ByteArrayOutputStream(bytes.length()/2); 
	//将每2位16进制整数组装成一个字节 
	for(int i=0;i<bytes.length();i+=2) 
	baos.write((hexString.indexOf(bytes.charAt(i))<<4 |hexString.indexOf(bytes.charAt(i+1)))); 
	return new String(baos.toByteArray()); 
	} */
}

