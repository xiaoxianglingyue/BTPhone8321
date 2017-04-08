package com.xintu.smartcar.btphone.biz;

import android.util.Log;

import com.xintu.smartcar.btphone.bean.OutputFrame;


//实现具体协议

public class BizProtocol {
	private static final String TAG = "BTPhone";
	//上电初始化
	private final static String REQ_REBOOT = "CZ";			
	//设置上电自动连接
	private final static String REQ_SET_AUTOCONN = "MG";
	//取消上电自动连接 
	private final static String REQ_CANCEL_AUTOCONN = "MH";
	//进入配对模式
	private final static String REQ_ENTER_PAIRING_MODE = "CA";
	//取消配对模式
	private final static String REQ_CANCEL_PAIRING_MODE = "CB";
	//连接HFP
	private final static String REQ_CONNECT_HFP = "CC";
	//断开HFP
	private final static String REQ_DISCONNECT_HFP = "CD";
	//接听来电
	private final static String REQ_ANSWER_INCOMMING_CALL = "CE";
	//拒听来电
	private final static String REQ_REJECT_INCOMMING_CALL = "CF";
	//结束通话
	private final static String REQ_TERMINATE_CALL = "CG";
	//拨打电话
	private final static String REQ_DIAL_NUM = "CW";
	//重拨
	private final static String REQ_REDIAL = "CH";
	//请求电话本
	private final static String REQ_PHONEBOOK = "PA";
	//请求已拨电话
	private final static String REQ_PHONEBOOK_DIALED = "PH";
	//请求已接电话
	private final static String REQ_PHONEBOOK_ANSWERED = "PI";
	//请求未接电话
	private final static String REQ_PHONEBOOK_MISSED = "PJ";
	//设置设备名称
	private final static String REQ_SET_DEV_NAME = "MM";
	//设置设备PIN码
	private final static String REQ_SET_PIN_CODE = "MN";
	//拨分机号
	private final static String REQ_DIALEXTENSION_NUM="CX";
	//连接最后一个AV 设备
	private static final String REQ_CONNECT_LASTUSED = "MI";
	//查看HFP的连接状态
	private static final String REQ_CHECK_HFPSTATUS = "CY";
	
	private static final String REQ_CHECK_PARIRECORD="MX";

	private static byte[] formatReqData(String strInput) {
/*		int iLen = strInput.length();
		byte[] byteContent = new byte[5 + iLen];
		byteContent[0] = 'A';
		byteContent[1] = 'T';
		byteContent[2] = '#';
		byteContent[3] = 'C';
		byteContent[4] = 'A';
		byteContent[5] = '\r';
		byteContent[6] = '\n';
		
		return byteContent;*/
		
		String strOutput = "AT#" + strInput + "\r\n";
		return strOutput.getBytes();
	}

	//1. 模块重新启动
	public static OutputFrame reqReboot() {
		byte []byteResult = formatReqData(REQ_REBOOT);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;
	}
	//2. 设置上电自动连接
	public static OutputFrame reqSetAutoConn() {
		byte []byteResult = formatReqData(REQ_SET_AUTOCONN);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;
	}
	//3. 取消上电自动连接
	public static OutputFrame reqCancelAutoConn() {
		byte []byteResult = formatReqData(REQ_CANCEL_AUTOCONN);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;		
	}
	//4. 进入配对模式
	public static OutputFrame reqEnterPairingMode() {
		byte []byteResult = formatReqData(REQ_ENTER_PAIRING_MODE);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;		
	}
	//5. 取消配对模式
	public static OutputFrame reqCancelPairingMode() {
		byte []byteResult = formatReqData(REQ_CANCEL_PAIRING_MODE);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;		
	}
	//6. 连接HFP
	public static OutputFrame reqConnectHFP(int iIndex) {
		byte []byteResult = formatReqData(REQ_CONNECT_HFP + iIndex);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;		
	}
	//7. 断开HFP
	public static OutputFrame reqDisconnectHFP() {
		byte []byteResult = formatReqData(REQ_DISCONNECT_HFP);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;		
	}
	//8. 接听来电
	public static OutputFrame reqAnswerIncommingCall() {
		byte []byteResult = formatReqData(REQ_ANSWER_INCOMMING_CALL);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;		
	}	
	//9. 拒听来电
	public static OutputFrame reqRejectIncommingCall() {
		byte []byteResult = formatReqData(REQ_REJECT_INCOMMING_CALL);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;		
	}	
	//10. 挂断电话
	public static OutputFrame reqTerminateCall() {
		byte []byteResult = formatReqData(REQ_TERMINATE_CALL);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;		
	}
	//11. 拨打电话
	public static OutputFrame reqDialNum(String strNumber) {
		
		byte []byteResult = formatReqData(REQ_DIAL_NUM + strNumber);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;		
	}
	//12. 重拨
	public static OutputFrame reqRedial() {
		byte []byteResult = formatReqData(REQ_REDIAL);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;		
	}

	//13. 电话本
	public static OutputFrame reqPhoneBook() {
		byte []byteResult = formatReqData(REQ_PHONEBOOK);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;		
	}	

	//14. 已拨电话
	public static OutputFrame reqPhoneBook_dialed() {
		byte []byteResult = formatReqData(REQ_PHONEBOOK_DIALED);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;		
	}	
	//15. 已接
	public static OutputFrame reqPhoneBook_answered() {
		byte []byteResult = formatReqData(REQ_PHONEBOOK_ANSWERED);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;		
	}	
	//16. 未接
	public static OutputFrame reqPhoneBook_missed() {
		byte []byteResult = formatReqData(REQ_PHONEBOOK_MISSED);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;		
	}	

	//17. 设置设备名(自己定义的名称)
	public static OutputFrame reqSetDeviceName(String strName ) {
		byte []byteResult = formatReqData(REQ_SET_DEV_NAME + strName);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;		
	}	
	//18. 设置Pincode
	public static OutputFrame reqSetPinCode(String strPinCode) {
		byte []byteResult = formatReqData(REQ_SET_PIN_CODE + strPinCode);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;		
	}
	//19. 拨分机号
	public static OutputFrame reqSendDTMF(String strNumber) {
		byte []byteResult = formatReqData(REQ_DIALEXTENSION_NUM + strNumber);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;		
	}
	//20. 发送获取蓝牙名称的命令(得到蓝牙本本身的名称)
	public static OutputFrame reqDeviceName(){
		byte []byteResult = formatReqData(REQ_SET_DEV_NAME);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;	
	}
	//21. 连接最后一个 AV 设备
	public static OutputFrame reqConnectLastUsed(){
		byte []byteResult = formatReqData(REQ_CONNECT_LASTUSED);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;	
	}
	//22. 查看HFP的连接状态
	public static OutputFrame reqCheckHFPStatus() {
		// TODO Auto-generated method stub
		byte []byteResult = formatReqData(REQ_CHECK_HFPSTATUS);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;
	}
	
	public static OutputFrame reqCheckPariRecord(){
		byte []byteResult = formatReqData(REQ_CHECK_PARIRECORD);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;
	}
	
	public static OutputFrame reqConnMobile(){
		byte []byteResult = formatReqData(REQ_CHECK_PARIRECORD);
		OutputFrame outFrame = new OutputFrame(byteResult);
		return outFrame;
	}
}
