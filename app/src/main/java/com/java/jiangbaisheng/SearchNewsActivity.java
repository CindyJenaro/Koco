package com.java.jiangbaisheng;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SearchNewsActivity extends AppCompatActivity {

    SearchView kocoSV;
    View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState,
                         @Nullable PersistableBundle persistentState){

        super.onCreate(savedInstanceState, persistentState);
        Log.d("debug", "in SearchNewsActivity");

        setContentView(R.layout.activity_main);
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


    @Override
    protected void onPause(){
        super.onPause();
    }

}
