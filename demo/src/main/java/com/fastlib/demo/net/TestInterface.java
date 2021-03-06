package com.fastlib.demo.net;

import com.fastlib.net2.utils.ResultTransformer;
import com.fastlib.demo.list_view.ItemBean;
import com.fastlib.net2.utils.Body;
import com.fastlib.net2.utils.RequestTo;

import java.util.List;

/**
 * Created by sgfb on 2020\03\02.
 */
@ResultTransformer(ResponseTransformer.class)
public interface TestInterface{

    @RequestTo(url = "http://192.168.3.20:8082/getItemList")
    List<ItemBean> getItemList(@Body("page") int page, @Body("size")int size);
}
