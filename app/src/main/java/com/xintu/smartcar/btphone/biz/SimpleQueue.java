package com.xintu.smartcar.btphone.biz;

import java.util.ArrayList;
import java.util.List;

import com.xintu.smartcar.btphone.bean.InputFrame;



public class SimpleQueue {
	private final static int PREDEF_BUFFER_LEN = 4096;
	private byte[] m_byteBuffer = new byte[PREDEF_BUFFER_LEN];
	private int m_iTotalLen = 0;
	//将读到的内容加入到buffer中，如果能返回一个完整frame,则返回，否则返回空
	public void addBytes(byte[] byteInput, int iInputLen) {
		System.arraycopy(byteInput, 0, m_byteBuffer, m_iTotalLen, iInputLen);
		m_iTotalLen += iInputLen;
	}
	public InputFrame readFrame() {
		if (m_iTotalLen < 2) {
			return null;
		}
		int iEndPos = 0;
		int i= 0;
		for (i=0; i<m_iTotalLen-1; i++) {
			if (m_byteBuffer[i] == '\r' && m_byteBuffer[i + 1] == '\n') {
				iEndPos = i;
				break;
			}
		}
		if (i == m_iTotalLen-1) {
			//找到头没有\r\n
			return null;
		}
		InputFrame inputFrame = new InputFrame(m_byteBuffer, iEndPos);
		int iRest = m_iTotalLen - iEndPos - 2;
		for (i=0; i<iRest; i++) {
			m_byteBuffer[i] = m_byteBuffer[iEndPos + 2 + i];
		}
		m_iTotalLen = iRest;
		
		return inputFrame;
	}
	
	
private List<String> m_listContent;
	
	
	public SimpleQueue() {
		m_listContent = new ArrayList<String>();
	}
	public synchronized String getData() {
		if (m_listContent.size() == 0) {
			return null;
		}
		String item = m_listContent.get(0);
		m_listContent.remove(0);
		
		return item;
	}
	
	public synchronized void setData(String item) {
		m_listContent.add(item);
	}
	
	public synchronized boolean isEmpty() {
		return m_listContent.isEmpty();
	}
}
