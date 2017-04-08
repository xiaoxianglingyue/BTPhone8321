package com.xintu.smartcar.btphone.bean;

public class InputFrame {
	public int m_iFrameLen;  //包括了头与长度两个字节, 就是m_byteFrame的长度
	public byte[] m_byteFrame;

	public InputFrame(byte[] byteInput, int iFrameLen) {
		m_iFrameLen = iFrameLen;
		m_byteFrame = new byte[m_iFrameLen];
		System.arraycopy(byteInput, 0, m_byteFrame, 0, m_iFrameLen);
	}
}
