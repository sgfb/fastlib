package com.fastlib;

import android.os.Build;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by sgfb on 2019\12\03.
 */
public class SimpleHttpCoreImpl extends HttpCore{

    public SimpleHttpCoreImpl(String url) {
        super(url);
    }

    @Override
    protected Map<String, String> createHeader() {
        Map<String,String> map=new HashMap<>();
        map.put(HeaderDefinition.KEY_HOST,URLUtil.getHost(mUrl));
        map.put(HeaderDefinition.KEY_ACCEPT,"*/*");
        map.put(HeaderDefinition.KEY_AGENT,String.format(Locale.getDefault(),"%s %s","android", Build.VERSION.SDK));
        map.put(HeaderDefinition.KEY_CONNECTION,"keep-alive");
        map.put(HeaderDefinition.KEY_CACHE_CONTROL,"no-cache");
        map.put(HeaderDefinition.KEY_ACCEPT_ENCODING,"gzip,deflate,br");
        return map;
    }

    @Override
    protected String getRequestMethod() {
        return "GET";
    }


}
