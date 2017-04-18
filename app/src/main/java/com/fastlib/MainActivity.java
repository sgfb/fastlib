package com.fastlib;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.fastlib.adapter.FastAdapter;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;

import com.fastlib.app.EventObserver;
import com.fastlib.app.FastActivity;
import com.fastlib.app.FastDialog;
import com.fastlib.app.Plus;
import com.fastlib.app.TaskAction;
import com.fastlib.app.TaskChain;
import com.fastlib.app.TaskChainHead;
import com.fastlib.base.OldViewHolder;
import com.fastlib.bean.NetFlow;
import com.fastlib.db.DatabaseGetCallback;
import com.fastlib.db.FastDatabase;
import com.fastlib.db.SaveUtil;
import com.fastlib.net.Listener;
import com.fastlib.net.Request;
import com.fastlib.utils.Utils;
import com.fastlib.widget.TitleBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 16/12/29.
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends FastActivity{

    public MainActivity(){
        EventObserver.build(this);
    }

    @Bind(R.id.bt)
    private void commit(View v){

    }

    @Bind(R.id.bt2)
    private void commit2(View v){

    }

    @Override
    protected void alreadyPrepared(){

    }
}