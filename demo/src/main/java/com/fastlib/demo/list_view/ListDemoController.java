package com.fastlib.demo.list_view;

import com.fastlib.aspect.ThreadOn;
import com.fastlib.demo.net.TestInterface;
import com.fastlib.utils.fitout.AutoFit;

import java.util.List;

/**
 * Created by sgfb on 2020\03\02.
 */
public class ListDemoController {
    @AutoFit
    TestInterface mTestModel;
    int mPage;

    @ThreadOn(ThreadOn.ThreadType.WORK)
    public List<ItemBean> getItemBean(boolean isRefresh){
        if(isRefresh) mPage=0;
        mPage+=1;
        return mTestModel.getItemList(mPage,10);
    }
}
