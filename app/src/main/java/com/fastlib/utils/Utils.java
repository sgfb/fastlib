package com.fastlib.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sgfb on 16/7/11.
 * 公共工具类
 */
public class Utils{

    private Utils(){
        //no instance
    }

    /**
     * 对字符串进行MD5加密
     * @param source
     * 			需要进行加密的字符串
     * @param is16bits
     * 			加密长度,true为16位反之32位
     * @return
     */
    public static String getMd5(String source,boolean is16bits){
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
        if(is16bits)
            return md5StrBuff.substring(8, 24).toUpperCase(Locale.getDefault()); //16位加密，从第9位到25位
        else
            return md5StrBuff.toString();
    }

    /**
     * sha1文件检验
     * @param filePath
     * @return
     */
    public static byte[] getSha1(String filePath){
        try {
            FileInputStream in=new FileInputStream(new File(filePath));
            MessageDigest md= MessageDigest.getInstance("SHA-1");
            byte[] data=new byte[1024*1024*10];
            int len;
            while((len=in.read(data))!=-1)
                md.update(data,0,len);
            in.close();
            return md.digest();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 是否是正规手机号
     * @param phone
     * @return
     */
    public static boolean isPhoneNumber(String phone){
        Pattern p = Pattern.compile("^[1][3-8]\\d{9}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    /**
     * 获取某数的二进制的具体几位
     * @param raw
     * @param start
     * @param end
     * @return
     */
    public static int getSomeBits(long raw,int start,int end){
		int flag=0;
		if(start>end)
			return flag;
		for(int i=start;i<end;i++)
			flag|=(int) Math.pow(2,64-i);
		flag&=raw;
		return flag;
	}

    public static int bytesToInt(byte... data){
        int var=0;
        for(int i=0;i<data.length;i++){
            int middle=data[i];
            var|=((0xff&middle)<<i*8);
        }
        return var;
    }

    public static int bytesToInt(int start,int end,byte... data){
        if(end<start)
            throw new IllegalArgumentException("start大于end");
        byte[] bytes=new byte[end-start];
        for (int i=0;i<end-start;i++)
            bytes[i]=data[start+i];
        return bytesToInt(bytes);
    }
}
