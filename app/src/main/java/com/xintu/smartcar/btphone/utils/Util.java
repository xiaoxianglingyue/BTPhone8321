package com.xintu.smartcar.btphone.utils;

import android.content.Context;

import com.xintu.smartcar.btphone.application.BTPhoneApplication;
import com.xintu.smartcar.btphone.db.dao.ContactsDao;

public class Util {
	private static ContactsDao m_contactsDao;
	private static ImportAssetsUtil importAssetsUtil;

	public Util(){
		importAssetsUtil =new ImportAssetsUtil(getContext());
		m_contactsDao = new ContactsDao(getContext());
	}

	public static Context getContext(){
		BTPhoneApplication app=BTPhoneApplication.getInstance();
		Context context = app.getApplicationContext();
		return context;
	}

	
	public static String inComingName(String strNumber){
		String strName="";
		if(!"".equals(importAssetsUtil.importGeneralContact(strNumber,true))){
			String[] strSplit = importAssetsUtil.importGeneralContact(strNumber,true).split(",");
			strName=strSplit[0];
		}else{
			if(strNumber.substring(0, 1).equals("0")){
				strName = m_contactsDao.findName(strNumber);//查询到人�??
				if(null!=strName&&!"".equals(strName)){

				}else{
					String strNumber1=strNumber.substring(3, strNumber.length());
					strName = m_contactsDao.findName(strNumber1);//查询到人�??
					if(null!=strName&&!"".equals(strName)){

					}else{
						String strNumber2=strNumber.substring(4, strNumber.length());
						strName = m_contactsDao.findName(strNumber2);//查询到人�??
						if(null!=strName&&!"".equals(strName)){

						}else{

						}
					}
				}
			}else{
				strName = m_contactsDao.findName(strNumber);
				if(null!=strName&&!"".equals(strName)){

				}else{
					String strNumber1="0086"+strNumber;
					strName = m_contactsDao.findName(strNumber1);
					if(null!=strName&&!"".equals(strName)){
					}else{
						String strNumber2="86"+strNumber;
						strName = m_contactsDao.findName(strNumber2);
						if(null!=strName&&!"".equals(strName)){

						}else{
							String strNumber3="00"+strNumber;
							strName = m_contactsDao.findName(strNumber3);
							if(null!=strName&&!"".equals(strName)){

							}else{

							}
						}
					}
				}
			}
		}
		return strName;
	}
}
