package com.example.a123.test;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.Calendar;



public class SettingActivity extends AppCompatActivity {
    String Sent_TrainNo;
    String Sent_TrainType;
    String Sent_StartTime;
    String Sent_EndTime;
    String Sent_Date;
    int Sent_DelayTime,myear,mmonth,mday,mhour,mminute;
    Context context;
    Calendar calendar;
    int StartStationID, EndStationID;

    AlarmManager am;
    PendingIntent pi;
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 設定這個頁面XML的layout名稱
        setContentView(R.layout.activity_setting);

        // 設定要顯示回上一頁的按鈕
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        CheckBox SettingWillGo = (CheckBox)findViewById(R.id.checkBox);
        //CheckBox SettingWakeup = (CheckBox)findViewById(R.id.checkBox2);

        context = this;
        calendar = Calendar.getInstance();
        Bundle args = this.getIntent().getExtras();
        StartStationID = args.getInt("StartStation");
        EndStationID = args.getInt("EndStation");
        Sent_TrainNo = args.getString("TrainNo");
        Sent_TrainType = args.getString("TrainType");
        Sent_StartTime = args.getString("StartTime");
        Sent_EndTime = args.getString("EndTime");
        Sent_Date = args.getString("Date");
        Sent_DelayTime = args.getInt("DelayTime");

        SettingWillGo.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    myear = Integer.valueOf(Sent_Date.substring(0,4));
                    mmonth = Integer.valueOf(Sent_Date.substring(5,7)) - 1;
                    if(mmonth == -1){
                        mmonth = 11;
                        myear -= 1;
                    }
                    mday = Integer.valueOf(Sent_Date.substring(8,10));
                    mhour = Integer.valueOf(Sent_StartTime.substring(0,2)) - 1;
                    if(mhour < 0){
                        mhour =23;
                        mday -= 1;
                    }
                    mminute = Integer.valueOf(Sent_StartTime.substring(3,5));
                    calendar.set(myear, mmonth, mday, mhour, mminute, 0);
                    long whattime = calendar.getTimeInMillis();
                    Intent intent = new Intent(context, PlayReceiver.class);
                    intent.putExtra("TrainNo",Sent_TrainNo);
                    pi = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_ONE_SHOT);

                    am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    am.set(AlarmManager.RTC_WAKEUP, whattime, pi);

                }else{
                    am.cancel(pi);
                }
            }
        });

        /*SettingWakeup.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    myear = Integer.valueOf(Sent_Date.substring(0,4));
                    mmonth = Integer.valueOf(Sent_Date.substring(5,7)) - 1;
                    if(mmonth == -1){
                        mmonth = 11;
                        myear -= 1;
                    }
                    mday = Integer.valueOf(Sent_Date.substring(8,10));
                    mhour = Integer.valueOf(Sent_EndTime.substring(0,2));
                    mminute = Integer.valueOf(Sent_EndTime.substring(3,5)) -5 + Sent_DelayTime;
                    Log.d("Hour",String.valueOf(mhour));
                    Log.d("Minute",String.valueOf(mminute));
                    if(mminute < 0){
                        mminute += 60;
                        mhour -= 1;
                    }else if(mminute > 60){
                        mminute -= 60;
                        mhour += 1;
                    }
                    calendar.set(myear, mmonth, mday, mhour, mminute, 0);
                    long whattime = calendar.getTimeInMillis();
                    Intent call = new Intent(context, MainActivity.class);
                    pi2 = PendingIntent.getBroadcast(context, 0, call, 0);

                    am2 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                    am2.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis(), pi2);
                }else{
                    am2.cancel(pi2);
                }
            }
        });
        */
    }
}
