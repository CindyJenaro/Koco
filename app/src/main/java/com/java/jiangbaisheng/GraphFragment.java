package com.java.jiangbaisheng;

import android.os.Bundle;
import android.view.*;
import androidx.fragment.app.*;

public class GraphFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.news_list_fragment, container, false);
        return view;
    }
}
