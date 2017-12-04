package com.fastlib;

import android.os.Environment;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{


    @Override
    protected void alreadyPrepared(){

    }

    @Bind(R.id.bt)
    private void commit(){
        final File file=new File(Environment.getExternalStorageDirectory(),"multiThreadAccessFile.txt");
        try{
            file.createNewFile();
            RandomAccessFile accessFile=new RandomAccessFile(file,"rw");
            accessFile.setLength(1000);
            accessFile.close();
            RandomAccessFile accessFile2=new RandomAccessFile(file,"rw");
            accessFile2.seek(100);
            accessFile2.writeChars("hello,world");
        }catch (IOException e){
            e.printStackTrace();
        }

//        mThreadPool.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    RandomAccessFile fileAccess=new RandomAccessFile(file,"rw");
//                    for(int i=0;i<50;i++)
//                        fileAccess.writeChars("aaaaaaaaa\n");
//                    fileAccess.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        mThreadPool.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    file.delete();
//                    file.createNewFile();
//                    RandomAccessFile fileAccess=new RandomAccessFile(file,"rw");
//                    fileAccess.seek(500);
//                    for(int i=0;i<50;i++)
//                        fileAccess.writeChars("bbbbbbbb2\n");
//                    fileAccess.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    @Bind(R.id.bt2)
    private void commit2(){

    }
}