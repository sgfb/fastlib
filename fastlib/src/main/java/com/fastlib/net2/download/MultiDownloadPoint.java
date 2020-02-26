package com.fastlib.net2.download;

import android.support.annotation.NonNull;

/**
 * Created by sgfb on 2020\02\24.
 */
public class MultiDownloadPoint implements Comparable<MultiDownloadPoint>{
    public long start;
    public long end;

    public MultiDownloadPoint() {
    }

    public MultiDownloadPoint(long start) {
        this.start = start;
        this.end=start;
    }

    public long length(){
        return end-start;
    }

    @Override
    public int compareTo(@NonNull MultiDownloadPoint o) {
        return (int) (start-o.start);
    }
}
