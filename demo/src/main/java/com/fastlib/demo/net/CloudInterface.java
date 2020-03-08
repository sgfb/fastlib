package com.fastlib.demo.net;

import com.fastlib.net2.core.Method;
import com.fastlib.net2.utils.Body;
import com.fastlib.net2.utils.FinalHeader;
import com.fastlib.net2.utils.FinalParam;
import com.fastlib.net2.utils.RequestTo;

import java.io.File;

/**
 * Created by sgfb on 2020\03\06.
 */
public interface CloudInterface{

    @RequestTo(url = "https://cloud.189.cn/v2/listFiles.action")
    @FinalHeader({"Cookie","s_fid=114F25D4E19EF7AC-2EC9934907E8E4B3; lvid=c2a6f9c46ded1fc233be2204f8392f5b; nvid=1; trkId=0AF083F6-EF73-4132-A661-E94D6C0679CE; cityCode=zj; userId=201%7C20170100000099013775; Login_Hash=; apm_ct=20200305181250045; apm_ip=115.194.185.193; apm_uid=50C243C19DAC623E2BC88C49769B1E13; apm_ua=4F588AB576B4D270B6952055DC6854D8; JSESSIONID=aaaCx2lEBwWc6hHOTdRcx; apm_sid=278644DEE5ADE72F9A6006A876B2085E; COOKIE_LOGIN_USER=D392F1E9559240995A614CDDE72D10DB97E4602EC9114654D817E8CB8EE19A3D7BB574C6F1E29C62C78E0235D902EAFC9D6B53D859FAF1D22A467B6CA0159C834FC13572830E990D4113F8DC37F90593D9F20384; IS_SHOWN_IPV6=true; edrive_view_mode=icon; offline_Pic_Showed=true; wpsGuideStatus=true"})
    @FinalParam({"isGroupSpace","false","orderBy","1","order","ASC","pageNum","1","pageSize","60","noCache","0.2620797335795906"})
    ResponseCloudFile getFileList(@Body("fileId")String id);

    @RequestTo(method = Method.POST,url = "https://hd02.upload.cloud.189.cn/v1/DCIWebUploadAction")
    @FinalParam({"sessionKey","3896cea8-4d5b-48ae-9b34-f5e29eeff9f5","parentId","8151115627900552","opertype","1"})
    String uploadFile(@Body("Filedata")File file);
}
