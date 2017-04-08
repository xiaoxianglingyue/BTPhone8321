package com.xintu.smartcar.btphone.biz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

import com.xintu.smartcar.btphone.bean.InputFrame;
import com.xintu.smartcar.btphone.listener.FrameListener;


public class BizBase extends Thread implements Runnable{
	private static final String TAG = "BTPhone";
	private InputStream m_is;
	private OutputStream m_os;
	private boolean m_bRunning = false;
	private static BizBase instance = null;
	private FrameListener m_frameListener;
	public static BizBase getInstance() {
		if (instance == null) {
			new BizBase();
		}
		return instance;
	}
	
	public BizBase() {
		instance = this; 
	}
	
	public void startBizBase(InputStream is, OutputStream os, FrameListener frameListener) {
		m_is = is;
		m_os = os;
		m_frameListener = frameListener;
		//启动线程
		m_bRunning = true;
		start();
	}
	
	public void stopBizBase() {
		m_bRunning = false;
		this.interrupt();
		try {
			this.join(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean sendReq(byte[] byteContent) {
		try {
			/*
			 int iLength = byteContent.length;
			//byte[] byteTemp = new byte[iLength];
			for (int i=0; i<iLength; i++) {
				Log.e(TAG, "VLUE " + i +"," + byteContent[i]);
			}
			*/
			Log.e(TAG, "sendReq " +  byteContent.length + "," + new String(byteContent));
			
			m_os.write(byteContent);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public boolean sendReq(byte[] byteContent, int iLength) {
		try {

			m_os.write(byteContent, 0, iLength);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void run() {
		byte[] byteBuffer = new byte[1024];
		SimpleQueue simpleQueue = new SimpleQueue();
		// TODO Auto-generated method stub
		while(m_bRunning) {
			//read the inputstream
			try {			
				int iRead = m_is.read(byteBuffer);
/*				if (iRead > 0) {
					byte[] byteTemp = new byte[iRead];
					System.arraycopy(byteBuffer, 0, byteTemp, 0, iRead);
					//Log.e(TAG, "RRRRRRRRRRRRRRRRRR  " + iRead + "," + new String(byteTemp));
				}*/
				if (iRead > 0) {
					simpleQueue.addBytes(byteBuffer, iRead);
					while(true) {
						InputFrame inputFrame = simpleQueue.readFrame();
						
						if (inputFrame != null) {
							//Log.e(TAG, "frame info " + new String(inputFrame.m_byteFrame));
							m_frameListener.onReceiveFrame(inputFrame);
						}
						else {
							break;
						}
					}
				}
				
				Thread.sleep(50);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

}
