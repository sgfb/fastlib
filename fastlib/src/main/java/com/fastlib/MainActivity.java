package com.fastlib;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.url_image.FastImage;
import com.fastlib.url_image.bean.FastImageConfig;
import com.fastlib.url_image.request.BitmapRequestEntrance;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    @Bind(R.id.image)
    ImageView mImage;
    @Bind(R.id.list)
    ListView mList;

    @Override
    protected void alreadyPrepared(){
//        MyAdapter adapter=new MyAdapter();
//        List<String> list=new ArrayList<>();
//        FastImageConfig config=FastImage.getInstance().getConfig();
//
//        config.mSaveFolder=getExternalCacheDir();
//        FastImage.getInstance().setConfig(config);
//        list.add("https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1526524434&di=e90aaa558e58a253e6214d7f1079335f&src=http://imgsrc.baidu.com/imgad/pic/item/a8773912b31bb051acab2d123d7adab44aede079.jpg");
//        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1526535285861&di=f2e841e0efa1d4bc27e9baebb17e393a&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2Fa50f4bfbfbedab64a3f831fbfc36afc379311e06.jpg");
//        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1526535296442&di=2db00f7bc76e0e21141b1d4f7857938e&imgtype=jpg&src=http%3A%2F%2Fimg2.imgtn.bdimg.com%2Fit%2Fu%3D2791674927%2C1091799816%26fm%3D214%26gp%3D0.jpg");
//        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1526552796840&di=763950ed1c6831c0ad0657cc9766c12f&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2Fb21c8701a18b87d6e79bd4240c0828381f30fd34.jpg");
//        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1527147528&di=8b34f42fa34fd6fc17835797d565a9b5&imgtype=jpg&er=1&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2Faa18972bd40735fa2de8778295510fb30f2408f4.jpg");
//        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1526552822057&di=7437ff17808042d391330b62936a9627&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2Fadaf2edda3cc7cd9ac2b4ce23201213fb80e9182.jpg");
//        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1526552834965&di=10acc48ebf2fb08f3ce41f06af6b91c5&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2Fbd3eb13533fa828b705c67dff61f4134970a5a8b.jpg");
//        adapter.setData(list);
//        mList.setAdapter(adapter);
    }

    @Bind(R.id.bt)
    private void commit(){
        mImage.setVisibility(View.VISIBLE);
        FastImage.getInstance().startRequest(
                BitmapRequestEntrance.factory(this)
                .bitmapRequestByUrl("https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1526524434&di=e90aaa558e58a253e6214d7f1079335f&src=http://imgsrc.baidu.com/imgad/pic/item/a8773912b31bb051acab2d123d7adab44aede079.jpg")
                .setImageView(mImage)
        );
    }

    @Bind(R.id.bt2)
	private void commit2(){
        mImage.setVisibility(View.VISIBLE);
        FastImage.getInstance().startRequest(
                BitmapRequestEntrance.factory(this)
                        .bitmapRequestByUrl("https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1526524434&di=e90aaa558e58a253e6214d7f1079335f&src=http://imgsrc.baidu.com/imgad/pic/item/a8773912b31bb051acab2d123d7adab44aede079.jpg")
                        .setRequestWidth(1000)
                        .setRequestHeight(1000)
                        .setImageView(mImage)
        );
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FastImage.getInstance().clearMemory();
    }
}