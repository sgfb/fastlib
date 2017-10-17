package com.fastlib.local_test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fastlib.R;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastFragment;
import com.fastlib.app.task.Action;
import com.fastlib.app.task.Task;

/**
 * Created by sgfb on 17/10/11.
 */
@ContentView(R.layout.act_second)
public class MyFragment extends FastFragment{

    @Override
    protected void alreadyPrepared() {
        System.out.println("my fragment prepared");
    }
}
