package com.java.jiangbaisheng;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class GraphItemAdapter extends SimpleAdapter {

    private Context context;
    private List<Map<String, Object>> list;

    GraphItemAdapter(Context context, List<Map<String, Object>> data,
                     @LayoutRes int resource, String[] from, @IdRes int[] to){

        super(context, data, resource, from, to);
        this.context = context;
        this.list = data;
        Log.d("debug", "in adapter create once");
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d("debug", "in getView once");

        View view = null;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.entity_item, null);
        }else {
            view = convertView;
        }


        TextView title = view.findViewById(R.id.entity_title);
        TextView abstractInfo = view.findViewById(R.id.entity_abstract);
        KocoListView propertiesList = view.findViewById(R.id.entity_properties_list);
        KocoListView parentEntitiesList = view.findViewById(R.id.parent_entities_list);
        KocoListView childEntitiesList = view.findViewById(R.id.child_entities_list);

        title.setText((String)list.get(position).get("title"));
        abstractInfo.setText((String)list.get(position).get("abstractInfo"));

        try{

            JSONObject covidJson = new JSONObject((String)list.get(position).get("covid"));
            JSONObject propertiesJson = covidJson.getJSONObject("properties");
            JSONArray relationsJsonArray = covidJson.getJSONArray("relations");

            SimpleAdapter propertiesAdapter = new SimpleAdapter(context,
                    putProperties(propertiesJson),
                    R.layout.entity_property_item, new String[]{"propertyKey", "propertyValue"},
                    new int[]{R.id.property_key, R.id.property_value});
            propertiesList.setAdapter(propertiesAdapter);

            SimpleAdapter parentEntitiesAdapter = new SimpleAdapter(context,
                    putParentEntities(relationsJsonArray),
                    R.layout.entity_relation_item, new String[]{"relationKey", "relationValue"},
                    new int[]{R.id.relation_key, R.id.relation_value});
            parentEntitiesList.setAdapter(parentEntitiesAdapter);

            SimpleAdapter childEntitesAdapter = new SimpleAdapter(context,
                    putChildEntities(relationsJsonArray),
                    R.layout.entity_relation_item, new String[]{"relationKey", "relationValue"},
                    new int[]{R.id.relation_key, R.id.relation_value});
            childEntitiesList.setAdapter(childEntitesAdapter);

        } catch (JSONException e){
            Log.d("debug", "JSONException occured!");
        } catch (Exception e){
            Log.d("debug", e.toString());
        }

        return view;
    }


    public List<Map<String, Object>> putProperties(JSONObject propertiesJson){

        List<Map<String, Object>> list = new LinkedList<>();

        Iterator<String> keys = propertiesJson.keys();
        while(keys.hasNext()){

            Map<String, Object> currentProperty = new HashMap<>();
            String sKey = keys.next();
            currentProperty.put("propertyKey", sKey);
            String value = propertiesJson.optString(sKey);
            currentProperty.put("propertyValue", value);
            list.add(currentProperty);

        }

        return list;

    }

    public List<Map<String, Object>> putParentEntities(JSONArray relationsJsonArray){

        List<Map<String, Object>> list = new LinkedList<>();

        for (int k = 0; k < relationsJsonArray.length(); k++) {

            try{

                JSONObject currentRelationJson = relationsJsonArray.getJSONObject(k);
                String forward = currentRelationJson.getString("forward");
                if(forward.equals("false")) {
                    continue;
                }

                Map<String, Object> currentRelation = new HashMap<>();

                String relationKey = currentRelationJson.getString("relation");
                currentRelation.put("relationKey", relationKey);
                String relationValue = currentRelationJson.getString("label");
                currentRelation.put("relationValue", relationValue);

                list.add(currentRelation);

            } catch (JSONException e){
                e.printStackTrace();
            }

        }

        return list;

    }

    public List<Map<String, Object>> putChildEntities(JSONArray relationsJsonArray){

        List<Map<String, Object>> list = new LinkedList<>();

        for (int k = 0; k < relationsJsonArray.length(); k++) {

            try{

                JSONObject currentRelationJson = relationsJsonArray.getJSONObject(k);
                String forward = currentRelationJson.getString("forward");
                if(forward.equals("true")) {
                    continue;
                }

                Map<String, Object> currentRelation = new HashMap<>();

                String relationKey = currentRelationJson.getString("relation");
                currentRelation.put("relationKey", relationKey);
                String relationValue = currentRelationJson.getString("label");
                currentRelation.put("relationValue", relationValue);

                list.add(currentRelation);

            } catch (JSONException e){
                e.printStackTrace();
            }

        }

        return list;

    }

}
