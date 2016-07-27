package com.fastlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fastlib.adapter.MultiTypeAdapter;
import com.fastlib.base.OldViewHolder;
import com.fastlib.db.FastDatabase;
import com.fastlib.net.Request;
import com.fastlib.net.Result;
import com.fastlib.test.ImageCache;
import com.fastlib.utils.TimeUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 16/5/10.
 *
 */
public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bt=(Button)findViewById(R.id.bt);
        Button bt2=(Button)findViewById(R.id.bt2);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                FastDatabase.getDefaultInstance().saveOrUpdate("aa");

            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("db:"+FastDatabase.getDefaultDatabaseName()+" data:");
                List<String> list=FastDatabase.getDefaultInstance().getAll(String.class);
                if(list!=null)
                for(String s:list)
                    System.out.println(s+"\n");
            }
        });
//        RecyclerView list=(RecyclerView)findViewById(R.id.list);
//        list.setLayoutManager(new LinearLayoutManager(this));
//        list.setItemAnimator(new DefaultItemAnimator());
//        list.setAdapter(new MyTypeAdapter());
//        ItemTouchHelper ith=new ItemTouchHelper(new MyCallBack(ItemTouchHelper.UP|ItemTouchHelper.DOWN,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT));
//        ith.attachToRecyclerView(list);
    }

    class MyTypeAdapter extends MultiTypeAdapter {

        public MyTypeAdapter(Context context,Request request){
            super(context,request);
        }

        @Override
        public void binding(int position, MultiTypeAdapter.ObjWithType owy, OldViewHolder holder) {
            if(owy.type==0)
                holder.setText(android.R.id.text1,(String)owy.obj);
            else{
                Bean.Data data= (Bean.Data) owy.obj;
                ImageView iv=holder.getView(R.id.image);
                holder.setText(R.id.text,data.data);
                Glide.with(MainActivity.this).load(data.image).crossFade().into(iv);
            }
        }

        @Override
        public List<ObjWithType> translate(Result result){
            Gson gson=new Gson();
            Type type=new TypeToken<List<Bean>>(){}.getType();
            List<Bean> list=gson.fromJson(result.getBody(), type);
            List<ObjWithType> datas=new ArrayList<>();
            for(Bean b:list){
                datas.add(new ObjWithType(0,b.title));
                if(b.data!=null)
                for(Bean.Data d:b.data)
                    datas.add(new ObjWithType(1,d));
            }
            return datas;
        }

        @Override
        public Map<Integer, Integer> getLayoutId(){
            Map<Integer,Integer> map=new HashMap<>();
            map.put(0,android.R.layout.simple_list_item_1);
            map.put(1,R.layout.item_test);
            return map;
        }

        @Override
        public void getMoreDataRequest(Request request) {

        }

        @Override
        public void getRefreshDataRequest(Request request) {

        }
    }

//    class MyTypeAdapter extends RecyclerView.Adapter<Holder>{
//
//        @Override
//        public Holder onCreateViewHolder(ViewGroup parent, int viewType){
//            TextView tv=new TextView(MainActivity.this);
//            tv.setText("hello,world");
//            tv.setPadding(40,40,40,40);
//            return new Holder(tv);
//        }
//
//        @Override
//        public void onBindViewHolder(Holder holder, int position) {
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return 20;
//        }
//    }
//
//    class Holder extends RecyclerView.ViewHolder{
//
//        public Holder(View itemView){
//            super(itemView);
//        }
//    }
//
//    class MyCallBack extends ItemTouchHelper.SimpleCallback{
//
//        public MyCallBack(int dragDirs, int swipeDirs) {
//            super(dragDirs, swipeDirs);
//        }
//
//        @Override
//        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target){
//            System.out.println("onMove:"+viewHolder.getAdapterPosition()+","+target.getAdapterPosition());
//            return true;
//        }
//
//        @Override
//        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction){
//            System.out.println("onSwiped:"+direction);
////            mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
//        }
//
//        @Override
//        public boolean isLongPressDragEnabled() {
//            return super.isLongPressDragEnabled();
//        }
//
//        @Override
//        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){
//            if(actionState!=ItemTouchHelper.ACTION_STATE_IDLE){
//                final float alpha=1-Math.abs(dX)/ ScreenUtils.getScreenWidth(MainActivity.this);
//                viewHolder.itemView.setAlpha(alpha);
//            }
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//        }
//    }
}