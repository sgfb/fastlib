package com.fastlib.net2.param.interpreter;

import androidx.core.util.Pair;

import com.fastlib.net2.param.RequestParam;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 2019\12\24.
 * 将请求参数解释为符合FormData类型的输出
 */
public class FormDataInterpreter implements ParamInterpreter {
    public static final String BOUNDARY = "------AndroidFormBoundary" + Long.toHexString(System.currentTimeMillis());
    private final String CRLF = "\r\n";
    private final String START = "--" + BOUNDARY + CRLF;
    private final String END = "--" + BOUNDARY + "--" + CRLF;

    @Override
    public List<InputStream> interpreter(RequestParam param) {
        List<InputStream> list=new ArrayList<>();
        Map<String, List<String>> surfaceParam = param.getSurfaceParam();

        StringBuilder strSb = new StringBuilder();
        //字符串参数
        for (Map.Entry<String, List<String>> entry : surfaceParam.entrySet()) {
            for (String value : entry.getValue()) {
                strSb.append(START)
                        .append("Content-Disposition: form-data; name=").append('"').append(entry.getKey()).append('"').append(CRLF).append(CRLF)
                        .append(value).append(CRLF);
            }
        }
        list.add(new ByteArrayInputStream(strSb.toString().getBytes()));

        StringBuilder fileSb=new StringBuilder();
        for(Pair<String,Object> pair:param.getBottomParam()){
            if(pair.second instanceof File){
                File file= (File) pair.second;
                fileSb.reverse();
                fileSb.append(START)
                        .append("Content-Disposition: form-data; name=").append('"').append(pair.first).append('"').append("; ")
                        .append("filename=").append('"').append(file.getName()).append('"').append(CRLF)
                        .append("Content-type: ").append(URLConnection.guessContentTypeFromName(file.getName())).append(CRLF).append(CRLF);
                list.add(new ByteArrayInputStream(fileSb.toString().getBytes()));
                try {
                    list.add(new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                list.add(new ByteArrayInputStream(CRLF.getBytes()));
            }
        }
        list.add(new ByteArrayInputStream(END.getBytes()));
        return list;
    }
}
