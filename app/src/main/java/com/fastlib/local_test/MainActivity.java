package com.fastlib.local_test;

import com.fastlib.R;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.app.task.Action;
import com.fastlib.app.task.EmptyAction;
import com.fastlib.app.task.NoParamAction;
import com.fastlib.app.task.NoReturnAction;
import com.fastlib.app.task.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 2017/9/21.
 */
@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity {

    @Override
    protected void alreadyPrepared() {

    }

    @Bind(R.id.bt)
    private void commit() {
        loading();
        startTask(Task.begin().cycleList(new NoParamAction<List<Integer>>() {

            @Override
            protected List<Integer> executeAdapt() {
                ArrayList<Integer> list = new ArrayList<>();
                list.add(1);
                list.add(2);
                list.add(3);
                return list;
            }
        })
                .filter(new Action<Integer, Boolean>() {
                    @Override
                    protected Boolean execute(Integer param) throws Throwable {
                        return param%2==0;
                    }
                })
                .again(new Action<List<Integer>,Object>(){

                    @Override
                    protected Object execute(List<Integer> param) throws Throwable{
                        if(param!=null&&!param.isEmpty())
                            for(int i:param)
                                System.out.println(i);
                        else System.out.println("list is empty");
                        return null;
                    }
                }), new NoReturnAction<Throwable>() {
            @Override
            public void executeAdapt(Throwable param) {
                param.printStackTrace();
            }
        }, new EmptyAction() {
            @Override
            protected void executeAdapt() {
                dismissLoading();
            }
        });
    }

    @Bind(R.id.bt2)
    private void commit2() {

    }
}