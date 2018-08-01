package com.fastlib;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.utils.KeyBoardUtils;
import com.fastlib.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 18/7/30.
 */
@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    int mClickItemBottom=0;
    int mScreenHeight=800;
    Rect mRect=new Rect();
    @Bind(R.id.listview)
    ListView mListView;
    @Bind(R.id.edittext)
    EditText mEditText;

    @Override
    public void alreadyPrepared(){
        MyAdapter myAdapter=new MyAdapter();
        List<String> list=new ArrayList<>();
        mScreenHeight=ScreenUtils.getScreenHeight();

        for(int i=0;i<100;i++)
            list.add(Integer.toString(i));
        myAdapter.setData(list);
        mListView.setAdapter(myAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                mClickItemBottom=view.getBottom();
                mEditText.setVisibility(View.VISIBLE);
                mEditText.setFocusable(true);
                mEditText.setFocusableInTouchMode(true);
                mEditText.requestFocus();
                KeyBoardUtils.openKeybord(mEditText);
            }
        });
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getWindow().getDecorView().getWindowVisibleDisplayFrame(mRect);
                System.out.println(mRect.bottom);
                if(mRect.bottom< mScreenHeight/3*2){
                    mRect.bottom-=mEditText.getHeight();
                    mListView.smoothScrollBy(mClickItemBottom-mRect.bottom,220);
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        if(mEditText.getVisibility()!=View.GONE){
            mEditText.setVisibility(View.GONE);
        }
        else
        super.onBackPressed();
    }
}
