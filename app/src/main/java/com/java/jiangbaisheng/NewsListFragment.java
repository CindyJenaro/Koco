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

//        Cursor cursor = getActivity().getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, PROJECTION, null, null, null);
//        Adapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
//                cursor, new String[]{ContactsContract.RawContacts.})
        kocoLV = (ListView)view.findViewById(R.id.news_list);
//        kocoLV.setAdapter();


        return view;

    }

    private void initSearchView(){

        kocoSV.setIconifiedByDefault(false);
        kocoSV.setSubmitButtonEnabled(true);
        kocoSV.onActionViewExpanded();
        kocoSV.setQueryHint("搜索");
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


}
