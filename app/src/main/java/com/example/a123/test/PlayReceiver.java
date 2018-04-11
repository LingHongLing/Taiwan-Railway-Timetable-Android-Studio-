package com.example.a123.test;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 123 on 2017/1/3.
 */

public class PlayReceiver extends BroadcastReceiver {
    long vT[]={300,100,300,100};
    int DelayTime = 0;
    String TrainNo, desc;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bData = intent.getExtras();
        NotificationManager noMgr = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent call = new Intent(context, MainActivity.class);
        //非必要,可以利用intent傳值
        call.putExtra("notiId", 1);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, call, 0);

        TrainNo = bData.getString("TrainNo");
        int icon = R.drawable.common_google_signin_btn_icon_dark;
        String urlString2 = "http://ptx.transportdata.tw/MOTC/v2/Rail/TRA/LiveTrainDelay?$format=JSON";
        //Log.d("Hot Text:", urlString2);
        AsyncHttpClient client2 = new AsyncHttpClient();
        client2.get(urlString2, new JsonHttpResponseHandler() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                for (int j = 0; j < response.length(); j++) {
                    if (TrainNo.equals(response.optJSONObject(j).optString("TrainNo"))) {
                        DelayTime = response.optJSONObject(j).optInt("DelayTime");
                        //Log.d("DeleteTime_After", String.valueOf(DeleteTime[i]));
                        break;
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONArray error) {

                Log.d("TimeApi","Fail");
            }
        });
        String title = "火車時刻通知";

        if(DelayTime == 0){
            desc = "車次:" + TrainNo + " 將準時到達";
        }else{
            desc = "車次:" + TrainNo + " 誤點" + DelayTime + "分";
        }
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = new Notification.Builder(context)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(desc)
                .setContentIntent(pIntent)
                .setSmallIcon(icon)
                .setWhen(System.currentTimeMillis())
                .setSound(soundUri)
                .setVibrate(vT)
                .build();
        noMgr.notify(1, notification);

    }
}
