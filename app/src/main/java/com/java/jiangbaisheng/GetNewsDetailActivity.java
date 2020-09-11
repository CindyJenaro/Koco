package com.java.jiangbaisheng;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetNewsDetailActivity extends AppCompatActivity {

    Button returnBtn;
    FloatingActionButton shareBtn;
    ImageButton sharing_tip;
    private IWXAPI api;
    private static final int THUMB_SIZE = 150;
    private static final String APP_ID = "wx0e6dee95bbf7795c";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d("debug", "in GetNewsDetailActivity");

        setContentView(R.layout.news_detail);

        returnBtn = findViewById(R.id.return_button);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        shareBtn = findViewById(R.id.share_button);
        sharing_tip = findViewById(R.id.sharing_tip);
        initSharingMethod();

        Bundle bud = getIntent().getExtras();

        if (bud != null) {

            if(bud.containsKey("title")){
                TextView title = findViewById(R.id.detail_title);
                title.setText(bud.getString("title"));
            }
            if(bud.containsKey("date")){
                TextView date = findViewById(R.id.detail_time);
                date.setText(bud.getString("date"));
            }
            if(bud.containsKey("author")){
                TextView author = findViewById(R.id.detail_author);
               author.setText(bud.getString("author"));
            }
            if(bud.containsKey("content")){
                TextView content = findViewById(R.id.detail_text);
                content.setText(bud.getString("content"));
                if(bud.getString("content").equals("暂无原文")){
                    content.setGravity(Gravity.CENTER);
                    content.setTextColor(getColor(R.color.gray));
                }
            }
            if(bud.containsKey("source")){
                TextView source = findViewById(R.id.detail_source);
                source.setText(bud.getString("source"));
            }
        }

    }

    public void initSharingMethod(){

        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        api.registerApp(APP_ID);

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sharing_tip.setVisibility(View.VISIBLE);

            }
        });

        sharing_tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sharing_tip.setVisibility(View.GONE);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{

                            WXTextObject textObj = new WXTextObject();
                            textObj.text = "";

                            textObj.text += "新闻标题：" + ((TextView)findViewById(R.id.detail_title)).getText().toString()
                                    + "\n\n新闻内容：" + ((TextView)findViewById(R.id.detail_text)).getText().toString();

                            WXMediaMessage msg = new WXMediaMessage();
                            msg.mediaObject = textObj;
                            msg.description = "Koco News Detail";

                            SendMessageToWX.Req req = new SendMessageToWX.Req();
                            req.message = msg;
                            req.scene = SendMessageToWX.Req.WXSceneSession;
                            api.sendReq(req);

                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }

}
