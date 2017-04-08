package com.xintu.smartcar.btphone.utils;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

/**
 * reference apache commons <a
 * href="http://commons.apache.org/codec/">http://commons.apache.org/codec/</a>
 *
 * @author Aub
 *
 */
public class GlobalUtil {
    /**
     * 用于建立十六进制字符的输出的小写字符数组
     */
    private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    /**
     * 用于建立十六进制字符的输出的大写字符数组
     */
    private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data
     *            byte[]
     * @return 十六进制char[]
     */
    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }
    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data
     *            byte[]
     * @param toLowerCase
     *            <code>true</code> 传换成小写格�? �? <code>false</code> 传换成大写格�?
     * @return 十六进制char[]
     */
    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }
    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data
     *            byte[]
     * @param toDigits
     *            用于控制输出的char[]
     * @return 十六进制char[]
     */
    protected static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }
    /**
     * 将字节数组转换为十六进制字符�?
     *
     * @param data
     *            byte[]
     * @return 十六进制String
     */
    public static String encodeHexStr(byte[] data) {
        return encodeHexStr(data, true);
    }
    /**
     * 将字节数组转换为十六进制字符�?
     *
     * @param data
     *            byte[]
     * @param toLowerCase
     *            <code>true</code> 传换成小写格�? �? <code>false</code> 传换成大写格�?
     * @return 十六进制String
     */
    public static String encodeHexStr(byte[] data, boolean toLowerCase) {
        return encodeHexStr(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }
    /**
     * 将字节数组转换为十六进制字符�?
     *
     * @param data
     *            byte[]
     * @param toDigits
     *            用于控制输出的char[]
     * @return 十六进制String
     */
    protected static String encodeHexStr(byte[] data, char[] toDigits) {
        return new String(encodeHex(data, toDigits));
    }
    /**
     * 将十六进制字符数组转换为字节数组
     *
     * @param data
     *            十六进制char[]
     * @return byte[]
     * @throws RuntimeException
     *             如果源十六进制字符数组是�?个奇怪的长度，将抛出运行时异�?
     */
    public static byte[] decodeHex(char[] data) {
        int len = data.length;
        if ((len & 0x01) != 0) {
            throw new RuntimeException("Odd number of characters.");
        }
        byte[] out = new byte[len >> 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }
        return out;
    }
    /**
     * 将十六进制字符转换成�?个整�?
     *
     * @param ch
     *            十六进制char
     * @param index
     *            十六进制字符在字符数组中的位�?
     * @return �?个整�?
     * @throws RuntimeException
     *             当ch不是�?个合法的十六进制字符时，抛出运行时异�?
     */
    protected static int toDigit(char ch, int index) {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal character " + ch
                    + " at index " + index);
        }
        return digit;
    }
    
	/** 
     * int整数转换�?4字节的byte数组 
     *  
     * @param i 
     *            整数 
     * @return byte数组 
     */  
    public static byte[] intToByte4(int i) {  
        byte[] targets = new byte[4];  
        targets[3] = (byte) (i & 0xFF);  
        targets[2] = (byte) (i >> 8 & 0xFF);  
        targets[1] = (byte) (i >> 16 & 0xFF);  
        targets[0] = (byte) (i >> 24 & 0xFF);  
        return targets;  
    }  
  
    /** 
     * long整数转换�?8字节的byte数组 
     *  
     * @param lo 
     *            long整数 
     * @return byte数组 
     */  
    public static byte[] longToByte8(long lo) {  
        byte[] targets = new byte[8];  
        for (int i = 0; i < 8; i++) {  
            int offset = (targets.length - 1 - i) * 8;  
            targets[i] = (byte) ((lo >>> offset) & 0xFF);  
        }  
        return targets;  
    }  
  
    /** 
     * short整数转换�?2字节的byte数组 
     *  
     * @param s 
     *            short整数 
     * @return byte数组 
     */  
    public static byte[] unsignedShortToByte2(int s) {  
        byte[] targets = new byte[2];  
        targets[0] = (byte) (s >> 8 & 0xFF);  
        targets[1] = (byte) (s & 0xFF);  
        return targets;  
    }  
  
    /** 
     * byte数组转换为无符号short整数 
     *  
     * @param bytes 
     *            byte数组 
     * @return short整数 
     */  
    public static int byte2ToUnsignedShort(byte[] bytes) {  
        return byte2ToUnsignedShort(bytes, 0);  
    }  
  
    /** 
     * byte数组转换为无符号short整数 
     *  
     * @param bytes 
     *            byte数组 
     * @param off 
     *            �?始位�? 
     * @return short整数 
     */  
    public static int byte2ToUnsignedShort(byte[] bytes, int off) {  
        int high = bytes[off];  
        int low = bytes[off + 1];  
        return (high << 8 & 0xFF00) | (low & 0xFF);  
    }  
  
    /** 
     * byte数组转换为int整数 
     *  
     * @param bytes 
     *            byte数组 
     * @param off 
     *            �?始位�? 
     * @return int整数 
     */  
    public static int byte4ToInt(byte[] bytes, int off) {  
        int b0 = bytes[off] & 0xFF;  
        int b1 = bytes[off + 1] & 0xFF;  
        int b2 = bytes[off + 2] & 0xFF;  
        int b3 = bytes[off + 3] & 0xFF;  
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;  
    }  

    
  //低位在前，高位在后的字节
    int byteToInt(byte[] B, int off)
    {
	    int ans;
	    ans = ((int)B[off] & 0xff)<<0;
	    ans +=((int)B[off + 1] & 0xff)<<8;
	    ans +=((int)B[off + 2] & 0xff)<<16;
	    ans +=((int)B[off + 3] & 0xff)<<24;
	    return ans;
    }
	/**
	 * 将一个单字节的byte转换�?32位的int
	 * 
	 * @param b
	 *            byte
	 * @return convert result
	 */
	public static int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}

	/**
	 * 将一个单字节的Byte转换成十六进制的�?
	 * 
	 * @param b
	 *            byte
	 * @return convert result
	 */
	public static String byteToHex(byte b) {
		int i = b & 0xFF;
		return Integer.toHexString(i);
	}    
	
	public static String arrUCSToString(byte[] byteData) {
		String strResult = null;
		try {
			strResult = new String(byteData, "UnicodeBigUnmarked");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strResult;
	}
	
    public static void main(String[] args) {
        String srcStr = "待转换字符串";
        String encodeStr = encodeHexStr(srcStr.getBytes());
        String decodeStr = new String(decodeHex(encodeStr.toCharArray()));
        System.out.println("转换前：" + srcStr);
        System.out.println("转换后：" + encodeStr);
        System.out.println("还原后：" + decodeStr);
    }
    
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
}
