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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.statistics_fragment, container, false);

        getdata();
        String keyword = "病毒";
//        getgraphdata(keyword);

        return view;
    }

    HashMap<String, Integer> students = new HashMap<>();

    public void getdata() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.v("YX", "11");
                    URL url=new URL("https://covid-dashboard.aminer.cn/api/dist/epidemic.json");
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

                        Iterator<String> iterator = testjson.keys();//使用迭代器
                        while (iterator.hasNext()) {
                            String key = iterator.next();//获取key
//                            Log.v("YX",key);//国家
                            String coviddata = testjson.getString(key);//获取value

                            JSONObject co = new JSONObject(coviddata);//读
                            JSONArray array = co.getJSONArray("data");
                            String lastdata = array.getString(array.length()-1);//取出最后一个日期
//                            Log.v("YX",lastdata);//最后的日期

                        }
                    }
                } catch (Exception e) {
                    Log.v("YX", e.toString());
                    System.out.println(e.toString());
                }
            }
        }).start();
    }

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
                            String j = json.toString();//一个对象的json string

                            String urls = json.getString("url");
                            Log.v("YX", urls);
                            String abstractinfo = json.getString("abstractInfo");
                            Log.v("YX", abstractinfo);
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
