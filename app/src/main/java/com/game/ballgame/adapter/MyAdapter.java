package com.game.ballgame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.game.ballgame.R;
import com.game.ballgame.model.UserData;

import java.util.ArrayList;

/**
 * Created by wangrh on 2015/12/6.
 */
public class MyAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<UserData> list;
    private ViewHolder holder;

    public MyAdapter(Context context,ArrayList<UserData> list)
    {
        this.context = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.list_item, null);
            holder.date = (TextView) convertView.findViewById(R.id.item_date);
            holder.score = (TextView) convertView.findViewById(R.id.item_score);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.date.setText(list.get(position).getDate());
        holder.score.setText(list.get(position).getScore());

        return convertView;
    }

    private static class ViewHolder {
        TextView date;
        TextView score;
    }
}
