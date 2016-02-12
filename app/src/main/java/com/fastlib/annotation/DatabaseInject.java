package com.fastlib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DatabaseInject{

	//远程数据源定位符 类属性
	String remoteUri() default "";

	//数据库中表名 类属性
	String tableName() default "";
	
	//数据库中列名 字段属性
	String columnName() default "";
	
	//不存入数据库 字段属性
	boolean ignore() default false;
	
	//是否主键 字段属性
	boolean key_primary() default false;
	
	//自动增长，如果不是主键，将自动忽视 字段属性
	boolean autoincrement() default false;
}
