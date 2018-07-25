package com.hhkj.mylibrary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.fastlib.Module;

/**
 * Created by sgfb on 2018/7/24.
 */
@Module("cc")
public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv=new TextView(this);
        tv.setText("just test");
        tv.setTextSize(16);
        tv.setPadding(10,10,10,10);
        setContentView(tv);
    }
}
