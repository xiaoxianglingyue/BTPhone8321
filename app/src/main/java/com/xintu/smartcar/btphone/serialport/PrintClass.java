package com.xintu.smartcar.btphone.serialport;


//####################################
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;

public class PrintClass {  
	//������  
	private static InputStream in; 
	//�����  
	private static OutputStream out;   
	private static final String PORT = "/dev/ttyMT1";
	//����   
	private SerialPort serialPort; 


	private static PrintClass instance = null;
	public static Context context;
	public PrintClass(Context context){
		instance = this;
		this.context=context;
	}
	
	public static PrintClass getInstance(Context context) {
		if (instance == null) {
			instance = new PrintClass(context);
			
		}
		return instance;
	}
	
	public void openSerialPort() 
	{  
		try {   
			serialPort = new SerialPort(new File(PORT), 9600);
			in = serialPort.getInputStream();
			out = serialPort.getOutputStream();    
		} 
		catch (SecurityException e) {   
			e.printStackTrace();  
		} catch (IOException e) {
			e.printStackTrace();  
		} 
	}  
	public OutputStream getOutputStream() {
		return out;
	}
	public InputStream getInputStream() {
		return in;
	}
	
	public void closeSerialPort() {
		try {
			out.close(); 
			in.close();   
			serialPort.close();  
		} catch (IOException e) {
			e.printStackTrace();  
		} 
	}
}
