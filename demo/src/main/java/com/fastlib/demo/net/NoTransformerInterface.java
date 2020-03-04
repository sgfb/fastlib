package com.fastlib.demo.net;

import com.fastlib.net2.core.HeaderDefinition;
import com.fastlib.net2.core.Method;
import com.fastlib.net2.utils.Body;
import com.fastlib.net2.utils.FinalHeader;
import com.fastlib.net2.utils.Header;
import com.fastlib.net2.utils.RequestTo;

/**
 * Created by sgfb on 2020\03\04.
 */
public interface NoTransformerInterface {

    @RequestTo(method = Method.POST,url = "http://101.200.51.39/wuyou/shop/listProd")
    String testJsonRequest(@Header(HeaderDefinition.KEY_CONTENT_TYPE) String contentType,@Body("type") int type, @Body("pageNum") int page, @Body("pageSize")int size);
}
