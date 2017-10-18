package com.fastlib.local_test;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.fastlib.R;
import com.fastlib.adapter.MultiAdapter;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.app.task.Action;
import com.fastlib.app.task.Task;

import java.util.List;

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
        startTask(Task.begin(new Action<String, String>() {

            @Override
            protected String execute(String param) {
                return "hello";
            }
        }).cycle(new Action<String, Integer[]>() {

            @Override
            protected Integer[] execute(String param) {
                return new Integer[0];
            }
                }).next(new Action<String,String>(){

                    @Override
                    protected String execute(String param) {
                        return param;
                    }
                }).again(new Action<List<String>,String>(){

                    @Override
                    protected String execute(List<String> param) {
                        return null;
                    }
                }));
    }

    @Bind(R.id.bt2)
    private void commit2() {

    }
}