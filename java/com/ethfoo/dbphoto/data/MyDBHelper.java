package com.ethfoo.dbphoto.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/6/8.
 */
public class MyDBHelper extends SQLiteOpenHelper {

    final static String CREATE_DB = "create table LikedList (id integer primary key autoincrement," +
            Constants.DB_PHOTO_ID+" text, " +
            Constants.DB_ALBUM_ID+" text, " +
            Constants.DB_PHOTO_URL+" text, "+
            Constants.DB_CREATE_TIME+" text)";


    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB);
        Log.e("MyDBHelper", "database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
