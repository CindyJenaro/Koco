package com.java.jiangbaisheng;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GraphFragment extends Fragment {

    SearchView searchView;
    String searchkey;
    TextView hint;
    GraphItemAdapter kocoGA;
    ListView entityList;
    View view;
    StringBuilder whole_entity = null;
    List<Map<String, Object>> graphEntityList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.graph_fragment, container, false);
        searchView = view.findViewById(R.id.search);
        entityList = view.findViewById(R.id.entity_list);
        graphEntityList = new LinkedList<>();
        initSearchView();
        initListView();
        return view;
    }

    private void initSearchView(){

        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("查询实体信息");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

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

                graphEntityList.clear();
                getgraphdata(searchkey);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        kocoGA.notifyDataSetChanged(); //Ui线程中更新listview
                    }
                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String queryText) {
                return false;
            }

        });

    }

    private void initListView(){

        kocoGA = new GraphItemAdapter(getActivity(), getgraphdata(""),
                R.layout.entity_item, new String[]{"title", "abstractInfo", "covid"},
                new int[]{R.id.entity_title, R.id.entity_abstract});

        entityList.setAdapter(kocoGA);

    }

    public List<Map<String, Object>> getgraphdata(final String keyword) {

        if(keyword.equals("")){

//            Map<String, Object> map = new HashMap<>();
//            map.put("title", "eeeeee");
//            graphEntityList.add(map);

            return graphEntityList;
        }

        graphEntityList.clear();

        try {
            URL url = new URL("https://innovaapi.aminer.cn/covid/api/v1/pneumonia/entityquery?entity=" + keyword);
            HttpURLConnection connect = (HttpURLConnection)url.openConnection();
            InputStream input = connect.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            String line = null;
            System.out.println(connect.getResponseCode());

            while ((line = in.readLine()) != null) {

                //创建Json实例
                JSONObject jobj = new JSONObject(line);//builder读取了JSON中的数据。
                //直接传入JSONObject来构造一个实例

                JSONArray array = jobj.getJSONArray("data");
                if(array.length()==0){//查询无结果
                    hint = view.findViewById(R.id.entity_hint);
                    hint.setVisibility(View.VISIBLE);
                    hint.setText("无相关实体！");
                }
                else{

                    hint = view.findViewById(R.id.entity_hint);
                    hint.setVisibility(View.GONE);

                    for (int i = 0; i < array.length(); i++) {

                        Map<String, Object> currentEntity = new HashMap<>();

                        JSONObject currentEntityJson = array.getJSONObject(i);
                        String title = currentEntityJson.getString("label");
                        currentEntity.put("title", title);

                        String abstractInfo = currentEntityJson.getString("abstractInfo");
                        JSONObject abstractJson = new JSONObject(abstractInfo);
                        //以下三个会有一个有描述
                        String enwiki = abstractJson.getString("enwiki");
                        Log.v("YX", enwiki);
                        String baidu = abstractJson.getString("baidu");
                        Log.v("YX", baidu);
                        String zhwiki = abstractJson.getString("zhwiki");
                        Log.v("YX", zhwiki);

                        if(!enwiki.equals("")){
                            abstractInfo = enwiki;
                        } else if(!baidu.equals("")){
                            abstractInfo = baidu;
                        } else if(!zhwiki.equals("")){
                            abstractInfo = zhwiki;
                        } else {
                            abstractInfo = "暂无摘要！";
                        }
                        currentEntity.put("abstractInfo", abstractInfo);

                        String covid = abstractJson.getString("COVID");

                        currentEntity.put("covid", covid);

                        graphEntityList.add(currentEntity);

                    }

                }
            }
        } catch (Exception e) {

            Log.d("debug", e.toString());
        }

        Log.d("debug", "the size of graphEntityList: " + graphEntityList.size());

        return graphEntityList;

    }
}
