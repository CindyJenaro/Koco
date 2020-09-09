package com.java.jiangbaisheng;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.SearchView;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;

import androidx.fragment.app.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    ViewPager kocoVP;
    TabLayout kocoTL;
    String newsjson=null;

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

        getnews();

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
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//    }


    public void getnews(){
        handler.postDelayed(runnable,1000);
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //每次获取两千条

            getnewsurl("https://covid-dashboard.aminer.cn/api/events/list?type=paper&page="+1+"&size=2000");
            handler.postDelayed(this, 300000);//五分钟之后获取新的新闻
        }
    };

    public void insert_to_database(String json,String newsid, String title,String time, String content, String type) {
        Newsdata news = new Newsdata();
        news.setJson(json);
        news.setNewsid(newsid);
        news.setTitle(title);
        news.setTime(time);
        news.setContent(content);
        news.setType(type);
        Newsdatabase
                .getInstance(this)
                .getNewsDao()
                .insertdata(news);

//        Newsdata news2 =  Newsdatabase.getInstance(this).getNewsDao().getbyid(1);
//        Newsdatabase.getInstance(this).getNewsDao().deletedata(news2);


    }



    private void query() {
        Log.v("YX","In query!");
        List<Newsdata> allUsers = Newsdatabase
                .getInstance(this)
                .getNewsDao()
                .getall();

        List<Newsdata> paper = Newsdatabase
                .getInstance(this)
                .getNewsDao()
                .getwithtype("paper");

        for(int i=0;i<paper.size();i++){
            String title=allUsers.get(i).getTitle();
            String id=allUsers.get(i).getType();
            Log.v("YX","查到了title："+title);
            Log.v("YX","type是"+id);
        }

        for(int i=0;i<allUsers.size();i++){
            String id=allUsers.get(i).getNewsid();
            String title=allUsers.get(i).getTitle();
            Log.v("YX","查到"+Integer.toString(i)+"的id:"+id);
            Log.v("YX","查到了title："+title);
        }

    }

    private void delAll() {
        Log.v("YX","删除");
        Newsdatabase
                .getInstance(this)
                .getNewsDao()
                .deleteAll();
    }


    public void getnewsurl(final String urlStr){

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    URL url=new URL(urlStr);
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
                            String j = json.toString();

                            String id= json.getString("_id");
                            String content= json.getString("content");
                            String time = json.getString("time");
                            String title= json.getString("title");
                            String type= json.getString("type");
//                            Log.v("YX",id);
                            insert_to_database(j,id,title,time,content,type);
//                            Log.v("YX",j);

                        }
                    }

//                    query();

                } catch (Exception e) {
//                    Log.v("YX", e.toString());
                    System.out.println(e.toString());

                }
            }


        }).start();
    }

}