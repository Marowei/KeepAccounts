package com.example.aeroprobing.keepaccounts;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
/**
 * @file List.java
 * @version 1.0.0
 * @author 浩威
 *
 * @version 1.0.0 清單頁面功能完成
 */
public class List extends ActionBarActivity {
    /***宣告區塊***/
    int year,month,radioCheck=2;
    Button btnBack,btnSearch;
    TextView textInput,textOutput,textAverage,textMoneyTotal,textTime;
    Double totalMoney=0.0,averageMoney=0.0,inputMoney=0.0,outputMoney=0.0;
    Spinner spinnerItem;
    ListView listItem;
    RadioButton rbY,rbM,rbAll;
    RadioGroup rg;

    class DataPackage{
        public Long counter;
        public int index;
        public String name;
        public String note;
        public Double price;
        public int year;
        public int month;
        public int day;
    }
    private CharSequence[] itemData={"全部","伙食費用","服飾費用","生活費","交通費","學雜費","娛樂開銷費","水電瓦斯費","信用卡支出","醫療費用","收入"};
    private String[] data=new String[]{"伙食費用","服飾費用","生活費","交通費","學雜費","娛樂開銷費","水電瓦斯費","信用卡支出","醫療費用","收入"};
    DataPackage[] dataPackageAry;

    CharSequence item_opt;
    int item_optNum;
    Cursor listViewCursor=null;
    DataBase dataBase;
    SQLiteDatabase dbrw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setUI();
        //建立資料庫
        dataBase=new DataBase(this);
        dbrw=dataBase.getWritableDatabase();

        /***接收MainActivity傳來的***/
        Intent getDate=this.getIntent();
        Bundle getDateBundle=getDate.getExtras();
        year=getDateBundle.getInt("year");
        month=getDateBundle.getInt("month");
        textTime.setText("顯示 "+year+" 年 "+month+" 月");
        /***Radio Group選擇事件***/
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.radioButtonAll://Radio check到 全部
                        rbAll.setChecked(true);
                        rbY.setChecked(false);
                        rbM.setChecked(false);
                        radioCheck=0;
                        textTime.setText("顯示全部");
                        break;
                    case R.id.radioButtonY://Radio check到 年
                        rbAll.setChecked(false);
                        rbY.setChecked(true);
                        rbM.setChecked(false);
                        radioCheck=1;
                        textTime.setText("顯示 "+year+" 年");
                        break;
                    case R.id.radioButtonM://Radio check到 月
                        rbAll.setChecked(false);
                        rbY.setChecked(false);
                        rbM.setChecked(true);
                        radioCheck=2;
                        textTime.setText("顯示 "+year+" 年 "+month+" 月");
                        break;
                }
            }
        });
        /***ListView選擇事件***/
        listItem.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listViewCursor.moveToPosition(position);//游標指向該資料
            }
        });
        /***搜尋按鈕***/
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbSearch();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /***建立 Intent List 傳 MainActivity***/
                Intent returnMain = new Intent();
                setResult(3, returnMain);
                finish();
            }
        });

        /***Spinner 建立Adapter***/
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_dropdown_item,itemData);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//採用單項資料顯示
        spinnerItem.setAdapter(adapter);
        /***Spinner選擇事件***/
        spinnerItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            /***選擇到執行***/
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item_opt=itemData[position];
                item_optNum=position-1;
            }
            /***尚未選擇***/
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("no_choose", "請選擇項目");
            }
        });
    }
    /***CONTEXT_MENU***/
    protected static final int MENU_DELECT= Menu.FIRST;
    protected static final int MENU_UPDATE= Menu.FIRST+1;
    protected static final int MENU_BACK= Menu.FIRST+2;
    @Override
    /***創建ContextMenu選項***/
    public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu,v,menuInfo);
        if(v==listItem)
        {
            menu.add(0,MENU_DELECT,1,"刪除");
            menu.add(0,MENU_UPDATE,2,"更新");
            menu.add(0,MENU_BACK,3,"返回");
        }
    }
    @Override
    /***ContextMenu選擇事件***/
    public boolean onContextItemSelected(MenuItem mItem)
    {
        if(mItem.getMenuInfo() instanceof AdapterView.AdapterContextMenuInfo)
        {
            AdapterView.AdapterContextMenuInfo menuInfo=(AdapterView.AdapterContextMenuInfo)mItem.getMenuInfo();
            DataPackage dataPackage = dataPackageAry[menuInfo.position];
            long menu_id=dataPackage.counter;
            switch (mItem.getItemId())
            {   /***Context選擇刪除事件***/
                case MENU_DELECT:

                    if(dbDelect(menu_id))
                        dbSearch();
                    else
                        Toast.makeText(List.this, "找不到該筆資料" , Toast.LENGTH_SHORT).show();
                    Toast.makeText(List.this, "刪除成功" , Toast.LENGTH_SHORT).show();
                    break;
                /***Context選擇更新事件***/
                case MENU_UPDATE:
                    Toast.makeText(List.this, "更新"+dataPackage.counter , Toast.LENGTH_SHORT).show();
                    if (dbDelect(menu_id))
                    {
                        /***建立Intent 將回傳值傳回MainActivity頁面***/
                        Intent returnMain=new Intent();
                        /***建立Bundle 攜帶回傳資料***/
                        Bundle returnMainBundle=new Bundle();
                        returnMainBundle.putString("name", dataPackage.name);
                        returnMainBundle.putString("note", dataPackage.note);
                        returnMainBundle.putDouble("price", dataPackage.price);
                        returnMainBundle.putInt("reYear", dataPackage.year);
                        returnMainBundle.putInt("reMonth", dataPackage.month);
                        returnMainBundle.putInt("reDay", dataPackage.day);
                        returnMain.putExtras(returnMainBundle);
                        setResult(4, returnMain);
                        finish();
                    }
                    break;
                /***Context選擇返回事件***/
                case MENU_BACK:
                    break;
            }
        }
        return super.onContextItemSelected(mItem);
    }
    /**
     * @brief 刪除itemTable的某筆資料 WHERE _id.
     * @since 1.0.0
     * @param id 想刪除資料的_id對應值.
     */
    public boolean dbDelect(long id)
    {
        int count=0;
        count=dbrw.delete("itemTable","_id='"+id+"'",null);
        if (count>0)
            return true;
        else
            return false;
    }
    /**
     * @brief 資料庫搜尋功能，並計算收入支出之總額及平均.
     * @since 1.0.0
     */
    public void dbSearch()
    {
        inputMoney=0.0;
        outputMoney=0.0;
        choseSerach(radioCheck);
        UpdateAdapter(listViewCursor);
        /***計算平均金額、總金額***/
        if(listViewCursor.getCount()>0) {
            totalMoney = inputMoney - outputMoney;
            averageMoney = totalMoney / listViewCursor.getCount();
            BigDecimal BigD = new BigDecimal(averageMoney);
            BigD = BigD.setScale(4, BigDecimal.ROUND_HALF_UP);// 小數後面四位, 四捨五入
            averageMoney = BigD.doubleValue();
            textInput.setText(String.valueOf(inputMoney));
            textOutput.setText(String.valueOf(outputMoney));
            if (totalMoney >= 0)
                textMoneyTotal.setText("總收入: " + totalMoney);
            else if(totalMoney < 0)
                textAverage.setText("總支出: " + (-totalMoney));
            if (averageMoney >= 0)
                textAverage.setText("\t平均收入: " + averageMoney);
            else
                textAverage.setText("\t平均支出: " + (-averageMoney));
        }else
        {
            textInput.setText("");
            textOutput.setText("");
            textAverage.setText("");
            textMoneyTotal.setText("");
        }
    }
    /***更新ListView的Adapter***/
    /**
     * @brief 根據Spinner及check條件，決定資料庫搜尋匹配辦法.
     * @since 1.0.0
     * @param check check選擇項目(全部、年、月).
     */
    public void choseSerach(int check) {
        String checkIf = null;
        String[] checkValue=null;
        String checkItemIf=null;
        String[] checkItemValue=null;
        String item_str = "" + item_optNum;
        if (check==0)
        {
            checkItemIf ="item = ?";
            checkItemValue=new String[]{item_str};
        }else if(check==1)
        {
            checkIf="y = ?";
            checkValue=new String[]{String.valueOf(year)};
            checkItemIf ="item = ? AND y = ?";
            checkItemValue=new String[]{item_str, "" + String.valueOf(year)};
        }else if(check==2)
        {
            checkIf="y = ? AND m = ?";
            checkValue=new String[]{String.valueOf(year), String.valueOf(month)};
            checkItemIf ="item = ? AND y = ? AND m = ?";
            checkItemValue=new String[]{item_str, "" + String.valueOf(year), "" + String.valueOf(month)};
        }

            listViewCursor = null;
            if (item_opt == "全部") {
                listViewCursor = dbrw.query(
                        "itemTable",
                        new String[]{"_id", "item", "name", "note", "price", "y", "m", "d"},
                        checkIf,
                        checkValue,
                        null,
                        null,
                        "price" + " DESC",
                        null);
            } else if (item_optNum >= 0 && item_optNum <= 9)//伙食費用 ~收入的判斷條件
            {

                listViewCursor = dbrw.query(
                        "itemTable",
                        new String[]{"_id", "item", "name", "note", "price", "y", "m", "d"},
                        checkItemIf,
                        checkItemValue,
                        null,
                        null,
                        "price" + " DESC",
                        null);
            } else
                Toast.makeText(List.this, "尚未選擇搜尋方法", Toast.LENGTH_SHORT).show();

    }
    /**
     * @brief 並取出Cursor資料，並更新至ListView.
     * @since 1.0.0
     * @param cursor 輸入游標(此用資料庫搜尋指定).
     */
    public void UpdateAdapter(Cursor cursor)
    {
        if(cursor != null && cursor.getCount()>=0)
        {
            String[] from=new String[]{"name","note","price","y","m","d"};
            SimpleCursorAdapter adapter=new SimpleCursorAdapter(this,R.layout.btn_listview_layout,
                    cursor,from,
                    new int[]{R.id.txtItem,R.id.txtNote,R.id.txtPrice,R.id.txtYear,R.id.txtMonth,R.id.txtDay},0);
            int len = listViewCursor.getCount();
            if (len>0)
            {
                /***動態配置記憶體***/
                dataPackageAry = new DataPackage[len];
                listViewCursor.moveToFirst();//將游標移動至 佇列第一個
                int i=0;
                do
                {
                    /***將資料逐一讀出***/
                    DataPackage dataPackage;
                    dataPackage = new DataPackage();
                    dataPackage.counter = listViewCursor.getLong(0);
                    dataPackage.index = listViewCursor.getInt(1);
                    dataPackage.name = listViewCursor.getString(2);
                    dataPackage.note = listViewCursor.getString(3);
                    dataPackage.price = listViewCursor.getDouble(4);
                    dataPackage.year = listViewCursor.getInt(5);
                    dataPackage.month = listViewCursor.getInt(6);
                    dataPackage.day = listViewCursor.getInt(7);
                    dataPackageAry[i] = dataPackage;
                    /***金錢收入分開計算***/
                    if(dataPackage.index==9)
                        inputMoney+=dataPackage.price;
                    else
                        outputMoney+=dataPackage.price;
                i++;
                } while (cursor.moveToNext());//佇列遞增時修改
            }
            listItem.setAdapter(adapter);
        }
        registerForContextMenu(listItem);
    }
    /**
     * @brief 設定元件連結.
     * @since 1.0.0
     */
    public void setUI()
    {
        btnBack=(Button)findViewById(R.id.btnBack);
        btnSearch=(Button)findViewById(R.id.btnSearch);
        textInput=(TextView)findViewById(R.id.textInput);
        textOutput=(TextView)findViewById(R.id.textOutput);
        textAverage=(TextView)findViewById(R.id.textAverage);
        textMoneyTotal=(TextView)findViewById(R.id.textMoneyTotal);
        textTime=(TextView)findViewById(R.id.textTime);
        spinnerItem=(Spinner)findViewById(R.id.spinnerItem);
        listItem=(ListView)findViewById(R.id.listItem);
        rg=(RadioGroup)findViewById(R.id.radioGroup);
        rbY=(RadioButton)findViewById(R.id.radioButtonY);
        rbM=(RadioButton)findViewById(R.id.radioButtonM);
        rbAll=(RadioButton)findViewById(R.id.radioButtonAll);
    }
}
