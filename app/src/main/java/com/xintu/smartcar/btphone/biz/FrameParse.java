package com.xintu.smartcar.btphone.biz;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import android.util.Log;

import com.xintu.smartcar.btphone.bean.InputFrame;
import com.xintu.smartcar.btphone.bean.InputStruc;
import com.xintu.smartcar.btphone.utils.GlobalUtil;


public class FrameParse {
	//帧数据解析

	//1. 上电初始化成功
	public static final String IND_INIT_OK = "IS";
	//2. 进入配对模式
	public static final String IND_ENDTER_PAIRING_MODE = "II";
	//3. 退出配对模式
	public static final String IND_QUIT_PAIRING_MODE = "IJ";
	//4. 连接中，上层提示“正在连接”
	public static final String IND_CONNECTING = "IV";
	//5. 已连接，上层提示“已连接”
	public static final String IND_CONNECTED = "IB";
	//6. 已断开， 上层提示“已断开”
	public static final String IND_DISCONNECTED = "IA";
	//7. 打出电话， IC13051094312
	public static final String IND_DIAL = "IC";
	//8. 来电, ID13401000936
	public static final String IND_INCOMMING_CALL = "ID";
	//9. 通话中，
	public static final String IND_ACTIVE_CALL = "IG";
	//10. 挂机
	public static final String IND_HANGUP_CALL = "IF";
	//11. A2DP 音乐播放中
	public static final String IND_A2DP_PLAY = "MB";
	//12. A2DP 已连接
	public static final String IND_A2DP_CONN = "MA";
	//13. 设备名称, MMBLUECAR
	public static final String IND_DEVICE_NAME = "MM";
	//14. PIN CODE, MN1234
	public static final String IND_DEVICE_PINCODE = "MN";
	//15. 配对列表, MX17c0191c7d363iPhone
	public static final String IND_PAIRED_LIST = "MX";
	//16. 电话本条目 , PB?????????????+8613701102680
	public static final String IND_PB_ITEM = "PB";
	//17/ 电话本结束
	public static final String IND_PB_COMPLETED = "PC";
	//18. 已连接的设备名，即手机名
	public static final String IND_CONNECTED_PHONENAME = "SA";
	//19. A2DP 已断开
	public static final String IND_A2DP_DISCONN= "MY";
	//20. 来电名字显示
	public static final String IND_INCOMMING_CALLNAME= "IQ";
	// 21. HFP状态
	public static final String IND_HFP_STATUS="MG";
	//22. 通话记录
	public static final String IND_PD_ITEM = "PD";
    //23. 配对列表
	public static final String IND_PARI_LIST = "MX";
	
	public static final String IND_CONN_MOBLIE = "CC";

	//解析接收到的frame,生成结构化数据
	public static InputStruc doParse(InputFrame inputFrame) {

		String 	strFrame = new String(inputFrame.m_byteFrame);

		InputStruc inputStruc = new InputStruc();
		inputStruc.m_strResult = strFrame;
		//只有几种协情况需要进行解析
		if (strFrame.startsWith(IND_PB_ITEM)) {
			//电话本
			//电话本
			String str=strFrame.substring(2);
			
           // Log.d("MM", decode(str));
			String str1[]=str.split("�");
			inputStruc.m_strAttach1=str1[0];
			inputStruc.m_strAttach2=str1[1];
		}else if(strFrame.startsWith(IND_PD_ITEM)){
			String str=strFrame.substring(2);
			inputStruc.m_strAttach2=str;
		}
		else if (strFrame.startsWith(IND_PAIRED_LIST)) {
			//已配对的列表
		}
		else if (strFrame.startsWith(IND_INCOMMING_CALL)) {
			//来电
			inputStruc.m_strAttach1 = strFrame.substring(2); //从第二个字符开始是电话号码
		}
		else if (strFrame.startsWith(IND_DIAL)) {
			//打出电话
			inputStruc.m_strAttach1 = strFrame.substring(2); //从第二个字符开始是电话号码
		}
		else if (strFrame.startsWith(IND_CONNECTED_PHONENAME)) {
			inputStruc.m_strAttach1 = strFrame.substring(2); //从第二个字符开始
		}
		else if(strFrame.startsWith(IND_DEVICE_NAME)) {
			//得到蓝牙本地蓝牙名称
			inputStruc.m_strAttach1 = strFrame.substring(2); //从第二个字符开始
		}
		else if(strFrame.startsWith(IND_INCOMMING_CALLNAME)){
			//来电名字
			inputStruc.m_strAttach1 = strFrame.substring(2); //从第二个字符串开始是来电名字
		}
		else if(strFrame.startsWith(IND_HFP_STATUS)){//查看HFP的状态
			//Log.d("MM", "连接成功");
		}else if(strFrame.startsWith(IND_PARI_LIST)){
			
		}
		else {
			//do nothing 
		}
		return inputStruc;
	}
	

	






}
