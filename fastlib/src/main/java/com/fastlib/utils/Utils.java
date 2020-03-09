package com.fastlib.utils;

import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.core.graphics.drawable.DrawableCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
     * 元素合并为集合
     * @param values 零散元素
     * @param <T> 元素类型
     * @return 元素集合
     */
    public static <T> List<T> listOf(T... values){
        List<T> list=new ArrayList<>();
        if(values==null)
            return list;
        Collections.addAll(list, values);
        return list;
    }

    /**
     * 安全转换字符串为整型
     * @param value 字符串
     * @param defValue 默认值
     * @return 如果成功返回字符串转换后的整形，失败则返回默认值
     */
    public static int safeValueOf(String value, int defValue){
        try{
            return Integer.parseInt(value);
        }catch (NumberFormatException e){
            return defValue;
        }
    }

    /**
     * 安全转换字符串为长整型
     * @param value 字符串
     * @param defValue 默认值
     * @return 转换失败返回默认值
     */
    public static long safeValueOf(String value, long defValue){
        try{
            return Long.parseLong(value);
        }catch (NumberFormatException e){
            return defValue;
        }
    }

    /**
     * 安全转换字符串为单精浮点型
     * @param value 字符串
     * @param defValue 默认值
     * @return 转换失败返回默认值
     */
    public static float safeValueOf(String value, float defValue){
        try{
            return Float.parseFloat(value);
        }catch (NumberFormatException e){
            return defValue;
        }
    }

    /**
     * 安全转换字符串为双精浮点型
     * @param value 字符串
     * @param defValue 默认值
     * @return 转换失败返回默认值
     */
    public static double safeValueOf(String value, double defValue){
        try{
            return Double.parseDouble(value);
        }catch (NumberFormatException e){
            return defValue;
        }
    }

    /**
     * Drawable染色
     * @param src 原始Drawable
     * @param color 染色
     * @return 染色后的Drawable
     */
    public static Drawable tintDrawable(Drawable src,@ColorInt int color){
        Drawable wrapDrawable= DrawableCompat.wrap(src.mutate());
        DrawableCompat.setTint(wrapDrawable,color);
        return wrapDrawable;
    }

    /**
     * 对字符串进行MD5加密
     * @param source 需要进行加密的字符串
     * @param is16bits 加密长度,true为16位反之32位
     * @return MD5加密后字符串
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
        StringBuilder md5StrBuff = new StringBuilder();

        for (byte aByteArray : byteArray) {
            if (Integer.toHexString(0xFF & aByteArray).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & aByteArray));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & aByteArray));
        }
        if(is16bits)
            return md5StrBuff.substring(8, 24).toUpperCase(Locale.getDefault()); //16位加密，从第9位到25位
        else
            return md5StrBuff.toString();
    }

    /**
     * sha1文件检验
     * @param filePath 文件路径
     * @param type     文件校验类型
     * @return 校验码
     */
    public static byte[] getFileVerify(String filePath,FileVerifyType type){
        String typeStr=type.toString();
        try {
            FileInputStream in=new FileInputStream(new File(filePath));

            MessageDigest md= MessageDigest.getInstance(typeStr);
            byte[] data=new byte[8192];
            int len;
            while((len=in.read(data))!=-1)
                md.update(data,0,len);
            data=md.digest();
            in.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 是否是正规手机号
     * @param phone 验证号码
     * @return true是手机号，否则false
     */
    public static boolean isPhoneNumber(String phone){
        if(TextUtils.isEmpty(phone)) return false;
        Pattern p = Pattern.compile("^[1][3-8]\\d{9}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    /**
     * 字符串部分字染色
     * @param start
     * @param end
     * @param text
     * @param color
     * @return
     */
    public static SpannableStringBuilder getTextSomeOtherColor(int start,int end,String text,int color){
        SpannableStringBuilder ssb=new SpannableStringBuilder(text);
        ForegroundColorSpan foregroundColor=new ForegroundColorSpan(color);
        ssb.setSpan(foregroundColor,start,end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
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

    /**
     * 多字节转数字。默认小数端,最多处理8字节
     * @param bytes
     * @return
     */
    public static long bytesToNumber(byte... bytes){
        long number=0;
        for(int i=0;i<bytes.length;i++){
            long temp=bytes[i];
            number+=temp<<(i*8);
        }
        return number;
    }

    public static int bytesToInt(byte... data){
        int var=0;
        for(int i=0;i<data.length;i++){
            int middle=data[i];
            var|=((0xff&middle)<<i*8);
        }
        return var;
    }

    /**
     * 文件校验类型
     */
    public enum FileVerifyType{
        SHA_1("SHA-1"),
        MD5("MD5");

        private String flag;

        FileVerifyType(String flag){
            this.flag=flag;
        }

        @Override
        public String toString() {
            return flag;
        }
    }
}