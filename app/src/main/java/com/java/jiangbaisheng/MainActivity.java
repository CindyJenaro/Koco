package com.java.jiangbaisheng;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.SearchView;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;

import androidx.fragment.app.*;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    ViewPager kocoVP;
    TabLayout kocoTL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSystemService(Context.INPUT_METHOD_SERVICE);
        Log.d("debug", "-------------------\nThe first debug message");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        kocoVP = findViewById(R.id.view_pager);
        kocoTL = findViewById(R.id.tabs);

        initFragment();

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "You died.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    private void initFragment(){
        Log.d("debug", "in initFragment");

        List<String> titles = new ArrayList<>();
        titles.add("新闻列表");
        titles.add("疫情数据");
        titles.add("疫情图谱");
        titles.add("知疫学者");

        for(int i = 0; i < titles.size(); i++){
            kocoTL.addTab(kocoTL.newTab().setText(titles.get(i)));
        }

        ArrayList<Fragment> kocoFrags = new ArrayList<>();
        kocoFrags.add(new NewsListFragment());
        kocoFrags.add(new StatisticsFragment());
        kocoFrags.add(new GraphFragment());
        kocoFrags.add(new AcademicFragment());

        FragmentAdapter kocoFPA = new FragmentAdapter(getSupportFragmentManager(), kocoFrags, titles);
        kocoVP.setAdapter(kocoFPA);
        kocoVP.setCurrentItem(0);
        // connect TabLayout with ViewPager
        kocoTL.setupWithViewPager(kocoVP);
        // set adapter for TabLayout
        kocoTL.setTabsFromPagerAdapter(kocoFPA);

    }

    // internal class of MainActivity
    class FragmentAdapter extends FragmentPagerAdapter{

        private List<String> titles;
        private List<Fragment> fragments;

        public FragmentAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles){
            super(fm);
            this.fragments = fragments;
            this.titles = titles;
        }

        @Override
        public Fragment getItem(int position){
            return fragments.get(position);
        }

        @Override
        public int getCount(){
            return fragments.size();
        }

        @Override
        public int getItemPosition(Object object){
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position){
            if(null == titles)
                return null;
            else
                return titles.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object){

            // long live the item!
            // super.destroyItem(container, position, object);

        }
    }

//    @Override
//    public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            imm.hideSoftInputFromWindow(mListView.getWindowToken(), 0);
//        }
//    }

}