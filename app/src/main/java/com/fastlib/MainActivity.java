package com.fastlib;

import android.widget.EditText;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.db.FastDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by sgfb on 17/8/7.
 */
@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity {
    @Bind(R.id.path)
    EditText mPath;
    Bean mList;

    @Override
    protected void alreadyPrepared(){
        FastDatabase.getConfig().setVersion(6);
    }

    @Bind(R.id.bt)
    private void commit(){
        Student student=new Student();
        student.list=getStrings();
        student.name="sgfb";
        student.age=20;
        student.score=98;
        FastDatabase.getDefaultInstance(this).saveOrUpdate(student);
    }

    private List<String> getStrings2(){
        List<String> list=new ArrayList<>();
        list.add("hello");
        list.add(",world");
        return list;
    }

    private ArrayList<Bean<String>> getStrings(){
        ArrayList<Bean<String>> list=new ArrayList<>();
        Bean<String> tb1=new Bean<>();
        Bean<String> tb2=new Bean<>();
        tb1.status=new Status();
        tb1.status.success=true;
        tb1.status.message="just a message";
        tb1.data="hello,world";
        tb2.status=new Status();
        tb2.status.message="2message";
        tb2.data="may I question?";
        list.add(tb1);
        list.add(tb2);
        return list;
    }

    @Bind(R.id.bt2)
    private void commit2(){
        List<Student> student=FastDatabase.getDefaultInstance(this).get(Student.class);
        System.out.println("location");
    }

    @Bind(R.id.bt3)
    private void commit3() {
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


    private boolean isEncrypted(byte flag) {
        return flag >> 7 == 1;
    }

    private String getCompressLevel(byte flag) {
        int offsetFlag = flag >> 5;
        offsetFlag &= 3; //保留两位bit位
        switch (offsetFlag) {
            case 0:
                return "Normal compress";
            case 1:
                return "Fast compress";
            case 2:
                return "Maximum compress";
            case 3:
                return "Super fast compress";
            default:
                return "unknown";
        }
    }
}