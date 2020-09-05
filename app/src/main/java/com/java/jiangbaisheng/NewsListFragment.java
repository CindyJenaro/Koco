package com.java.jiangbaisheng;

import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.*;
import android.util.Log;
import androidx.fragment.app.*;

public class NewsListFragment extends Fragment {

    SearchView kocoSV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        Log.d("debug","in onCreateView, NewsListFragment");
        View view = inflater.inflate(R.layout.news_list_fragment, container, false);
        if(view == null)
            Log.d("debug", "view == null");
        else
            Log.d("debug", "view != null");

        kocoSV = (SearchView)view.findViewById(R.id.search);

        if(kocoSV == null)
            Log.d("debug", "kocoSV == null");
        else
            Log.d("debug", "kocoSV != null");
        initSearchView();

        return view;

    }

    private void initSearchView(){

        kocoSV.setIconifiedByDefault(false);
        kocoSV.setSubmitButtonEnabled(true);

        kocoSV.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            private String TAG = getClass().getSimpleName();

            @Override
            public boolean onQueryTextSubmit(String query) {

//                if(kocoSV != null){
//                    // collapse keyboard:
//                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                    if(imm != null){
//                        imm.hideSoftInputFromWindow(kocoSV.getWindowToken(), 0);
//                    }
//                    kocoSV.clearFocus();
//                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String queryText) {
                return false;
            }
        });

    }
}
