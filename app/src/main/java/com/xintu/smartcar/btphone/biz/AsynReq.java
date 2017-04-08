package com.xintu.smartcar.btphone.biz;

import com.xintu.smartcar.btphone.bean.OutputFrame;


//异步请求管理
public class AsynReq {
	public void send(OutputFrame outFrame) {
		BizBase bizBase = BizBase.getInstance();
		bizBase.sendReq(outFrame.m_byteFrame);
	}
}
