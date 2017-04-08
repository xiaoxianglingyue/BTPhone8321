package com.xintu.smartcar.btphone.utils;

public class MyLock {
	private Object m_objNotify = new Object();
	private boolean m_tagBlock = false;
	
	public void needBlock() {
		//调用此函数，以判断是否要进入block状态
		synchronized(m_objNotify) {
			if (m_tagBlock == true) {
				try {
					m_objNotify.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void setBlock() {
		synchronized(m_objNotify) {
			m_tagBlock = true; 
		}
	}
	
	public void wakeBlock() {
		synchronized(m_objNotify) {
			m_tagBlock = false;
			m_objNotify.notify();
		}
	}
	
	
}
