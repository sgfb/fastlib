package com.fastlib;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.app.task.Action;
import com.fastlib.app.task.EmptyAction;
import com.fastlib.app.task.NetAction;
import com.fastlib.app.task.NoReturnAction;
import com.fastlib.app.task.Task;
import com.fastlib.net.Request;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    Task t1=Task.begin("name")
            .next(new Action<String,Integer>(){

                @Override
                protected Integer execute(String param) throws Throwable {
                    return param.length();
                }
            })
            .cycle(new Action<Integer,String[]>(){

                @Override
                protected String[] execute(Integer param) throws Throwable {
                    System.out.println(param);
                    return new String[]{"a","b","c"};
                }
            });
    Task t2=Task.begin(new NoReturnAction<String>(){

        @Override
        public void executeAdapt(String param) {
            System.out.println(param);
        }
    });


    @Override
    protected void alreadyPrepared(){

    }

    @Bind(R.id.bt)
    private void commit(){
        startTask(t1);
        startTask(t2);
    }

    @Bind(R.id.bt2)
    private void commit2(){
        startTask(Task.begin(new Request("get", "http://www.baidu.com"))
                        .next(new NetAction<String,Integer>() {

                            @Override
                            protected Integer executeAdapt(String r, Request request) {
                                return r.length();
                            }
                        })
                .next(new NoReturnAction<Integer>() {
                    @Override
                    public void executeAdapt(Integer param) {
                        System.out.println("length:"+param);
                    }
                })
                , new NoReturnAction<Throwable>() {
                    @Override
                    public void executeAdapt(Throwable param) {
                        param.printStackTrace();
                    }
                }, new EmptyAction() {
                    @Override
                    protected void executeAdapt() {

                    }
                });
    }
}