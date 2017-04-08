package com.xintu.smartcar.btphone.bean;

//电话本条目
public class PBItem {
	public final static int CALL_TYPE_NONE = -1;		//
	public final static int CALL_TYPE_PHONEITEM = 0;	//电话本条目
	public final static int CALL_TYPE_MISSED = 1;		//未接
	public final static int CALL_TYPE_RECEIVED = 2;		//已接
	public final static int CALL_TYPE_DIALED = 3;		//已拨
	
	public String m_strName;
	public String m_strNumber;
	
	public int m_iCallType;				//类型，未接、已接....
	
	public PBItem() {
		reset();
	}
	
	public void reset() {
		m_strName = "";
		m_strNumber = "";
		m_iCallType = CALL_TYPE_NONE;
	}
	
	
}
