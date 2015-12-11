package com.game.ballgame.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by wangrh on 2015/12/3.
 */
public class BallView extends ImageView {


    public BallView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }


    public BallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public void moveTo(int x,int y){
        super.setX(x);
        super.setY(y);
    }
}
