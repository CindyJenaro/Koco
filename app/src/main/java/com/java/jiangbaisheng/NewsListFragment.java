package com.java.jiangbaisheng;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.util.Log;
import androidx.fragment.app.*;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NewsListFragment extends Fragment {

    View view;
    SearchView kocoSV;
    ListView kocoLV;
    RefreshableView kocoRV;
    NewsItemAdapter kocoSA;
    Button kocoHistoryBtn;
    ListView historyListView;
    List<String> searchHistory;
    SimpleAdapter kocoHistoryAdapter;
    List<Map<String, Object>> historyEntryList;
    List<Map<String, Object>> newsEntryList;
    int currentPointer = 1;
    boolean first_called_putdata = true;
    boolean searchingFlag = false;

    int typeFlag = NewsItemAdapter.BOTH;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){

        Log.d("debug","in onCreateView, NewsListFragment");
        view = inflater.inflate(R.layout.news_list_fragment, container, false);

        kocoSV = view.findViewById(R.id.search);
        kocoLV = view.findViewById(R.id.news_list);

        historyEntryList = new LinkedList<Map<String, Object>>();
        newsEntryList = new LinkedList<Map<String, Object>>();

        initSearchView();
        initTypeList();
        initListView();


        kocoRV = view.findViewById(R.id.refreshable_view);
        kocoRV.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override // overriding child interface's method
            public void onRefresh() {
                try{
                    Log.d("debug", "I am refreshing!");
//                    MainActivity mainActivity = (MainActivity) getActivity();
//                    mainActivity.getnews();
                    putData();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            kocoSA.notifyDataSetChanged(); //Ui线程中更新listview
                        }
                    });

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            kocoHistoryAdapter.notifyDataSetChanged(); //Ui线程中更新listview
                        }
                    });

                } catch(Exception e){
                    e.printStackTrace();
                }
                kocoRV.finishRefreshing();
            }
        }, 1);

        return view;

    }

    private void initSearchView(){

        searchHistory = new LinkedList<>();
        kocoHistoryBtn = view.findViewById(R.id.toggle_history);
        historyListView = view.findViewById(R.id.history_list);
        kocoHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(historyListView.getVisibility() == View.VISIBLE){
                    historyListView.setVisibility(View.GONE);
                } else if(historyListView.getVisibility() == View.GONE){
                    historyListView.setVisibility(View.VISIBLE);
                    kocoSV.setIconified(false);
                    putHistoryData();
                    kocoHistoryAdapter.notifyDataSetChanged();
                }

            }
        });

        kocoHistoryAdapter = new SimpleAdapter(getActivity(), putHistoryData(),
                R.layout.search_history_item, new String[]{"history_text"},
                new int[]{R.id.history_item_text});

        historyListView.setAdapter(kocoHistoryAdapter);

        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                View currentItem = getViewByPosition(position, historyListView);
                TextView currentText = currentItem.findViewById(R.id.history_item_text);
                kocoSV.setQuery(currentText.getText(), true);

                Log.d("debug", "item " + position + " clicked");

            }
        });

        kocoSV.setSubmitButtonEnabled(true);
        kocoSV.setQueryHint("查询新闻关键词");
        kocoSV.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                if (kocoSV != null) {

                    if(!searchHistory.contains(query)){
                        searchHistory.add(0, query);
                    }

                    putHistoryData();
                    Log.d("debug", "size of searchHistory: " + searchHistory.size());

                    InputMethodManager imm = (InputMethodManager)getActivity().
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {

                        imm.hideSoftInputFromWindow(kocoSV.getWindowToken(), 0);
                    }
                    kocoSV.clearFocus();

                    MainActivity mainActivity = (MainActivity)getActivity();
                    ArrayList<String> newsIds = mainActivity.findnewsbykey(query);
                    newsEntryList.clear();
                    for(String newsid : newsIds){

                        Newsdata currentData = Newsdatabase.
                                getInstance(getContext()).getNewsDao().getbyNewsId(newsid);

                        Map<String, Object> map = new HashMap<>();
                        String id = currentData.getNewsid();
                        map.put("id", id);
                        String title = currentData.getTitle();
                        map.put("title", title);
                        String date = "" + currentData.getTime();
                        map.put("date", date);
                        Boolean viewed = currentData.isViewed();
                        map.put("viewed", viewed);

                        String json = currentData.getJson();
                        try{
                            JSONObject jobj = new JSONObject(json);
                            String type = jobj.getString("type");
                            map.put("type", type);
                        } catch(JSONException e) {
                            Log.d("debug", "JSONException occured!");
                            e.printStackTrace();
                        }

                        newsEntryList.add(0, map);
                    }

                    kocoSA.notifyDataSetChanged();
                    kocoHistoryAdapter.notifyDataSetChanged();

                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String queryText) {
                return false;
            }

        });


    }

    private void initTypeList(){

        final Button typeNews = view.findViewById(R.id.type_news);
        final Button typePaper = view.findViewById(R.id.type_paper);
        final Button typeAdd = view.findViewById(R.id.type_add);
        final LinearLayout typeAddList = view.findViewById(R.id.type_add_list);
        final Button typeAddNews = view.findViewById(R.id.type_add_news);
        final Button typeAddPaper = view.findViewById(R.id.type_add_paper);

        typeNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                typeNews.setVisibility(View.GONE);
                typeAddNews.setVisibility(View.VISIBLE);

                typeFlag = typeFlag & NewsItemAdapter.PAPER_ONLY;
                kocoSA.setFlag(typeFlag);
                kocoSA.notifyDataSetChanged();

            }
        });

        typePaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                typePaper.setVisibility(View.GONE);
                typeAddPaper.setVisibility(View.VISIBLE);

                typeFlag = typeFlag & NewsItemAdapter.NEWS_ONLY;
                kocoSA.setFlag(typeFlag);
                kocoSA.notifyDataSetChanged();

            }
        });

        typeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                typeAddList.setVisibility(View.VISIBLE);

            }
        });

        typeAddNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                typeNews.setVisibility(View.VISIBLE);
                typeAddList.setVisibility(View.GONE);
                typeAddNews.setVisibility(View.GONE);

                typeFlag = typeFlag | NewsItemAdapter.NEWS_ONLY;
                kocoSA.setFlag(typeFlag);
                kocoSA.notifyDataSetChanged();

            }
        });

        typeAddPaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                typePaper.setVisibility(View.VISIBLE);
                typeAddList.setVisibility(View.GONE);
                typeAddPaper.setVisibility(View.GONE);

                typeFlag = typeFlag | NewsItemAdapter.PAPER_ONLY;
                kocoSA.setFlag(typeFlag);
                kocoSA.notifyDataSetChanged();

            }
        });

    }

    private void initListView(){

        kocoSA = new NewsItemAdapter(getActivity(), putData(),
                R.layout.news_list_item, new String[]{"title", "date", "type", "id", "viewed"},
                new int[]{R.id.news_title, R.id.news_date, R.id.news_type, R.id.news_id, R.id.news_viewed});

        kocoLV.setAdapter(kocoSA);

        kocoLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Log.d("debug", "item " + position + " clicked");

                View currentItem = getViewByPosition(position, kocoLV);
                TextView news_title_view = currentItem.findViewById(R.id.news_title);
                if(news_title_view.getText().equals(getContext().getString(R.string.end_of_list))){
                    return;
                }
                news_title_view.setTextColor(getContext().getColor(R.color.gray));
                TextView news_id_view = currentItem.findViewById(R.id.news_id);
                String news_id = news_id_view.getText().toString();
                Newsdata currentData = Newsdatabase.
                        getInstance(getContext()).getNewsDao().getbyNewsId(news_id);
                Log.d("debug", "clicked data ID:");
                Log.d("debug", currentData.getNewsid());

                try{

                    currentData.setViewed(true);
                    Newsdatabase.getInstance(getContext()).getNewsDao().insertdata(currentData);

                    Intent intent = new Intent();
                    intent.setClass(getContext(), GetNewsDetailActivity.class);

                    JSONObject jobj = new JSONObject(currentData.getJson());
                    intent.putExtra("title", jobj.getString("title"));
                    intent.putExtra("date", "日期：" + jobj.getString("time"));
                    intent.putExtra("viewed", currentData.isViewed());

                    if(!jobj.getString("content").equals("")){
                        intent.putExtra("content", jobj.getString("content"));
                    } else{
                        intent.putExtra("content", "暂无原文");
                    }

                    if(!jobj.getString("source").equals("")){
                        intent.putExtra("source", "来源：" + jobj.getString("source"));
                    } else{
                        intent.putExtra("source", "来源：未知网站");
                    }

                    JSONArray jauthors = jobj.getJSONArray("authors");
                    String authors = "";

                    for(int i = 0; i < jauthors.length(); i++) {

                        JSONObject jauthor = jauthors.getJSONObject(i);
                        if(i == 0){
                            if(jauthor.has("name"))
                                authors += jauthor.getString("name");
                            else if(jauthor.has("name_zh"))
                                authors += jauthor.getString("name_zh");
                        }

                        else{
                            if(jauthor.has("name"))
                                authors += ", " + jauthor.getString("name");
                            else if(jauthor.has("name_zh"))
                                authors += ", " + jauthor.getString("name_zh");
                        }
                    }

                    intent.putExtra("author", "作者：" + authors);
                    getContext().startActivity(intent);

                } catch (JSONException e) {
                    Log.d("debug", "JSONException occured!");
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

    }

    public List<Map<String, Object>> putData(){

//        Map<String, Object> map1 = new HashMap<String, Object>();
//        map1.put("title", "钟南山发明新药起死回生    九成重症病人或因此获益");
//        map1.put("date", "2020-9-7");
//        map1.put("type", "news");
//        newsEntryList.add(0, map1);


        List<Newsdata> allUsers = Newsdatabase
                .getInstance(getActivity())
                .getNewsDao()
                .getall();
        Log.d("debug", "the length of the list is " + allUsers.size());

        try{

            if(searchingFlag == false){

                for(int idx = 0 + currentPointer; idx < 17 + currentPointer; idx++){

                    Map<String, Object> map = new HashMap<>();
                    Newsdata currentData = allUsers.get(idx);
                    String id = currentData.getNewsid();
                    map.put("id", id);
                    String title = currentData.getTitle();
                    map.put("title", title);
                    String date = "" + currentData.getTime();
                    map.put("date", date);
                    Boolean viewed = currentData.isViewed();
                    map.put("viewed", viewed);

                    String json = currentData.getJson();
                    try{
                        JSONObject jobj = new JSONObject(json);
                        String type = jobj.getString("type");
                        map.put("type", type);
                    } catch(JSONException e) {
                        Log.d("debug", "JSONException occured!");
                        e.printStackTrace();
                    }

                    newsEntryList.add(0, map);
                }

                currentPointer += 17;

            } else{

                // don't put any data in; data is already in the newsEntryList
                searchingFlag = false;
            }


        } catch (ArrayIndexOutOfBoundsException e){

            Log.d("debug", "No items in dataset yet.");

        } catch (Exception e){

            e.printStackTrace();

        }



        if(first_called_putdata){

            Map<String, Object> noMoreItems = new HashMap<>();
            noMoreItems.put("title", getString(R.string.end_of_list));
            newsEntryList.add(noMoreItems);
            first_called_putdata = false;
        }

        return newsEntryList;
    }

    public List<Map<String, Object>> putHistoryData(){

        Log.d("debug", "in putHistoryData(again)");

        historyEntryList.clear();

        for(int idx = 0; idx < searchHistory.size(); idx++){

            Map<String, Object> map = new HashMap<>();
            map.put("history_text", searchHistory.get(idx));
            historyEntryList.add(0, map);

        }

        Log.d("debug", "size of historyEntryList: " + historyEntryList.size());

        return historyEntryList;

    }

    public View getViewByPosition(int pos, ListView listView) {
        int firstListItemPosition = listView.getFirstVisiblePosition();
        int lastListItemPosition = firstListItemPosition
                + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
}
