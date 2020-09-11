package com.java.jiangbaisheng;

import android.content.Context;
import android.media.Image;
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
import android.widget.Button;
import android.widget.ImageButton;

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
    ArrayList<Fragment> kocoFrags;
    NewsListFragment newsListFragment;
    StatisticsFragment statisticsFragment;
    GraphFragment graphFragment;
    AcademicFragment academicFragment;
    ImageButton refreshBtn;
    String newsjson = null;
    FragmentAdapter kocoFPA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSystemService(Context.INPUT_METHOD_SERVICE);
        Log.d("debug", "-------------------\nThe first debug message");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            delAll();
            getnews(); //获取新闻
        } catch (Exception e) {}



        kocoVP = findViewById(R.id.view_pager);
        kocoTL = findViewById(R.id.tabs);

        loadFragments();

        refreshBtn = findViewById(R.id.refresh_button);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Log.d("debug", "refreshBtn onClick");
                kocoFPA.notifyDataSetChanged();
                newsListFragment.kocoSA.notifyDataSetChanged();

            }
        });



    }

    private void loadFragments(){

        List<String> titles = new ArrayList<>();
        titles.add("新闻列表");
        titles.add("疫情数据");
        titles.add("疫情图谱");
        titles.add("知疫学者");

        for(int i = 0; i < titles.size(); i++){
            kocoTL.addTab(kocoTL.newTab().setText(titles.get(i)));
        }

        kocoFrags = new ArrayList<>();
        kocoFrags.add(newsListFragment = new NewsListFragment());
        kocoFrags.add(statisticsFragment = new StatisticsFragment());
        kocoFrags.add(graphFragment = new GraphFragment());
        kocoFrags.add(academicFragment = new AcademicFragment());

        kocoFPA = new FragmentAdapter(getSupportFragmentManager(), kocoFrags, titles);
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

    public void getnews(){
        handler.postDelayed(runnable,1000);
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            //每次获取两百条
            getnewsurl("https://covid-dashboard.aminer.cn/api/events/list?type=paper&page="+1+"&size=200");
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

        Newsdatanochange newsnochange = new Newsdatanochange();
        newsnochange.setJson(json);
        newsnochange.setNewsid(newsid);
        newsnochange.setTitle(title);
        newsnochange.setTime(time);
        newsnochange.setContent(content);
        newsnochange.setType(type);
        Newsdatabase
                .getInstance(this)
                .getNewsDao()
                .insertdatanochange(newsnochange);

    }



    private void query() {
        Log.v("YX","In query!");

        List<Newsdatanochange> all = Newsdatabase
                .getInstance(this)
                .getNewsDao()
                .getallnochange();

        for(int i=0;i<all.size();i++){
            int title=all.get(i).getId();
            String type=all.get(i).getType();
            Log.v("YX","查到了id："+Integer.toString(title));
            Log.v("YX","type是"+type);
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
                            String content = json.getString("content");
                            String time = json.getString("time");
                            String title= json.getString("title");
                            String type= json.getString("type");
                            insert_to_database(j,id,title,time,content,type);

                        }
                    }


                } catch (Exception e) {

                    System.out.println(e.toString());

                }
            }


        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{

                kocoFPA.notifyDataSetChanged();
                newsListFragment.kocoSA.notifyDataSetChanged();

        } catch(Exception e){}

    }

    public  ArrayList<String> findnewsbykey(String key){
        ArrayList<String> newslist = new ArrayList<String>();

        List<Newsdata> allnews = Newsdatabase
                .getInstance(this)
                .getNewsDao()
                .getall();

        for(int i=0;i<allnews.size();i++){
            String title=allnews.get(i).getTitle();
            if(title.contains(key)){
                newslist.add(allnews.get(i).getNewsid());
            }
        }

        return newslist;
    }
}