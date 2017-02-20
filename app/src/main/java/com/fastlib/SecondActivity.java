package com.fastlib;

import android.os.Bundle;
import android.view.View;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.LocalData;
import com.fastlib.app.FastActivity;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by sgfb on 17/2/16.
 */
public class SecondActivity extends FastActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Bind(value = R.id.bt,bindType = Bind.BindType.LONG_CLICK)
    @LocalData(value ={"bean.json","string"},from ={LocalData.GiverType.ASSETS,LocalData.GiverType.INTENT_PARENT})
    private void commit(View v,Bean data,String str){
        System.out.println(data);
        System.out.println(str);
    }
}
