package com.fastlib.test.elf;

/**
 * Created by sgfb on 18/4/11.
 * elf文件头部定义
 */
public final class ELF{

    private ELF(){}

    public static long[] splitBytes(byte[] bytes,int... splitPoint){
        long[] splits=new long[splitPoint.length];
        int cursor=0;

        for(int i=0;i<splits.length;i++){
            byte[] element=new byte[splitPoint[i]];
            for(int e=0;e<element.length;e++)
                element[e]=bytes[cursor++];
            splits[i]=joinBytes(element);
        }
        return splits;
    }

    private static long joinBytes(byte[] bytes){
        long join=0;
        for(int i=0;i<bytes.length;i++)
            join|=bytes[i]<<((bytes.length-i-1)*8);
        return join;
    }

    public interface IdentHeader{
        //长度位意义,单位为字节,按顺序往下
        byte IDENT_MAGO=4; //0x7f紧接ELF ascii码
        byte IDENT_CLASS=1; //1代表32位 2代表64位
        byte IDENT_DATA=1; //1或2 小端对齐或大端对齐
        byte IDENT_VERSION=1; //版本号 1为ELF源版本
        /**
         * 操作系统 0System V 1HP_UX 2NetBSD 3Linux 4GUN Hurd 6Solaris 7AIX 8IRIX 9FreeBSD 10Tru64 11Novell Modesto
         * 12OpenBSD 13OpenVMS 14NonStop Kernel 15AROS 16Fenix OS 17CloudABI
         */
        byte IDENT_SABI=1;
        byte IDENT_ABIVERSION=1; //操作系统版本，有时候是空的PAD置8字节
        byte IDENT_PAD=7; //预留位

        byte TYPE=2; //1,2,3,4 分别定义此文件是否可 浮动，运行，分享，独立或者非独立
        byte MACHINE=2; //体系结构 2SPARC 3x86 8MIPS 20PowerPC 22S390 40ARM 42SuperH 50IA-64 62x86-64 183AArch64 243RISC-V
        byte VERSION=4; //版本号 1为ELF源版本
        //根据32位还是64位不同长度
        byte[] ENTRY={4,8}; //目录起始位置
        byte[] PHOFF={4,8}; //Program Header起始位置
        byte[] SHOFF={4,8}; //Section Header起始位置
        byte FLAGS=4; //处理器相关标识
        byte EHSIZE=2; //头部长度
        byte PHENTSIZE=2; //Program头部长度
        byte PHNUM=2; //Program头部条目数量
        byte SHENTSIZE=2; //Section头部长度
        byte SHNUM=2; //Section头部目录数量
        byte SHSTRNDX=2; //Section头部字符串索引长度
    }

    public interface ProgramHeader{
        byte TYPE=4; //段类型
        byte[] FLAGS_64={0,4}; //段依赖标识（仅存在于64位系统）
        byte[] OFFSET={4,8}; //文件到该段第一个字节便宜
        byte[] VADDR={4,8}; //段第一个字节到内存中的虚拟地址
        byte[] PADDR={4,8}; //段到内存中物理地址
        byte[] FILESZ={4,8}; //段在文件映射中占用字节数
        byte[] MEMSZ={4,8}; //段在内存映射中占用字节数
        byte[] FLAGS_32={4,0}; //段依赖标识（仅存在于32位系统）
        byte[] ALIGN={4,8}; //在内存中如何对齐 0和1不对齐 其他数值幂2
        byte[] PAD={4,8}; //填充
    }

    public interface SectionHeader{
        byte NAME=4; //与Section字符索引表的相对索引
        byte TYPE=4; //头类型
        byte[] FLAGS={4,8}; //Section属性
        byte[] ADDR={4,8}; //内存中虚地址
        byte[] OFFSET={4,8}; //文件偏移
        byte[] SIZE={4,8}; //
        byte LINK=4; //
        byte INFO=4; //额外信息
        byte[] ADDR_ALIGN={4,8}; //地址对齐 数值幂2
        byte[] ENT_SIZE={4,8}; //
        byte[] PAD={4,2}; //填充
    }
}