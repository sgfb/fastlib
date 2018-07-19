package com.fastlib.app;

import android.widget.TextView;

import com.fastlib.R;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.annotation.Event;
import com.fastlib.app.module.FastActivity;

/**
 * Created by sgfb on 18/7/18.
 */
@ContentView(R.layout.test_act_main)
public class FastActivityImplement extends FastActivity {
    @Bind(R.id.text)
    TextView mText;

    final String mTestStr="just test";
    boolean isAlreadyPrepared=false;
    int mTestValue=-1;

    @Override
    public void alreadyPrepared(){
        isAlreadyPrepared=true;
    }

    @Bind(R.id.bt)
    private void bt(){
        EventObserver.getInstance().sendEvent(this,mTestStr);
    }

    @Bind(R.id.bt2)
    private void bt2(){
        getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                mTestValue=1;
            }
        });
    }

    @Event
    private void eTestStr(String event){
        mText.setText(event);
    }
}
