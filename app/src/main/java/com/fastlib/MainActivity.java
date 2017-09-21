package com.fastlib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.app.task.EmptyAction;
import com.fastlib.app.task.NetAction;
import com.fastlib.app.task.NoParamAction;
import com.fastlib.app.task.Task;
import com.fastlib.db.FastDatabase;
import com.fastlib.net.NetManager;
import com.fastlib.net.Request;
import com.fastlib.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by sgfb on 17/8/7.
 */
@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity {
    @Bind(R.id.path)
    EditText mPath;

    @Override
    protected void alreadyPrepared(){

    }

    @Bind(R.id.bt)
    private void commit(TextView v){

    }

    @Bind(R.id.bt2)
    private void commit2(TextView view){

    }

    @Bind(R.id.bt3)
    private void commit3() {

    }
}