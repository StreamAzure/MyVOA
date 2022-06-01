package com.stream.myvoa.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.stream.myvoa.bean.VOAObject;

/**
 * 本地SQLite数据库
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    //数据库版本号
    private static Integer Version = 1;

    public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        //context:上下文 name：数据库名称 factory:可选的游标工厂（通常是NULL） version：数据库版本，值必须为整数且递增
    }

    /**
     * 初次使用时建表
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreateVoaTable = "create table voa (id integer primary key, title text, album text,"+
                "mp3_url text, lrc_path text, content text)";
        db.execSQL(sqlCreateVoaTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
