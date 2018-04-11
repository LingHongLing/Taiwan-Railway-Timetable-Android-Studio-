package com.example.a123.test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class Main2Activity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    TextView tx1, tx2, tx3;
    ListView mListView;
    Main2Adapter mAdapter;
    int StartStationID, EndStationID, Time_find;
    String Date_find;
    String [] CheckType_num = new String [] {"1115","12A1","1108","1100","1101","1102","1107","1110","1120","1130","1131","1132","1140","1141","1150","1154","12A0","1152","1282","1104","1106","1281","1270","1112","1111","1155","1135","1103","1122","1113","12B0","1134","4200","1133","1151","5230","1280","1105","1121","1114"};
    String [] CheckType = new String[] {"莒光","單機迴送","自強","自強","自強","太魯閣","普悠瑪","莒光","復興","電車","區間","區間快","普快","柴快車","柴油車","柴客","調車列車","行包專車","臨時客迴","自強","自強","柴迴","普通貨車","莒光","莒光","柴客","區間","自強","復興","莒光","試轉運","兩鐵","特種","電車","普通車","特種","客迴","自強","復興","莒光"};
    boolean API_INFO_Check = false,API_Time_Check = false;
    String [] TrainNo;
    String [] TrainClassificationID;
    String [] StartTime;
    String [] EndTime;
    String [] TrainType;
    int [] DeleteTime;
    int [] DelayTime;
    int [] Cost_time;
    int [] Insert_sort;
    boolean  DateChange_torf = false;
    Train_Info_class Train_StartEnd;
    Train_Info_class Train_Delay;
    ProgressDialog mDialog;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        findViews();
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Loading Data...");
        Bundle bundle_rec = getIntent().getExtras();

        StartStationID = bundle_rec.getInt("StartStationID");
        EndStationID = bundle_rec.getInt("EndStationID");
        Date_find = bundle_rec.getString("Date");
        Time_find = bundle_rec.getInt("Time");
        DateChange_torf = bundle_rec.getBoolean("DateChange");
        if(DateChange_torf){
            Log.d("DateChange","true");
        }else{
            Log.d("DateChange","false");
        }
        mAdapter = new Main2Adapter(this);
        mListView.setAdapter(mAdapter);
        Train_StartEnd = new Train_Info_class();
        Train_Delay = new Train_Info_class();
        Load_TrainInfo_api();
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        JSONObject jsonObject = (JSONObject) parent.getAdapter().getItem(position);
        String Sent_TrainNo = jsonObject.optString("TrainNo");
        String Sent_TrainType = jsonObject.optString("TrainType");
        String Sent_StartTime = jsonObject.optString("StartTime");
        String Sent_EndTime = jsonObject.optString("EndTime");
        int Sent_DelayTime = jsonObject.optInt("DelayTime");

        Intent sentIntent = new Intent(this, SettingActivity.class);
        sentIntent.putExtra("StartStationID", StartStationID);
        sentIntent.putExtra("EndStationID", EndStationID);
        sentIntent.putExtra("TrainNo", Sent_TrainNo);
        sentIntent.putExtra("TrainType", Sent_TrainType);
        sentIntent.putExtra("StartTime", Sent_StartTime);
        sentIntent.putExtra("EndTime", Sent_EndTime);
        sentIntent.putExtra("Date", Date_find);
        sentIntent.putExtra("DelayTime", Sent_DelayTime);

        startActivity(sentIntent);
    }


    public class Train_Info_class{
        int length;
        JSONArray array_info;
    }

    private void Load_TrainInfo_api() {
        mDialog.show();
        String urlString = "http://ptx.transportdata.tw/MOTC/v2/Rail/TRA/DailyTimetable/OD/" + String.valueOf(StartStationID) + "/to/" + String.valueOf(EndStationID) + "/" + Date_find + "?$format=JSON";
        //Log.d("Hot Text:", urlString);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(urlString, new JsonHttpResponseHandler(){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                API_INFO_Check = true;
                Train_StartEnd.array_info = response;
                Train_StartEnd.length = response.length();
                Load_TrainTime_api();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONArray error) {
                Log.d("InfoApi","Fail");
            }
        });

      //return Info_length;
    }

    private void Load_TrainTime_api(){
        String urlString2 = "http://ptx.transportdata.tw/MOTC/v2/Rail/TRA/LiveTrainDelay?$format=JSON";
        //Log.d("Hot Text:", urlString2);
        AsyncHttpClient client2 = new AsyncHttpClient();
        client2.get(urlString2, new JsonHttpResponseHandler() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                mDialog.dismiss();
                API_Time_Check = true;
                Train_Delay.array_info = response;
                Train_Delay.length = response.length();
                Sent_Info();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONArray error) {
                mDialog.dismiss();
                Log.d("TimeApi","Fail");
            }
        });
    }

    private void findViews() {
        //tx1 = (TextView)findViewById(R.id.textView2);
        //tx2 = (TextView)findViewById(R.id.textView3);
        //tx3 = (TextView)findViewById(R.id.textView4);
        mListView = (ListView) findViewById(R.id.main_listview);


    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void Sent_Info(){
        //Log.d("Train_StartEnd.array",Train_StartEnd.array_info.optJSONObject(0).optJSONObject("DailyTrainInfo").optString("TrainNo"));
        TrainNo = new String[Train_StartEnd.length];
        TrainClassificationID = new String[Train_StartEnd.length];
        StartTime = new String[Train_StartEnd.length];
        EndTime = new String[Train_StartEnd.length];
        TrainType = new String[Train_StartEnd.length];
        DelayTime = new int[Train_StartEnd.length];
        for(int i = 0; i < Train_StartEnd.length; i++){
            DelayTime[i] = 0;
        }
        Cost_time = new int[Train_StartEnd.length];
        Insert_sort = new int[Train_StartEnd.length];
        DeleteTime = new int[Train_StartEnd.length];
        int check = Boolean.compare(API_INFO_Check,API_Time_Check);
        if(check == 0){
            //Log.d("BothTrue:",String.valueOf( Train_StartEnd.length));
            for(int i = 0 ; i <  Train_StartEnd.length; i++){
                //Log.d("DeleteTime_Origino_NEW", String.valueOf(DeleteTime[i]));
                TrainNo[i] = Train_StartEnd.array_info.optJSONObject(i).optJSONObject("DailyTrainInfo").optString("TrainNo");
                TrainClassificationID[i] = Train_StartEnd.array_info.optJSONObject(i).optJSONObject("DailyTrainInfo").optString("TrainClassificationID");
                StartTime[i] = Train_StartEnd.array_info.optJSONObject(i).optJSONObject("OriginStopTime").optString("DepartureTime");
                EndTime[i]= Train_StartEnd.array_info.optJSONObject(i).optJSONObject("DestinationStopTime").optString("ArrivalTime");
                //Log.d("\nStartTime",StartTime[i]);
                Cost_time[i] = (Integer.valueOf(EndTime[i].charAt(0)) - '0') * 600 + (Integer.valueOf(EndTime[i].charAt(1)) - '0') * 60 + (Integer.valueOf(EndTime[i].charAt(3)) - '0') * 10 + (Integer.valueOf(EndTime[i].charAt(4)) - '0');
                if((Integer.valueOf(EndTime[i].charAt(0)- '0') == 0) && (Integer.valueOf(EndTime[i].charAt(1)- '0') == 0)){
                    Cost_time[i] += 1440;
                    Log.d("Test", EndTime[i]);
                }
                if((Integer.valueOf(EndTime[i].charAt(0)- '0') == 0) && (Integer.valueOf(EndTime[i].charAt(1)- '0') == 1)){
                    Cost_time[i] += 1500;
                }
                DeleteTime[i] = (Integer.valueOf(StartTime[i].charAt(0)) - '0') * 600 + (Integer.valueOf(StartTime[i].charAt(1)) - '0') * 60 + (Integer.valueOf(StartTime[i].charAt(3)) - '0') * 10 + (Integer.valueOf(StartTime[i].charAt(4)) - '0');
                /*Log.d("char1", String.valueOf((Integer.valueOf(StartTime[i].charAt(0)) - '0') * 600));
                Log.d("char2", String.valueOf((Integer.valueOf(StartTime[i].charAt(1)) - '0') * 60));
                Log.d("char3", String.valueOf((Integer.valueOf(StartTime[i].charAt(3)) - '0') * 10));
                Log.d("char4", String.valueOf((Integer.valueOf(StartTime[i].charAt(4)) - '0')));
                Log.d("DeleteTime_Origino", String.valueOf(DeleteTime[i]));*/
                if(!DateChange_torf) {
                    for (int j = 0; j < Train_Delay.length; j++) {
                        if (TrainNo[i].equals(Train_Delay.array_info.optJSONObject(j).optString("TrainNo"))) {
                            DelayTime[i] = Train_Delay.array_info.optJSONObject(j).optInt("DelayTime");
                            DeleteTime[i] += DelayTime[i];
                            Cost_time[i] += DelayTime[i];
                            //Log.d("DeleteTime_After", String.valueOf(DeleteTime[i]));
                            break;
                        }
                    }
                }else{
                    for (int j = 0; j < Train_Delay.length; j++) {
                        DelayTime[i] = -1;
                    }
                }
                for(int j = 0; j < 40; j++){
                    if(CheckType_num[j].equals(TrainClassificationID[i])){
                        TrainType[i] = CheckType[j];
                        break;
                    }
                }
            }

            boolean [] tf = new boolean[ Train_StartEnd.length] ;
            for(int i =0; i < Train_StartEnd.length;i++){
                tf[i] = true;
            }
            for(int i = 0; i <  Train_StartEnd.length; i++){
                int maxx = 999999, index = 0;
                for(int j = 0 ; j <  Train_StartEnd.length; j++){
                    if(tf[j] && (Cost_time[j] < maxx)){
                        index = j;
                        maxx = Cost_time[j];
                    }
                }
                Insert_sort[i] = index;
                tf[index] = false;
            }

            Log.d("InfoLength", String.valueOf( Train_StartEnd.length));
            JSONArray Sent_jsonArray = new JSONArray();
            for(int i =0 ; i <  Train_StartEnd.length; i++){
                JSONObject tmpobj = new JSONObject();
                try {
                    tmpobj.put("TrainNo", TrainNo[Insert_sort[i]]);
                    tmpobj.put("TrainType", TrainType[Insert_sort[i]]);
                    tmpobj.put("StartTime", StartTime[Insert_sort[i]]);
                    tmpobj.put("EndTime", EndTime[Insert_sort[i]]);
                    tmpobj.put("DelayTime", DelayTime[Insert_sort[i]]);
                    //Log.d("Time_find\n", String.valueOf(Time_find));
                    //Log.d("DeleteTime\n", String.valueOf(DeleteTime[Insert_sort[i]]));
                    if((Time_find < DeleteTime[Insert_sort[i]])) {
                        Sent_jsonArray.put(tmpobj);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            mAdapter.updateData(Sent_jsonArray);
        }
    }
}


