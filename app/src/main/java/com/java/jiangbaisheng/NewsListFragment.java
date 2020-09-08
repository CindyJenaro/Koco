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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsListFragment extends Fragment {

    View view;
    SearchView kocoSV;
    ListView kocoLV;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        Log.d("debug","in onCreateView, NewsListFragment");
        view = inflater.inflate(R.layout.news_list_fragment, container, false);

        kocoSV = (SearchView)view.findViewById(R.id.search);
        initSearchView();

        kocoLV = (ListView)view.findViewById(R.id.news_list);
        SimpleAdapter kocoSA = new SimpleAdapter(getActivity(), putData(),
                R.layout.news_list_item, new String[]{"title", "date", "source"},
                new int[]{R.id.news_title, R.id.news_date, R.id.news_source});
        kocoLV.setAdapter(kocoSA);

        return view;

    }

    private void initSearchView(){

        kocoSV.setIconifiedByDefault(false);
        kocoSV.setSubmitButtonEnabled(true);
        kocoSV.onActionViewExpanded();
        kocoSV.setQueryHint("");
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

    public List<Map<String, Object>> putData(){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        Map<String, Object> map1 = new HashMap<String, Object>();
//        map1.put("title", "我是皮卡丘我是皮卡丘我是皮卡丘我是皮卡丘我是皮卡丘");
//        map1.put("date", "1407-101-219");
//        map1.put("source", "www.gschalk.org");
//        list.add(map1);

        List<Newsdata> allUsers = Newsdatabase
                .getInstance(getActivity())
                .getNewsDao()
                .getall();

        for(int idx = 0; idx < 6; idx++){
            Map<String, Object> map = new HashMap<>();
            Newsdata currentData = allUsers.get(idx);
            String id = currentData.getNewsid();
            String title = currentData.getTitle();
            Log.d("debug", title);
            map.put("title", title);
            String date = currentData.getTime();
            Log.d("debug", date);
            map.put("date", date);
            String source = currentData.getSource();
            Log.d("debug", source);
            map.put("source", source);
        }
        return list;
    }
}
