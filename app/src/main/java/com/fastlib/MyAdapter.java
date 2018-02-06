package com.fastlib;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ImageView;

import com.fastlib.adapter.BindingGroup;
import com.fastlib.adapter.MultiTypeAdapter;
import com.fastlib.base.CommonViewHolder;
import com.fastlib.net.Request;
import com.fastlib.test.UrlImage.request.BitmapRequestEntrance;

import java.util.List;

/**
 * Created by Administrator on 2018/2/6.
 */
public class MyAdapter extends MultiTypeAdapter{
    private TitleGroup mTitleGroup;
    private DataGroup mDataGroup;

    public MyAdapter(Context context) {
        super(context);
        mTitleGroup=new TitleGroup();
        mDataGroup =new DataGroup();
        mTitleGroup.addData("我是标题");
        addGroup(mTitleGroup);
        addGroup(mDataGroup);
    }

    public void refresh(){
        mDataGroup.refresh();
    }

    public void setRefreshLayout(SwipeRefreshLayout refreshLayout){
        mDataGroup.setRefreshLayout(refreshLayout);
    }

    public class DataGroup extends BindingGroup<ResponseData,Response<List<ResponseData>>> {

        @Override
        protected void requestRefreshBefore(Request request) {
            request.put("page",1);
        }

        @Override
        protected void requestMoreBefore(Request request) {
            request.increment("page",1);
        }

        @NonNull
        @Override
        protected Request initRequest() {
            return new Request("http://192.168.31.181:8080/FastProject/Test")
                    .put("page",1)
                    .put("size",10);
        }

        @Override
        protected List<ResponseData> translate(Response<List<ResponseData>> responseData) {
            return responseData.data;
        }

        @Override
        protected void rebinding(int positionOfRecyclerView, int positionOfGroup, ResponseData data, CommonViewHolder holder) {
            holder.setText(R.id.text,data.name);
//            Glide.with(mContext).load(data.cover).into((ImageView)holder.getView(R.id.image));
            BitmapRequestEntrance
                    .factory(mContext)
                    .bitmapRequestByUrl(data.cover)
                    .setImageView((ImageView)holder.getView(R.id.image))
                    .start();
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item;
        }
    }

    public class TitleGroup extends MultiTypeAdapter.RecyclerGroup<String>{

        @Override
        protected void binding(int positionOfRecyclerView, int positionOfGroup, String data, CommonViewHolder holder) {
            holder.setText(android.R.id.text1,data);
        }

        @Override
        protected int getLayoutId() {
            return android.R.layout.simple_list_item_1;
        }
    }
}
