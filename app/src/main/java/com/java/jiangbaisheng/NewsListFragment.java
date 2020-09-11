package com.java.jiangbaisheng;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.*;
import android.util.Log;
import androidx.fragment.app.*;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsListFragment extends Fragment {

    View view;
    SearchView kocoSV;
    ListView kocoLV;
    RefreshableView kocoRV;
    SimpleAdapter kocoSA;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){

        Log.d("debug","in onCreateView, NewsListFragment");
        view = inflater.inflate(R.layout.news_list_fragment, container, false);

        kocoSV = view.findViewById(R.id.search);
        kocoLV = view.findViewById(R.id.news_list);

        initSearchView();
        initTypeList();
        initListView();


        kocoRV = view.findViewById(R.id.refreshable_view);
        kocoRV.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override // overriding child interface's method
            public void onRefresh() {
                try{
                    Log.d("debug", "I am refreshing!");
                    Thread.sleep(3000);
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.getnews();
                    kocoSA.notifyDataSetChanged();

                } catch(Exception e){
                    e.printStackTrace();
                }
                kocoRV.finishRefreshing();
            }
        }, 1);

        return view;

    }

    private void initSearchView(){

        kocoSV.setSubmitButtonEnabled(true);
        kocoSV.setQueryHint("mdzz");
        kocoSV.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            private String TAG = getClass().getSimpleName();

            @Override
            public boolean onQueryTextSubmit(String query) {

                if (kocoSV != null) {

                    InputMethodManager imm = (InputMethodManager)getActivity().
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {

                        imm.hideSoftInputFromWindow(kocoSV.getWindowToken(), 0);
                    }
                    kocoSV.clearFocus();
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
                // todo

            }
        });

        typePaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                typePaper.setVisibility(View.GONE);
                typeAddPaper.setVisibility(View.VISIBLE);
                // todo

            }
        });

        typeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                typeAddList.setVisibility(View.VISIBLE);
                // todo

            }
        });

        typeAddNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                typeNews.setVisibility(View.VISIBLE);
                typeAddList.setVisibility(View.GONE);
                typeAddNews.setVisibility(View.GONE);

            }
        });

        typeAddPaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                typePaper.setVisibility(View.VISIBLE);
                typeAddList.setVisibility(View.GONE);
                typeAddPaper.setVisibility(View.GONE);

            }
        });

    }

    private void initListView(){

        kocoSA = new NewsItemAdapter(getActivity(), putData(),
                R.layout.news_list_item, new String[]{"title", "date", "type", "id"},
                new int[]{R.id.news_title, R.id.news_date, R.id.news_type, R.id.news_id});
        kocoLV.setAdapter(kocoSA);
        kocoLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Log.d("debug", "item " + position + " clicked");

                View currentItem = getViewByPosition(position, kocoLV);
                TextView news_title_view = currentItem.findViewById(R.id.news_title);
                news_title_view.setTextColor(getContext().getColor(R.color.gray));
                TextView news_id_view = currentItem.findViewById(R.id.news_id);
                String news_id = news_id_view.getText().toString();
                Newsdata currentData = Newsdatabase.
                        getInstance(getContext()).getNewsDao().getbyNewsId(news_id);
                Log.d("debug", "clicked data ID:");
                Log.d("debug", currentData.getNewsid());

                try{

                    currentData.setViewed(true); // record viewed history
                    if(currentData.isViewed()){
                        Log.d("debug", "currentData is viewed:");
                        Log.d("debug", currentData.getNewsid());
                    }



                    Intent intent = new Intent();
                    intent.setClass(getContext(), GetNewsDetailActivity.class);

                    JSONObject jobj = new JSONObject(currentData.getJson());
                    intent.putExtra("title", jobj.getString("title"));
                    intent.putExtra("date", "日期：" + jobj.getString("time"));

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
                        if(i == 0)
                            authors += jauthor.getString("name");
                        else
                            authors += ", " + jauthor.getString("name");

                    }

                    intent.putExtra("author", "作者：" + authors);
                    getContext().startActivity(intent);

                } catch (JSONException e) {
                    Log.d("debug", "JSONException occured!");
                    e.printStackTrace();
                }

            }
        });

    }

    public List<Map<String, Object>> putData(){

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

//        Map<String, Object> map1 = new HashMap<String, Object>();
//        map1.put("title", "钟南山发明新药起死回生    九成重症病人或因此获益");
//        map1.put("date", "2020-9-7");
//        map1.put("type", "news");
//        list.add(map1);



        List<Newsdata> allUsers = Newsdatabase
                .getInstance(getActivity())
                .getNewsDao()
                .getall();

        try{

            for(int idx = 1; idx < 17; idx++){

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


                list.add(map);
            }

        } catch (ArrayIndexOutOfBoundsException e){

            Log.d("debug", "No items in dataset yet.");

        }
        catch (Exception e){

            Log.d("debug", e.toString());

        }



        Map<String, Object> noMoreItems = new HashMap<>();
        noMoreItems.put("title", getString(R.string.end_of_list));
        list.add(noMoreItems);
        return list;
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
