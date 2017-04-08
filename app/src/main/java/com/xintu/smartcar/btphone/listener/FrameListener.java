package com.xintu.smartcar.btphone.listener;

import com.xintu.smartcar.btphone.bean.InputFrame;


public abstract interface FrameListener {
	public abstract void onReceiveFrame(InputFrame inputFrame);
}


