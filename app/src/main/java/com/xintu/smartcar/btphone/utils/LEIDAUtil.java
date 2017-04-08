package com.xintu.smartcar.btphone.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.util.Log;

public class LEIDAUtil {

	private static File mFlashlightFile;
	private static File mAccStateFile;
	private static File mCarbackStateFile;
	private static File mCloudMirrorTemp;
	private static File mFMSendPower;
	private static File mFMSendNum;
	private static void openFile() {
		mFlashlightFile = new File("/proc/xunhu_rander");//雷达开关
		mAccStateFile=new File("/proc/xunhu_accdet");//ACC开关
		mCarbackStateFile=new File("/proc/xunhu_carback");//后置摄像头
		mCloudMirrorTemp=new File("/sys/class/thermal/thermal_zone1/temp");//机器温度
		mFMSendPower=new File("/proc/xunhu_kt0806l_power");//FM开关
		mFMSendNum=new File("/proc/xunhu_kt0806l_channel");//FM发送频道值，默认值9150，即91.50
	}

	public static boolean searchFmSendProc(){
		mFMSendPower=new File("/proc/xunhu_kt0806l_power");//FM开关
		if(mFMSendPower.exists()){
			return true;
		}
		return false;
	}
	
	public static boolean searchFmSendNumProc(){
		mFMSendPower=new File("/proc/xunhu_kt0806l_channel");//FM开关
		if(mFMSendPower.exists()){
			return true;
		}
		return false;
	}

	public static void setFMSendPower(boolean enabled) {
		openFile();
		if (enabled) {
			proc_write_fmsendpower(new char[]{'1'}, 1);
		} else {
			proc_write_fmsendpower(new char[]{'0'}, 1);
		}
	}

	public static void setFMSendNum(String str) {
		openFile();
		proc_write_fmsendnum(str);
	}

	public static void setCarbackPower(boolean isopen){
		openFile();
		if (isopen) {
			proc_write_carback(new char[]{'1'}, 1);
		} else {
			proc_write_carback(new char[]{'0'}, 1);
		}
	}

	public static void setFlashlightEnabled(boolean enabled) {
		openFile();
		if (enabled) {
			proc_write(new char[]{'1'}, 1);
		} else {
			proc_write(new char[]{'0'}, 1);
		}
	}

	public static String proc_read(){
		openFile();
		String s="";
		FileReader reader = null;
		BufferedReader br = null;
		try{
			reader = new FileReader(mAccStateFile);
			br = new BufferedReader(reader);
			if ((s = br.readLine()) != null) {
				Log.v("TOTOTO", "");
				return s;
			}

		}catch (IOException e){
			e.printStackTrace();
		}finally{
			try {
				if(br!=null)
					br.close();
				if(reader!=null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return s;
	}
	
	public static String proc_readFMsendNum(){
		openFile();
		String s="";
		FileReader reader = null;
		BufferedReader br = null;
		try{
			reader = new FileReader(mFMSendNum);
			br = new BufferedReader(reader);
			if ((s = br.readLine()) != null) {
				return s;
			}

		}catch (IOException e){
			e.printStackTrace();
		}finally{
			try {
				if(br!=null)
					br.close();
				if(reader!=null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return s;
	}

	public static String proc_readtemp(){
		openFile();
		String s="";
		FileReader reader = null;
		BufferedReader br = null;
		try{
			reader = new FileReader(mCloudMirrorTemp);
			br = new BufferedReader(reader);
			if ((s = br.readLine()) != null) {
				return s;
			}

		}catch (IOException e){
			e.printStackTrace();
		}finally{
			try {
				if(br!=null)
					br.close();
				if(reader!=null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return s;
	}
	
	public static String proc_readfmpower(){
		openFile();
		String s="";
		FileReader reader = null;
		BufferedReader br = null;
		try{
			reader = new FileReader(mFMSendPower);
			br = new BufferedReader(reader);
			if ((s = br.readLine()) != null) {
				return s;
			}

		}catch (IOException e){
			e.printStackTrace();
		}finally{
			try {
				if(br!=null)
					br.close();
				if(reader!=null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return s;
	}

	private static void proc_write(char[] chars, int count){
		try{
			FileWriter writer = new FileWriter(mFlashlightFile);
			writer.write(chars, 0, count);
			writer.flush();
			writer.close();
			writer = null;
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	private static void proc_write_carback(char[] chars, int count){
		FileWriter writer=null;
		try{
			writer = new FileWriter(mCarbackStateFile);
			if(writer!=null){
				writer.write(chars, 0, count);
				writer.flush();
				writer.close();
				writer = null;
			}
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	private static void proc_write_fmsendpower(char[] chars, int count){
		FileWriter writer=null;
		try{
			writer = new FileWriter(mFMSendPower);
			if(writer!=null){
				writer.write(chars, 0, count);
				writer.flush();
				writer.close();
				writer = null;
			}
		}catch (IOException e){
			Log.e("POPOPO", "exception");
		}
	}

	private static void proc_write_fmsendnum(String str){
		FileWriter writer=null;
		try{
			writer = new FileWriter(mFMSendNum);
			if(writer!=null){
				writer.write(str);
				writer.flush();
				writer.close();
				writer = null;
			}
		}catch (IOException e){
			e.printStackTrace();
			Log.e("TOTOTO", "exception");
			Log.e("TOTOTO", e.toString());
		}
	}

	private static File mopenFileAudio;
	private static void openFileAudio() {
		mopenFileAudio = new File("/proc/xunhu_saudio");
	}

	public static void setAudioFileEnabled(boolean enabled) {
		openFileAudio();
		if (enabled) {
			proc_write_audio(new char[]{'1'}, 1);
		} else {
			proc_write_audio(new char[]{'0'}, 1);
		}
	}

	private static void proc_write_audio(char[] chars, int count){

		try{
			FileWriter writer = new FileWriter(mopenFileAudio);
			writer.write(chars, 0, count);
			writer.flush();
			writer.close();
			writer = null;
		}catch (IOException e){
			e.printStackTrace();
		}
	}	
	
	
	public static String getSystemVersion(){
		String version=android.os.Build.DISPLAY;
		if(version!=null&&version.contains("_")){
			String[] strs_version=version.split("_");
			if(strs_version.length>=4){
				String vs=strs_version[strs_version.length-2].replace("V", "");
				return vs;
			}
		}
		return "";
	}
	
	public static String proc_readAudio(){//TODO l
		openFileAudio();
		String s="";
		FileReader reader = null;
		BufferedReader br = null;
		try{
			reader = new FileReader(mopenFileAudio);
			br = new BufferedReader(reader);
			if ((s = br.readLine()) != null) {
				return s;
			}

		}catch (IOException e){
			e.printStackTrace();
		}finally{
			try {
				if(br!=null)
					br.close();
				if(reader!=null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return s;
	}
}
