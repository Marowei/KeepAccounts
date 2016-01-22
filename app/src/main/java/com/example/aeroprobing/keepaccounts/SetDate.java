package com.example.aeroprobing.keepaccounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
/**
 * @file SetDate.java
 * @version 1.0.0
 * @author 浩威
 *
 * @version 1.0.0 修改日期頁面
 */
public class SetDate extends ActionBarActivity {
    /***宣告***/
    String strYear,strMonth,strDay;
    int year,month,day;
    TextView textSetDate;
    Button btnSetDate;
    DatePicker dpSetDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_date);
        setUI();//設定元件
        /***接收MainActivity傳來的***/
        Intent getDate=this.getIntent();
        Bundle getDateBundle=getDate.getExtras();
        strYear=getDateBundle.getString("strYear");
        strMonth=getDateBundle.getString("strMonth");
        strDay=getDateBundle.getString("strDay");
        textSetDate.setText(strYear+"月"+strMonth+"月"+strDay+"日");
        dpSetDate.init(Integer.parseInt(strYear),Integer.parseInt(strMonth)-1,Integer.parseInt(strDay),//設定年月日初始值
                /***DatePicker更改觸發事件***/
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int dpyear, int dpmonth,//日期變更事件
                                              int dpday) {
                        year=dpyear;
                        month=dpmonth + 1;
                        day=dpday;
                        strYear = DateFix(year);
                        strMonth  = DateFix(month);
                        strDay  = DateFix(day);
                        textSetDate.setText(strYear+"月"+strMonth+"月"+strDay+"日");
                    }
                });
        btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /***建立Intent SetDate傳 MainActivity***/
                Intent returnMain=new Intent();
                Bundle returnMainBundle=new Bundle();
                returnMainBundle.putInt("reYear", year);
                returnMainBundle.putInt("reMonth",month);
                returnMainBundle.putInt("reDay",day);
                returnMain.putExtras(returnMainBundle);
                setResult(2, returnMain);
                finish();

            }});

    }
    /**
     * @brief 日期補零修正函式 int轉string.
     * @since 1.0.0
     * @param date 初始圖源A.
     * @return 轉換後的日期字串;
     */
    private static String DateFix(int date)
    {
        if (date >= 10)
            return String.valueOf(date);
        else
            return "0" + String.valueOf(date);
    }
    /**
     * @brief 設定元件連結.
     * @since 1.0.0
     */
    public void setUI() {
        textSetDate=(TextView)findViewById(R.id.textSetDate);
        btnSetDate=(Button)findViewById(R.id.btnSetDate);
        dpSetDate=(DatePicker)findViewById(R.id.dpSetDate);
    }

}
