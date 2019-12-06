package com.fastlib;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

<<<<<<< HEAD
import java.lang.reflect.Field;
import java.util.List;

=======
>>>>>>> a55fc6a244e6d9470f7218d8573e2c53158d5c37
/**
 * Created by Administrator on 2018/7/27.
 */
@RunWith(JUnit4.class)
public class JavaTest{

    @Test
<<<<<<< HEAD
    public void testByte(){
        int i=1512;
        byte[] data=new byte[4];
        data[0]= (byte) (i>>24);
        data[1]= (byte) (i>>16);
        data[2]= (byte) (i>>8);
        data[3]= (byte) (i);
        System.out.println(Byte.toUnsignedInt(data[3]));
    }

    private int byteToInt(byte[] data){
        int value=0;
        value|=data[0]<<24;
        value|=data[1]<<16;
        value|=data[2]<<8;
        value|=data[3];
        return value;
=======
    public void testMain(){
        System.out.println(System.currentTimeMillis());
>>>>>>> a55fc6a244e6d9470f7218d8573e2c53158d5c37
    }
}