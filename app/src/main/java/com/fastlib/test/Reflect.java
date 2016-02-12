package com.fastlib.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 使反射变得更简单容易
 * 
 * @author Sgfb
 *
 */
public class Reflect{
	
	/**
	 * 使用方法:reflectString(对象,"对象.子对象.孙对象...")
	 * 
	 * @param obj
	 * @param fieldName
	 * @return 反射回字符串
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static String reflectString(Object obj,String fieldName) throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException{
		int index=-1;
		String content=null;
		Field field=null;
		
		if((index=fieldName.indexOf('.'))!=-1){
			String firstName=fieldName.substring(0, index);
			String lastName=fieldName.substring(index+1, fieldName.length());
			Field firstField=obj.getClass().getDeclaredField(firstName);
			Field lastField=null;
			Object obj2=null;
			
			firstField.setAccessible(true);
			obj2=firstField.get(obj);
			if(lastName.indexOf('.')!=-1){
				content=reflectString(obj2,lastName);
				return content;
			}
			lastField=obj2.getClass().getDeclaredField(lastName);
			lastField.setAccessible(true);
			content=(String)lastField.get(obj2);
		}
		else{
			field=obj.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			content=(String)field.get(obj);
		}
		return content;
	}
	
	/**
	 * 
	 * @param obj
	 * @param fieldName
	 * @return
	 * @throws NoSuchFieldException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public static boolean isList(Object obj,String fieldName) throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException{
		boolean bList=false;
		Field field=null;
		
		field=obj.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		try{
			bList=field.get(obj).getClass().asSubclass(List.class)!=null;
		}catch(ClassCastException e){
			return false;
		}
		return bList;
	}

	public static String objToStr(Object obj){
		if(obj instanceof Integer)
			return Integer.toString((int)obj);
		else if(obj instanceof String)
			return (String)obj;
		else if(obj instanceof Long)
			return Long.toString((long)obj);
		else if(obj instanceof Float)
			return Float.toString((float)obj);
		else if(obj instanceof Double)
			return Double.toString((double)obj);
		else if(obj instanceof Short)
			return Short.toString((short)obj);
		else
		    return null;
	}
}
