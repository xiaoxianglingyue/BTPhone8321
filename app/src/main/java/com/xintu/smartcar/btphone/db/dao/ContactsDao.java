package com.xintu.smartcar.btphone.db.dao;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xintu.smartcar.bluetoothphone.iface.CallInfo;
import com.xintu.smartcar.bluetoothphone.iface.SelectContactInfo;
import com.xintu.smartcar.btphone.bean.ContactItem;
import com.xintu.smartcar.btphone.db.DBHelper;


public class ContactsDao {

	private DBHelper m_dbHelper;
	// 汉字转拼音的工具
	private HanyuPinyinOutputFormat format;
	// 存放拼音使用的字符串数组
	private String[] pinyin;
	private  String contacts;
	private Context context;
	public ContactsDao(Context context){
		this.context=context;
		m_dbHelper = new DBHelper(context);
		format = new HanyuPinyinOutputFormat();
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
	}

	//清空
	public void clearAll() {
		SQLiteDatabase db = m_dbHelper.getWritableDatabase();
		db.execSQL("delete from contacts");
		revertSeq();
		db.close();
	}	
	/**
	 * �??��据库中添加数�??
	 * @param name   姓名
	 * @param number 电话号码
	 * @param type   电话性质（单位，�??或个人手机）
	 */
	public void save(String number,String name,String namepinyin)
	{
		SQLiteDatabase db = m_dbHelper.getWritableDatabase();
		db.execSQL("insert into  contacts(number,name,namepinyin) values(?,?,?)",new Object[]{number,name,namepinyin});
		db.close();
	}

	public int findAllCount()
	{
		m_dbHelper=new DBHelper(context);
		int result=0;
		SQLiteDatabase db = m_dbHelper.getReadableDatabase();
		Cursor cursor=db.query("contacts", new String[]{"number"}, null, null, null, null, null);
		if(cursor.moveToNext())
		{

			result=cursor.getCount();
		}
		cursor.close();
		db.close();
		Log.i("Tag", "到这3"+result);
		return result;
	}
	/**
	 * 
	 * @param number 电话号码
	 * @param mode   电话类型
	 */
	public void update(String name,String number,int mode)
	{

		SQLiteDatabase db = m_dbHelper.getWritableDatabase();
		db.execSQL("update contacts (name,number,mode) values(?,?,?)", new Object[]{name,number,mode});

		db.close();		
	}


	/**
	 * 根据电话号码进行模糊查询号码的个�??
	 * @param number
	 * @return
	 */

	public int findCountName(String namepinyin)
	{
		int result=0;

		SQLiteDatabase db = m_dbHelper.getReadableDatabase();
		Cursor cursor=db.query("contacts", new String[]{"name","namepinyin"}, "namepinyin like ?", new String[]{"%"+namepinyin+"%"}, null, null, null);
		if(cursor.moveToNext())
		{
			result=cursor.getCount();
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * 根据电话号码进行模糊查询号码的个�??
	 * @param number
	 * @return
	 */

	public int findCnt(String number)
	{
		int result=0;
		SQLiteDatabase db = m_dbHelper.getReadableDatabase();
		Cursor cursor=db.query("contacts", new String[]{"name","number"}, "number like ?", new String[]{number+"%"}, null, null, null);
		if(cursor.moveToNext())
		{
			result=cursor.getCount();
		}
		cursor.close();
		db.close();
		return result;
	}

	//根据电话号码前几位，获取该号码的全部
	public String[] findContactByPartNumber(String strPartNumber) {
		String strNumber="";
		String strName = "";
		SQLiteDatabase db = m_dbHelper.getReadableDatabase();
		Cursor cur=db.query("contacts", new String[]{"name","number"}, "number like ?", new String[]{strPartNumber + "%"}, null, null, null);
		if(cur.moveToNext()) {
			strName = cur.getString(cur.getColumnIndex("name"));
			strNumber = cur.getString(cur.getColumnIndex("number"));
		}	
		String[] strArr = new String[2];
		strArr[0] = strNumber;
		strArr[1] = strName;
		return strArr;
	}

	/**
	 * 根据电话号码查询人名
	 * @param number
	 * @return
	 */
	public String findName(String number){
		String name="";
		SQLiteDatabase db = m_dbHelper.getReadableDatabase();
		Cursor cur=db.query("contacts", new String[]{"name","number"}, "number = ?", new String[]{number}, null, null, null);
		if(cur.moveToNext()){
			name = cur.getString(cur.getColumnIndex("name"));
		}
		cur.close();
		db.close();
		return name;
	}

	public boolean isExitNumber(String number){
		String strNumber="";
		SQLiteDatabase db = m_dbHelper.getReadableDatabase();
		Cursor cur=db.query("contacts", new String[]{"number"}, "number = ?", new String[]{number}, null, null, null);
		if(cur.moveToNext()){
			strNumber = cur.getString(cur.getColumnIndex("number"));
			if(!"".equals(strNumber)){
				return true;
			}
		}
		cur.close();
		db.close();
		return false;
	}
	//根据人名字查找电话号�??
	public String findNumber(String name){
		String number="";
		Log.d("MainActivity", "daozhe2"+name);

		Log.d("MainActivity", "name"+name);
		SQLiteDatabase db = m_dbHelper.getReadableDatabase();
		Cursor cursor=db.query("contacts", new String[]{"number","name"}, "name like ?", new String[]{"%"+name+"%"}, null, null, null);
		if(cursor.moveToNext()){
			number = cursor.getString(cursor.getColumnIndex("number"));
			Log.d("MainActivity", "number"+number);
		}
		cursor.close();
		db.close();
		return number;
	}

	//根据人名字查找电话号�??
	public String findNumberisExit(String namepinyin){
		String number="";
		SQLiteDatabase db = m_dbHelper.getReadableDatabase();
		Cursor cursor=db.query("contacts", new String[]{"number","namepinyin"}, "namepinyin like ?", new String[]{"%"+namepinyin+"%"}, null, null, null);
		if(cursor.moveToNext()){
			number = cursor.getString(cursor.getColumnIndex("number"));
		}
		cursor.close();
		db.close();
		return number;
	}


	//根据人名字查找电话号�??
	public ArrayList<String> findListNumber(String name){
		String number="";
		Log.d("MainActivity", "number");
		ArrayList<String> numbers=new ArrayList<String>();
		SQLiteDatabase db = m_dbHelper.getReadableDatabase();
		Cursor cursor=db.query("contacts", new String[]{"number","name"}, "name=?", new String[]{name}, null, null, null);
		while(cursor.moveToNext()){
			number = cursor.getString(cursor.getColumnIndex("number"));
			numbers.add(number);
		}
		cursor.close();
		db.close();
		return numbers;
	}

	//根据人名字拼音查找电话号�??
	public  ArrayList<String> findAllName(String namepinyin){
		String name="";
		String number="";
		int i=0;
		ArrayList <String> names=new ArrayList<String>();
		SQLiteDatabase db = m_dbHelper.getReadableDatabase();
		Cursor cursor=db.query("contacts", new String[]{"name","number","namepinyin"}, "namepinyin like ?", new String[]{namepinyin}, null, null, null);
		while(cursor.moveToNext()){
			i=cursor.getCount();
			name = cursor.getString(cursor.getColumnIndex("name"));
			number=cursor.getString(cursor.getColumnIndex("number"));
			names.add("   "+i+"     "+name+":"+number);
		}
		cursor.close();
		db.close();
		return names;
	}

	//根据人名字拼音查找电话号
		public  ArrayList<CallInfo> findAllName3(String namepinyin){
			int i=0;
			ArrayList<CallInfo> list=new ArrayList<CallInfo>();
			SQLiteDatabase db = m_dbHelper.getReadableDatabase();
			Cursor cursor=db.query("contacts", new String[]{"name","number","namepinyin"}, "namepinyin like ?", new String[]{"%"+namepinyin+"%"}, null, null, null);
			while(cursor.moveToNext()){
				i++;
				CallInfo callInfo=new CallInfo();
				callInfo.count=cursor.getCount();
				callInfo.m_strName=cursor.getString(cursor.getColumnIndex("name"));
				callInfo.m_strNumber=cursor.getString(cursor.getColumnIndex("number"));
				list.add(callInfo);
				if(i==11){
					i=0;
					break;
				}
			}
			cursor.close();
			db.close();
			return list;
		}
	
	//根据人名字拼音查找电话号
	public  SelectContactInfo findAllName2(String namepinyin){
		int i=0;
		SelectContactInfo selectContactInfo=new SelectContactInfo();
		SQLiteDatabase db = m_dbHelper.getReadableDatabase();
		Cursor cursor=db.query("contacts", new String[]{"name","number","namepinyin"}, "namepinyin like ?", new String[]{"%"+namepinyin+"%"}, null, null, null);
		while(cursor.moveToNext()){
			i++;
			CallInfo callInfo=new CallInfo();
			callInfo.count=cursor.getCount();
			callInfo.m_strName=cursor.getString(cursor.getColumnIndex("name"));
			callInfo.m_strNumber=cursor.getString(cursor.getColumnIndex("number"));
			selectContactInfo.selectContacts.add(callInfo);
			if(i==11){
				i=0;
				break;
			}
		}
		cursor.close();
		db.close();
		return selectContactInfo;
	}
	
	//根据人名字拼音查找电话号
		public  CallInfo findAllNameData(String namepinyin){
			int i=0;
			CallInfo names=new CallInfo();
			SQLiteDatabase db = m_dbHelper.getReadableDatabase();
			Cursor cursor=db.query("contacts", new String[]{"name","number","namepinyin"}, "namepinyin like ?", new String[]{"%"+namepinyin+"%"}, null, null, null);
			while(cursor.moveToNext()){
				i++;
				CallInfo selectContact=new CallInfo();
				selectContact.count=cursor.getCount();
				selectContact.m_strName=cursor.getString(cursor.getColumnIndex("name"));
				selectContact.m_strNumber=cursor.getString(cursor.getColumnIndex("number"));
				if(i==11){
					i=0;
					break;
				}
			}
			cursor.close();
			db.close();
			return names;
		}
	
	public String findAllNumber(String namepinyin){
		String number="";
		Log.d("MainActivity", "daozhe2"+namepinyin);

		SQLiteDatabase db = m_dbHelper.getReadableDatabase();
		Cursor cursor=db.query("contacts", new String[]{"number","namepinyin"}, "namepinyin like ?", new String[]{"%"+namepinyin+"%"}, null, null, null);
		if(cursor.moveToNext()){
			number = cursor.getString(cursor.getColumnIndex("number"));
		}
		cursor.close();
		db.close();
		return number;
	}

	/**
	 * 查找出所有的电话号码
	 * @return List集合
	 */
	public ArrayList<ContactItem> findAll()
	{
		m_dbHelper = new DBHelper(context);
		ArrayList<ContactItem> contactList=new ArrayList<ContactItem>();
		SQLiteDatabase db = m_dbHelper.getReadableDatabase();
		Cursor cursor=db.query("contacts", null, null, null, null, null, null);
		ContactItem item  =null;
		while(cursor.moveToNext()) 	{
			item = new ContactItem();
			item.m_strName =cursor.getString(cursor.getColumnIndex("name"));
			item.m_strNumber = cursor.getString(cursor.getColumnIndex("number"));
			item.setPinyin(getStringPinYin(item.m_strName));	
			contactList.add(item);
		}
		cursor.close();
		db.close();
		return contactList;
	}



	/**
	 * 分页找出�??��的记�??
	 * @return List集合
	 */
	public List<ContactItem> findByPage(int page)
	{
		int pageIndex=page*10;
		List<ContactItem> contactList=new ArrayList<ContactItem>();
		SQLiteDatabase db = m_dbHelper.getWritableDatabase();
		Cursor cursor=db.rawQuery("select * from contacts limit 10 offset ?", new String[]{pageIndex+""});
		while(cursor.moveToNext()) {
			ContactItem item = new ContactItem();
			item.m_strName = cursor.getString(cursor.getColumnIndex("name"));
			item.m_strNumber = cursor.getString(cursor.getColumnIndex("number"));
			contactList.add(item);
		}
		db.close();
		return contactList;
	}

	/**
	 * 根据电话号码更新电话本中指定记录的mode
	 * @param number 电话号码
	 * @param mode  
	 */
	public void update(String number,String mode)
	{

		SQLiteDatabase db = m_dbHelper.getWritableDatabase();
		db.execSQL("update contacts set mode=? where number=?", new Object[]{mode,number});
		db.close();		
	}

	/**
	 * 根据姓名删除电话本中对应的记�??
	 * @param number 电话号码
	 */
	public void delete(String name)
	{

		SQLiteDatabase db = m_dbHelper.getWritableDatabase();
		db.execSQL("delete from contacts where name=?", new Object[]{name});
		db.close();
	}



	/**
	 * 字符转拼�??
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

	public void clearFeedTable(){

		SQLiteDatabase db = m_dbHelper.getWritableDatabase();
		db.delete("contacts", null, null);
		revertSeq();
	}

	private void revertSeq() {
		String sql = "update sqlite_sequence set seq=0 where name='"+contacts+"'";
		SQLiteDatabase db =  m_dbHelper.getWritableDatabase();
		db.execSQL(sql);
		// m_dbHelper.free();
	}

}
