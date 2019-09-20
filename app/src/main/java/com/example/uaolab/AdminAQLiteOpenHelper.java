package com.example.uaolab;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminAQLiteOpenHelper extends SQLiteOpenHelper {
    public AdminAQLiteOpenHelper(Context context, String nombre, SQLiteDatabase.CursorFactory
            factory, int version) {
        super(context, nombre, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table freeFall(num int primary key, tiempo float ,gravedad float)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists freeFall");
        db.execSQL("create table freeFall(num int primary key, tiempo float ,gravedad float)");

    }
}
