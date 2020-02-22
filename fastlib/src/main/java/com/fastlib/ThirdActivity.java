package com.fastlib;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by sgfb on 2020\02\21.
 */
public class ThirdActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent=new Intent();
        intent.putExtra("name","I'm third activity");
        setResult(RESULT_OK,intent);
    }
}
