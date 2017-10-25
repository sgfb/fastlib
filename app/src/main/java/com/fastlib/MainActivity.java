package com.fastlib;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;

/**
 * Created by Administrator on 2017/10/25.
 */
@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{

    @Override
    protected void alreadyPrepared() {

    }

    @Bind(R.id.bt)
    private void commit(){
        Request request=Request.obtain("http://myqqy.vicp.io/std/admnxzcmr/customer/ls.json");
//        request.put("phone","13411111111");
//        request.put("password","123456");
//        request.put("Rtn_Token",1);
        request.putHeader("X-CustomToken","eyJhbGciOiJIUzI1NiJ9.eyJ0YWIiOiJjb20uc3R1ZHlpbmcuY20ubW9kZWwuaW1wbC5jdXN0b21lci5DdXN0b21lciIsIm1vZGVsIjoie1wiaWRcIjpcIjEyNzc2XCIsXCJzbm9cIjpcImN1c3RvbWVyXzIwMTcwNjE0MDAwMDEyNzc2XCIsXCJkZWdyZWVJZFwiOlwiMVwiLFwiY2hnRGVncmVlSWRcIjpcIjgwMlwiLFwiY2hnRGdyQ2hlY2tJZFwiOlwiNzk1XCIsXCJpc0RpckFwcFwiOmZhbHNlLFwidGVhY2hlclRpbWVJZFwiOlwiMTEwMTRcIixcInBob25lXCI6XCIxMzQxMTExMTExMVwiLFwicGFzc3dvcmRcIjpcIkUxMEFEQzM5NDlCQTU5QUJCRTU2RTA1N0YyMEY4ODNFXCIsXCJhZGRyZXNzXCI6XCLkvZnmna3ljLrmiJHku6xcIixcInNleFwiOlwiZlwiLFwic2lnbmFtZVwiOlwi5byg5LiJXCIsXCJ1c2VybmFtZVwiOlwi5byg5LiJXCIsXCJwaWN0dXJlXCI6XCJodHRwOi8vdmlkZW8ueHFiYW4uY29tL0N1c3RvbWVyLzIwMTctMTAtMjUvMTUwODkyOTEzODYwMF8zNTUuanBnXCIsXCJzY2hvb2xcIjpcIuS4iua1t-Wkp-WtplwiLFwiY29sRGVncmVlXCI6XCLmnKznp5FcIixcInVpZENhcmRcIjpcIjYxNDU0NTg1NDU4NTI0NVwiLFwicmVhbG5hbWVBdXRoXCI6MSxcInJlYWxuYW1lQXV0aElkXCI6XCI2NlwiLFwicWNBdXRoXCI6MSxcInFjQXV0aElkXCI6XCI3M1wiLFwiY29udHJhY3RBdXRoXCI6MSxcImRlZ3JlZUF1dGhcIjowLFwiY29udGludWVTdW1TaWduRGF5XCI6MSxcImNvdW50Vmlld1wiOjgyOSxcInN0YXR1c1wiOjIsXCJsbmdcIjoxMjAuMzAzNjI2LFwibGF0XCI6MzAuNDIzNDUyLFwibG9jYXRpb25cIjpcIjEyMS4zOTUxNTgsMzEuMzExNTYwXCIsXCJmb3JtYXR0ZWRfYWRkcmVzc1wiOlwi5LiK5rW35biC6YeR5bGx5Yy6XCIsXCJhZGNvZGVcIjpcIjMxMDExM1wiLFwiZG55TG5nXCI6MTIwLjA0MTYyLFwiZG55TGF0XCI6MzAuMjkwNzE0LFwiZG55QWRkcmVzc1wiOlwi5rip5bee5biC55Ge5a6J5biC6JSh5a6F5Lit6LevNzDlj7dcIixcImRueUdpc1VwZGF0ZVRpbWVcIjpcIjIwMTctMDctMDEgMTU6MzU6NDlcIixcImFyZWFzSWRcIjpcIjMzMDExMFwiLFwicHJvdmluY2VJZFwiOlwiMzMwMDAwXCIsXCJjaXR5SWRcIjpcIjMzMDEwMFwiLFwiY2l0eU5hbWVcIjpcIuadreW3nuW4glwiLFwiYXJlYXNOYW1lXCI6XCLkvZnmna3ljLpcIixcInByb3ZpbmNlTmFtZVwiOlwi5rWZ5rGf55yBXCIsXCJob21lUHJvdmluY2VOYW1lXCI6XCLmtZnmsZ_nnIEt5p2t5bee5biCXCIsXCJhY2NvdW50c0lkXCI6XCJcIixcImFnZW50SWRcIjpcIixcIixcImlBbU1lbWJlcnNcIjpmYWxzZSxcIm5vdHZpcmdpblwiOnRydWUsXCJyZW1vdmVkXCI6MCxcImFkZFVzZXJJZFwiOlwiMVwiLFwiYWRkVXNlck5hbWVcIjpcImFkbWluXCIsXCJhZGRVc2VyVGltZVwiOlwiMjAxNy0wNi0xNCAxODowMzo0OFwiLFwiZWRpdFVzZXJJZFwiOlwiMTI3NzZcIixcImVkaXRVc2VyTmFtZVwiOlwi5byg5LiJKDEzNDExMTExMTExKVwiLFwiZWRpdFVzZXJUaW1lXCI6XCIyMDE3LTEwLTI1IDE5OjEwOjExXCIsXCJpc0FkZFVzZXJUeXBlXCI6XCJzdGFmZlwiLFwiaXNFZGl0VXNlclR5cGVcIjpcImN1c3RvbWVyXCIsXCJwdWJsaXNoZWRcIjp0cnVlLFwic29ydHNcIjowLFwiZGlzcGxheVwiOjEsXCJkaWdlc3RcIjowLFwicmFuZ2VcIjotMS4wLFwiaURpc3BsYXlTdGFydFwiOjAsXCJpRGlzcGxheUxlbmd0aFwiOjEwLFwic0VjaG9cIjoxLFwiY3VycGFnZVwiOjAsXCJyb3dzXCI6MTAsXCJpc0FkbWluXCI6ZmFsc2V9IiwiaWQiOiIxMjc3NiIsImV4cCI6MTUwOTk2NzQxM30.cKFk3j5aQzkG7BCAED33J8XtplTNhI5iDDtP0agD3NM");
        request.setListener(new SimpleListener<String>(){

            @Override
            public void onResponseListener(Request r, String result) {
                System.out.println("result:"+result);
            }
        });
        net(request);
    }
}
