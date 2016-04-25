package com.fastlib;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fastlib.db.SaveUtil;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity{
    final String NAME="default";
    final String KEY="id";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bt=(Button)findViewById(R.id.button);
        Button bt2=(Button)findViewById(R.id.button2);
        final EditText et=(EditText)findViewById(R.id.et);
        final TextView tv=(TextView)findViewById(R.id.tv);
    }
}