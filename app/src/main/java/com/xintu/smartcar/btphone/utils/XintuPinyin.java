package com.xintu.smartcar.btphone.utils;


import android.util.Log;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class XintuPinyin {
	private static final String TAG = "BluetoothPhone";
	private static HanyuPinyinOutputFormat format1;
	private static String[] pinyin;
	public static String getStringPinYin(String str)
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

	public static String getCharacterPinYin(char c)
	{
		try
		{
			pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format1);
		}
		catch (BadHanyuPinyinOutputFormatCombination e)
		{
			e.printStackTrace();
		}
		if (pinyin == null)
			return null;
		return pinyin[0];
	}
	
	public static String doConvert(String strInput) {
		try {	
			format1 = new HanyuPinyinOutputFormat();
			format1.setToneType(HanyuPinyinToneType.WITHOUT_TONE);	 
			 
			String strResult = getStringPinYin(strInput);
			Log.e(TAG, "doConvert " + strInput + "," + strResult);
			return strResult;
		}
		catch (Exception e) {
			return "AAAA";
		}
	}
}