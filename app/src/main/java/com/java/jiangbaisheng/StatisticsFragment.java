package com.java.jiangbaisheng;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.*;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.*;

import com.bin.david.form.annotation.SmartColumn;
//import com.bin.david.form.annotation.SmartTable;
import com.bin.david.form.core.SmartTable;
import com.bin.david.form.core.TableConfig;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat;
import com.bin.david.form.data.format.bg.ICellBackgroundFormat;
import com.bin.david.form.data.format.draw.MultiLineDrawFormat;
import com.bin.david.form.data.format.draw.TextDrawFormat;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatisticsFragment extends Fragment {
    HashMap<String,String> stat = new HashMap<>();
    SmartTable table;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.statistics_with_table, container, false);
        getdata();//获取疫情数据

        table= view.findViewById(R.id.table);//初始化table
        return view;
    }

    public class Statitics {
        public Statitics(String country, String province, String county, String confirmed, String suspected, String cured, String dead, String severe, String risk) {
            this.country = country;
            this.province = province;
            this.county = county;
            this.confirmed = confirmed;
            this.suspected = suspected;
            this.cured = cured;
            this.dead = dead;
            this.severe = severe;
            this.risk = risk;

        }

        // CONFIRMED,SUSPECTED, CURED, DEAD, SEVERE, RISK, inc24
        //    name：版块名称，count：目标值，restaurant：餐饮数量，ka：KA数量，wholesale：流通批发数量，industry：工业加工数量，other：其它数量
        private String country;
        private String province;
        private String county;
        private String  confirmed;
        private String  suspected;
        private String  cured;
        private String  dead;
        private String  severe;
        private String  risk;

    }

    public void getdata() {
        new Thread(new Runnable() {
            public boolean runing = false;
            @Override
            public void run() {
                try {
                    runing = true;
                    Log.v("YX", "11");
                    URL url=new URL("https://covid-dashboard.aminer.cn/api/dist/epidemic.json");
                    HttpURLConnection connect=(HttpURLConnection)url.openConnection();
                    InputStream input=connect.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(input));
                    String line = null;
                    System.out.println(connect.getResponseCode());
                    StringBuffer sb = new StringBuffer();
                    List<Statitics> list = new ArrayList<>();
                    while ((line = in.readLine()) != null) {
                        sb.append(line);

                        //创建Json实例
                        JSONObject testjson = new JSONObject(line);//builder读取了JSON中的数据。
                        //直接传入JSONObject来构造一个实例

                        Iterator<String> iterator = testjson.keys();//使用迭代器
                        while (iterator.hasNext()) {
                            String key = iterator.next();//获取key
//                            Log.v("YX",key);//国家
                            String coviddata = testjson.getString(key);//获取value

                            JSONObject co = new JSONObject(coviddata);//读
                            JSONArray array = co.getJSONArray("data");
                            String lastdata = array.getString(array.length()-1);//取出最后一个日期
//                            Log.v("YX",lastdata);//最后的日期
//                            Log.v("YX",Integer.toString(stat.size()));
//                            stat.put(key,lastdata);

                            //去掉中括号
                            lastdata=lastdata.replace("[","");
                            lastdata=lastdata.replace("]","");

                            stat.put(key,lastdata);

                        }

                        //进行排序
                        Set set=stat.keySet();
                        Object[] arr=set.toArray();
                        Arrays.sort(arr);
                        for(Object key:arr){
                            String[] splitlocation = key.toString().split("\\|");//国家分开
                            String[] splitnum = stat.get(key).split(",");//数字分开
                            if(splitlocation.length == 1){//只有国家
                                if( splitlocation[0].length()>12){
                                    splitlocation[0]=insertstr(splitlocation[0],"\n",12);
                                }
                                list.add(new Statitics( splitlocation[0],"","",splitnum[0], splitnum[1], splitnum[2], splitnum[3], splitnum[4], splitnum[5]));
                            }
                            else if(splitlocation.length == 2) {
                                if( splitlocation[0].length()>12){
                                    splitlocation[0]=insertstr(splitlocation[0],"\n",12);
                                }
                                if( splitlocation[1].length()>12){
                                    splitlocation[1]=insertstr(splitlocation[1],"\n",12);
                                    if( splitlocation[1].length()>24){
                                        splitlocation[1]=insertstr(splitlocation[1],"\n",24);
                                    }
                                }
                                list.add(new Statitics(splitlocation[0], splitlocation[1], "", splitnum[0], splitnum[1], splitnum[2], splitnum[3], splitnum[4], splitnum[5]));
                            }
                            else if(splitlocation.length == 3){
                                if( splitlocation[0].length()>12){
                                    splitlocation[0]=insertstr(splitlocation[0],"\n",12);
                                }
                                if( splitlocation[1].length()>12){
                                    splitlocation[1]=insertstr(splitlocation[1],"\n",12);
                                    if( splitlocation[1].length()>24){
                                        splitlocation[1]=insertstr(splitlocation[1],"\n",24);
                                    }
                                }
                                if( splitlocation[2].length()>12){
                                    splitlocation[2]=insertstr(splitlocation[2],"\n",12);
                                    if( splitlocation[2].length()>24){
                                        splitlocation[2]=insertstr(splitlocation[2],"\n",24);
                                    }
                                    if( splitlocation[2].length()>36){
                                        splitlocation[2]=insertstr(splitlocation[2],"\n",36);
                                    }
                                }
                                list.add(new Statitics( splitlocation[0],splitlocation[1],splitlocation[2],splitnum[0], splitnum[1], splitnum[2], splitnum[3], splitnum[4], splitnum[5]));

                            }
                        }




                        //加入表格

                        Column<String> country = new Column<>("country", "country");
                        Column<String> province = new Column<>("province", "province");
                        Column<String> county = new Column<>("county", "county");
                        Column<String> confirmed = new Column<>("confirmed", "confirmed");
                        Column<String> suspected = new Column<>("suspected", "suspected");
                        Column<String> cured = new Column<>("cured", "cured");
                        Column<String> dead = new Column<>("dead", "dead");
                        Column<String> severe = new Column<>("severe", "severe");
                        Column<String> risk = new Column<>("risk", "risk");

                        //设置该列当字段相同时自动合并
                        country.setAutoMerge(true);
                        province.setAutoMerge(true);
                        TableData<Statitics> tableData = new TableData<>("疫情数据", list, country,province,county,confirmed, suspected, cured, dead, severe, risk);
                        table.getConfig().setShowXSequence(false);
                        table.getConfig().setShowYSequence(false);
                        table.setTableData(tableData);
                        table.getConfig().setContentStyle(new FontStyle());
                        country.setFixed(true);
//                        table.getConfig().setLeftAndTopBackgroundColor(155);
//                        table.getConfig().setMinTableWidth(3000);
//                        table.setSortColumn(country,false);


//                        //设置列标题背景和颜色
//                        table.getConfig().setColumnCellBackgroundFormat(background);
//                        //设置统计行背景和颜色
//                        table.getConfig().setCountBgCellFormat(background);

                    }
                } catch (Exception e) {
                    Log.v("YX", e.toString());
                    System.out.println(e.toString());
                }
            }
        }).start();
    }


//    ICellBackgroundFormat<Column> background = new BaseCellBackgroundFormat<Column>() {
//        @Override
//        public int getBackGroundColor(Column column) {
//            return ContextCompat.getColor(getContext(), R.color.almost_white);
//        }
//
//        @Override
//        public int getTextColor(Column column) {
//            if("country".equals(column.getColumnName())){
//                return ContextCompat.getColor(getContext(), R.color.colorAccent);
//            }
//            return TableConfig.INVALID_COLOR;
//        }
//    };

    public String insertstr(String src, String dec, int position){
        StringBuffer stringBuffer = new StringBuffer(src);
        return stringBuffer.insert(position, dec).toString();
    }



}
