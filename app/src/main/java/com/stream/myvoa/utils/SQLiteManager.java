package com.stream.myvoa.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.stream.myvoa.bean.VOAObject;

public class SQLiteManager {
    private static SQLiteDatabase db;

    public static void initDB(Context context){
        SQLiteHelper sqLiteHelper = new SQLiteHelper(context, "voa.db", null,1);
        db = sqLiteHelper.getWritableDatabase();
    }

    public static void insertVOA(VOAObject voaObject){
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", voaObject.getTitle());
        contentValues.put("album",voaObject.getAlbum());
        contentValues.put("mp3_url", voaObject.getmp3_url());
        contentValues.put("lrc_path", voaObject.getlrc_path());
        contentValues.put("content", voaObject.getContent());
        db.insert("voa", null, contentValues);
    }

    public static boolean isVOAExist(String title){
        String sql = "select * from voa where title = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{title});
        cursor.moveToFirst();
        return !cursor.isAfterLast();
    }
}
