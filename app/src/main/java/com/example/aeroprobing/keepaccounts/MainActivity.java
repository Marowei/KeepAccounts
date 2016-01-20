package com.example.aeroprobing.keepaccounts;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
/**
 * @file MainActivity.java
 * @version 1.0.0
 * @author 浩威
 *
 * @version 1.0.0 主頁面功能完成
 */
public class MainActivity extends FragmentActivity {
    /***宣告***/
    private int itemIndex=0;//用來索引button項目
    public int year,month,day;//存放日期的全域變數
    private int[] indexArray=new int[]{ //圖片按鈕R.id
            R.id.btnEat,
            R.id.btnClothes,
            R.id.btnHouse,
            R.id.btnCar,
            R.id.btnBook,
            R.id.btnGame,
            R.id.btnGas,
            R.id.btnBandCard,
            R.id.btnHealth,
            R.id.btnMoney};
    String[] itemName=new String[]{ //圖片按鈕對應名稱
            "伙食費用",
            "服飾費用",
            "生活費",
            "交通費",
            "學雜費",
            "娛樂開銷費",
            "水電瓦斯費",
            "信用卡支出",
            "醫療費用",
            "收入"};
    public String strYear,strMonth,strDay;//顯示用的日期字串
    private Button btnChangeDate,btnAdd,btnList;
    private Button[] btnItem=new Button[10];
    private EditText editRemark,editMoney;
    private TextView textDate,textItem;
    DataBase dataBase;
    SQLiteDatabase dbrw;
    static String table="itemTable";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUI();//連結元件
        /***建造資料庫***/
        dataBase=new DataBase(this);
        dbrw=dataBase.getWritableDatabase();
        Toast.makeText(this,"資料庫建立 ? "+dbrw.isOpen()+" , 版本 : "+dbrw.getVersion(),Toast.LENGTH_SHORT).show();
        /***取得日期***/
        Calendar TodayDate = Calendar.getInstance();    //透過Calendar取的資料
        year = TodayDate.get(Calendar.YEAR);       //取得年(今日)
        month  = TodayDate.get(Calendar.MONTH) + 1;  //取得月(今日)
        day = TodayDate.get(Calendar.DAY_OF_MONTH);//取得日(今日)
        strYear=DateFix(year);                     //Int轉字串並添加補0機制
        strMonth=DateFix(month);
        strDay=DateFix(day);
        textDate.setText(strYear+"年"+strMonth+"月"+strDay+"日");
        /***按鈕區塊事件監聽***/
        for(int index=0;index<indexArray.length;index++)
            btnItem[index].setOnClickListener(btnListener);
        /***新增按鈕***/
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                /***費用及項目不為空值才執行***/
                if(!editMoney.getText().toString().equals("")&&!textItem.getText().toString().equals(""))
                {
                    long id = 0;
                    double price=0.0;
                    String note="",name="";

                    note=editRemark.getText().toString();
                    name=textItem.getText().toString();
                    /***處理 price為 負值 or 非浮點數問題***/
                    try
                    {
                        price= Double.parseDouble(editMoney.getText().toString());
                        if(price<0)
                            throw new Exception("負值");
                    }
                    catch (Exception err)
                    {
                        Toast.makeText(MainActivity.this, "費用輸入錯誤" , Toast.LENGTH_SHORT).show();
                        return;
                    }
                    /***建立ContentValues 來做為新增用***/
                    ContentValues cv=new ContentValues();
                    cv.put("item",itemIndex);
                    cv.put("note", note);
                    cv.put("name", name);
                    cv.put("price", price);
                    cv.put("y", year);
                    cv.put("m",month);
                    cv.put("d", day);
                    /***資料項正確則新增***/
                    try
                    {
                        id=dbrw.insert(table, null, cv);
                    }
                    /***資料錯誤返回***/
                    catch (Exception err)
                    {
                        Toast.makeText(MainActivity.this, "新增失敗" , Toast.LENGTH_SHORT).show();
                        return;
                    }
                        Toast.makeText(MainActivity.this, "新增成功", Toast.LENGTH_SHORT).show();
                        editRemark.setText("");
                        editMoney.setText("");
                        textItem.setText("");
                }else
                {
                    Toast.makeText(MainActivity.this, "尚未選擇項目或輸入費用" , Toast.LENGTH_SHORT).show();
                }
            }});
        /***跳頁至List***/
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /***建立Intent MainActivity傳List***/
                Intent toList = new Intent();
                toList.setClass(MainActivity.this, List.class);
                //建立Bundle攜帶資料
                Bundle toListBundle = new Bundle();
                toListBundle.putInt("year", year);
                toListBundle.putInt("month", month);
                toList.putExtras(toListBundle);
                // 執行附帶資料的 Intent
                startActivityForResult(toList, 1);
            }
        });
            /***跳頁至SetDate***/
            btnChangeDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /***建立Intent MainActivity傳SetDate***/
                    Intent toSetDate=new Intent();
                    toSetDate.setClass(MainActivity.this, SetDate.class);
                    //建立Bundle攜帶資料
                    Bundle toSetDateBundle=new Bundle();
                    toSetDateBundle.putString("strYear", strYear);
                    toSetDateBundle.putString("strMonth", strMonth);
                    toSetDateBundle.putString("strDay", strDay);
                    toSetDate.putExtras(toSetDateBundle);
                    // 執行附帶資料的 Intent
                    startActivityForResult(toSetDate, 3);
                }
            });
    }
    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        {
            /***接收SetDate回傳值***/
            if(resultCode==2)
            {
                /***取得Bundle內的資料***/
                Bundle recodeSetDateBundle=data.getExtras();
                year = recodeSetDateBundle.getInt("reYear");
                month = recodeSetDateBundle.getInt("reMonth");
                day=recodeSetDateBundle.getInt("reDay");
                strYear=DateFix(year);
                strMonth=DateFix(month);
                strDay=DateFix(day);
                textDate.setText(strYear+"年"+strMonth+"月"+strDay+"日");

            }
            /***接收List 修改功能的回傳值***/
            if(resultCode==4)
            {
                /***取得Bundle內的資料***/
                Bundle recodeListBundle=data.getExtras();
                textItem.setText("" + recodeListBundle.getString("name"));
                editRemark.setText("" + recodeListBundle.getString("note"));
                editMoney.setText("" + recodeListBundle.getDouble("price"));
                year = recodeListBundle.getInt("reYear");
                month = recodeListBundle.getInt("reMonth");
                day=recodeListBundle.getInt("reDay");
                strYear=DateFix(year);
                strMonth=DateFix(month);
                strDay=DateFix(day);
                textDate.setText(strYear+"年"+strMonth+"月"+strDay+"日");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /***Button區塊監聽事件 實作***/
    private Button.OnClickListener btnListener= new Button.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            //建表

            int indexID=v.getId();
            //搜尋Button並匹配ID至index
            for(int index=0;index<indexArray.length;index++)
            {
                if(indexArray[index]==indexID)
                {
                    itemIndex=index;
                    break;
                }
            }
            //顯示項目
            textItem.setText(itemName[itemIndex]);
        }
    };

    /**
     * @brief 日期補零修正函式 int轉string.
     * @since 1.0.0
     * @param date 初始圖源A.
     * @return 轉換後的日期字串;
     */
    public static String DateFix(int date)
    {
        if (date >= 10)
            return String.valueOf(date);
        else
            return "0" + String.valueOf(date);
    }
    /***關閉資料庫***/
     protected void onStop()
     {
         super.onStop();

     }
    protected void onDestroy()
    {
      super.onDestroy();
        dbrw.close();
    }
    /**
     * @brief 設定元件連結.
     * @since 1.0.0
     */
    public void setUI()
    {
        for(int index=0; index<indexArray.length; index++)
            btnItem[index] = (Button) findViewById(indexArray[index]);
        btnAdd=(Button)findViewById(R.id.btnAdd);
        btnList=(Button)findViewById(R.id.btnList);
        btnChangeDate=(Button)findViewById(R.id.btnChangeDate);
        editMoney=(EditText)findViewById(R.id.editMoney);
        editRemark=(EditText)findViewById(R.id.editRemark);
        textDate=(TextView)findViewById(R.id.textDate);
        textItem=(TextView)findViewById(R.id.textItem);


    }

}
