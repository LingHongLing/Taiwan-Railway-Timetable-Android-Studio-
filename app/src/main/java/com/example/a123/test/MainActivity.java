package com.example.a123.test;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private String[] City = new String[]{"臺北/基隆地區", "桃園地區", "新竹地區", "苗栗地區", "臺中地區", "彰化地區", "南投地區", "雲林地區", "嘉義地區", "臺南地區", "高雄地區", "屏東地區", "臺東地區", "花蓮地區", "宜蘭地區", "平溪/深澳線", "內灣/六家線", "集集線", "沙崙線"};
    //27
    private String[][] Station = new String[][]{
            {"福隆", "貢寮", "雙溪", "牡丹", "三貂嶺", "猴硐", "瑞芳", "四腳亭", "暖暖", "基隆", "三坑", "八堵", "七堵", "百福", "五堵", "汐止", "汐科", "南港", "松山", "臺北", "萬華", "板橋", "浮洲", "樹林", "南樹林", "山佳", "鶯歌"},
            {"桃園", "內壢", "中壢", "埔心", "楊梅", "富岡"},
            {"北湖", "新豐", "竹北", "北新竹", "新竹", "三姓橋", "香山"},
            {"崎頂", "竹南", "談文", "大山", "後龍", "龍港", "白沙屯", "新埔", "通霄", "苑裡", "造橋", "豐富", "苗栗", "南勢", "銅鑼", "三義"},
            {"日南", "大甲", "台中港", "清水", "沙鹿", "龍井", "大肚", "追分", "泰安", "后里", "豐原", "潭子", "太原", "臺中", "大慶", "烏日", "新烏日", "成功"},
            {"彰化", "花壇", "大村", "圓林", "永靖", "社頭", "田中", "二水"},
            {"源泉", "濁水", "龍泉", "集集", "水里", "車埕"},
            {"林內", "石榴", "斗六", "斗南", "石龜"},
            {"大林", "民雄", "嘉北", "嘉義", "水上", "南靖"},
            {"後壁", "新營", "柳營", "林鳳營", "隆田", "拔林", "善化", "南科", "新市", "永康", "大橋", "臺南", "保安", "仁德", "中洲", "長榮大學", "沙崙"},
            {"大湖", "路竹", "岡山", "橋頭", "楠梓", "新左營", "左營", "高雄", "鳳山", "後庄", "九曲堂"},
            {"六塊厝", "屏東", "歸來", "麟洛", "西勢", "竹田", "潮州", "崁頂", "南州", "鎮安", "林邊", "佳冬", "東海", "枋寮", "加祿", "內獅", "坊山"},
            {"古莊", "大武", "瀧溪", "金崙", "太麻里", "知本", "康樂", "臺東", "山里", "鹿野", "瑞源", "瑞和", "關山", "海端", "池上"},
            {"富里", "東竹", "東里", "玉里", "三民", "瑞穗", "富源", "大富", "光復", "萬榮", "鳳林", "南平", "豐田", "壽豐", "平和", "志學", "吉安", "花蓮", "北埔", "景美", "新城", "崇德", "和仁", "和平"},
            {"漢本", "武塔", "南澳", "東澳", "永樂", "蘇澳", "蘇澳新", "新馬", "冬山", "羅東", "中里", "二結", "宜蘭", "四城", "礁溪", "頂埔", "頭城", "外澳", "龜山", "大溪", "大里", "石城"},
            {"菁桐", "平溪", "嶺腳", "望古", "十分", "大華", "三貂嶺", "海科館"},
            {"新竹", "北新竹", "千甲", "新莊", "竹中", "六家", "上員", "榮華", "竹東", "橫山", "九讚頭", "合興", "富貴", "內灣"},
            {"二水", ",源泉", "濁水", "龍泉", "集集", "水里", "車埕"},
            {"長榮大學", "沙崙"}
    };
    private Context context;
    private Spinner Start_City_spinner;
    private Spinner End_City_spinner;
    private Spinner Start_Station_spinner;
    private Spinner End_Station_spinner;
    private Button Choose_Time_bt, Choose_Date_bt, ChangeStation_bt, send_bt;
    private EditText Choose_Time_text, Choose_Date_text;

    ProgressDialog mDialog;
    ArrayAdapter<String> adapter_sc;
    ArrayAdapter<String> adapter_ss;
    ArrayAdapter<String> adapter_ec;
    ArrayAdapter<String> adapter_es;
    String Start_Station;
    String End_Station;
    int Start_City_num, Start_Station_num = 0, End_City_num, End_Station_num = 0;
    JSONArray Station_All;
    int[] StationID;
    String[] StationName;
    TextView tx;
    int Station_length;
    String Sent_Date;
    int Sent_Time;
    boolean bt_torf1 = false, bt_torf2 = false, DateChange_torf = false;
    int mHour, mMinute;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        findViews();
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Loading Data...");
        loadData();
        Init();
        context = this;
        adapter_sc = new ArrayAdapter<String>(this, R.layout.myspinner, City);
        adapter_sc.setDropDownViewResource(R.layout.myspinner);
        Start_City_spinner.setAdapter(adapter_sc);
        Start_City_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Start_City_num = Start_City_spinner.getSelectedItemPosition();
                // Start_City_num = Start_City_spinner.getSelectedItemPosition();

                adapter_ss = new ArrayAdapter<String>(context, R.layout.myspinner, Station[position]);
                adapter_ss.setDropDownViewResource(R.layout.myspinner);
                Start_Station_spinner.setAdapter(adapter_ss);
                if (bt_torf1) {
                    Start_Station_spinner.setSelection(Start_Station_num);
                    bt_torf1 = false;
                    Start_Station_num = 0;
                    //Log.d("Change","ChangeStart");
                } else {
                    Start_Station_spinner.setSelection(Start_Station_num);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Start_Station_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //Start_Station_num = position;
                Start_Station = Start_Station_spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adapter_ec = new ArrayAdapter<String>(this, R.layout.myspinner, City);
        adapter_ec.setDropDownViewResource(R.layout.myspinner);
        End_City_spinner.setAdapter(adapter_ec);
        End_City_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //End_City_num= End_City_spinner.getSelectedItemPosition();

                adapter_es = new ArrayAdapter<String>(context, R.layout.myspinner, Station[position]);
                adapter_es.setDropDownViewResource(R.layout.myspinner);
                End_Station_spinner.setAdapter(adapter_es);
                if (bt_torf2) {
                    End_Station_spinner.setSelection(End_Station_num);
                    bt_torf2 = false;
                    End_Station_num = 0;
                } else {
                    End_Station_spinner.setSelection(End_Station_num);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        End_Station_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //End_Station_num = position;
                End_Station = End_Station_spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ChangeStation_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Start_City_num = End_City_spinner.getSelectedItemPosition();
                End_City_num = Start_City_spinner.getSelectedItemPosition();
                Start_Station_num = End_Station_spinner.getSelectedItemPosition();
                End_Station_num = Start_Station_spinner.getSelectedItemPosition();
                String tmp_s = Start_Station;
                Start_Station = End_Station;
                End_Station = tmp_s;
                bt_torf2 = true;
                bt_torf1 = true;


                if (Start_City_num != End_City_num) {

                    Start_City_spinner.setSelection(Start_City_num);
                    //Start_Station_spinner.setSelection(Start_Station_num);

                    End_City_spinner.setSelection(End_City_num);
                    //End_Station_spinner.setSelection(End_Station_num);
                    //Start_Station_spinner.setSelection(Start_Station_num);
                    //End_Station_spinner.setSelection(End_Station_num);
                } else {

                    Start_Station_spinner.setSelection(Start_Station_num);
                    End_Station_spinner.setSelection(End_Station_num);
                }
            }
        });

        Choose_Time_bt.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        Choose_Date_bt.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        send_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int Sent_StartStation_num = 0, Sent_EndStation_num = 0;
                for (int i = 0; i < Station_length; i++) {
                    if (Start_Station.equals(StationName[i])) {
                        Sent_StartStation_num = i;
                    }
                    if (End_Station.equals(StationName[i])) {
                        Sent_EndStation_num = i;
                    }
                }

                //new一個intent物件，並指定Activity切換的class
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Main2Activity.class);
                if (DateChange_torf) {
                    Log.d("DateChange", "true");
                } else {
                    Log.d("DateChange", "false");
                }
                //new一個Bundle物件，並將要傳遞的資料傳入
                Log.d("Sent_Time", String.valueOf(Sent_Time));
                Bundle bundle = new Bundle();
                bundle.putInt("StartStationID", StationID[Sent_StartStation_num]);
                bundle.putInt("EndStationID", StationID[Sent_EndStation_num]);
                bundle.putInt("Time", Sent_Time);
                bundle.putString("Date", Sent_Date);
                bundle.putBoolean("DateChange", DateChange_torf);
                //將Bundle物件assign給intent
                intent.putExtras(bundle);

                //切換Activity
                startActivity(intent);
                //MainActivity.this.finish();
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void findViews() {
        Start_City_spinner = (Spinner) findViewById(R.id.Start_City_spi);
        End_City_spinner = (Spinner) findViewById(R.id.End_City_spi);
        Start_Station_spinner = (Spinner) findViewById(R.id.Start_Station_spi);
        End_Station_spinner = (Spinner) findViewById(R.id.End_Station_spi);
        ChangeStation_bt = (Button) findViewById(R.id.Change_bt);
        Choose_Date_bt = (Button) findViewById(R.id.Choose_Date_bt);
        Choose_Time_bt = (Button) findViewById(R.id.Choose_Time_bt);
        send_bt = (Button) findViewById(R.id.send_bt);
        Choose_Date_text = (EditText) findViewById(R.id.Date_text);
        Choose_Time_text = (EditText) findViewById(R.id.Time_text);
        //tx = (TextView) findViewById(R.id.textView);
    }

    private void loadData() {
        mDialog.show();
        String urlString = "http://ptx.transportdata.tw/MOTC/v2/Rail/TRA/Station?%24format=JSON";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(urlString, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //Log.d("Hot Text:", response.toString());
                mDialog.dismiss();
                try {
                    Station_All = new JSONArray(response.toString());
                    Station_length = Station_All.length();
                    StationID = new int[Station_length];
                    StationName = new String[Station_length];
                    for (int i = 0; i < Station_length; i++) {
                        StationID[i] = Station_All.getJSONObject(i).getInt("StationID");
                        StationName[i] = Station_All.getJSONObject(i).getJSONObject("StationName").getString("Zh_tw");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONArray error) {
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(),
                        //"Error: " + statusCode + " " + e.getMessage(),
                        "資料載入錯誤\n請確認網路狀況",
                        Toast.LENGTH_LONG).show();
                // Log error message
                Log.e("Hot Text:", statusCode + " " + e.getMessage());
            }

        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showDatePickerDialog() {
        // 設定初始日期
        final int mYear, mMonth, mDay;
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // 跳出日期選擇器
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        long now_time = (mYear * 360) + ((mMonth + 1) * 30) + mDay;
                        long choose_time = (year * 360) + ((monthOfYear + 1) * 30) + dayOfMonth;
                        if ((choose_time - now_time) > 60) {
                            Toast.makeText(getApplicationContext(),
                                    "尚未提供查詢超過60天之火車資訊查詢服務", Toast.LENGTH_LONG).show();
                            Choose_Date_text.setText(mYear + "-" + (mMonth + 1) + "-"
                                    + mDay);
                        } else if ((choose_time - now_time) < 0) {
                            Toast.makeText(getApplicationContext(),
                                    "尚未提供查詢過去日期之服務", Toast.LENGTH_LONG).show();
                            Choose_Date_text.setText(mYear + "-" + (mMonth + 1) + "-"
                                    + mDay);
                        } else {
                            Choose_Date_text.setText(year + "-" + (monthOfYear + 1) + "-"
                                    + dayOfMonth);
                            Sent_Date = year + "-";
                            if (monthOfYear < 9) {
                                Sent_Date += "0" + (monthOfYear + 1) + "-";
                            } else {
                                Sent_Date += (monthOfYear + 1) + "-";
                            }
                            if (dayOfMonth < 10) {
                                Sent_Date += "0" + dayOfMonth;
                            } else {
                                Sent_Date += dayOfMonth;
                            }
                            Choose_Time_text.setText("5:00");
                            Sent_Time = 300;
                            DateChange_torf = true;
                        }

                    }
                }, mYear, mMonth, mDay);
        dpd.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showTimePickerDialog() {
        // 設定初始時間

        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // 跳出時間選擇器
        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        // 完成選擇，顯示時間
                        Sent_Time = (hourOfDay * 60) + minute;
                        Choose_Time_text.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        tpd.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void Init() {
        int mYear, mMonth, mDay, mHour, mMinute;
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        Start_Station = "福隆";
        End_Station = "福隆";
        Sent_Time = (mHour * 60) + mMinute;
        Choose_Date_text.setText(mYear + "-" + (mMonth + 1) + "-"
                + mDay);
        Choose_Time_text.setText(mHour + ":" + mMinute);
        Sent_Date = mYear + "-";
        if (mMonth < 9) {
            Sent_Date += "0" + (mMonth + 1) + "-";
        } else {
            Sent_Date += (mMonth + 1) + "-";
        }
        if (mDay < 10) {
            Sent_Date += "0" + mDay;
        } else {
            Sent_Date += mDay;
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        AppIndex.AppIndexApi.start(client2, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client2, getIndexApiAction());
        client2.disconnect();
    }
}
