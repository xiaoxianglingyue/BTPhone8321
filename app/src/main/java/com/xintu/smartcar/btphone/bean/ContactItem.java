package com.xintu.smartcar.btphone.bean;

import java.io.Serializable;

public class ContactItem implements Serializable{
	
	public int m_iID;
	public String m_strName;
	public String m_strNumber;
	//人员姓名拼音
	private String pinyin;
	//人员姓名拼音首字母
	private String sortletter;
	public ContactItem() {
	}
	
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	public String getSortletter() {
		return sortletter;
	}
	public void setSortletter(String sortletter) {
		this.sortletter = sortletter;
	}
}
