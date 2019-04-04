package com.example.ethomas13.todolistapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by ethomas13 on 3/5/2019.
 */

public class DBManager extends SQLiteOpenHelper
{
    static final String TAG = "DBManager";
    static final String DB_NAME = "toDo.db";
    static final int DB_VERSION = 3;

    static final String TABLE_NAME_LIST= "List";
    static final String C_LIST_ID = BaseColumns._ID;
    static final String C_LIST_DESCRIPTION = "description";

    static final String TABLE_NAME_ITEM= "Item";
    static final String C_ITEM_ID = BaseColumns._ID;
    static final String C_ITEM_DESCRIPTION = "Description";
    static final String C_ITEM_DATE = "Date";
    static final String C_ITEM_COMPLETED = "Completed";
    static final String C_ITEM_LIST_ID = "ItemListID";

    public DBManager(Context context)
    {
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        String sql = "create table "
                + TABLE_NAME_LIST + " ("
                + C_LIST_ID + " integer primary key autoincrement, "
                + C_LIST_DESCRIPTION + " text" + ")";

        String sql2 = "create table "
                + TABLE_NAME_ITEM + " ("
                + C_ITEM_ID + " integer primary key autoincrement, "
                + C_ITEM_DESCRIPTION + " text, "
                + C_ITEM_DATE + " text, "
                + C_ITEM_COMPLETED + " int,"
                + C_ITEM_LIST_ID + " int, FOREIGN KEY("
                + C_ITEM_LIST_ID + ") REFERENCES " + TABLE_NAME_LIST + " (" + C_LIST_ID + ") )";
        Log.d(TAG, sql);
        database.execSQL(sql);
        Log.d(TAG, sql2);
        database.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        database.execSQL("drop table if exists " + TABLE_NAME_LIST);
        database.execSQL("drop table if exists " + TABLE_NAME_ITEM);
        onCreate(database);
    }
}
