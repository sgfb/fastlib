package com.fastlib;

import android.os.Bundle;
import android.view.View;

import com.fastlib.annotation.Bind;
import com.fastlib.app.FastActivity;
import com.fastlib.db.And;
import com.fastlib.db.DatabaseListener;
import com.fastlib.db.FastDatabase;
import com.fastlib.db.FilterCommand;
import com.fastlib.db.FilterCondition;

import java.io.File;

/**
 * Created by sgfb on 16/12/29.
 */
public class MainActivity extends FastActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Bind(R.id.bt)
    public void commit1(View v){
        FilterCommand fc=new And(FilterCondition.biger("10")).or(FilterCondition.emptyValue("sex")).and(FilterCondition.smaller("age","10"));
        displayFilterCommand(fc);
    }

    @Bind(R.id.bt2)
    public void commit2(View v){

    }

    private void displayFilterCommand(FilterCommand fc){
        if(fc==null)
            return;
        StringBuilder sb=new StringBuilder();
        sb.append("where ").append(fc.getFilterCondition().getExpression("id"));
        fc=fc.getNext();
        while(fc!=null){
            sb.append(" ").append(fc.getType()==FilterCommand.TYPE_AND?"and ":"or ").append(fc.getFilterCondition().getExpression("id"));
            fc=fc.getNext();
        }
        System.out.println(sb.toString());
    }
}