package com.fastlib.test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.os.Environment;

/**
 * zip压缩工具
 * 
 * @author Bao
 *
 */
public class ZipUtils {
	
	private ZipUtils(){}
	
	private static void compressToFile(ZipOutputStream zos,String srcPath)throws IOException{
		String prefix=srcPath.substring(srcPath.lastIndexOf(File.separator)==-1?0:srcPath.lastIndexOf(File.separator)+1);
		ZipEntry ze=new ZipEntry(prefix);
		
		zos.putNextEntry(ze);
		BufferedInputStream input=new BufferedInputStream(new FileInputStream(srcPath));
		int length;
		byte[] data=new byte[1024];
		while((length=input.read(data, 0, 1024))!=-1)
		    zos.write(data, 0,length);
		zos.flush();
		input.close();
		zos.closeEntry();
	}
	
	private static void strCompressToFile(ZipOutputStream zos,String content,String destPath)throws FileNotFoundException,IOException{
		ZipEntry ze=new ZipEntry("content.txt");
		
		zos.putNextEntry(ze);
		zos.write(content.getBytes("ISO-8859-1"));
		zos.flush();
		zos.closeEntry();
	}
	
	public static boolean compressStr(String content,File destFile){
		CheckedOutputStream cos=null;
		ZipOutputStream zos=null;
		
		if(!destFile.getParentFile().exists())
			destFile.mkdirs();
		try {
			cos = new CheckedOutputStream(new FileOutputStream(destFile.getAbsolutePath()),new CRC32());
			zos=new ZipOutputStream(cos);
			strCompressToFile(zos,content,destFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean compressFile(File srcFile,File destFile){
		CheckedOutputStream cos;
		ZipOutputStream zos;

		if(!destFile.getParentFile().exists())
			destFile.mkdirs();
		try {
			if(!destFile.exists())
			    destFile.createNewFile();
			cos = new CheckedOutputStream(new FileOutputStream(destFile.getAbsolutePath()),new CRC32());
			zos=new ZipOutputStream(cos);
			compressToFile(zos,srcFile.getAbsolutePath());
		} catch (IOException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static String uncompressFile(File file){
		CheckedInputStream cis;
		ZipInputStream zis;
		
		try {
			cis=new CheckedInputStream(new FileInputStream(file),new CRC32());
			zis=new ZipInputStream(cis);
			byte[] data=new byte[(int)file.length()];
			zis.getNextEntry();
			zis.read(data);
			String s=new String(data,"ISO-8859-1");
			cis.close();
			zis.closeEntry();
			zis.close();
			return s;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "error";
	}

	/**
	 * 字符串压缩
	 * @param raw
	 * @return
	 * @throws IOException
     */
	public static byte[] compressStr(String raw) throws IOException {
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		GZIPOutputStream out=new GZIPOutputStream(baos);
		out.write(raw.getBytes());
		out.close();
		return baos.toByteArray();
	}

	/**
	 * 解压压缩数据成字符串
	 * @param compressData
	 * @return
	 * @throws IOException
     */
	public static String uncompressStr(byte[] compressData) throws IOException {
		ByteArrayInputStream bais=new ByteArrayInputStream(compressData);
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		GZIPInputStream in=new GZIPInputStream(bais);
		byte[] data=new byte[4096];
		int len;
		while((len=in.read(data))!=-1&&!Thread.currentThread().isInterrupted())
			baos.write(data,0,len);
		in.close();
		return new String(baos.toByteArray());
	}
}