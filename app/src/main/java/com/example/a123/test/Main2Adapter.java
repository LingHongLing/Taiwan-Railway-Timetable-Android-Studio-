package com.example.a123.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.example.a123.test.R.id.TrainNo;
import static com.example.a123.test.R.id.TrainType;

/**
 * Created by 123 on 2016/12/22.
 */

public class Main2Adapter extends BaseAdapter {

    Context mContext;
    LayoutInflater mInflater;
    JSONArray mJsonArray;
    String Train_No;

    // 用來儲存row裡每個view的id，以免每次都要取一次
    private static class ViewHolder {
        public TextView TrainType_tv;
        public TextView TrainNo_tv;
        public TextView Inouttime_tv;
        public TextView DelayTime_tv;


        //public TextView Start_End_tv;
    }

    // 類別的建構子
    public Main2Adapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mJsonArray = new JSONArray();

    }

    // 輸入JSON資料
    public void updateData(JSONArray jsonArray) {
        mJsonArray = jsonArray;

        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return mJsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        return mJsonArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.row_main, null);
            holder = new ViewHolder();
            holder.TrainType_tv = (TextView) convertView.findViewById(TrainType);
            holder.TrainNo_tv = (TextView) convertView.findViewById(TrainNo);
            holder.Inouttime_tv = (TextView) convertView.findViewById(R.id.Time_inout);
            holder.DelayTime_tv = (TextView) convertView.findViewById(R.id.TimeDelay);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        JSONObject jsonObject = (JSONObject) getItem(position);

        if(jsonObject.has("TrainType")){
            Train_No = jsonObject.optString("TrainType");
            holder.TrainType_tv.setText(Train_No);
        }
        if(jsonObject.has("TrainNo")){
            Train_No = jsonObject.optString("TrainNo");
            holder.TrainNo_tv.setText(Train_No);
        }
        if(jsonObject.has("StartTime") && jsonObject.has("EndTime")){
            holder.Inouttime_tv.setText(jsonObject.optString("StartTime") + "~" + jsonObject.optString("EndTime"));
        }
        boolean ttorff =true;

        if(jsonObject.has("DelayTime")){
            int time_tmp = jsonObject.optInt("DelayTime");
            if(time_tmp == 0){
                holder.DelayTime_tv.setText("準點");
                ttorff = false;
            }else if(time_tmp == -1){

            }else{
                holder.DelayTime_tv.setText("晚" + time_tmp + "分");
                ttorff = false;
            }
        }

        if(ttorff) {
            holder.DelayTime_tv.setText("");
        }

        return convertView;
    }
}
