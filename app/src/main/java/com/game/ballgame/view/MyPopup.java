package com.game.ballgame.view;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.game.ballgame.R;
import com.game.ballgame.adapter.MyAdapter;
import com.game.ballgame.model.UserData;

import java.util.ArrayList;

/**
 * Created by wangrh on 2015/12/6.
 */

public class MyPopup extends PopupWindow {

    private View conentView;
    private Dialog chartDialog;
    private ListView listView;
    private ArrayList<UserData> list;
    private MyAdapter adapter;


    public MyPopup(final Activity context) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popup, null);
        final int h = context.getWindowManager().getDefaultDisplay().getHeight();
        final int w = context.getWindowManager().getDefaultDisplay().getWidth();
        this.setContentView(conentView);
        this.setWidth(LayoutParams.WRAP_CONTENT);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        ColorDrawable dw = new ColorDrawable(0000000000);
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(android.R.style.Animation_Dialog);
        TextView addTaskLayout = (TextView) conentView
                .findViewById(R.id.chart);
        TextView teamMemberLayout = (TextView) conentView
                .findViewById(R.id.about);

        list = new ArrayList<>();

        View view = LayoutInflater.from(context).inflate(
                R.layout.chart_dialog, null);
        listView = (ListView)view.findViewById(R.id.chart_list);
        adapter = new MyAdapter(context,list);
        listView.setAdapter(adapter);

        chartDialog = new Dialog(context);
        chartDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        chartDialog.setContentView(view);


        addTaskLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                MyPopup.this.dismiss();
                chartDialog.show();
                chartDialog.getWindow().setLayout(w/2, (int)(h/1.2));
                chartDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                SQLiteDatabase  mSQLiteDatabase = context.openOrCreateDatabase("Game.db",Context.MODE_PRIVATE,null);

                Cursor cursor = mSQLiteDatabase.rawQuery("select gamedate,scorenum from score order by scorenum desc",null);
                while (cursor.moveToNext())
                {
                    UserData userData = new UserData(cursor.getString(0),cursor.getString(1));
                    list.add(userData);
                }
                cursor.close();
                mSQLiteDatabase.close();
                adapter.notifyDataSetChanged();
            }
        });

        teamMemberLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                MyPopup.this.dismiss();
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.about_dialog);
                dialog.show();
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            }
        });
    }

    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 18);
        } else {
            this.dismiss();
        }
    }

}
