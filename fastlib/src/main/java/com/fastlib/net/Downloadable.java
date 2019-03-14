package com.fastlib.net;

import android.graphics.Point;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by sgfb on 16/2/22.
 * 网络请求时保存为文件
 */
public interface Downloadable{

    /**
     * 如果{@link #changeIfHadName()}支持且成功修改名字后替换当前的下载目标
     * @param newFile 修改名字后的文件
     */
    void setChangedFile(File newFile);

    /**
     * 获取下载目标文件位置
     * @return 目标文件
     */
    File getTargetFile();

    /**
     * 分段支持
     * @return 为null不支持，{@link DownloadSegment#supportBreak}为true时支持中断续传，不为null且supportBreak为false为分段
     */
    DownloadSegment supportSegment();

    /**
     * 如果服务器给予了文件名,是否修改文件名
     * @return 是否支持
     */
    boolean changeIfHadName();

    /**
     * 标识文件过期与没过期是否下载的判断
     * @return 如果为空则不判断过期直接下载，否则过期才下载
     */
    String expireTime();

    OutputStream getOutputStream()throws IOException;

    class DownloadSegment{
        private boolean supportBreak;
        private long start;
        private long end;

        public DownloadSegment(){
            supportBreak=true;
        }

        public DownloadSegment(long start, long end){
            if(start>=end) throw new IndexOutOfBoundsException("start不能大于等于end");
            this.start = start;
            this.end = end;
        }

        public boolean breakMode() {
            return supportBreak;
        }

        public long getStart() {
            return start;
        }

        public long getEnd() {
            return end;
        }
    }
}
