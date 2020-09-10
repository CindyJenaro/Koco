package com.java.jiangbaisheng;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class GetNewsDetailActivity extends AppCompatActivity {

    Button returnBtn;
    FloatingActionButton shareBtn;

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
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "You died.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show(); // demo
            }
        });

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

}
