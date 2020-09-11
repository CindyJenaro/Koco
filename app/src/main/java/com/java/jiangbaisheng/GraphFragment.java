package com.java.jiangbaisheng;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import androidx.fragment.app.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class GraphFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.graph_fragment, container, false);
        Log.v("YX", "0");
        String keyword = "病毒";
        getgraphdata(keyword);
        return view;
    }

    //

    public void getgraphdata(final String keyword) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.v("YX", "1");
                    URL url=new URL("https://innovaapi.aminer.cn/covid/api/v1/pneumonia/entityquery?entity="+keyword);
                    HttpURLConnection connect=(HttpURLConnection)url.openConnection();
                    InputStream input=connect.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(input));
                    String line = null;
                    System.out.println(connect.getResponseCode());
                    StringBuffer sb = new StringBuffer();
                    while ((line = in.readLine()) != null) {
                        sb.append(line);

                        //创建Json实例
                        JSONObject testjson = new JSONObject(line);//builder读取了JSON中的数据。
                        //直接传入JSONObject来构造一个实例

                        JSONArray array = testjson.getJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject json = array.getJSONObject(i);    //取出数组中的对象
//                            String j = json.toString();//一个对象的json string

                            String urls = json.getString("url");
                            Log.v("YX", urls);
                            String label = json.getString("label");
                            Log.v("YX", label);
                            String abstractinfo = json.getString("abstractInfo");
                            Log.v("YX", abstractinfo);
                            JSONObject json2 = new JSONObject(abstractinfo);
                            String enwiki = json2.getString("enwiki");
                            Log.v("YX", enwiki);
                            String baidu = json2.getString("baidu");
                            Log.v("YX", baidu);
                            String zhwiki = json2.getString("zhwiki");
                            Log.v("YX", zhwiki);
                            String covid = json2.getString("COVID");
//                            Log.v("YX", covid);
                            JSONObject json3 = new JSONObject(covid);
                            JSONArray array2 = json3.getJSONArray("relations");
//                            Log.v("YX", array2.toString());
                            for (int k = 0; k < array2.length(); k++) {
                                JSONObject json4 = array2.getJSONObject(k);
                                String relation = json4.getString("relation");
                                Log.v("YX",relation);
                                String url2 = json4.getString("url");
                                Log.v("YX",url2);
                                String label2 = json4.getString("label");
                                Log.v("YX",label2);
                                String forward = json4.getString("forward");
                                Log.v("YX",forward);
                            }

//                            JSONArray array2 = json4.getJSONArray("data");

//                            JSONArray array2 = json2.getJSONArray("COVID");
//                            for (int k = 0; k < array2.length(); k++) {
//                                JSONObject jsonob = array.getJSONObject(i);
//                                String enwiki = jsonob.getString("enwiki");
//                                Log.v("YX", enwiki);
//                                String baidu = jsonob.getString("baidu");
//                                Log.v("YX", baidu);
//                                String zhwiki = jsonob.getString("zhwiki");
//                                Log.v("YX", zhwiki);
//                                String covid = jsonob.getString("COVID");
//
//
//                                JSONObject json3 = new JSONObject(abstractinfo);
//                                JSONArray array3 = json3.getJSONArray("relations");
//                                for (int l = 0; l < array3.length(); l++) {
//                                    JSONObject jsonob2 = array.getJSONObject(i);
//                                    String relation = jsonob2.getString("relation");
//                                    Log.v("YX", relation);
//                                    String url2 = jsonob2.getString("url");
//                                    Log.v("YX", url2);
//                                    String label2 = jsonob2.getString("label");
//                                    Log.v("YX", label2);
//                                    String forward = jsonob2.getString("forward");
//                                    Log.v("YX", forward);
//                                }
//
//
//                            }

//                            String covid = json.getString("COVID");
//                            Log.v("YX",covid);
                            String img = json.getString("img");
                            Log.v("YX",img);


                        }
                    }
                } catch (Exception e) {
                    Log.v("YX", e.toString());
                    System.out.println(e.toString());
                }
            }


        }).start();
    }



}
