package com.game.ballgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by wangrh on 2015/12/2.
 */
public class FirstUsePage extends Activity{

    private SharedPreferences mShare;
    private boolean isFirst;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShare = getSharedPreferences("game", Context.MODE_PRIVATE);
        isFirst = mShare.getBoolean("isFirst", true);

        try {
            sqLiteDatabase = this.openOrCreateDatabase("Game.db",MODE_PRIVATE,null);
            String createTable = "create table score (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    " gamedate TEXT NOT NULL, scorenum INTEGER NOT NULL)";
            sqLiteDatabase.execSQL(createTable);
            Log.d("FirstUse","create successfully");
        }catch (Exception e)
        {
            Log.d("FirstUse","create failed");
        }

        sqLiteDatabase.close();

        if(isFirst)
        {
            startActivity(new Intent(this,GuidePage.class));
            this.finish();
        }else
        {
            startActivity(new Intent(this,SplashPage.class));
            this.finish();
        }
    }
}
