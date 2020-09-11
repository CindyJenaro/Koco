package com.java.jiangbaisheng;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

import java.util.List;
import java.util.Map;

public class NewsItemAdapter extends SimpleAdapter {

    private Context context;
    private List<Map<String, Object>> list;
    private int typeFlag = BOTH;
    public static final int NONE = 0;
    public static final int NEWS_ONLY = 1;
    public static final int PAPER_ONLY = 2;
    public static final int BOTH = 3;

    NewsItemAdapter(Context context, List<Map<String, Object>> data,
                    @LayoutRes int resource, String[] from, @IdRes int[] to){

        super(context, data, resource, from, to);
        this.context = context;
        this.list = data;
        this.typeFlag = typeFlag;
    }

    public void setFlag(int typeFlag){
        this.typeFlag = typeFlag;
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.news_list_item, null);
        }else {
            view = convertView;
        }


        TextView title = view.findViewById(R.id.news_title);
        TextView date = view.findViewById(R.id.news_date);
        TextView type = view.findViewById(R.id.news_type);
        TextView id = view.findViewById(R.id.news_id);


        if(!list.get(position).get("title").equals(context.getString(R.string.end_of_list))){

            try{

                title.setText((String)list.get(position).get("title"));
                date.setText((String)list.get(position).get("date"));
                type.setText((String)list.get(position).get("type"));
                id.setText(list.get(position).get("id").toString());

                // need to bring them back manually
                title.setGravity(Gravity.LEFT);
                view.setBackground(context.getDrawable(R.drawable.item_border));

                if((Boolean)list.get(position).get("viewed")){

                    title.setTextColor(context.getColor(R.color.gray));

                } else{

                    title.setTextColor(context.getColor(R.color.almost_black));

                }
            } catch (Exception e){}



        }else{

            title.setText((String)list.get(position).get("title"));
            date.setText("");
            type.setText("");
            id.setText("");
            title.setTextColor(context.getColor(R.color.gray));
            title.setGravity(Gravity.CENTER);
            view.setBackgroundColor(context.getColor(R.color.almost_white));
            view.findViewById(R.id.unlike_buttonset).setVisibility(View.GONE);

        }

        if(typeFlag == NONE){
            view.setVisibility(View.GONE);
        } else if(typeFlag == NEWS_ONLY && type.getText().equals("paper")){
            view.setVisibility(View.GONE);
        } else if(typeFlag == PAPER_ONLY && type.getText().equals("news")){
            view.setVisibility(View.GONE);
        } else{
            view.setVisibility(View.VISIBLE);
        }

        return view;
    }

}
