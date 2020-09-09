package com.java.jiangbaisheng;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.RelativeLayout;

public class UnlikeButtonSet extends RelativeLayout {

    private View view;
    private Button operationBtn;
    private Button unlike;

    public UnlikeButtonSet(Context context, AttributeSet attrs){

        super(context, attrs);

        view = LayoutInflater.from(context).inflate(R.layout.unlike_button_set,
                    null, true);
        addView(view); // important

        operationBtn = view.findViewById(R.id.op_button);
        unlike = view.findViewById(R.id.unlike);

        operationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("debug", "triggered already");
                unlike.setVisibility(View.VISIBLE);

            }
        });

        unlike.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // remove item from database and refresh
                unlike.setText("已点赞"); // trickly demo
            }
        });
    }

}
