package com.fastlib.demo.route;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.fastlib.demo.R;

/**
 * Created by sgfb on 2020\03\01.
 */
public class RouteDemoSecondActivity extends AppCompatActivity {
    public static final String ARG_STR_TEXT ="name";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_route_2);

        String name=getIntent().getStringExtra(ARG_STR_TEXT);
        ((TextView)findViewById(R.id.text)).setText(TextUtils.isEmpty(name)?"没有传参":name);
    }
}
