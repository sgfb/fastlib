package com.fastlib.demo.aspect;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.fastlib.demo.R;

/**
 * Created by sgfb on 2020\03\05.
 */
public class SecondActivity extends AppCompatActivity{
    public static final String RES_STR_TEST ="test";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_aspect_demo);
        findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent=new Intent();
                intent.putExtra(RES_STR_TEST,"hello,world");
                setResult(RESULT_OK,intent);
            }
        });
    }
}
