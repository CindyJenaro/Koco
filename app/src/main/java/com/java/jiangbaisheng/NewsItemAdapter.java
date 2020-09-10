package com.java.jiangbaisheng;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

import java.util.List;
import java.util.Map;

public class NewsItemAdapter extends SimpleAdapter {

    private Context context;
    private List<Map<String, Object>> list;

    NewsItemAdapter(Context context, List<Map<String, Object>> data,
                    @LayoutRes int resource, String[] from, @IdRes int[] to){

        super(context, data, resource, from, to);
        this.context = context;
        this.list = data;
    }


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
        TextView index = view.findViewById(R.id.news_index);


        if(!list.get(position).get("title").equals(context.getString(R.string.end_of_list))){

            title.setText((String)list.get(position).get("title"));
            date.setText((String)list.get(position).get("date"));
            type.setText((String)list.get(position).get("type"));
            index.setText(list.get(position).get("index").toString());

            if((Boolean)list.get(position).get("viewed")){

                title.setTextColor(context.getColor(R.color.gray));

            }

        }else{

            title.setText((String)list.get(position).get("title"));
            date.setText("");
            type.setText("");
            title.setTextColor(context.getColor(R.color.gray));
            title.setGravity(Gravity.CENTER);
            view.setBackgroundColor(context.getColor(R.color.almost_white));
            view.findViewById(R.id.unlike_buttonset).setVisibility(View.GONE);

        }


        return view;
    }

}
