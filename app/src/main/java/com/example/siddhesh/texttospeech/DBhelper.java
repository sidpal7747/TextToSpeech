package com.example.siddhesh.texttospeech;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by SIDDHESH on 10-01-2017.
 */

public class DBhelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "sample";
    private static final String TABLE_NAME = "HISTORY";
    private static final int DATABASE_VERSION = 2;
    private static final String create = "CREATE TABLE "+TABLE_NAME+" (Id INTEGER PRIMARY KEY AUTOINCREMENT, Usertext varchar(255));";

    private Context context;

    public DBhelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(create);
            //Toast.makeText(context, ""+TABLE_NAME+" is created", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
            onCreate(db);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
