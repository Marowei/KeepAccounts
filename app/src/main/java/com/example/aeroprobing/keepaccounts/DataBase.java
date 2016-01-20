package com.example.aeroprobing.keepaccounts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @file DataBase.java
 * @version 1.0.0
 * @author 浩威
 *
 * @version 1.0.0 資料庫
 */
public class DataBase extends SQLiteOpenHelper{
    private static final String dataBase="DItem.db";    //資料庫名稱
    private static final int version=1;                 //資料庫版本

    /***Database建構子***/
    /**
     * @brief 創建資料庫.
     * @since 1.0.0
     * @param context 輸入Activity(this).
     * @param name 資料庫名稱.
     * @param factory 輸入Factory游標.
     * @param version 輸入資料庫版本.
     */
    public DataBase(Context context,String name,SQLiteDatabase.CursorFactory factory,int version)
    {
        super(context,name,factory,version);
    }
    /**
     * @brief 創建資料庫.
     * @since 1.0.0
     * @param context 輸入Activity(this).
     */
    public DataBase(Context context)
    {
        this(context, dataBase, null, version);
    }
    /***創建資料表***/
    @Override
    public void onCreate(SQLiteDatabase db) {
        //項目資料表 ID,名稱,種類,備註,費用,年,月,日
        db.execSQL("CREATE TABLE itemTable (_id integer primary key autoincrement,item INTEGER,name TEXT no null,note TEXT," +
                "price REAL no null,y INTEGER,m INTEGER,d INTEGER)");
    }
    /***資料庫更新***/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS itemTable");//刪除項目資料表
        onCreate(db);//重新建置
    }
}
