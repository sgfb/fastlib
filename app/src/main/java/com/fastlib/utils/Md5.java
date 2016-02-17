package com.fastlib.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class Md5 {
	
	
	public static final int MD5_16=0x0000;
	public static final int MD5_32=0x0001;
	
	
	private Md5(){
		
	}
	
	/**
	 * 对字符串进行MD5加密
	 * @param source
	 * 			需要进行加密的字符串
	 * @param type
	 * 			加密长度，0为16位，1为32位(除了0以外任意数都为32位)
	 * 			取常量MD5_16和常量MD5_32
	 * @return
	 */
	 public static String getMd5(String source,int type){
		 MessageDigest messageDigest = null;       
	     
	      try {       
	          messageDigest = MessageDigest.getInstance("MD5");       
	     
	          messageDigest.reset();       
	     
	          messageDigest.update(source.getBytes("UTF-8"));       
	      } catch (NoSuchAlgorithmException e) {       
	          System.out.println("NoSuchAlgorithmException caught!");       
	          System.exit(-1);       
	      } catch (UnsupportedEncodingException e) {       
	          e.printStackTrace();       
	      }       
	     
	      byte[] byteArray = messageDigest.digest();       
	     
	      StringBuffer md5StrBuff = new StringBuffer();       
	        
	      for (int i = 0; i < byteArray.length; i++) {                   
	          if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)       
	              md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));       
	          else       
	              md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));       
	      }       
	      if(type==MD5_16){
	    	  //16位加密，从第9位到25位  
		      return  "f1f348ebf7e6"+md5StrBuff.substring(8, 24).toString().toUpperCase(Locale.getDefault())+"6401";     
	      }else{
		      return "f1f348ebf7e6"+md5StrBuff.toString()+"6401";
	      }
	 }
}
