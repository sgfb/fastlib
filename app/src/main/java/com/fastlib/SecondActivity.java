package com.fastlib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.fastlib.app.EventObserver;

/**
 * Created by sgfb on 16/2/2.
 */
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_layout);
        init();
    }

    private void init(){
        Button bt=(Button)findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventObserver.getInstance().sendLocalEvent("I'm from secondActivity");
            }
        });
    }
}
