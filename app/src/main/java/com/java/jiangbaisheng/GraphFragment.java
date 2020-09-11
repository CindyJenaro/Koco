package com.java.jiangbaisheng;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GraphFragment extends Fragment {

    SearchView searchView;
    String searchkey;
    TextView content;
    View view;
    StringBuilder whole_entity = null;
    ArrayList<String> ent = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.graph_fragment, container, false);
        searchView = view.findViewById(R.id.search);
        initSearchView();

        Log.v("YX", "0");
//        String keyword = "病毒";
//        getgraphdata(keyword);
        return view;
    }

    private void initSearchView(){

        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("查询实体信息");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            private String TAG = getClass().getSimpleName();

            @Override
            public boolean onQueryTextSubmit(String query) {

                if (searchView != null) {

                    searchkey = query;

                    InputMethodManager imm = (InputMethodManager)getActivity().
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {

                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                    }
                    searchView.clearFocus();
                }

                Log.v("YX", "1");
                ent.clear();
                getgraphdata(searchkey);


                return true;
            }

            @Override
            public boolean onQueryTextChange(String queryText) {
                return false;
            }

        });

    }

    public void getgraphdata(final String keyword) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
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
                        if(array.length()==0){//查询无结果
                            content = view.findViewById(R.id.entity_title);
                            content.setText("无相关实体！");
                            content = view.findViewById(R.id.entity_description);
                            content.setText("无");
                        }
                        else{
                            content = view.findViewById(R.id.entity_title);
                            content.setText(searchkey);


                            for (int i = 0; i < array.length(); i++) {
                                JSONObject json = array.getJSONObject(i);
                                String urls = json.getString("url");        //实体网页
                                Log.v("YX", urls);
                                String label = json.getString("label");     //实体名称
                                Log.v("YX", label);
                                String img = json.getString("img");
                                Log.v("YX",img);

//                                whole_entity.append(label).append("\n" ).append( urls ).append("\n");

                                String abstractinfo = json.getString("abstractInfo");
//                            Log.v("YX", abstractinfo);
                                JSONObject json2 = new JSONObject(abstractinfo);
                                //以下三个会有一个有描述
                                String enwiki = json2.getString("enwiki");
                                Log.v("YX", enwiki);
                                String baidu = json2.getString("baidu");
                                Log.v("YX", baidu);
                                String zhwiki = json2.getString("zhwiki");
                                Log.v("YX", zhwiki);

//                                whole_entity.append(enwiki).append(" ").append(baidu).append(" ").append(zhwiki).append("\n");
                                ent.add(label+"\n"+urls+"\n"+enwiki+" "+baidu+" "+zhwiki+" "+"\n");
                                String covid = json2.getString("COVID");
//                            Log.v("YX", covid);
                                JSONObject json3 = new JSONObject(covid);

                                JSONObject json5 = json3.getJSONObject("properties");
                                List<String> list = new ArrayList<String>();
                                Iterator<String> keys = json5.keys();
                                while(keys.hasNext()){
                                    String sKey = keys.next();
                                    Log.v("YX", sKey);
                                    String value = json5.optString(sKey);
                                    Log.v("YX", value);
//                                    whole_entity.append(sKey).append(" ").append(value).append("\n");
                                    ent.add(sKey+" "+value+"\n");
//                                    list.add();
                                }


                                JSONArray array2 = json3.getJSONArray("relations");     //实体关系
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

                                    if(forward.equals("true")){
//                                        whole_entity.append(label2).append(relation).append(label).append("\n");
                                        ent.add(label2+relation+label+"\n");
                                    }
                                    else{
//                                        whole_entity.append(label).append(relation).append(label2).append("\n");
                                        ent.add(label+relation+label2+"\n");
                                    }




                                }

                            }
                            boolean first = true;

                            StringBuilder result = new StringBuilder();
                            Log.v("YX","为啥不输出");
                            for(String string :ent) {
                                if(first) {
                                    first=false;
                                }else{
                                    result.append(",");
                                }
                                result.append(string);
                            }
                            content = view.findViewById(R.id.entity_description);
                            content.setText(result.toString());

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
