package com.fastlib;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.EventObserver;
import com.fastlib.app.FastActivity;
import com.fastlib.app.PhotoResultListener;
import com.fastlib.base.AbsPreviewImageActivity;
import com.fastlib.base.RoundDrawable;
import com.fastlib.db.FastDatabase;
import com.fastlib.db.FunctionCommand;
import com.fastlib.net.CookedListener;
import com.fastlib.net.GlobalListener;
import com.fastlib.net.NetManager;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;
import com.fastlib.net.SimpleMockProcessor;
import com.fastlib.utils.ImageUtil;
import com.fastlib.utils.N;
import com.fastlib.utils.ScreenUtils;
import com.fastlib.utils.TimeUtil;
import com.fastlib.utils.Utils;
import com.fastlib.widget.RoundImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;

/**
 * Created by sgfb on 17/8/7.
 */
@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    @Bind(R.id.path)
    EditText mPath;

    public void beginTask(Task task){
        Task firstTask=task;
        while(firstTask.getPrevious()!=null)
            firstTask=firstTask.getPrevious();
        processTask(firstTask);
    }

    public void processTask(Task task){
        Object obj=task.getReturn();
        Task nextTask=task.getNext();
        if(nextTask!=null){
            nextTask.getAction().setParam(obj);
            processTask(nextTask);
        }
    }

    @Override
    protected void alreadyPrepared(){

    }

    @Bind(R.id.bt)
    private void commit() throws IOException{
        beginTask(Task.begin(new DefaultAction<String,String>(){

            @Override
            public String execute(String param){
                return "b";
            }
        }).cycle(new DefaultAction<String[],String>(){

            @Override
            public String execute(String[] param){
                return param+"a";
            }
        }).next(new DefaultAction<String,String>(){

            @Override
            public String execute(String param){
                System.out.println(param+"o");
                return null;
            }
        }));
//        File file=new File(Environment.getExternalStorageDirectory(),mPath.getText().toString());
//        if(file.exists()&&file.isFile()){
//            InputStream in=new FileInputStream(file);
//            byte[] data=new byte[4096];
//            int offset=0;
//
//            in.read(data,0, ZipFlag.LENGTH_HEADER_BASE);
//            if(0x04034b50== Utils.bytesToNumber(Arrays.copyOfRange(data,0,offset+=ZipFlag.HEADER_SIGNATURE))){
//                System.out.println("是Zip压缩文件，开始解析");
//                offset+=1; //跳过版本兼容号
//                System.out.println("解压最低需要版本:"+Utils.bytesToNumber(Arrays.copyOfRange(data,offset,offset+=ZipFlag.HEADER_VERSION)));
//                byte generateFlag=data[offset];
//                System.out.println("压缩方式:"+Utils.bytesToNumber(Arrays.copyOfRange(data,offset,offset+=ZipFlag.HEADER_COMPRESSION_METHOD)));
//                short HEADER_VERSION=2; //解压文件需要最低版本
//                short HEADER_GENERAL_PURPOSE_FLAG=2; //通用标志位
//                short HEADER_COMPRESSION_METHOD=2; //压缩方式
//                short HEADER_LAST_MOD_FILE_TIME=2; //文件最后修改时间
//                short HEADER_LAST_MOD_FILE_DATE=2; //文件最后修改日期
//                short HEADER_CRC_32=4; //校验码
//                short HEADER_COMPRESSED_SIZE=4; //压缩后大小
//                short HEADER_UNCOMPRESSED_SIZE=4; //未压缩大小
//                short HEADER_FILE_NAME_LENGTH=2;  //文件名长度(n)
//                short HEADER_EXTRA_FIELD_LENGTH=2; //额外区域长度（m）
//            }
//            else System.out.println("不是Zip压缩文件");
//            in.close();
//        }
    }

    @Bind(R.id.bt2)
    private void commit2(){

    }

    @Bind(R.id.bt3)
    private void commit3(View view){

    }


    private boolean isEncrypted(byte flag){
        return flag>>7==1;
    }

    private String getCompressLevel(byte flag){
        int offsetFlag=flag>>5;
        offsetFlag&=3; //保留两位bit位
        switch (offsetFlag){
            case 0:return "Normal compress";
            case 1:return "Fast compress";
            case 2:return "Maximum compress";
            case 3:return "Super fast compress";
            default:return "unknown";
        }
    }
}