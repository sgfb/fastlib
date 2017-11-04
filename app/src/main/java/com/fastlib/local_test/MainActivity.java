package com.fastlib.local_test;

import com.fastlib.R;
import com.fastlib.ScalableViewGroup;
import com.fastlib.ScalableViewGroup2;
import com.fastlib.User;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.db.FastDatabase;
import com.fastlib.db.SaveUtil;
import com.fastlib.utils.N;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by sgfb on 2017/9/21.
 */
@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity {
    @Override
    protected void alreadyPrepared() {

    }

    @Bind(R.id.bt)
    private void commit() {
        User user=new User();
        user.id=1;
        user.name="sgfb";
        FastDatabase.getDefaultInstance(this).saveOrUpdate(user);
    }

    @Bind(R.id.bt2)
    private void commit2() {
        SaveUtil.saveToSp(this,"user","id",1);
        SaveUtil.saveToSp(this,"user","name","sgfb");
    }

}