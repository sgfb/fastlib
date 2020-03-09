package com.fastlib.demo;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.fastlib.demo.aspect.AspectDemoActivity;
import com.fastlib.demo.list_view.ListDemoActivity;
import com.fastlib.demo.net.CloudActivity;
import com.fastlib.demo.route.RouteDemoActivity;
import com.lxj.xpopup.XPopup;

/**
 * Created by sgfb on 2020\03\03.
 * demo的第一界面.仅提供引导作用不使用任何框架
 */
public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        final MyBottomPop fragmentPop=new MyBottomPop(this);
        findViewById(R.id.startRouterDemo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RouteDemoActivity.class));
            }
        });
        findViewById(R.id.startListDemo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ListDemoActivity.class));
            }
        });
        findViewById(R.id.startNetDemo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this,NetDemoActivity.class));
                startActivity(new Intent(MainActivity.this,CloudActivity.class));
            }
        });
        findViewById(R.id.startAspectDemo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this,AspectDemoActivity.class));
                fragmentPop.onCreate();
            }
        });
    }
}
