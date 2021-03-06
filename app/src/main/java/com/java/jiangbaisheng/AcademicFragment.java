package com.java.jiangbaisheng;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.*;

import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AcademicFragment extends Fragment {
    private WebView webview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.academic_fragment, container, false);

        webview = view.findViewById(R.id.wv_webview);
        //此方法可以在webview中打开链接而不会跳转到外部浏览器
        webview.setWebViewClient(new WebViewClient());
        //此方法可以启用html5页面的javascript
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDefaultTextEncodingName("UTF -8");//设置默认为utf-8
        webview.loadUrl("file:///android_asset/AMiner-传染病学呼吸科专家画像-专家学者.htm");
        return view;

    }

    public void getzhiyidata() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Log.v("YX", "11");
                    URL url=new URL("https://innovaapi.aminer.cn/predictor/api/v1/valhalla/highlight/get_ncov_expers_list?v=2");
                    HttpURLConnection connect=(HttpURLConnection)url.openConnection();
                    InputStream input=connect.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(input));
                    String line = null;
                    System.out.println(connect.getResponseCode());
                    StringBuffer sb = new StringBuffer();
                    List<StatisticsFragment.Statitics> list = new ArrayList<>();
                    while ((line = in.readLine()) != null) {
                        sb.append(line);

                        //创建Json实例
                        JSONObject testjson = new JSONObject(line);//builder读取了JSON中的数据。
                        //直接传入JSONObject来构造一个实例


                    }
                } catch (Exception e) {
                    Log.v("YX", e.toString());
                    System.out.println(e.toString());
                }
            }
        }).start();
    }

}
