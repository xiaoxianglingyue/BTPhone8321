package com.xintu.smartcar.btphone;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.http.util.EncodingUtils;

import com.xintu.smartcar.bluetoothphone.iface.CallInfo;
import com.xintu.smartcar.btphone.adapter.ContactAdapter;
import com.xintu.smartcar.btphone.adapter.RadioGroupAdapter;
import com.xintu.smartcar.btphone.application.BTPhoneApplication;
import com.xintu.smartcar.btphone.bean.CallRecord;
import com.xintu.smartcar.btphone.bean.ContactItem;
import com.xintu.smartcar.btphone.bean.PBItem;
import com.xintu.smartcar.btphone.bean.SelectContact;
import com.xintu.smartcar.btphone.biz.BizMain;
import com.xintu.smartcar.btphone.biz.RadioButtonModel;
import com.xintu.smartcar.btphone.db.dao.CallRecordsDao;
import com.xintu.smartcar.btphone.db.dao.ContactsDao;
import com.xintu.smartcar.btphone.receiver.PhoneReceiver;
import com.xintu.smartcar.btphone.service.ConnService;
import com.xintu.smartcar.btphone.service.DialFloatService;
import com.xintu.smartcar.btphone.utils.CopyFile;
import com.xintu.smartcar.btphone.utils.CrashHandler;
import com.xintu.smartcar.btphone.utils.ImportAssetsUtil;
import com.xintu.smartcar.btphone.utils.IsNetWork;
import com.xintu.smartcar.btphone.utils.LEIDAUtil;
import com.xintu.smartcar.btphone.utils.PinyinComparator;
import com.xintu.smartcar.btphone.utils.SharedPreferencesUtil;
import com.xintu.smartcar.btphone.utils.UploadfileUtil;
import com.xintu.smartcar.btphone.utils.XintuPinyin;
import com.xintu.smartcar.btphone.view.MyListView;
import com.xintu.smartcar.btphone.view.SideBar;
import com.xintu.smartcar.btphone.view.MyListView.OnRefreshListener;
import com.xintu.smartcar.btphone.view.SideBar.OnTouchingLetterChangedListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("DefaultLocale") 
public class MainActivity extends Activity implements OnClickListener, OnItemClickListener{
	private static final String TAG  = "BTMainActivity";
	private ConnService myService;
	private ServiceConnection serviceConnection;
	private static final String CONNECTED_SIGN = "(已连接)";
	// 汉字转拼音的工具类
	private HanyuPinyinOutputFormat format;
	// 存放拼音使用的字符串数组
	private String[] pinyin;
	private BizMain m_bizMain;
	private static LinearLayout layout_contacts;
	private static LinearLayout layout_keyboard;
	private static LinearLayout layout_btdevices;
	private static LinearLayout dialcall;
	private static LinearLayout hungcall;
	private static RadioButton rbtn_contacts;
	private static RadioButton rbtn_keyboard;
	private static RadioButton rbtn_btdevice;
	private static RadioButton rbtn_callrecord;
	private ContactsDao m_contactsDao;
	private MyListView addresslist_listview;
	private SideBar addresslist_sidebar;
	//拼音比较器
	private PinyinComparator comparator;
	//private int pageNumber;
	//用户列表展现的数据
	private ArrayList<ContactItem> persons;
	private ArrayList<ContactItem> personsSource;
	private TextView contact_name;//,devices_title;
	private EditText tv_phoneNumber;
	private static TextView tv_phoneName;
	private TextView addresslist_dialog;
	//联系人列表
	private ArrayList<ContactItem> m_listContact;
	private ArrayList<CallRecord>  m_listCallRecord;
	private ContactAdapter m_adapterContact = null;//电话本适配器
	private static ImageView iv_delete;
	private Handler mMsgHandler;
	private String m_strPhoneNumber = "";
	float x, y, upx, upy;
	private boolean m_isPaused = true;
	private String m_name="";
	private SoundPool spool;//声明一个SoundPool
	private ArrayAdapter<String> contactDetailAdapter;
	private ArrayAdapter<String> numberAdapter;
	private ListView lv_number,lv_contact_details; 
	ArrayList<CallInfo> list;
	ArrayList<String> contactDetailList;
	LinearLayout linearLayoutMain,contacts_list,layout_back_contact,lv_back_contact;
	RelativeLayout show_contact;
	@SuppressLint("UseSparseArrays") 
	private Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	private AudioManager am = null;
	private EditText et_search_contact;
	//通讯录人名存放路径
	//private final static String strContactFilePath = "/storage/sdcard0/xintudata/";
	private final static String strContactFileName = "contact.txt"; 
	//private final static String strContactFileSpec = strContactFilePath + strContactFileName;
	private String contactsname;
	private String namepinyin;
	private static TextView save_number;
	private TextView txtProgressPrompt;
	private TextView tv_local_devices;
	private static int isfirst=0;
	private static boolean ishung=false;
	private static String spnumber="";
	private SharedPreferences preference;
	private Editor edit;
	private ImageView iv_zero;
	private TextView btnPhoneBook;
	private int allNumberCount=0;
	private ImportAssetsUtil importAssetsUtil;
	private String str_sernum="";
	//private String type="330327";
	private int k=0;
	String strUrl="http://114.215.149.117/whatever/SerService.php?type=";
	//String strUrl="http://3rd.ximalaya.com/zhubo_categories/2/hot_zhubos?i_am=lcxt&page=1&per_page=1&uni=xxx";
	String res="";
	URL url=null;
	File fileTmp;
	String sddir="";
	String file_path="";
	ProgressDialog dialog;
	public static int JSON_FILE=101;
	public static int JSON_NOFILE=102;
	private Dialog hasDialog =null;
	private Dialog notDialog =null;
	private Dialog delDialog =null;
	public Handler mainhandler;
	private GridView gv;
	private RadioGroupAdapter radioGroupAdapter;
	private List<RadioButtonModel> itemList;
	private int item=0;
	private int clickcount=0;
	private Thread m_thread = null;
	private int m_iCounter = 0;
	String ss;
	AlertDialog.Builder builder;
	private TextView tv_connectdevices;
	private int type=0;

	@SuppressLint("HandlerLeak") Handler m_handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==1001){
				Bundle bundle= msg.getData();
				m_name=bundle.getString("m_name");
				m_name=m_name.replaceAll( "[\\p{P}+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]" , "");
				if(isNumeric(ToDBC(m_name))!=true){
					namepinyin=getStringPinYin(m_name);
					String str=importAssetsUtil.importGeneralContact(m_name,false);
					if("".equals(str)){
						//String strConnStatus =SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "isBtConn", "false");
						if(MyListener.m_bConnected==true){
							if(null!=namepinyin&&!"".equals(namepinyin)){

								if(null!=m_contactsDao.findNumberisExit(namepinyin)&&!"".equals(m_contactsDao.findNumberisExit(namepinyin))){
									list=m_contactsDao.findAllName3(namepinyin);
									int i=m_contactsDao.findCountName(namepinyin);
									if(i==1){
										str="请确认";
									}else{
										str="请选择";
									}
									BTPhoneApplication.getInstance().isbackcall=false;
									if(BTPhoneApplication.getInstance().isdialogtop==true){
										BTPhoneApplication.getInstance().dialogActivity.finish();
										BTPhoneApplication.getInstance().isdialogtop=false;
									}
									try{
										Intent intent=new Intent(MainActivity.this,DialogActivity.class);
										intent.putExtra("list", list);
										startActivity(intent);
									}catch(IndexOutOfBoundsException e){}
									Intent sendbroad = new Intent();
									sendbroad.setAction("com.xintu.btphone.phonehaslist");
									if(list.size()>5){
										sendbroad.putExtra("name_list", list.size()-1+"");
									}else{
										sendbroad.putExtra("name_list", list.size()+"");
									}
									sendbroad.putExtra("speech", str);
									sendBroadcast(sendbroad);
								}else{
									BTPhoneApplication.getInstance().isbackcall=false;
									str="没有与"+m_name+"匹配的电话号码！";
									Intent sendbroad = new Intent();
									sendbroad.setAction("com.xintu.btphone.recalltoman");
									sendbroad.putExtra("speechhit", str);
									sendBroadcast(sendbroad);
									dialcall.setVisibility(ViewGroup.VISIBLE);
									hungcall.setVisibility(ViewGroup.GONE);
								}
							}
						}else{
							speech();
						}
					}else if(!"".equals(str)&&str.length()>=2){
						//String strConnStatus =SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "isBtConn", "false");
						if(MyListener.m_bConnected==true){
							list=importAssetsUtil.importListGeneralContact(m_name);
							if(null!=list){
								if(BTPhoneApplication.getInstance().isdialogtop==true){
									BTPhoneApplication.getInstance().dialogActivity.finish();
									BTPhoneApplication.getInstance().isdialogtop=false;
								}
								try{
									Intent intent=new Intent(MainActivity.this,DialogActivity.class);
									intent.putExtra("list", list);
									startActivity(intent);
								}catch(IndexOutOfBoundsException e){}
								Intent sendbroad = new Intent();
								sendbroad.setAction("com.xintu.btphone.phonehaslist");
								if(list.size()==1){
									str="请确认";
								}else{
									str="请选择";
								}
								if(list.size()>5){
									sendbroad.putExtra("name_list", list.size()-1+"");
								}else{
									sendbroad.putExtra("name_list", list.size()+"");
								}
								sendbroad.putExtra("speech", str);
								sendBroadcast(sendbroad);
							}else{
								BTPhoneApplication.getInstance().isbackcall=false;
								str="没有与"+m_name+"匹配的电话号码！";
								Intent sendbroad = new Intent();
								sendbroad.setAction("com.xintu.btphone.recalltoman");
								sendbroad.putExtra("speechhit", str);
								sendBroadcast(sendbroad);
								dialcall.setVisibility(ViewGroup.VISIBLE);
								hungcall.setVisibility(ViewGroup.GONE);
							}
						}else{
							speech();
						}
						BTPhoneApplication.getInstance().isbackcall=false;
					}
				}else{
					String strn=importAssetsUtil.importGeneralContact(m_name,true);
					if("".equals(strn)){
						BTPhoneApplication.getInstance().isbackcall=false;
						String name=m_contactsDao.findName(m_name);
						if(null!=name&&!"".equals(name)){
							//String strConnStatus =SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "isBtConn", "false");
							if(MyListener.m_bConnected==true){
								list=new ArrayList<CallInfo>();
								CallInfo contact=new CallInfo();
								contact.m_strName=name;
								contact.m_strNumber=m_name;
								list.add(contact);
								if(BTPhoneApplication.getInstance().isdialogtop==true){
									BTPhoneApplication.getInstance().dialogActivity.finish();
									BTPhoneApplication.getInstance().isdialogtop=false;
								}
								try{
									Intent intent=new Intent(MainActivity.this,DialogActivity.class);
									intent.putExtra("list", list);
									startActivity(intent);
								}catch(IndexOutOfBoundsException e){}
								Intent sendbroad = new Intent();
								sendbroad.setAction("com.xintu.btphone.phonehaslist");
								sendbroad.putExtra("name_list", "1");
								sendbroad.putExtra("speech", "请确认");
								sendBroadcast(sendbroad);
							}else{
								speech();
							}
						}else{
							BTPhoneApplication.getInstance().isbackcall=false;
							//String strConnStatus =SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "isBtConn", "false");
							if(MyListener.m_bConnected==true){
								list=new ArrayList<CallInfo>();
								CallInfo contact=new CallInfo();
								contact.m_strName="未知号码";
								contact.m_strNumber=m_name;
								list.add(contact);
								if(BTPhoneApplication.getInstance().isdialogtop==true){
									BTPhoneApplication.getInstance().dialogActivity.finish();
									BTPhoneApplication.getInstance().isdialogtop=false;
								}
								try{
									Intent intent=new Intent(MainActivity.this,DialogActivity.class);
									intent.putExtra("list", list);
									startActivity(intent);
								}catch(IndexOutOfBoundsException e){}
								Intent sendbroad = new Intent();
								sendbroad.setAction("com.xintu.btphone.phonehaslist");
								sendbroad.putExtra("name_list", "1");
								sendbroad.putExtra("speech", "请确认");
								sendBroadcast(sendbroad);
							}else{
								speech();
							}
						} 
					}else{
						BTPhoneApplication.getInstance().isbackcall=false;
						//String strConnStatus =SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "isBtConn", "false");
						if(MyListener.m_bConnected==true){
							String[] strSplit = strn.split(",");
							String strName = strSplit[0];
							String strNumber = strSplit[1];
							list=new ArrayList<CallInfo>();
							CallInfo contact=new CallInfo();
							contact.m_strName=strName;
							contact.m_strNumber=strNumber;
							list.add(contact);
							if(BTPhoneApplication.getInstance().isdialogtop==true){
								BTPhoneApplication.getInstance().dialogActivity.finish();
								BTPhoneApplication.getInstance().isdialogtop=false;
							}
							try{
								Intent intent=new Intent(MainActivity.this,DialogActivity.class);
								intent.putExtra("list", list);
								startActivity(intent);
							}catch(IndexOutOfBoundsException e){}
							Intent sendbroad = new Intent();
							sendbroad.setAction("com.xintu.btphone.phonehaslist");
							sendbroad.putExtra("name_list", "1");
							sendbroad.putExtra("speech", "请确认");
							sendBroadcast(sendbroad);
						}else{
							speech();
						}
					}
				}
			}else if(msg.what == 1002){
				finish();
			}else if(msg.what == 1004){
				if(BTPhoneApplication.getInstance().flag==true){
					hungup();
					String strConnStatus =SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "isBtConn", "false");
					if("true".equals(strConnStatus)){
						if(m_contactsDao.findAllCount()==0){
							startThread();
						}
					}
					BTPhoneApplication.getInstance().flag=false;
				}
			}else if(msg.what == 1009){
				if(BTPhoneApplication.getInstance().flag==false){
					finish();
				}
			}else if(msg.what==5000){
				final String str=(String) msg.obj;
				lv_contact_details.setFadingEdgeLength(0);
				lv_contact_details.setLayoutParams(new LayoutParams(  
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				if(null!=m_contactsDao.findNumber(str)&&!"".equals(m_contactsDao.findNumber(str))){
					contactDetailList=m_contactsDao.findListNumber(str);
				}
				if(null!=contactDetailList&&!"".equals(contactDetailList)){			
					for(String s:contactDetailList){
						contactDetailAdapter.add(s);
					}
					lv_back_contact.addView(lv_contact_details);
				}
				//点击详细列表中的条目进入蓝牙拨号页面
				lv_contact_details.setOnItemClickListener(new OnItemClickListener() {//响应listview中的item的点击事件  
					@Override  
					public void onItemClick(AdapterView<?> arg0, View arg1, int position,  
							long id) {  
						// TODO Auto-generated method stub
						String num=contactDetailList.get(position).toString();
						m_strPhoneNumber=num;
						tv_phoneNumber.setText(m_strPhoneNumber);
						rbtn_contacts.setBackgroundResource(R.drawable.contactss2);
						rbtn_keyboard.setBackgroundResource(R.drawable.keyboard1);
						rbtn_btdevice.setBackgroundResource(R.drawable.bluetooth_devices2);
						layout_contacts.setVisibility(GridView.GONE);
						layout_keyboard.setVisibility(GridView.VISIBLE);
						layout_btdevices.setVisibility(GridView.GONE);
					}  
				});
			}
		}
	};
	@SuppressLint("HandlerLeak") Handler reqMACHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==2015){
				reqMAC();
				clickcount=0;
				m_iCounter=0;
			}else if(msg.what==2016){
				clickcount=0;
				m_iCounter=0;
			}
		}
	};
	//判断字符串是否是数字
	public static boolean isNumeric(String str){ 
		Pattern pattern = Pattern.compile("[0-9]*"); 
		return pattern.matcher(str).matches();    
	}

	/**
	 * 全角转半角
	 * @param input String.
	 * @return 半角字符串
	 */
	public static String ToDBC(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '\u3000') {
				c[i] = ' ';
			} else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
				c[i] = (char) (c[i] - 65248);
			}
		}
		String returnString = new String(c);
		return returnString;
	}

	/*private int nameCount(String namepinyin) {//根据姓名查看人名个数
		//String strName = m_contactsDao.findName(m_strPhoneNumber);
		int iCounter = m_contactsDao.findCountName(namepinyin);	
		return iCounter;
	}*/

	public void dial(String m_name,String number){

		Intent intent=new Intent(MainActivity.this,DialActivity.class);
		doDial(number.trim());
		intent.putExtra("name",m_name);
		intent.putExtra("number",number);
		startActivity(intent);
		m_name="";
		number="";
	}	

	private void doDial(String number){
		dialcall.setVisibility(ViewGroup.GONE);
		hungcall.setVisibility(ViewGroup.VISIBLE);
		BTPhoneApplication.getInstance().m_isDailByMirror = true;
		m_bizMain.reqDialNum(number);
		tv_phoneName.setText("");
		BTPhoneApplication.getInstance().tempNumber=number;
	}

	//把通讯录存放到文件中
	@SuppressLint("NewApi") 
	private void saveContactFile() {
		try {
			/*File fileDir = new File(strContactFilePath);
			if (fileDir.exists() == false) {
				fileDir.mkdirs();
			}*/
			File[] fileTmp = this.getExternalFilesDirs("");
			if (!fileTmp[0].exists()) {
				fileTmp[0].mkdirs();
			}
			String m_rootFolder = fileTmp[0].getAbsolutePath() + "/"+strContactFileName;
			File file = new File(m_rootFolder);
			if (file.exists()) {
				file.delete();
			}
			FileOutputStream fos = new FileOutputStream(file);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			bw.write("中国移动,zhongguoyidong");
			bw.newLine();
			bw.write("中国联通,zhongguoliantong");
			bw.newLine();
			bw.write("中国电信,zhongguodianxin");
			bw.newLine();
			for (int i = 0; i < persons.size(); i++) {
				ContactItem item = persons.get(i);
				String strFullName = replaceBlank(item.m_strName);
				String strPinyin = replaceBlank(XintuPinyin.doConvert(strFullName));
				bw.write(strFullName + "," + strPinyin);
				bw.newLine();
			}
			List<String> listGeneral = importAssetsUtil.getFromAssets("pb.txt");
			for (int i=0; i<listGeneral.size(); i++) {
				String strCurr = listGeneral.get(i);
				String[] strSplit = strCurr.split(",");
				if (strSplit.length == 2) {
					String strName = strSplit[0];
					//String strNumber = strSplit[1];
					//int iType = PBItem.PHONE_NUM_TYPE_OTHER;
					String strPinyin = getStringPinYin(strName);
					bw.write(strName + "," + strPinyin);
					bw.newLine();
				}
			}
			bw.close();
		}
		catch (Exception e) {
		}
		Intent sendbroad = new Intent();
		sendbroad.setAction("com.xintu.btphone.resetcallman");
		sendBroadcast(sendbroad);
	}

	public String replaceBlank(String str) {  
		String dest = "";  
		if (str!=null) {  
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");  
			Matcher m = p.matcher(str);  
			dest = m.replaceAll("");  
		}  
		return dest;  
	}  
	//create message handler 
	@SuppressLint("HandlerLeak") 
	private Handler createMsgHandler() {
		Handler handler = new Handler(){
			@SuppressWarnings("unused")
			@Override
			public void handleMessage(final Message msg) {
				if (m_isPaused) { //如果当前在后台运行，就不处理消息
					//Log.e(TAG, "当前在后台状态");
					return;
				}
				else {
					//Log.e(TAG, "当前在前台状态");
				}
				switch(msg.what){
				case MyListener.BTMSG_CONNECTING://正在连接
					txtProgressPrompt.setText("正在连接手机...");
					break;
				case MyListener.BTMSG_CONNECTED://连接成功
					LEIDAUtil.setAudioFileEnabled(true);//连接成功打开功放
					txtProgressPrompt.setText("");
					//保存一个已经链接的状态true
					SharedPreferencesUtil.saveStringData(BTPhoneApplication.getInstance(), "isBtConn", "true");
					btnPhoneBook.setVisibility(Button.VISIBLE);
					//连接成功时， 才可以可以同步电话本
					//自动进行同步操作
					txtProgressPrompt.setText("开始同步电话簿...");
					showToast("已经连接到手机");
					//延时8秒后再开始同步电话本
					Thread threadDelay = new Thread() {
						public void run() {
							try {
								Thread.sleep(8000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							syncPhoneBook();
						}
					};
					threadDelay.start();
					BTPhoneApplication.getInstance().isconn=true;
					//String strConnStatus =SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "isBtConn", "false");
					if(MyListener.m_bConnected==true){
						spnumber=SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "spnumber", "");
						if(!"".equals(spnumber)){
							tv_phoneName.setText("上次通话");
							save_number.setText(spnumber);
						}
					}
					break;
				case MyListener.BTMSG_SEND_CONNBTNAME://已连接设备的名称
					String strDevicesName=(String) msg.obj;
					if(!"".equals(strDevicesName)){
						SharedPreferencesUtil.saveStringData(BTPhoneApplication.getInstance(), "strDevicesName", strDevicesName);
						tv_connectdevices.setText(strDevicesName+CONNECTED_SIGN);
					}
					break;
				case MyListener.BTMSG_DISCONNECTED: //断开连接
					SharedPreferencesUtil.saveStringData(BTPhoneApplication.getInstance(), "isBtConn", "false");
					tv_connectdevices.setText("");
					k=0;
					et_search_contact.setHint("");
					BTPhoneApplication.getInstance().isconn=false;
					txtProgressPrompt.setText("");
					//断开连接时，不能同步电话本
					btnPhoneBook.setVisibility(Button.INVISIBLE);
					//清空数据-------start-----------
					//m_contactsDao.clearFeedTable();
					m_contactsDao.clearAll();
					try{
						if(null!=m_listContact)
							m_listContact.clear();
						if(null!=persons)
							persons.clear();
						if(personsSource!=null)
							personsSource.clear();
						if(null!=m_adapterContact&&!"".equals(m_adapterContact))
							m_adapterContact.notifyDataSetChanged();
					}catch(IndexOutOfBoundsException e){e.printStackTrace();}

					//------------------end------------
					tv_phoneName.setText("");
					save_number.setText("");
					break;
				case MyListener.BTMSG_PHONEBOOK_DATA://正在同步电话本
					//showToast("开始同步电话簿");
					String strName ="";
					String strNumber="";
					//直接根据pbitem进行判断
					PBItem pbItem = (PBItem)msg.obj;
					Log.e("BTPhone", pbItem.m_strName+"<<<>>>"+pbItem.m_strNumber);
					if (pbItem.m_strName == null) {
						pbItem.m_strName = "";
					}
					else if (pbItem.m_strName.equalsIgnoreCase("null")) {
						pbItem.m_strName = "";
					}
					if (pbItem.m_strNumber == null) {
						pbItem.m_strNumber = "";
					}
					else if (pbItem.m_strNumber.equalsIgnoreCase("null")) {
						pbItem.m_strNumber = "";
					}
					if (type == PBItem.CALL_TYPE_PHONEITEM) {//判断是通讯录中的电话本
						if (pbItem.m_strName == "" && pbItem.m_strNumber== "") {
							break;
						}
						//iContactNumber += 1;
						txtProgressPrompt.setText("已导入 " + personsSource.size() + " 个联系人");
						m_listContact=new ArrayList<ContactItem>();
						
						strName= pbItem.m_strName;
						strNumber= pbItem.m_strNumber;
						if(strName==null||"".equals(strName)){
							strName="未知";
						}
						String strnamepinyin=getStringPinYin(strName);
						if(!"".equals(strNumber)&&null!=strNumber){
							if(m_contactsDao.isExitNumber(strNumber.trim())==false)
								m_contactsDao.save(strNumber.trim(),strName.trim(),strnamepinyin.trim());
						}
						if(!"".equals(strNumber)&&null!=strNumber){
							if(isExistItem(strNumber.trim())==false){
								ContactItem curContact = new ContactItem();
								curContact.m_strName = strName.trim();
								curContact.setPinyin(getStringPinYin(strName.trim()));
								curContact.m_strNumber = strNumber.trim();
								m_listContact.add(curContact);
								personsSource.addAll(m_listContact);
							}
						}
						if(null!=m_listContact&&!"".equals(m_listContact))
							persons.addAll(filledData(m_listContact));
						if(null!=persons&&persons.size()>0){
							Collections.sort(persons, comparator);
							if(null!=m_adapterContact)
								m_adapterContact.notifyDataSetChanged();
						}
						addresslist_listview.hiddenMore();
						addresslist_listview.setonRefreshListener(new OnRefreshListener()
						{
							@Override
							public void onRefresh()
							{
								// TODO Auto-generated method stub
								addresslist_listview.onRefreshComplete("");
							}
						});
						addresslist_listview.setOnItemClickListener(MainActivity.this);
					}
					break;
				case MyListener.BTMSG_PHONEBOOK_COMPLETE://电话本同步完成
					txtProgressPrompt.setText("");
					
						saveContactFile();
					
					allNumberCount=m_contactsDao.findAllCount();
					showToast("成功导入"+allNumberCount+"个联系人");
					et_search_contact.setHint("搜索"+allNumberCount+"位联系人");
					break;
				case MyListener.BTMSG_INCOMING_CALL://新来电
					String strInCNumber=(String) msg.obj;//来电的电话号码
					BTPhoneApplication.getInstance().flag=true;
					BTPhoneApplication.getInstance().isComing=true;
					BTPhoneApplication.getInstance().iscall=true;
					Intent intent = new Intent(MainActivity.this, IncomingActivity.class);
					intent.putExtra("strInCNumber", strInCNumber);
					startActivity(intent);
					break;
				case MyListener.BTMSG_OUTGOING_CALL://当前处于拨号状态
					Log.e(TAG, "AA DDDDDDDDDDDDD 正在捃打电...........");
					//BTPhoneApplication.getInstance().isOutgoingCall=true;
					String strDiaNumber = (String)msg.obj;
					dialcall.setVisibility(ViewGroup.GONE);
					hungcall.setVisibility(ViewGroup.VISIBLE);
					if (BTPhoneApplication.getInstance().m_isDialOpened == false && BTPhoneApplication.getInstance().m_isDailByMirror == false) {
						String strDiaName = m_contactsDao.findName(strDiaNumber);
						m_strPhoneNumber=strDiaNumber;
						BTPhoneApplication.getInstance().ismobile=true;
						tv_phoneNumber.setText(m_strPhoneNumber);
						if(null!=strDiaNumber||!"".equals(strDiaNumber)){
							Intent intentCurrent = new Intent(MainActivity.this,DialActivity.class);
							if("".equals(strDiaName)){
								intentCurrent.putExtra("name", "");
							}else{
								intentCurrent.putExtra("name", strDiaName);
							}
							intentCurrent.putExtra("number", strDiaNumber);
							startActivity(intentCurrent);
						}
					}
					break;
				case MyListener.BTMSG_HUNGUP_CALL://挂机
					
					//String strConnState =SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "isBtConn", "false");
					if(MyListener.m_bConnected==true){
						spnumber=SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "spnumber", "");
						if(!"".equals(spnumber)){
							tv_phoneName.setText("上次通话");
							save_number.setText(spnumber);
						}
						if(m_contactsDao.findAllCount()==0){
							startThread();
						}
					}
					hungup();
					break;
				case MyListener.BTMSG_LOC_DEVICENAME://本地蓝牙名称
					String strLocDevicesName=(String) msg.obj;
					if(!"".equals(strLocDevicesName)){
						showToast("本地蓝牙名称获取成功！");
						tv_local_devices.setText(strLocDevicesName);
						SharedPreferencesUtil.saveStringData(BTPhoneApplication.getInstance(), "strLocDevicesName", strLocDevicesName);
					}else{
						showToast("本地蓝牙名称获取失败，请重试！");
					}
					break;
				case MyListener.BTMSG_INCOMMING_CALLNAME://来电号码显示
					String strIncomName=(String) msg.obj;
				}
				super.handleMessage(msg);
			}
		};		
		return handler;
	}

	/**判断电话号码是否存在*/
	private boolean isExistItem(String strNumber) {
		int iTotal = personsSource.size();
		for (int i=0; i<iTotal; i++) {
			ContactItem item = personsSource.get(i);
			if (item != null) {
				if (item.m_strNumber.equalsIgnoreCase(strNumber)) {
					return true;
				}
			}
		}
		return false;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_activity);
		findView();
		BTPhoneApplication.getInstance().mainActivity=MainActivity.this;
		preference = this.getSharedPreferences("spconfig", Context.MODE_PRIVATE);
		edit=preference.edit();
		//电话号码输入框，光标处理
		if (android.os.Build.VERSION.SDK_INT <= 10) {
			tv_phoneNumber.setInputType(InputType.TYPE_NULL);
		} else {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			try {
				Class<EditText> cls = EditText.class;
				Method setShowSoftInputOnFocus;
				setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
				setShowSoftInputOnFocus.setAccessible(true);
				setShowSoftInputOnFocus.invoke(tv_phoneNumber, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		format = new HanyuPinyinOutputFormat();
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		persons=new ArrayList<ContactItem>();
		personsSource=new ArrayList<ContactItem>();
		m_contactsDao = new ContactsDao(this);
		importAssetsUtil=new ImportAssetsUtil(MainActivity.this);
		mMsgHandler = createMsgHandler();
		numberAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.select_contact_item);
		lv_number.setAdapter(numberAdapter);
		contactDetailAdapter=new ArrayAdapter<String>(MainActivity.this, R.layout.device_name);
		lv_contact_details.setAdapter(contactDetailAdapter);
		init();
		fileTmp= getExternalFilesDir("");//获取手机内置卡的地址
		sddir=fileTmp.getAbsolutePath();
		//sddir=Environment.getExternalStorageDirectory().getAbsolutePath();
		file_path=sddir+"/sernum/serialnum.txt";//文件保存路径	
		if(CrashHandler.errorFileExit()==false){
			//Toast.makeText(getApplicationContext(), "没有错误LOG", Toast.LENGTH_LONG).show();
		}else{
			final String srcPath="/storage/sdcard0/crash_btphone_error/crash_BTPhone_error.txt";
			final String uploadUrl="http://114.215.149.117/upload/index.php";
			new Thread(){
				public void run(){
					UploadfileUtil.uploadFile(srcPath,uploadUrl);
				}
			}.start();
		}
		if(!"".equals(BTPhoneApplication.getInstance().come_number)){
			if(null!=DialFloatService.dialFloathandler)
				DialFloatService.dialFloathandler.sendEmptyMessage(2166);
		}
	}

	/**
	 * 初始化操作
	 */
	@SuppressLint("HandlerLeak") @SuppressWarnings("deprecation")
	public void init(){
		if(1280==screen()){
			dialog=new ProgressDialog(MainActivity.this);
			dialog.setTitle("提示");
			dialog.setMessage("正在获取序列号...");
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			Window win=dialog.getWindow();
			android.view.WindowManager.LayoutParams lp=win.getAttributes();
			win.setGravity(Gravity.CENTER);
			lp.x=280;
			lp.y=0;
			win.setAttributes(lp);
			dialog.setCancelable(false);
			dialog.setButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					finish();	
				}
			});
		}else{
			dialog=new ProgressDialog(MainActivity.this);
			dialog.setTitle("提示");
			dialog.setMessage("正在获取序列号...");
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setCancelable(false);
			dialog.setButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					finish();	
				}
			});
		}

		serviceConnection = new ServiceConnection() {
			/** 获取服务对象时的操作 */
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				myService = ((ConnService.ServiceBinder) service).getService();
				myService.notifyForeGround(); //通知进入前台工作状态
				m_bizMain = myService.m_bizMain;
				myService.m_listener.handlerMain = mMsgHandler;
			}
			/** 无法获取到服务对象时的操作 */
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				myService = null;
				m_bizMain = null;
			}
		};

		mainhandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				//通话计时
				if(msg.what==10000){
					tv_phoneName.setText("通话时间");
					save_number.setText((CharSequence) msg.obj);
				}else if(msg.what==10019){
					dialcall.setVisibility(ViewGroup.VISIBLE);
					hungcall.setVisibility(ViewGroup.GONE);
					isfirst=0;
					BTPhoneApplication.getInstance().flag=false;
					BTPhoneApplication.getInstance().isbackcall=false;
					if(MyListener.m_bConnected==true){
						spnumber=SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "spnumber", "");
						if(!"".equals(spnumber)){
							tv_phoneName.setText("上次通话");
							save_number.setText(spnumber);
						}
						if(m_contactsDao.findAllCount()==0){
							startThread();
						}
					}
					tv_phoneNumber.setText("");

					BTPhoneApplication.getInstance().ismobile=false;
					BTPhoneApplication.getInstance().isComing=false;	
					if(BTPhoneApplication.getInstance().isBTTop==false){
						finish();
					}

				}else if(msg.what==10020){
					ishung=(Boolean) msg.obj;
					if(ishung==true){
						dialcall.setVisibility(ViewGroup.VISIBLE);
						hungcall.setVisibility(ViewGroup.GONE);
					}
					BTPhoneApplication.getInstance().flag=false;
					BTPhoneApplication.getInstance().isbackcall=false;
					BTPhoneApplication.getInstance().isComing=false;
					//String strConnStatus =SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "isBtConn", "false");
					if(MyListener.m_bConnected==true){
						spnumber=SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "spnumber", "");
						if(BTPhoneApplication.getInstance().isflag==true){		
							BTPhoneApplication.getInstance().isflag=false;
						}else{
							spnumber=SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "spnumber", "");
							if(!"".equals(spnumber)){
								tv_phoneName.setText("上次通话");
								save_number.setText(spnumber);
							}
						}
						if(m_contactsDao.findAllCount()==0){
							startThread();
						}
					}
				}else if(msg.what==100){
					txtProgressPrompt.setText("");
				}else if(msg.what==JSON_FILE){//本地有文件,就读取出来显示。
					String str=(String) msg.obj;
					showHasDialog(str);
				}else if(msg.what==JSON_NOFILE){//本地没文件，就从服务器中读取。然后存到本地文件。
					showNotDialog();
				}
			}
		};
		DialActivity.mainhandler=mainhandler;
		DialFloatService.mainhandler=mainhandler;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(!"".equals(BTPhoneApplication.getInstance().come_number)){
			if(null!=DialFloatService.dialFloathandler)
				DialFloatService.dialFloathandler.sendEmptyMessage(2166);
		}
		if(null!=personsSource&&personsSource.size()>0){}else{
			fillPhoneBook();
		}
		
		PhoneReceiver.handler=m_handler;
		//bind service
		Intent intent = new Intent(MainActivity.this, ConnService.class);
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE); 
		Intent sendbroad = new Intent();
		sendbroad.setAction("com.xintu.btphone.startmain");
		sendBroadcast(sendbroad);
		BTPhoneApplication.getInstance().isActivityTop=true;
		m_isPaused = false;
		//本地蓝牙名称
		String strLocDevicesName=SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "strLocDevicesName", "H25302");
		tv_local_devices.setText(strLocDevicesName);
		//当前蓝牙是否连接
		//String strConnStatus =SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "isBtConn", "false");
		if(MyListener.m_bConnected==true){
			//当前连接的手机名
			String strDevicesName=SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "strDevicesName", "");
			tv_connectdevices.setText(strDevicesName+CONNECTED_SIGN);
		}else{
			tv_connectdevices.setText("");
		}
		if(BTPhoneApplication.getInstance().flag==false){
			dialcall.setVisibility(ViewGroup.VISIBLE);
			hungcall.setVisibility(ViewGroup.GONE);
		}
		try{
			allNumberCount=m_contactsDao.findAllCount();
		}catch(IllegalStateException s){}

		if(allNumberCount>0){
			et_search_contact.setHint("搜索"+allNumberCount+"位联系人");	
		}
		if(BTPhoneApplication.getInstance().isActionCall==false){
			dialcall.setVisibility(ViewGroup.VISIBLE);
			hungcall.setVisibility(ViewGroup.GONE);
			//String strConnState =SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "isBtConn", "false");
			if(MyListener.m_bConnected==true){
				spnumber=SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "spnumber", "");
				if(!"".equals(spnumber)){
					tv_phoneName.setText("上次通话");
					save_number.setText(spnumber);
				}
			}
		}else{
			dialcall.setVisibility(ViewGroup.GONE);
			hungcall.setVisibility(ViewGroup.VISIBLE);
		}
		
		if(BTPhoneApplication.getInstance().isbackcall==true){
			dialcall.setVisibility(ViewGroup.GONE);
			hungcall.setVisibility(ViewGroup.VISIBLE);
		}

		if(BTPhoneApplication.getInstance().isflag==true){		
			BTPhoneApplication.getInstance().isflag=false;
			tv_phoneNumber.setText(BTPhoneApplication.getInstance().tempNumber);
		}else{
			spnumber=SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "spnumber", "");
			if(!"".equals(spnumber)){
				tv_phoneName.setText("上次通话");
				save_number.setText(spnumber);
			}
		}
	} 

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		m_isPaused = true;
		//unbind service
		if(null!=myService){
			myService.notifyBackground(); //通知进入后台工作状态
			unbindService(serviceConnection);
		}
	}

	private void showHasDialog(String mess) {
		LayoutInflater inflater = (LayoutInflater) this.getLayoutInflater();
		View hasView = inflater.inflate(R.layout.dialog_has, null);
		hasDialog= new Dialog(this, R.style.dialog);
		hasDialog.setContentView(hasView);
		if(1280==screen()){
			Window win=hasDialog.getWindow();
			android.view.WindowManager.LayoutParams lp=win.getAttributes();
			win.setGravity(Gravity.CENTER);
			lp.x=280;
			lp.y=0;
			win.setAttributes(lp);
		}
		hasDialog.show();
		TextView tv_sernum=(TextView) hasView.findViewById(R.id.sernum);
		tv_sernum.setText(Html.fromHtml(mess));
	}


	private void showNotDialog() {
		LayoutInflater inflater = (LayoutInflater) this.getLayoutInflater();
		View notView = inflater.inflate(R.layout.dialog_not, null);
		notDialog = new Dialog(this, R.style.dialog);
		notDialog.setContentView(notView);
		if(1280==screen()){
			Window win=notDialog.getWindow();
			android.view.WindowManager.LayoutParams lp=win.getAttributes();
			win.setGravity(Gravity.CENTER);
			lp.x=280;
			lp.y=0;
			win.setAttributes(lp);
		}
		notDialog.show();
		gv=(GridView) notView.findViewById(R.id.gvreply);
		//设置获取序列号的数据
		radioGroupAdapter = new RadioGroupAdapter(this, getData());
		gv.setAdapter(radioGroupAdapter);
		//设置点击事件
		gv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				radioGroupAdapter.changeState(position);
				item=position+1;
			}
		});
	}

	private List<RadioButtonModel> getData(){
		itemList = new ArrayList<RadioButtonModel>();
		itemList.add(new RadioButtonModel("1", "●330327***"));
		itemList.add(new RadioButtonModel("2", "●330010***"));
		itemList.add(new RadioButtonModel("3", "●330328***"));
		itemList.add(new RadioButtonModel("4", "●330020***"));
		itemList.add(new RadioButtonModel("5", "●330329***"));
		itemList.add(new RadioButtonModel("6", "●330030***"));
		return itemList;
	}

	public void findView(){
		layout_contacts=(LinearLayout) findViewById(R.id.layout_contacts);
		layout_keyboard=(LinearLayout) findViewById(R.id.layout_keyboard);
		layout_btdevices=(LinearLayout) findViewById(R.id.layout_btdevices);
		contacts_list=(LinearLayout) findViewById(R.id.contacts_list);
		layout_back_contact=(LinearLayout) findViewById(R.id.layout_back_contact);
		lv_back_contact=(LinearLayout) findViewById(R.id.lv_back_contact);
		show_contact=(RelativeLayout) findViewById(R.id.show_contact);
		dialcall=(LinearLayout) findViewById(R.id.dialcall);
		hungcall=(LinearLayout) findViewById(R.id.hungcall);
		iv_zero=(ImageView) findViewById(R.id.iv_zero);
		lv_number= new ListView(this);
		lv_contact_details=new ListView(this);
		list=new ArrayList<CallInfo>();
		contactDetailList=new ArrayList<String>();
		m_listCallRecord=new ArrayList<CallRecord>();
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		spool = new SoundPool(11, AudioManager.STREAM_ALARM, 5);
		map.put(0, spool.load(this, R.raw.dtmf0, 0));
		map.put(1, spool.load(this, R.raw.dtmf1, 0));
		map.put(2, spool.load(this, R.raw.dtmf2, 0));
		map.put(3, spool.load(this, R.raw.dtmf3, 0));
		map.put(4, spool.load(this, R.raw.dtmf4, 0));
		map.put(5, spool.load(this, R.raw.dtmf5, 0));
		map.put(6, spool.load(this, R.raw.dtmf6, 0));
		map.put(7, spool.load(this, R.raw.dtmf7, 0));
		map.put(8, spool.load(this, R.raw.dtmf8, 0));
		map.put(9, spool.load(this, R.raw.dtmf9, 0));
		map.put(11, spool.load(this, R.raw.dtmf11, 0));
		map.put(12, spool.load(this, R.raw.dtmf12, 0));

		addresslist_listview = (MyListView) findViewById(R.id.addresslist_listview);
		addresslist_sidebar = (SideBar) findViewById(R.id.addresslist_sidebar);
		rbtn_contacts=(RadioButton) findViewById(R.id.rbtn_contacts);
		rbtn_keyboard=(RadioButton) findViewById(R.id.rbtn_keyboard);
		rbtn_btdevice=(RadioButton) findViewById(R.id.rbtn_btdevice);
		tv_phoneNumber=(EditText) findViewById(R.id.tv_phone_number);
		tv_phoneName=(TextView) findViewById(R.id.phone_name);
		save_number=(TextView) findViewById(R.id.save_number);
		txtProgressPrompt=(TextView) findViewById(R.id.txtProgressPrompt);
		iv_delete=(ImageView) findViewById(R.id.iv_delete);
		btnPhoneBook = (TextView) findViewById(R.id.btnPhoneBook);
		et_search_contact=(EditText) findViewById(R.id.et_search_contact);
		contact_name=(TextView) findViewById(R.id.contact_name);
		tv_local_devices=(TextView) findViewById(R.id.tv_local_devices);
		comparator = new PinyinComparator();
		addresslist_dialog = (TextView) findViewById(R.id.addresslist_dialog);
		addresslist_sidebar = (SideBar) findViewById(R.id.addresslist_sidebar);
		addresslist_sidebar.setTextView(addresslist_dialog);
		tv_connectdevices=(TextView) findViewById(R.id.tv_connectdevices);

		try{
			addresslist_sidebar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener()
			{
				@Override
				public void onTouchingLetterChanged(String s)
				{

					try{
						int position = m_adapterContact.getPositionForSection(s.charAt(0));
						if (position != -1)
						{
							addresslist_listview.setSelection(position + 1);
						}
					}catch(NullPointerException e){}
				}
			});
		}catch(NullPointerException e){}

		et_search_contact.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				// TODO Auto-generated method stub
				filterData(s.toString());
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				// TODO Auto-generated method stub
			}
			@Override
			public void afterTextChanged(Editable s)
			{
				// TODO Auto-generated method stub
			}
		});
		tv_phoneNumber.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				if(s.toString().equals("*#8888#")){
					//Toast.makeText(BTMainActivity.this, "进入测试界面", Toast.LENGTH_LONG).show();
					Intent intent = new Intent();
					intent.setComponent(ComponentName
							.unflattenFromString("com.bsm_wqy.validationtools/.VolidationToolsBTMainActivity"));
					startActivity(intent);
				}else if(s.toString().equals("*#88#")){//获取序列号
					if("".equals(sddir)){
						//Log.e("TOTOTO", "内置卡出现错误");
					}else{
						getFile(file_path);
					}
				}else if(s.toString().equals("*#86#")){//打开文件管理
					Intent mIntent = new Intent( ); 
					ComponentName comp = new ComponentName("com.mediatek.filemanager", "com.mediatek.filemanager.FileManagerOperationActivity"); 
					mIntent.setComponent(comp); 
					mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mIntent.setAction("android.intent.action.VIEW"); 
					startActivity(mIntent);
				}else if(s.toString().equals("*#85#")){
					//m_bizMain.reqSetDeviceName("Xintu_998");//设置本地蓝牙名称
					//m_bizMain.reqCheckPariRecord();//获取设备列表
					//m_bizMain.reqConnectHFP(2);//链接到第几个
					//m_bizMain.reqCheckHFPStatus();//查看HFP的状态
					//m_bizMain.reqDialNum("10086");
					reqMAC();//获取蓝牙取名字
					//m_bizMain.reqDisconnectHFP();//断开连接
				}else if(s.toString().equals("*#80#")){
					//http://blog.csdn.net/etzmico/article/details/7786525
					//http://www.open-open.com/code/view/1423803074982
					//文件复制，将SD卡文件复制到内置内存中
					CopyFile copyFile=new CopyFile(MainActivity.this);
					//Toast.makeText(getApplicationContext(), "正在为你计算可用空间的大小", Toast.LENGTH_SHORT).show();
					copyFile.copyFolder();

				}else if(s.toString().equals("*#83999#")){
					Intent intent = new Intent();
					intent.setComponent(ComponentName
							.unflattenFromString("com.android.SwitchBootAnimation/.SwitchBoot"));
					startActivity(intent);
				}else if(s.toString().equals("*#83781#")){
					Intent intent = new Intent();
					intent.setComponent(ComponentName
							.unflattenFromString("com.mediatek.engineermode/.EngineerMode"));
					startActivity(intent);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
			}
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub	
			}
		});

		/*//就是屏蔽ActionMode菜单
		tv_phoneNumber.setCustomSelectionActionModeCallback(new Callback() { 
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) { 
                return false;
            } 
            @Override
            public void onDestroyActionMode(ActionMode mode) {  
            } 
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) { 
                //这里可以添加自己的菜单选项（前提是要返回true的）
                return false;//返回false 就是屏蔽ActionMode菜单
            } 
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				// TODO Auto-generated method stub
				return false;
			}
        });*/

		iv_delete.setOnLongClickListener(new OnLongClickListener() {	
			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				m_strPhoneNumber="";
				tv_phoneNumber.setText("");
				tv_phoneName.setText("");
				return false;
			}
		});
		iv_zero.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				if(tv_phoneNumber.getText().toString().trim().length()>1){
					insertText(tv_phoneNumber, "+");
				}else{
					insertText(tv_phoneNumber, "+");
				}
				return true;
			}
		});
		
	}
	/**
	 * 从服务器上获取字符串
	 */
	public String doGet(String str){
		InputStream is=null;
		ByteArrayOutputStream baos=null;
		HttpURLConnection conn=null;
		try {
			url=new URL(str);
			conn=(HttpURLConnection) url.openConnection();
			conn.setReadTimeout(5000);
			conn.setConnectTimeout(5000);	
			conn.setRequestMethod("GET");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			if (conn.getResponseCode() == 200)
			{
				is = conn.getInputStream();
				baos = new ByteArrayOutputStream();
				int len = -1;
				byte[] buf = new byte[1024];
				while ((len = is.read(buf)) != -1)
				{
					baos.write(buf, 0, len);
				}
				baos.flush();
				return baos.toString();
			}
			else
			{
				throw new RuntimeException(" responseCode is not 200 ... ");
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try
			{
				if (is != null)
					is.close();
			} catch (IOException e)
			{
			}
			try
			{
				if (baos != null)
					baos.close();
			} catch (IOException e)
			{
			}
			conn.disconnect();
		}
		return null;
	}
	/**
	 * 判断本地是否有文件，如果有则读取并发消息，没有则发消息。
	 * @param path
	 */
	private void getFile(final String path) {//判断本地是否有文件
		File file0 = new File(path);
		if (file0.exists()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						FileInputStream fin = new FileInputStream(path);
						int len = fin.available();
						byte[] buffer = new byte[len];
						fin.read(buffer);
						res = EncodingUtils.getString(buffer, "UTF-8");
						Message msg = mainhandler.obtainMessage();
						msg.what = JSON_FILE;
						msg.obj = res;
						mainhandler.sendMessage(msg);
						fin.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();

		} else {
			if(null!=mainhandler){
				Message msg=mainhandler.obtainMessage();
				msg.what=JSON_NOFILE;
				mainhandler.sendMessage(msg);
			}
		}
	}
	/**
	 * 将字符串保存到文件中
	 * @param str
	 */
	private void saveFile(String str){
		File file=new File(file_path);
		if(!file.exists()){
			File dir=new File(file.getParent());
			dir.mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream fos=new FileOutputStream(file);
			fos.write(str.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void updateName() {
		String strName = m_contactsDao.findName(m_strPhoneNumber);
		int iCounter = m_contactsDao.findCnt(m_strPhoneNumber);
		if (strName != null && iCounter == 1) {
			tv_phoneName.setText(strName);
		}
		else {
			tv_phoneName.setText("");
		}
	}
	public void click(View v) throws InterruptedException{
		switch(v.getId()){
		case R.id.rbtn_contacts: 
			try{
				rbtn_contacts.setBackgroundResource(R.drawable.contactss1);
				rbtn_keyboard.setBackgroundResource(R.drawable.keyboard2);
				rbtn_btdevice.setBackgroundResource(R.drawable.bluetooth_devices2);
				layout_contacts.setVisibility(GridView.VISIBLE);
				layout_keyboard.setVisibility(GridView.GONE);
				layout_btdevices.setVisibility(GridView.GONE);
				BTPhoneApplication.getInstance().tempNumber="";
				tv_phoneNumber.setText("");
				spnumber=SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "spnumber", "");
				if(!"".equals(spnumber)){
					tv_phoneName.setText("上次通话");
					save_number.setText(spnumber);
				}
			}catch(IndexOutOfBoundsException e){e.printStackTrace();}
			break;
		case R.id.rbtn_keyboard:
			rbtn_contacts.setBackgroundResource(R.drawable.contactss2);
			rbtn_keyboard.setBackgroundResource(R.drawable.keyboard1);
			rbtn_btdevice.setBackgroundResource(R.drawable.bluetooth_devices2);
			layout_contacts.setVisibility(GridView.GONE);
			layout_keyboard.setVisibility(GridView.VISIBLE);
			layout_btdevices.setVisibility(GridView.GONE);
			BTPhoneApplication.getInstance().tempNumber="";
			tv_phoneNumber.setText("");
			break;
			
		case R.id.rbtn_btdevice:
			m_bizMain.reqCheckHFPStatus();//查看HFP的状态
			rbtn_contacts.setBackgroundResource(R.drawable.contactss2);
			rbtn_keyboard.setBackgroundResource(R.drawable.keyboard2);
			rbtn_btdevice.setBackgroundResource(R.drawable.bluetooth_devices1);
			layout_contacts.setVisibility(GridView.GONE);
			layout_keyboard.setVisibility(GridView.GONE);
			layout_btdevices.setVisibility(GridView.VISIBLE);
			BTPhoneApplication.getInstance().tempNumber="";
			tv_phoneNumber.setText("");
			break;
		case R.id.back:
			finish();
			break;
		case R.id.layout_enterparimode: //进入配对模式
			m_bizMain.reqEnterPairingMode();
			//m_bizMain.reqReboot();
			break;
		case R.id.iv_one:
			iv_delete.setVisibility(ViewGroup.VISIBLE);
			save_number.setText("");
			play(1);
			if(BTPhoneApplication.getInstance().flag==true){
				m_bizMain.reqSendDTMF("1");
			}
			updateName();
			getEditTextCursorIndex(tv_phoneNumber);
			insertText(tv_phoneNumber, "1");
			break;
		case R.id.iv_two:
			insertText(tv_phoneNumber, "2");
			iv_delete.setVisibility(ViewGroup.VISIBLE);
			save_number.setText("");
			play(2);
			if(BTPhoneApplication.getInstance().flag==true){
				m_bizMain.reqSendDTMF("2");
			}
			tv_phoneName.setText("");
			updateName();
			getEditTextCursorIndex(tv_phoneNumber);
			break;
		case R.id.iv_three:
			insertText(tv_phoneNumber, "3");
			iv_delete.setVisibility(ViewGroup.VISIBLE);
			save_number.setText("");
			getEditTextCursorIndex(tv_phoneNumber);
			play(3);
			if(BTPhoneApplication.getInstance().flag==true){
				m_bizMain.reqSendDTMF("3");
			}
			tv_phoneName.setText("");
			updateName();
			getEditTextCursorIndex(tv_phoneNumber);
			break;
		case R.id.iv_four:
			insertText(tv_phoneNumber, "4");
			iv_delete.setVisibility(ViewGroup.VISIBLE);
			save_number.setText("");
			play(4);
			if(BTPhoneApplication.getInstance().flag==true){
				m_bizMain.reqSendDTMF("4");
			}
			tv_phoneName.setText("");
			updateName();
			getEditTextCursorIndex(tv_phoneNumber);
			break;
		case R.id.iv_five:
			insertText(tv_phoneNumber, "5");
			iv_delete.setVisibility(ViewGroup.VISIBLE);
			save_number.setText("");
			play(5);
			if(BTPhoneApplication.getInstance().flag==true){
				m_bizMain.reqSendDTMF("5");
			}
			tv_phoneName.setText("");
			updateName();
			getEditTextCursorIndex(tv_phoneNumber);
			break;
		case R.id.iv_sex:
			insertText(tv_phoneNumber, "6");
			iv_delete.setVisibility(ViewGroup.VISIBLE);
			save_number.setText("");
			play(6);
			if(BTPhoneApplication.getInstance().flag==true){
				m_bizMain.reqSendDTMF("6");
			}
			tv_phoneName.setText("");
			updateName();
			getEditTextCursorIndex(tv_phoneNumber);
			break;
		case R.id.iv_seven:
			insertText(tv_phoneNumber, "7");
			iv_delete.setVisibility(ViewGroup.VISIBLE);
			save_number.setText("");
			play(7);
			if(BTPhoneApplication.getInstance().flag==true){
				m_bizMain.reqSendDTMF("7");
			}
			tv_phoneName.setText("");
			updateName();
			getEditTextCursorIndex(tv_phoneNumber);
			break;
		case R.id.iv_eight:
			insertText(tv_phoneNumber, "8");
			iv_delete.setVisibility(ViewGroup.VISIBLE);
			save_number.setText("");
			play(8);
			if(BTPhoneApplication.getInstance().flag==true){
				m_bizMain.reqSendDTMF("8");
			}
			tv_phoneName.setText("");
			updateName();
			getEditTextCursorIndex(tv_phoneNumber);
			break;
		case R.id.iv_nine:
			insertText(tv_phoneNumber, "9");
			iv_delete.setVisibility(ViewGroup.VISIBLE);
			save_number.setText("");
			play(9);
			if(BTPhoneApplication.getInstance().flag==true){
				m_bizMain.reqSendDTMF("9");
			}
			tv_phoneName.setText("");
			updateName();
			getEditTextCursorIndex(tv_phoneNumber);
			break;
		case R.id.iv_zero:
			insertText(tv_phoneNumber, "0");
			iv_delete.setVisibility(ViewGroup.VISIBLE);
			save_number.setText("");
			play(1);
			if(BTPhoneApplication.getInstance().flag==true){
				m_bizMain.reqSendDTMF("0");
			}
			tv_phoneName.setText("");
			updateName();
			getEditTextCursorIndex(tv_phoneNumber);
			break;
		case R.id.iv_star:
			insertText(tv_phoneNumber, "*");
			iv_delete.setVisibility(ViewGroup.VISIBLE);
			save_number.setText("");
			play(11);
			if(BTPhoneApplication.getInstance().flag==true){
				m_bizMain.reqSendDTMF("*");
			}
			tv_phoneName.setText("");
			getEditTextCursorIndex(tv_phoneNumber);
			break;
		case R.id.iv_well:
			insertText(tv_phoneNumber, "#");
			iv_delete.setVisibility(ViewGroup.VISIBLE);
			save_number.setText("");
			play(12);
			if(BTPhoneApplication.getInstance().flag==true){
				m_bizMain.reqSendDTMF("#");
			}
			tv_phoneName.setText("");
			getEditTextCursorIndex(tv_phoneNumber);
			break;
		case R.id.save_number:
			m_strPhoneNumber=save_number.getText().toString().trim();
			tv_phoneNumber.setText(m_strPhoneNumber);
			tv_phoneName.setText("");
			save_number.setText("");
			break;
		case R.id.iv_call:
			BTPhoneApplication.getInstance().flag=true;
			String number=save_number.getText().toString().trim();
			if(indexOfString(number,":")==true){}else{
				Intent intent=new Intent(MainActivity.this,DialActivity.class);
				String name="未知号码";
				if(!"".equals(tv_phoneNumber.getText().toString())&&null!=tv_phoneNumber.getText().toString()){
					//String strConnStatus =SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "isBtConn", "false");
					if(MyListener.m_bConnected==true){
						if(!"".equals(importAssetsUtil.importGeneralContact(tv_phoneNumber.getText().toString(),true))){
							String[] strSplit = importAssetsUtil.importGeneralContact(tv_phoneNumber.getText().toString(),true).split(",");
							name = strSplit[0];
							intent.putExtra("name", name);
						}else{
							name=m_contactsDao.findName(tv_phoneNumber.getText().toString());
							if(!"".equals(name)&&null!=name){
								intent.putExtra("name", name);
							}else{
								intent.putExtra("name", "未知号码");
							}
						}
						if(tv_phoneNumber.getText().toString().contains("+")){
							String strNumber=tv_phoneNumber.getText().toString().trim().replace("+", "00");
							doDial(strNumber);
						}else{
							doDial(tv_phoneNumber.getText().toString());
						}
						intent.putExtra("number", tv_phoneNumber.getText().toString());
						startActivity(intent);
					}else{
						speech();
					}

				}else if(!"".equals(save_number.getText().toString())&&null!=save_number.getText().toString()){

					//String strConnStatus =SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "isBtConn", "false");
					if(MyListener.m_bConnected==true){
						if(!"".equals(importAssetsUtil.importGeneralContact(save_number.getText().toString(),true))){
							String[] strSplit = importAssetsUtil.importGeneralContact(save_number.getText().toString(),true).split(",");
							name = strSplit[0];
						}else{
							name=m_contactsDao.findName(save_number.getText().toString());
							if(!"".equals(name)&&null!=name){
								intent.putExtra("name", name);
							}else{
								intent.putExtra("name", "未知号码");
							}
						}
						if(number.contains("+")){
							String strNumber=number.replace("+", "00");
							doDial(strNumber);
						}else{
							doDial(number);
						}
						intent.putExtra("number", number);
						startActivity(intent);
					}else{
						speech();
					}

				}else if(!"".equals(save_number.getText().toString())&&null!=save_number.getText().toString()&&!"".equals(tv_phoneNumber.getText().toString())&&null!=tv_phoneNumber.getText().toString()){
					//String strConnStatus =SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "isBtConn", "false");
					if(MyListener.m_bConnected==true){
						if(!"".equals(importAssetsUtil.importGeneralContact(tv_phoneNumber.getText().toString(),true))){
							String[] strSplit = importAssetsUtil.importGeneralContact(tv_phoneNumber.getText().toString(),true).split(",");
							name = strSplit[0];
							intent.putExtra("name", name);
						}else{
							name=m_contactsDao.findName(tv_phoneNumber.getText().toString());
							if(!"".equals(name)&&null!=name){
								intent.putExtra("name", name);	
							}else{
								intent.putExtra("name", "未知号码");
							}
						}
						if(number.contains("+")){
							String strNumber=number.replace("+", "00");
							doDial(strNumber);
						}else{
							doDial(number);
						}
						intent.putExtra("number",number);
						startActivity(intent);
					}else{
						speech();
					}
				}
			}
			break;
		case R.id.iv_hung:
			BTPhoneApplication.getInstance().flag=false;
			BTPhoneApplication.getInstance().isbackcall=false;
			hungup();
			if(MyListener.m_bConnected==true){
				if(m_contactsDao.findAllCount()==0){
					startThread();
				}
			}
			if(BTPhoneApplication.getInstance().isBTTop==false){
				finish();
				if(null!=BTPhoneApplication.getInstance().dialActivity)
					BTPhoneApplication.getInstance().dialActivity.finish();
			}
			break;
		case R.id.iv_delete:
			if (tv_phoneNumber.getText().toString().length() > 0) {
				if (tv_phoneNumber.getSelectionStart() < 1) {
					return;
				} else {
					deleteText(tv_phoneNumber);
					m_strPhoneNumber = tv_phoneNumber.getText().toString().trim();
				}
			}
			break;
		case R.id.iv_search_contact:
			String contact=et_search_contact.getText().toString().trim();
			filterData(contact);
			break;
		case R.id.iv_back_contact:
			contacts_list.setVisibility(ViewGroup.VISIBLE);
			show_contact.setVisibility(ViewGroup.VISIBLE);
			layout_back_contact.setVisibility(ViewGroup.GONE);
			lv_back_contact.setVisibility(ViewGroup.GONE);
			contact_name.setText("");
			contactDetailList.clear();
			lv_back_contact.removeView(lv_contact_details);
			contactDetailAdapter.clear();
			break;
		case R.id.iv_delete_search:
			String search_contact=et_search_contact.getText().toString().trim();
			if(search_contact.length()>0){
				String strname = search_contact.substring(0, search_contact.length()-1);
				et_search_contact.setText(strname);
			}
			if(search_contact.length()==0){
				et_search_contact.setText("");
			}
			break;
		case R.id.has_yes:
			hasDialog.dismiss();
			break;
		case R.id.get_sernum:
			//获取序列号
			if(IsNetWork.isNetWorkConnected(MainActivity.this)){
				notDialog.dismiss();
				new AsyncTask<Void, Void, Void>(){

					@Override
					protected void onPreExecute() {
						// TODO Auto-generated method stub
						super.onPreExecute();
						dialog.show();
					}

					@Override
					protected Void doInBackground(Void... arg0) {
						// TODO Auto-generated methFod stub
						if(item!=0){
							String type="330027";
							switch(item){
							case 1:
								type="330327";
								break;
							case 2:
								type="330010";
								break;
							case 3:
								type="330328";
								break;
							case 4:
								type="330020";
								break;
							case 5:
								type="330329";
								break;
							case 6:
								type="330030";
								break;
							}
							str_sernum = doGet(strUrl+type);
							if(null!=str_sernum){
								str_sernum = splitstr(str_sernum, "<body>", 1);
								str_sernum = splitstr(str_sernum, "</body>", 0);
								if ("error_conn".endsWith(str_sernum)) {
									str_sernum="服务器数据获取异常";
								} else if ("error_date".endsWith(str_sernum)) {
									str_sernum="服务器时间异常";
								} else if (isNumeric(str_sernum)) {
									saveFile(str_sernum);
								}
							}else{
								str_sernum="网络异常";
							}
						}else{
							str_sernum="请选择序列号分段";
						}
						return null;
					}
					@Override
					protected void onPostExecute(Void result) {
						// TODO Auto-generated method stub
						super.onPostExecute(result);
						if(dialog!=null&&dialog.isShowing()){
							dialog.dismiss();
						}
						showHasDialog(str_sernum);
					}
				}.execute();
			}else{
				//Log.e("TOTOTO", "没有网络");
				//Toast.makeText(BTMainActivity.this, "当前网络不可用!", Toast.LENGTH_SHORT).show();
				showToast("当前网络不可用!");
				//在这语音播报“网路不可用”
				Intent sendbroad = new Intent();
				sendbroad.setAction("com.xintu.btphone.speechhit");
				sendbroad.putExtra("speechhit", "当前网络不可用!");
				sendBroadcast(sendbroad);
			}
			break;
		case R.id.cancel:
			notDialog.dismiss();
			break;
		case R.id.tv_local_devices:
			Timer timer=new Timer();
			TimerTask task=new TimerTask(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					clickcount+=1;
					if(clickcount>=4)
						reqMACHandler.sendEmptyMessage(2015);
				}
			};
			timer.schedule(task, 2000);
			break;
		}	
	}

	@SuppressWarnings("unused")
	private class CounterThread implements Runnable {
		@SuppressWarnings("static-access")
		public void run() {
			while(true) {
				try {
					m_thread.sleep(1000);
				}
				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				m_iCounter += 1;
				if (m_iCounter <= 2) {
					if(clickcount>=5){
						Log.d("MY", "msg:"+clickcount);
						if(null!=reqMACHandler)
							reqMACHandler.sendEmptyMessage(2015);
						m_thread.interrupt();
					}
				}else{
					reqMACHandler.sendEmptyMessage(2016);
					m_thread.interrupt();
					
				}
				
				break;
			}
		}	
	}


	public String splitstr(String str, String strsp, int i) {

		String[] strarray = str.split(strsp);
		return strarray[i].trim();
	}

	/*public boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}*/
	/**光标的位置*/
	private int getEditTextCursorIndex(EditText mEditText) {
		return mEditText.getSelectionStart();
	}
	/** 向EditText指定光标位置插入字符串 */
	private void insertText(EditText mEditText, String mText) {
		mEditText.getText().insert(getEditTextCursorIndex(mEditText), mText);
		mEditText.setSelection(getEditTextCursorIndex(mEditText));

	}
	/** 向EditText指定光标位置删除字符串 */
	private void deleteText(EditText mEditText) {
		int loc= getEditTextCursorIndex(mEditText);
		if (!"".equals((mEditText.getText().toString()))) {
			mEditText.getText().delete(loc- 1,loc);
			mEditText.setSelection(loc-1);
		}
	}

	private void play(int id) {
		int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int current = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		float value = (float)0.7 / max * current;
		spool.setVolume(spool.play(id, value, value, 0, 0, 1f), value, value);
	}

	public static boolean indexOfString(String src, String dest) {
		boolean flag2 = false;
		if (src.indexOf(dest)!=-1) {
			flag2 = true;
		}
		return flag2;
	}

	//查看电话号码
	private void fillPhoneBook() {
		personsSource = m_contactsDao.findAll();
		m_listContact=new ArrayList<ContactItem>();
		m_listContact = filledData(personsSource);
		persons.addAll(m_listContact);
		Collections.sort(persons, comparator);
		m_adapterContact = new ContactAdapter(MainActivity.this, persons);
		addresslist_listview.setAdapter(m_adapterContact);
		addresslist_listview.setOnItemClickListener(MainActivity.this);
		addresslist_listview.hiddenMore();
		addresslist_listview.setonRefreshListener(new OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				// TODO Auto-generated method stub
				addresslist_listview.onRefreshComplete("");
			}
		});
		m_adapterContact.notifyDataSetChanged();//通知界面数据的适配器的数据更新了
	}

	private void hungup(){
		m_bizMain.reqTerminateCall();
		
		dialcall.setVisibility(ViewGroup.VISIBLE);
		hungcall.setVisibility(ViewGroup.GONE);
		BTPhoneApplication.getInstance().flag=false;
		BTPhoneApplication.getInstance().ismobile=false;
		BTPhoneApplication.getInstance().isComing=false;
		spnumber=SharedPreferencesUtil.getStringData(BTPhoneApplication.getInstance(), "spnumber", "");
		if(!"".equals(spnumber)){
			tv_phoneName.setText("上次通话");
			save_number.setText(spnumber);
		}
		LEIDAUtil.setAudioFileEnabled(false);
	}

	private void startThread(){
		new Thread() {
			public void run() {
				try {
					Thread.sleep(8000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				syncPhoneBook();
			}
		}.start();
	}

	private void syncPhoneBook() {
		m_bizMain.reqPhoneBook();//同步电话本
	}
	//同步电话话本按钮
	public void onPhoneBook(View v) {
		syncPhoneBook();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		allNumberCount=0;
		et_search_contact.setHint("");
		BTPhoneApplication.getInstance().isActivityTop=false;
		super.onStop();

		try{
			if(null!=m_listContact)
				m_listContact.clear();
			if(null!=persons)
				persons.clear();
			if(personsSource!=null)
				personsSource.clear();
			if(null!=m_adapterContact&&!"".equals(m_adapterContact))
				m_adapterContact.notifyDataSetChanged();
		}catch(IndexOutOfBoundsException e){e.printStackTrace();}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		allNumberCount=0;
		clickcount=0;
		BTPhoneApplication.getInstance().iscall=false;
		BTPhoneApplication.getInstance().isComing=false;
		BTPhoneApplication.getInstance().isBTTop=true;
		if(!"".equals(BTPhoneApplication.getInstance().come_number)){
			if(null!=DialFloatService.dialFloathandler)
				DialFloatService.dialFloathandler.sendEmptyMessage(2156);
		}
	}

	/**
	 * 数据按字母进行排序
	 * @param list
	 * @return
	 */
	private ArrayList<ContactItem> filledData(ArrayList<ContactItem> list)
	{
		if(null!=list&&!"".equals(list)){
			for (int i = 0; i < list.size(); i++)
			{
				String pinyin = list.get(i).getPinyin();
				if (pinyin == null || pinyin.length() == 0) {
					list.get(i).setSortletter("#");
				}
				else {
					String sortString = pinyin.substring(0, 1).toUpperCase();
					if (sortString.matches("[A-Z]"))
					{
						list.get(i).setSortletter(sortString.toUpperCase());
					}
					else
					{
						list.get(i).setSortletter("#");
					}
				}
			}
			return list;
		}else{
			return null;
		}	
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//点击通讯录中的条目进入通讯录详细页
		if(null!=persons&&persons.size()>0){
			ContactItem item = persons.get(position-1);
			if(item != null&&null!=m_handler){
				contactsname=item.m_strName.toString();
				Message msg=m_handler.obtainMessage();
				msg.what=5000;
				msg.obj=contactsname;
				m_handler.handleMessage(msg);
			}
			contacts_list.setVisibility(ViewGroup.GONE);
			show_contact.setVisibility(ViewGroup.GONE);
			layout_back_contact.setVisibility(ViewGroup.VISIBLE);
			lv_back_contact.setVisibility(ViewGroup.VISIBLE);
			contact_name.setText(contactsname);
		}
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
	}
	/**
	 * 字符转拼音
	 */
	public String getStringPinYin(String str)

	{
		StringBuilder sb = new StringBuilder();
		String tempPinyin = null;
		for (int i = 0; i < str.length(); ++i)
		{
			tempPinyin = getCharacterPinYin(str.charAt(i));
			if (tempPinyin == null)
			{
				sb.append(str.charAt(i));
			}
			else
			{
				sb.append(tempPinyin);
			}
		}
		return sb.toString();
	}

	public String getCharacterPinYin(char c)
	{
		try
		{
			pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
		}
		catch (BadHanyuPinyinOutputFormatCombination e)
		{
			e.printStackTrace();
		}
		if (pinyin == null)
			return null;
		return pinyin[0];
	}
	/**
	 * 过滤数据的方法
	 * @param key
	 */
	private void filterData(String key)
	{
		//如果内容为空字符，将数据源添加到显示数据中
		if (key.equals(""))
		{
			persons.clear();
			persons.addAll(filledData(personsSource));
			Collections.sort(persons, comparator);
			if(null!=m_adapterContact&&!"".equals(m_adapterContact))
				m_adapterContact.notifyDataSetChanged();
		}
		//数据不为空时，按找姓名中的汉字或拼音字母进行筛选
		else
		{
			ArrayList<ContactItem> list = new ArrayList<ContactItem>();
			for (int i = 0; i < personsSource.size(); i++)
			{
				if(personsSource.get(i).getPinyin().startsWith(key.toLowerCase())
						|| personsSource.get(i).m_strName.contains(key))
				{
					list.add(personsSource.get(i));
				}
			}
			persons.clear();
			if(null!=m_listContact){
				persons.addAll(filledData(list));
			}
			Collections.sort(persons, comparator);
			if(null!=m_adapterContact&&!"".equals(m_adapterContact))
				m_adapterContact.notifyDataSetChanged();
		}
	}
	public void speech(){
		Intent sendbroad = new Intent();
		sendbroad.setAction("com.xintu.btphone.speechhit");
		sendbroad.putExtra("speechhit", "蓝牙未连接");
		sendBroadcast(sendbroad);
	}

	@SuppressLint("DefaultLocale")
	public void reqMAC() {
		m_bizMain.reqDeviceName();
	}
	public static String decode(String bytes)//解码16进制的ASCII码
	{
		ByteArrayOutputStream baos=new ByteArrayOutputStream(bytes.length()/2);
		//将每2位16进制整数组装成一个字节
		String hexString="0123456789ABCDEF";
		for(int i=0;i<bytes.length();i+=2)
			baos.write((hexString.indexOf(bytes.charAt(i))<<4 |hexString.indexOf(bytes.charAt(i+1))));
		return new String(baos.toByteArray());
	}

	public int screen(){
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int widthPixels= dm.widthPixels;
		//int heightPixels= dm.heightPixels;
		return widthPixels;
	}

	public void showToast(String str){
		if(1280==screen()){
			Toast toast=Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER|Gravity.BOTTOM,280, 50);
			toast.show();
		}else{
			Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
		}
	}


	private void showDeleteDev() {
		LayoutInflater inflater = (LayoutInflater) this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_delete, null);
		delDialog= new Dialog(this, R.style.dialog);
		delDialog.setContentView(dialogView);
		Window win=delDialog.getWindow();
		android.view.WindowManager.LayoutParams lp=win.getAttributes();
		win.setGravity(Gravity.CENTER);
		lp.x=280;
		lp.y=0;
		win.setAttributes(lp);
		delDialog.show();
	}
}
