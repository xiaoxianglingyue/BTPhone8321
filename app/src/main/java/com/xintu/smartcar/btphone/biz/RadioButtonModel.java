package com.xintu.smartcar.btphone.biz;

/**
 * 回复Entity
 * @author ChenLiheng
 * @date 2015-6-15
 */
public class RadioButtonModel {

	private String id;
	
	private String content;
	

	public RadioButtonModel(String id, String content) {
		super();
		this.id = id;
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
