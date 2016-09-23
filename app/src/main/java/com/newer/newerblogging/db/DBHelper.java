package com.newer.newerblogging.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Chalmers on 2016-09-15 12:57.
 * email:qxinhai@yeah.net
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "NewerMicroblog.db";
    public static final int VERSION = 1;

    public static final String CREATE_TABLE_SINGLEMICROBLOG = "create table microblog(" +
            "id integer primary key autoincrement," +
            "microblog text )";

    public DBHelper(Context context){
        this(context,DATABASE_NAME,null,VERSION);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SINGLEMICROBLOG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
