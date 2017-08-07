package com.fastlib.utils.zip;

/**
 * Created by sgfb on 17/8/7.
 * Zip各结构的位意义.Zip中使用的是Dos时间记录结构
 * 日期说明
 * 0-4日(1-31)
 * 5-8月(1表January,2表February,…)
 * 9-15年，当前间隔1980年的年数（包括1980年）
 * 时间说明
 * 0-4秒
 * 5-10分(0-59)
 * 11-15时(0-23)
 */
public interface ZipFlag{
    /**
     * 头部.最后包括了n+m的可变长度文件名和可扩展区
     */
    short HEADER_SIGNATURE=4; //包括固定头标识,固定值 0x04034b50
    short HEADER_VERSION=2; //解压文件需要最低版本
    short HEADER_GENERAL_PURPOSE_FLAG=2; //通用标志位
    short HEADER_COMPRESSION_METHOD=2; //压缩方式
    short HEADER_LAST_MOD_FILE_TIME=2; //文件最后修改时间
    short HEADER_LAST_MOD_FILE_DATE=2; //文件最后修改日期
    short HEADER_CRC_32=4; //校验码
    short HEADER_COMPRESSED_SIZE=4; //压缩后大小
    short HEADER_UNCOMPRESSED_SIZE=4; //未压缩大小
    short HEADER_FILE_NAME_LENGTH=2;  //文件名长度(n)
    short HEADER_EXTRA_FIELD_LENGTH=2; //额外区域长度（m）

    /**
     * File descriptor数据在头部通用标志位为3时存在
     * 接在文件数据File data之后
     */
    short DESCRIPTOR_CRC_32=4; //CRC－32校验码
    short DESCRIPTOR_COMPRESSED_SIZE=4; //压缩后大小
    short DESCRIPTOR_UNCOMPRESSED_SIZE=4; //未压缩大小

    /**
     * Central Directory Header记录了压缩文件的目录信息，在这个数据区中每一条纪录对应在压缩源文件数据区中的一条数据
     * 接在descriptor之后.最后包括了n＋m＋k的可变长度文件名和额外扩展区和文件注释
     */
    short CDH_SIGNATURE=4; //CDH头标识 固定值0x02014b50
    short CDH_VERSION_MADE_BY=2; //压缩用的版本
    short CDH_VERSION_NEEDED_TO_EXTRACT=2; //解压需要最低版本
    short CDH_GENERAL_PURPOSE_FLAG=2; //通用位标志
    short CDH_COMPRESSION_METHOD=2; //压缩方法
    short CDH_LAST_MOD_FILE_TILE=2; //文件最后修改时间
    short CDH_LAST_MOD_FILE_DATE=2; //文件最后修改日期
    short CHD_CRC_32=4; //CRC－32校验码
    short CHD_COMPRESSED_SIZE=4; //压缩后大小
    short CHD_UNCOMPRESSED_SIZE=4; //未压缩大小
    short CHD_FILE_NAME_LENGTH=2; //文件名长度
    short CHD_EXTRA_FIELD_LENGTH=2; //扩展区长度
    short CHD_FILE_COMMENT_LENGTH=2; //文件注释长度
    short CHD_DISK_NUMBER_START=2; //文件开始位置的磁盘编号
    short CHD_INTERNAL_FILE_ATTR=2; //内部文件属性
    short CHD_EXTRENAL_FILE_ATTR=4; //外部文件属性
    short REALTIVE_OFFSET_OF_LOCAL_HEADER=4; //本地文件头相对位移

    /**
     * End of central directory record目录结束标识.目录结束标识存在于整个归档包的结尾，用户标记压缩的目录数据的结束。每个压缩文件必须由且只有一个EOCD记录
     * 最后注释长度可变n
     */
    short EOCD_SIGNATUR=4; //EOCD结束标记 固定值0x06054b50
    short EOCD_NUMBER_OF_THIS_DISK=2; //当前磁盘编号
    short EOCD_DISK_START_CENTRAL_DIRECTORY=2; //核心目录开始位置的磁盘编号
    short EOCD_TOTAL_CENTRAL_DIRECTORY_DISK=2; //改磁盘上记录的核心目录数量
    short EOCD_TOTAL_ENTRIES=2; //核心目录结构总数
    short EOCD_CENTRAL_DIRECTORY_SIZE=2; //核心目录大小 byte单位
    short EOCD_OFFSET_START_DISK_NUMBER=4; //核心目录开始位置相对于archive开始的位移
    short EOCD_COMMENT_LENGTH=2; //注释长度(n)
}