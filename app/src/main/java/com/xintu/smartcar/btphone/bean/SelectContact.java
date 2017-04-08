package com.xintu.smartcar.btphone.bean;

import java.io.Serializable;

public class SelectContact implements Serializable{

	private int count;
	private String name;
	private String number;
	
	public SelectContact(){}
	public SelectContact(int count,String name, String number) {
		this.count=count;
		this.name = name;
		this.number = number;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
}
