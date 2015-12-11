package com.game.ballgame.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.game.ballgame.R;

/**
 * Created by wangrh on 2015/12/3.
 */
public class HoleView extends ImageView {

    public HoleView(Context context) {
        super(context);
        this.setImageResource(R.drawable.hole1);
    }

    public HoleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setImageResource(R.drawable.hole1);
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(Color.BLACK);
//        canvas.drawCircle(60,60,40,paint);
//    }
}
