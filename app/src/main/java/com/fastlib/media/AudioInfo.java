package com.fastlib.media;

/**
 * Created by sgfb on 16/7/10.
 * 音频基础信息
 */
public interface AudioInfo{

    /**
     * 速率
     * @return
     */
    int getBitRate();

    /**
     * 通道
     * @return
     */
    int getChannels();

    /**
     * 采样位
     * @return
     */
    int getSamplingBit();

    /**
     * 采样频率
     * @return
     */
    int getSamplingRate();

    /**
     * 编码格式
     * @return
     */
    String getDecoder();
}
