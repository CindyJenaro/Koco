package com.java.jiangbaisheng;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GetNewsDetailActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState,
                         @Nullable PersistableBundle persistentState) {

        super.onCreate(savedInstanceState, persistentState);
        Log.d("debug", "in SearchNewsActivity");

        setContentView(R.layout.news_detail);
        //        kocoSV = findViewById(R.id.search);
        //
        //        if(kocoSV != null){
        //            // collapse keyboard:
        //            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //            if(imm != null){
        //                imm.hideSoftInputFromWindow(kocoSV.getWindowToken(), 0);
        //            }
        //            kocoSV.clearFocus();
        //        }
    }
}
