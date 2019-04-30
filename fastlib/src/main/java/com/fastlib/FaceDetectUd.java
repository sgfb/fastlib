package com.fastlib;

import android.content.Context;

import com.authreal.api.AuthBuilder;
import com.authreal.api.OnResultListener;
import com.fastlib.app.module.FastActivity;
import com.fastlib.app.task.NetAction;
import com.fastlib.app.task.Task;
import com.fastlib.app.task.ThreadType;
import com.fastlib.net.DefaultDownload;
import com.fastlib.net.Request;
import com.fastlib.utils.ContextHolder;
import com.fastlib.utils.N;
import com.fastlib.utils.TimeUtil;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.File;
import java.util.Date;
import java.util.Locale;

/**
 * 有盾人脸识别
 */
public class FaceDetectUd{
    public static final String PUB_KEY = "cb784f9a-dd06-49f3-81d4-e633a050b478";
    private final String ACTION_CANCEL="900001";

    private String mOrderId;
    private File mIdCardBackFile;
    private File mIdCardFrontFile;
    private PersonInfoScan mScanInfo;

    public void startDetect(final Context context) {
        mIdCardFrontFile = new File(context.getExternalCacheDir(), "idCardFront.jpg");
        mIdCardBackFile = new File(context.getExternalCacheDir(), "idCardBack.jpg");
        mIdCardFrontFile.delete();
        mIdCardBackFile.delete();
        Date date = new Date();
        mOrderId = "order_" +BuildConfig.APPLICATION_ID+"_"+TimeUtil.dateToString(date, "yyyyMMddkkmmss");
        AuthBuilder mAuthBuilder = new AuthBuilder(mOrderId, PUB_KEY, null, new OnResultListener() {
            @Override
            public void onResult(String s) {
                try {
                    System.out.println("auth:" + s);
                    Gson gson = new Gson();
                    mScanInfo = gson.fromJson(s, PersonInfoScan.class);

                    //000000为成功码
                    if (!"000000".equals(mScanInfo.ret_code)){
                        if(!ACTION_CANCEL.equals(mScanInfo.ret_code))
                            N.showLong(ContextHolder.getContext(),String.format(Locale.getDefault(),"错误码:%s",mScanInfo.ret_code));
                        else N.showLong(ContextHolder.getContext(),"用户取消操作");
                        return;
                    }
                    //人脸比对结果T或F
                    if (!"T".equals(mScanInfo.result_auth)){
                        N.showShort(ContextHolder.getContext(),"人脸比对未通过");
                        return;
                    }
                    downloadIdCard(context,mScanInfo.url_frontcard, mScanInfo.url_backcard);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                    N.showShort(ContextHolder.getContext(),"身份验证解析失败");
                }
            }
        });
        mAuthBuilder.faceAuth(context);
    }

    /**
     * 脸部识别后下载身份证正反面
     *
     * @param idCardFront 身份证正面照片链接
     * @param idCardBack  身份证反面照片链接
     */
    private void downloadIdCard(Context context,String idCardFront, final String idCardBack){
        System.out.println("startDown:" + idCardFront + "," + idCardBack);
        if(context instanceof FastActivity){
            FastActivity activity= (FastActivity) context;
            activity.startTask(Task.begin(new Request("get", idCardFront)
                    .setDownloadable(new DefaultDownload(mIdCardFrontFile))
                    .setCustomRootAddress("")
                    .setUseFactory(false))
                    .next(new NetAction<String, Request>() {

                        @Override
                        protected Request executeAdapt(String r, Request request) {
                            return new Request("get", idCardBack)
                                    .setDownloadable(new DefaultDownload(mIdCardBackFile))
                                    .setUseFactory(false)
                                    .setCustomRootAddress("");
                        }
                    })
                    .next(new NetAction<String, String>() {

                        @Override
                        protected String executeAdapt(String r, Request request) {
                            int centerLineIndex=mScanInfo.start_card.indexOf('-');
                            String validStart=centerLineIndex!=-1?mScanInfo.start_card.substring(0,centerLineIndex):"";
                            String validEnd=centerLineIndex!=-1?mScanInfo.start_card.substring(centerLineIndex+1,mScanInfo.start_card.length()):"";
                            System.out.println(String.format(Locale.getDefault(),"orderId:%s\nname:%s\nidNum:%s\nsex:%s\nadd:%s\nstateId:%s\nvalidStart:%s\nvalidEnd:%s\n",
                                    mOrderId,mScanInfo.id_name,mScanInfo.id_no,mScanInfo.flag_sex,mScanInfo.addr_card,mScanInfo.state_id,validStart,validEnd));
                            N.showLong(ContextHolder.getContext(),"认证成功");
//                            if(mListener!=null) mListener.onDetectSuccess(mOrderId,mScanInfo.id_name,mScanInfo.id_no,mScanInfo.flag_sex,mScanInfo.addr_card,mScanInfo.state_id,validStart,validEnd,mIdCardFrontFile,mIdCardBackFile);
                            return r;
                        }
                    }, ThreadType.MAIN));
        }
    }
}
