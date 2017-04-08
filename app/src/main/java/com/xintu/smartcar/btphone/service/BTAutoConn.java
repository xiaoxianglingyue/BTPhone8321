package com.xintu.smartcar.btphone.service;

import com.xintu.smartcar.btphone.MyListener;
import com.xintu.smartcar.btphone.application.BTPhoneApplication;
import com.xintu.smartcar.btphone.biz.BizMain;
import com.xintu.smartcar.btphone.utils.GetSystemVision;
import com.xintu.smartcar.btphone.utils.LEIDAUtil;
import com.xintu.smartcar.btphone.utils.MyLock;
import com.xintu.smartcar.btphone.utils.SharedPreferencesUtil;

import android.content.Context;
import android.util.Log;

public class BTAutoConn {
	private static final String TAG = "BTPhone";

	private BTAutoConnThread m_autoConnThread;
	private BizMain m_bizMain;
	private Context m_context;
	private boolean isflag=true;

	private MyLock m_threadLock = new MyLock();
	public boolean m_isBackground = true;

	public boolean m_isRunning = true;

	public BTAutoConn() {

	}

	public void blockThread() {
		m_threadLock.setBlock();
	}

	public void wakeThread() {
		m_threadLock.wakeBlock();
	}


	public void start(Context context, BizMain bizMain, MyListener myListener) {
		m_context = context;
		m_bizMain = bizMain;
		//启动自动连接线程
		m_autoConnThread = new BTAutoConnThread();
		m_autoConnThread.start();
	}
	public void stop() {

		m_autoConnThread.interrupt();
		try {
			m_autoConnThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class BTAutoConnThread extends Thread {
		public void run() {
			m_bizMain.reqSetAutoConn();//请求上电后自动连接
			String strDevicesName=SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "strDevicesName", "H25302");
			if ("H25302".equals(strDevicesName)) {
				//如果从来没有配对过设备，则没有操作
				//return;
			}else{
				//已配对设备存入数据库（相当于更新数据库）
			}
			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} //启动线程前停留5S

			long lCounter = 0;
			//int  iConnIndex = 0;
			//boolean bSetConnectLast = true;
			//DeviceInfo[] listPairedDevice; //已配对设备列表
			//Log.d("TOTOTO", "msg5:"+"到这");
			while(m_isRunning) {
				m_threadLock.needBlock();
				//sleep
				try {
					Thread.sleep(3 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				lCounter += 1;
				//是否要打开speaker
				if (MyListener.m_bStreamOn == true || MyListener.m_bActiveCall == true) {
					
					
					if("1".equals(LEIDAUtil.proc_readAudio())){//fm开
						LEIDAUtil.setAudioFileEnabled(false);
					}else if("0".equals(LEIDAUtil.proc_readAudio())){//fm关
						//在拨打电话的状态，查看功放是否被打开，没打开则打开
						if(LEIDAUtil.proc_readAudio().equals("0")){
							LEIDAUtil.setAudioFileEnabled(true);
						}
					}
					//LEIDAUtil.setAudioFileEnabled(true);
					//if(Integer.parseInt(GetSystemVision.getSystemVersion())>2000){
						//打开speaker
						/*String power_str=LEIDAUtil.proc_readfmpower();//TODO　ｌｌ
						try {
							int power=Integer.parseInt(power_str);
							if(power==0&&"0".equals(LEIDAUtil.proc_readAudio())){
								Log.d("MM", LEIDAUtil.proc_readAudio()+">>>>>1"+power);
								LEIDAUtil.setAudioFileEnabled(true);
								Log.d("MM", LEIDAUtil.proc_readAudio()+">>>>>2"+power);
							}
                            
						} catch (NumberFormatException e) {
							LEIDAUtil.setAudioFileEnabled(true);
						}*/
					/*}else{
						LEIDAUtil.setAudioFileEnabled(true);
					}*/
				}
				if (lCounter % 4 != 0) {
					continue;
				}

				if (BTPhoneApplication.getInstance().isConnDevices == false) {
					Log.v("TOTOTO", "isConnDevices=false不会自动链接");
					continue;
				}
				if (BTPhoneApplication.getInstance().flag==true) {
					continue;
				}

				//Log.e(TAG, "autoconn work!");
				//listPairedDevice = m_bizMain.reqGetPairedDevice();
				//if (listPairedDevice == null || listPairedDevice.length == 0) {
				//	continue;
				//}

				//先看当前是否已存在连接
				//String strConnStatus =SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "isBtConn", "false");
				
				if(MyListener.m_bConnected==true){
					//Log.v("HH", "链接成功");
					//当前有连接设备
					//for (int i=0; i<listPairedDevice.length; i++) {
					//	if (listPairedDevice[i].m_iDeviceID == iDeviceID) {
					//		bSetConnectLast = true;
					//		iConnIndex = 0;
					//		break;
					//	}
					//}
				}
				else { //当前没有连接的设备
					//Log.v("HH", "断开链接");
					m_bizMain.reqCancelAutoConn();//上电后取消自动链接
					m_bizMain.reqConnectHFP(1);//
					//尝试进行连接,最后一次连接的设备
					
					//m_bizMain.reqConnectLastUsed();

					/*if (bSetConnectLast == true) {
						m_bizMain.reqConnectLastUsed();
						bSetConnectLast = false;
						Log.e(TAG, "m_bizMain.reqConnect last connected LastUsed" );
					}
					else {
						if (listPairedDevice != null && listPairedDevice.length != 0) {
							if (iConnIndex <listPairedDevice.length) {
								String strMacAddr = listPairedDevice[iConnIndex].m_strMacAddr;
								m_bizMain.reqConnect(strMacAddr);
								bSetConnectLast = false;
								iConnIndex += 1;

								Log.e(TAG, "m_bizMain.reqConnect" + strMacAddr );
							}
							else {
								bSetConnectLast = true;
								iConnIndex = 0;
							}
						}
					}*/
				}
			}//while
		}//public void run();
	}
}
