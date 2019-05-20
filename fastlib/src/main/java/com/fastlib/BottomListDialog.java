package com.fastlib;

import android.widget.TextView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.BottomDialog;

/**
 * Create by sgfb on 2019/05/20
 * E-Mail:602687446@qq.com
 */
@ContentView(R.layout.dialog_bottom_list)
public class BottomListDialog extends BottomDialog {
    @Bind(R.id.text1)
    TextView mText1;
    @Bind(R.id.text2)
    TextView mText2;

    @Override
    protected void bindView() {
        System.out.println(mText1);
    }
}
