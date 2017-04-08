package com.xintu.smartcar.btphone.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Environment;
import android.util.Log;

public class UploadfileUtil {

	/** 上传文件至Server，srcPath：本地文件路径 
	 *   uploadUrl 服务器路径
	 * 
	 * */  
	public static void uploadFile(String srcPath, String uploadUrl)  
	{  
		String end = "\r\n";  
		String twoHyphens = "--";  
		String boundary = "******";  
		try  
		{  
			URL url = new URL(uploadUrl);  
			HttpURLConnection httpURLConnection = (HttpURLConnection) url  
					.openConnection();  
			// 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃  
			// 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。  
			httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K  
			// 允许输入输出流  
			httpURLConnection.setDoInput(true);  
			httpURLConnection.setDoOutput(true);  
			httpURLConnection.setUseCaches(false);  
			// 使用POST方法  
			httpURLConnection.setRequestMethod("POST");  
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");  
			httpURLConnection.setRequestProperty("Charset", "UTF-8");  
			httpURLConnection.setRequestProperty("Content-Type",  
					"multipart/form-data;boundary=" + boundary);  
			DataOutputStream dos = new DataOutputStream(  
					httpURLConnection.getOutputStream());  
			dos.writeBytes(twoHyphens + boundary + end);  
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""  
					+ srcPath.substring(srcPath.lastIndexOf("/") + 1)  
					+ "\""  
					+ end);  
			dos.writeBytes(end);  
			FileInputStream fis = new FileInputStream(srcPath);  
			byte[] buffer = new byte[8192]; // 8k  
			int count = 0;  
			// 读取文件  
			while ((count = fis.read(buffer)) != -1)  
			{  
				dos.write(buffer, 0, count);  
			}  
			fis.close();  

			dos.writeBytes(end);  
			dos.writeBytes(twoHyphens + boundary + twoHyphens + end);  
			dos.flush();  

			InputStream is = httpURLConnection.getInputStream();  
			InputStreamReader isr = new InputStreamReader(is, "utf-8");  
			BufferedReader br = new BufferedReader(isr);  
			String result = br.readLine();  

			Log.d("LL", result);
			//Toast.makeText(this, result, Toast.LENGTH_LONG).show();

			if("200".equals(result.toString().trim())){//文件上传服务器成功
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  
					try {  
						File dir = new File(Environment.getExternalStorageDirectory()  
								.getAbsolutePath() + File.separator + "crash_btphone_error");  
						deleteFile(dir);//删除本地文件，防止再次上传
					}catch(Exception e){
					}
				}else{
					Log.d("LL", "有异常>>>>>");
				}
			}
			dos.close();  
			is.close();  

		} catch (Exception e)  
		{  
			e.printStackTrace();  
			Log.d("LL", "有异常"+e);
		}  
	}  

	public static void deleteFile(File file){
		if(file.exists()) { 
			if(file.isFile()) {
				file.delete();
			}else{
				if(file.isDirectory()) {
					File files[] = file.listFiles(); 
					for(int i = 0; i < files.length; i++) {
						deleteFile(files[i]);
					}  
				}
				file.delete();
			}
		}
	}
}

