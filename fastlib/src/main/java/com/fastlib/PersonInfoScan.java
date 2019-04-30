package com.fastlib;

/**
 * Created by sgfb on 18/9/20.
 * E-mail: 602687446@qq.com
 * 身份证OCR和活体检测组合返回
 */
public class PersonInfoScan {
//    {
//        "classify":2
//        "addr_card":"浙江省杭州市滨江区越达巷",
//        "be_idcard":"0.9805",
//        "branch_issued":"滨江公安局",
//        "date_birthday":"1990.04.12",
//        "flag_sex":"男",
//        "id_name":"周伯通",
//        "id_no":"320421199011120054",
//        "auth_result":"T",
//        "result_status":"01",
//        "url_frontcard":"https://idsafe-auth.udcredit.com/front/4.0/api/file_download/....",
//        "url_backcard":"https://idsafe-auth.udcredit.com/front/4.0/api/file_download/....",
//        "url_photoget":"https://idsafe-auth.udcredit.com/front/4.0/api/file_download/....",
//        "url_photoliving":"https://idsafe-auth.udcredit.com/front/4.0/api/file_download/....",
//        "risk_tag": {
//            "living_attack": "0"
//        },
//        "state_id":"汉",
//        "start_card":"2017.02.03-2037.02.03",
//        "ret_msg":"操作成功",
//        "ret_code":"000000"
//    }
    /**
     * 0：复印件
     * 1：PS证件
     * 2：正常证件
     * 3：屏幕翻拍
     * 4：临时身份证
     * 5：其他
     */
    public int classify;
    public String addr_card;
    /**
     * 人脸比对相似度	该字段为2张用户照的比对结果，值在0到1之间
     */
    public String be_idcard;
    public String branch_issued;
    public String date_birthday;
    public String flag_sex;
    public String id_name;
    public String id_no;
    /**
     * 认证结果
     * T：认证通过(相似度>=0.7且人像比对结果为01)
     * F：认证未通过
     */
    public String result_auth;
    /**
     * 人像比对结果
     * 01：系统判断为同一人
     * 02：系统判断为不同人
     * 03：不能确定是否为同一人
     * 04：系统无法比对(人脸特征提取失败，公安网系统无法比对)
     * 05：库中无照片（公安库中没有网格照）
     */
    public String result_status;
    /**
     * 身份证人像面照下载地址
     */
    public String url_frontcard;
    /**
     * 身份证国徽面照下载地址
     */
    public String url_backcard;
    /**
     * 身份证头像照下载地址
     */
    public String url_photoget;
    /**
     * 活体照下载地址
     */
    public String url_photoliving;
    public String state_id;
    public String start_card;
    public String ret_msg;
    public String ret_code;
}
