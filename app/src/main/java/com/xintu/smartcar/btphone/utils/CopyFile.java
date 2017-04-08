package com.xintu.smartcar.btphone.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.xintu.smartcar.btphone.R;
import com.xintu.smartcar.btphone.biz.SimpleQueue;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class CopyFile
{
	private String sourceFile="/storage/sdcard1/autonavidata70/";
	private String newFile="/storage/sdcard0/autonavidata70/";
	private ProgressDialog myProgressDialog;
	private ProgressDialog dialogDelete = null;
	double size = 0;
	private double downLoadFileSize=0 ;
	private boolean startCopy=true;
	public Context context;
	private int count = 0;
	private boolean stopDelete=false;
	String ss="";
	private Dialog hasDialog = null;
	Thread copyThread=null;

	@SuppressLint("HandlerLeak") 
	private Handler handler=new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 1:
				myProgressDialog.setProgress((int)Math.ceil((downLoadFileSize/size)*100));
				if (myProgressDialog.getProgress()>=100)
				{
					if(null!=myProgressDialog){
						//关闭进度条
						myProgressDialog.dismiss();
					}
				}
				break;
			case 2:
				//startCopy=true;
				size=0;
				Toast.makeText(context, "文件拷贝异常", Toast.LENGTH_LONG).show();
				break;
			case 3:
				Toast.makeText(context, "该文件夹下的文件不存在", Toast.LENGTH_LONG).show();
				if(null!=myProgressDialog)
					myProgressDialog.dismiss();
				break;
			case 4:
				startCopy=true;
				if(null!=myProgressDialog)
					myProgressDialog.dismiss();
				size=0;
				Toast.makeText(context, "内存空间不足，请清理后再试", Toast.LENGTH_LONG).show();
				break;
			case 5:
				Toast.makeText(context, "获取文件大小失败,请检查文件!", Toast.LENGTH_LONG).show();
				break;
			case 6:
				if(null!=myProgressDialog)
					myProgressDialog.cancel();
				dialogMsg("正在复制文件，请稍后……",1);
				break;
			case 7:
				count=0;
				if(null!=dialogDelete)
					dialogDelete.dismiss();
				Toast.makeText(context, "文件删除成功！！！", Toast.LENGTH_LONG).show();
				break;
			case 8:
				count=0;
				if(null!=dialogDelete)
					dialogDelete.dismiss();
				stopDelete=false;
				Toast.makeText(context, "删除已取消！！！", Toast.LENGTH_LONG).show();
				break;
			}

			super.handleMessage(msg);
		}
	};	

	public CopyFile(Context context){
		this.context=context;
	}

	public void copyFolder()
	{
		startCopy=true;
		if(SdCardUtils.getSecondExterPath()!=null)//SD卡正常
		{
			//目标目录
			File newTargetDir = new File(newFile);
			//创建目录
			if(newTargetDir.exists()){//文件夹存在
				//文件存在,统计已经复制的文件的大小
				showHasDialog();

			}else{
				copyThread=new Thread(){
					public void run(){
						try {						
							size = getAllFileSize(sourceFile);
							sleep(1000);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							currentThread().interrupt();
							e.printStackTrace();
						}
						ss=getRomAvailableSize().substring(0,getRomAvailableSize().length()-2);
						double w= Double.parseDouble(ss.trim());
						if(size!=0){
							if(w>=4.80){
								sendMsg(6);
								copy(sourceFile,newFile);
							}else{
								sendMsg(4);
							}	
						}else{
							sendMsg(3);
						}
					}
				};
				copyThread.start();
				dialogMsg("正在读取文件的大小，请稍后……",0);
			}

		}else 
		{
			Toast.makeText(context, "SD卡不存在，请安装SD卡", Toast.LENGTH_LONG).show();
		}
	}
	//判断内置SD卡的大小
	public String getRomAvailableSize() {  
		File path = Environment.getDataDirectory();  
		StatFs stat = new StatFs(path.getPath());  
		@SuppressWarnings("deprecation")
		long blockSize = stat.getBlockSize();  
		@SuppressWarnings("deprecation")
		long availableBlocks = stat.getAvailableBlocks();  
		return Formatter.formatFileSize(context, blockSize * availableBlocks);  
	} 

	class MyCancelButton implements DialogInterface.OnClickListener{

		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			startCopy=false;
			if(null!=myProgressDialog){
				myProgressDialog.cancel();
			}
			if(null!=dialogDelete){
				stopDelete=true;
				dialogDelete.cancel();
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void dialogMsg(String str,int i){
		myProgressDialog=new ProgressDialog(context);
		myProgressDialog.setMessage(str);
		myProgressDialog.setMax(100);
		myProgressDialog.setProgress(0);
		myProgressDialog.setTitle("文件复制");
		myProgressDialog.setCancelable(true);
		myProgressDialog.setProgressStyle(i);
		myProgressDialog.setButton("取消", new MyCancelButton());
		myProgressDialog.show();
	}

	/**
	 * 
	 * @param fromFile 源文件的路径
	 * @param toFile   新文件的路径
	 * @return
	 */
	public int copy(String fromFile, String toFile)
	{
		//要复制的文件目录
		File[] currentFiles;
		File root = new File(fromFile);
		//如同判断SD卡是否存在或者文件是否存在
		//如果不存在则 return出去
		if(!root.exists())
		{
			return -1;
		}
		//sendMsg(0);
		//如果存在则获取当前目录下的全部文件 填充数组
		currentFiles = root.listFiles();
		//目标目录
		File targetDir = new File(toFile);
		//创建目录
		if(!targetDir.exists())
		{
			targetDir.mkdirs();
		}
		if(null!=currentFiles){
			//遍历要复制该目录下的全部文件
			for(int i= 0;i<currentFiles.length;i++)
			{
				if(currentFiles[i].isDirectory())//如果当前项为子目录 进行递归
				{
					if(startCopy==false){
						return -1;
					}
					copy(currentFiles[i].getPath() + "/", toFile + currentFiles[i].getName() + "/");

				}else//如果当前项为文件则进行文件拷贝
				{
					if(startCopy==false){
						return -1;
					}
					CopySdcardFile(currentFiles[i].getPath(), toFile + currentFiles[i].getName());
				}
			}
		}else{
			sendMsg(3);
			return -1;
		}
		return 0;
	}

	//文件拷贝
	//要复制的目录下的所有非子目录(文件夹)文件拷贝
	@SuppressWarnings("resource")
	public int CopySdcardFile(String fromFile, String toFile)
	{
		try 
		{
			InputStream fosfrom = new FileInputStream(fromFile);
			OutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024*1024];
			int c;
			//downLoadFileSize = 0;
			while ((c = fosfrom.read(bt)) !=-1) 
			{
				if(startCopy==false){
					sendMsg(2);
					if(null!=copyThread){
						downLoadFileSize=0;
						copyThread.interrupt();
					}
					return -1;
				}
				if(copyThread.isInterrupted()){
					break;
				}
				downLoadFileSize += c;
				sendMsg(1);
				fosto.write(bt, 0, c);
				fosto.flush();				
			}

			fosto.flush();
			fosfrom.close();
			fosto.close();
			return 0;
		} catch (Exception ex) 
		{
			return -1;
		}
	}
	/**
	 * 递归删除文件和文件夹
	 *
	 * @param file
	 *            要删除的根目录
	 */
	public void DeleteFile(final File file) {
		hasDialog.dismiss();
		count++;
		if(stopDelete==true){
			sendMsg(8);
			return;
		}
		if(count==2){
			delete();
		}
		if (file.exists() == false) {
			return;
		} else {
			if (file.isFile()) {
				file.delete();
				return;
			}
			if (file.isDirectory()) {
				File[] childFile = file.listFiles();
				if (childFile == null || childFile.length == 0) {
					file.delete();
					return;
				}
				for (File f : childFile) {
					DeleteFile(f);
				}
				file.delete();
				if(file.getName().equals("autonavidata70"))
					sendMsg(7);
			}
		}
	}


	private void sendMsg(int flag)
	{
		Message msg = new Message();
		msg.what = flag;
		handler.sendMessage(msg);
	}  

	//判断SD卡是否存在
	public boolean checkSDcard()
	{
		//String status=Environment.getExternalStorageState();
		if (Environment.getExternalStorageState().equals("/mnt/sdcard"))
		{
			return true;
		}
		else 
		{
			return false;
		}
	}

	private double getAllFileSize(String strFilePath) {
		double lSize = 0;
		SimpleQueue queue = new SimpleQueue();
		queue.setData(strFilePath);
		while(queue.isEmpty() == false) {
			String strCurPath = queue.getData();
			File file=new File(strCurPath);
			try {
				File flist[] = file.listFiles();
				for (int i=0; i<flist.length; i++) {
					if (flist[i].isDirectory()) {
						queue.setData(flist[i].getAbsolutePath());
					}
					else {
						lSize += flist[i].length();
					}
				}
			}
			catch (Exception e) {
				return lSize;
			}
		}
		return lSize;
	}

	private void showHasDialog() {
		View hintView = View.inflate(context,R.layout.dialog_hint, null);
		hasDialog= new Dialog(context, R.style.dialog);
		hasDialog.setContentView(hintView);
		hasDialog.show();
		ImageView deleteMap=(ImageView) hintView.findViewById(R.id.deleteMap);
		ImageView continueCopy=(ImageView) hintView.findViewById(R.id.continueCopy);

		//删除离线地图
		deleteMap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  
					try {  
						/*File dir = new File(Environment.getExternalStorageDirectory()  
								.getAbsolutePath() + File.separator + "autonavidata70");  */
						DeleteFile(new File(newFile));//删除本地没有复制完成的文件
					}catch(Exception e){
					}
				}else{
					//Toast.makeText(context, "文件删除异常", Toast.LENGTH_LONG).show();
				}
			}
		});
		//继续复制
		continueCopy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogMsg("正在读取文件的大小，请稍后……",0);
				copyThread=new Thread(){
					public void run(){
						double newFileSize=0;
						try {
							size = getAllFileSize(sourceFile);
							sleep(1000);
							newFileSize = getAllFileSize(newFile)/(1024*1024*1024);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							currentThread().interrupt();
							e.printStackTrace();
						}
						ss=getRomAvailableSize().substring(0,getRomAvailableSize().length()-2);
						double w= Double.parseDouble(ss.trim());

						double fileSize=w+newFileSize;
						if(fileSize!=0){
							if(fileSize>=4.80){
								sendMsg(6);
								copy(sourceFile,newFile);
							}else{
								sendMsg(4);
							}
						}else{
							sendMsg(3);
						}
					}
				};
				if(null!=hasDialog)
					hasDialog.dismiss();
				copyThread.start();
			}
		});
	}

	@SuppressWarnings("deprecation")
	public void delete(){
		dialogDelete = new ProgressDialog(context);
		dialogDelete.setMessage("正在删除高德离线地图...");
		dialogDelete.setCancelable(false);
		dialogDelete.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialogDelete.setButton("取消", new MyCancelButton());
		dialogDelete.show();
	}
}