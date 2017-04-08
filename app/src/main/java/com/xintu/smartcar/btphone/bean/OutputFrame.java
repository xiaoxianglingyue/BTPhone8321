package com.xintu.smartcar.btphone.bean;

public class OutputFrame {
	public int m_iFrameLen;
	public byte[] m_byteFrame;	
	
	public OutputFrame(byte []byteInput) {
		m_iFrameLen = byteInput.length;
		m_byteFrame = new byte[m_iFrameLen];
		System.arraycopy(byteInput, 0, m_byteFrame, 0, m_iFrameLen);
	}
	public OutputFrame(byte []byteInput, int iLen) {
		m_iFrameLen = iLen;
		m_byteFrame = new byte[m_iFrameLen];
		System.arraycopy(byteInput, 0, m_byteFrame, 0, m_iFrameLen);
	}
}
