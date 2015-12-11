package com.game.ballgame;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.game.ballgame.model.HoleCoordinate;
import com.game.ballgame.view.BallView;
import com.game.ballgame.view.HoleView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wangrh on 2015/12/4.
 */
public class GamePage extends Activity {

    public static final int STARTGAME = 0;
    public static final int GAMESCORE = 1;
    public static final int HOLECHANGE = 2;
    public static final int GAMELOSE = 3;
    public static final int GAMEWIN = 4;

    private float ballx,bally;
    private float ball_radius, hole_radius;
    private int screenwidth,screenheigth;
    private int gamelevel;
    private int levelnum;
    private int time_i = 0;
    private int score_i = 0;
    private int holesize = 21;
    private int historyHigh = 0;

    private double ball_direct = 1000; //if stay 3s lose the game

    private boolean timerThreadFlag,checkThreadFlag,isInGame,stayLongFlag;  //remember new game set timerThreadFlag = false

    private ArrayList<HoleCoordinate> holeCoordinates;

    private BallView ballView;
    private HoleView[] holeViews;
    private ImageView timerPic;
    private TextView score;
    private FrameLayout frameLayout;
    private Dialog exitDialog;
    private Dialog gameoverDialog;
    private ImageView dialogImage;
    private Button restartButton, gameoverExitButton, continueButton, pauseExitButton;
    private TextView dialogScore;

    private Sensor mSensor;
    private SensorManager mSensorManager;
    private Timer timer;
    private Timer gameTimer;
    private Timer holeTimer;
    private HoleCoordinate centerCoordinate;


    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            float x = event.values[SensorManager.DATA_X]*levelnum;
            float y = event.values[SensorManager.DATA_Y]*levelnum;
            ballx +=y;
            bally +=x;

            ball_direct += Math.sqrt(x*x + y*y);
            ballView.moveTo((int) ballx, (int) bally);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case STARTGAME:
                    if(time_i == 0)
                    {
                        timerPic.setImageResource(R.drawable.time_3);
                        timerPic.setVisibility(View.VISIBLE);
                        timerPic.bringToFront();
                        time_i++;
                    }else if(time_i == 1){
                        timerPic.setImageResource(R.drawable.time_2);
                        time_i++;
                    }else if(time_i == 2){
                        timerPic.setImageResource(R.drawable.time_1);
                        time_i++;
                    }else if(time_i == 3){
                        timerPic.setImageResource(R.drawable.time_s);
                        time_i++;
                    }else if(time_i == 4){

                        timer.cancel();
                        timerThreadFlag = true;
                        timerPic.setVisibility(View.INVISIBLE);
                        time_i++;

                        startGame();
                    }else {
                        timerThreadFlag = true;
                        if(timer != null)
                            timer.cancel();
                    }

                    break;
                case GAMESCORE:
                    score.setText(String.valueOf(score_i*10));
                    score_i++;
                    break;

                case HOLECHANGE:

                    for(int i=1;i<holesize;i++)
                    {
                        float x = holeCoordinates.get(i-1).getX();
                        float y = holeCoordinates.get(i-1).getY();
                        holeViews[i].setX(x-hole_radius);
                        holeViews[i].setY(y-hole_radius);
                    }
                    holeViews[0].setX(centerCoordinate.getX()-hole_radius);
                    holeViews[0].setY(centerCoordinate.getY() - hole_radius);

                    break;

                case GAMEWIN:

                    ballView.setVisibility(View.INVISIBLE);
                    dialogScore.setText(String.valueOf(score_i * 10));
                    dialogImage.setImageResource(R.drawable.win);

                    reset();
                    gameoverDialog.show();
                    gameoverDialog.getWindow().setLayout((int)(screenwidth/1.2), (int)(screenheigth/1.2));
                    gameoverDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    break;

                case GAMELOSE:

                    ballView.setVisibility(View.INVISIBLE);
                    dialogScore.setText(String.valueOf(score_i * 10));
                    dialogImage.setImageResource(R.drawable.failed);
                    reset();

                    gameoverDialog.show();
                    gameoverDialog.getWindow().setLayout((int)(screenwidth/1.2), (int)(screenheigth/1.2));
                    gameoverDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    break;
            }
        }
    };

    private void startGame() {


        ballView.setVisibility(View.VISIBLE);
        ballView.bringToFront();

        startTimer();
    }


    private void reset()
    {
        if(holeTimer != null)
            holeTimer.cancel();
        if(gameTimer != null)
            gameTimer.cancel();
        if(timer != null)
            timer.cancel();

        unregisterSensor();
        initParams();

        SQLiteDatabase mSQLiteDatabase = this.openOrCreateDatabase("Game.db",MODE_PRIVATE,null);

        Cursor cursor1 = mSQLiteDatabase.rawQuery("select max(scorenum) from score",null);
        cursor1.moveToFirst();
        historyHigh = cursor1.getInt(0);
        cursor1.close();
        mSQLiteDatabase.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        gamelevel = getIntent().getIntExtra("level", 0);
        levelnum = getLevelNum();

        setContentView(R.layout.game);

        Point point = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(point);
        screenwidth = point.x;
        screenheigth = point.y;

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        holeCoordinates = new ArrayList<>();
        holeViews = new HoleView[holesize];

        ballView = (BallView)findViewById(R.id.ball);
        timerPic = (ImageView)findViewById(R.id.timer);
        score = (TextView)findViewById(R.id.score);
        frameLayout = (FrameLayout)findViewById(R.id.game_frame);
        holeViews[0] = (HoleView)findViewById(R.id.hole0);

        //get dialog widget
        View gameoverView = LayoutInflater.from(this).inflate(R.layout.gameover_dialog,null);
        restartButton = (Button)gameoverView.findViewById(R.id.restart);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gameoverDialog.dismiss();
                initParams();
                time_i = 0;
                if(timer != null)
                    timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        handler.sendEmptyMessage(STARTGAME);

                    }
                },0,1000);
            }
        });

        gameoverExitButton = (Button)gameoverView.findViewById(R.id.exit);
        gameoverExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameoverDialog.dismiss();
                GamePage.this.finish();
            }
        });

        dialogImage = (ImageView)gameoverView.findViewById(R.id.gameover_pic);
        dialogScore = (TextView)gameoverView.findViewById(R.id.score);

        View pauseView = LayoutInflater.from(this).inflate(R.layout.pausegame_dialog,null);
        pauseExitButton = (Button)pauseView.findViewById(R.id.exit_btn);
        pauseExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
                reset();
                GamePage.this.finish();
            }
        });

        continueButton = (Button)pauseView.findViewById(R.id.continue_btn);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
                startTimer();
            }
        });



        centerCoordinate = new HoleCoordinate();

        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        ballView.measure(w, h);

        initParams();

        ball_radius = ballView.getMeasuredHeight()/2;
        holeViews[0].measure(w, h);
        hole_radius = holeViews[0].getMeasuredHeight()/2;

        for(int i=1;i<holesize;i++)
        {
            holeViews[i] = new HoleView(GamePage.this);
            frameLayout.addView(holeViews[i], new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
            holeViews[i].bringToFront();
            holeViews[i].setX(screenwidth/2-hole_radius);
            holeViews[i].setY(screenheigth/2-hole_radius);
        }

        exitDialog = new Dialog(GamePage.this);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.setContentView(pauseView);
        exitDialog.setCancelable(false);

        gameoverDialog = new Dialog(GamePage.this);
        gameoverDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        gameoverDialog.setContentView(gameoverView);
        gameoverDialog.setCancelable(false);


        SQLiteDatabase mSQLiteDatabase = this.openOrCreateDatabase("Game.db",MODE_PRIVATE,null);

        Cursor cursor = mSQLiteDatabase.rawQuery("select count(*) from score", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        if(count == 0)
        {
            historyHigh = 0;
        }else
        {
            Cursor cursor1 = mSQLiteDatabase.rawQuery("select max(scorenum) from score",null);
            cursor1.moveToFirst();
            historyHigh = cursor1.getInt(0);
            cursor1.close();
        }
        mSQLiteDatabase.close();


    }

    private void initParams()
    {
        holeCoordinates.clear();

        ballView.setVisibility(View.INVISIBLE);
        timerPic.setVisibility(View.INVISIBLE);
        score.setText("0");

        centerCoordinate.setX(screenwidth / 2);
        centerCoordinate.setY(screenheigth / 2);

        ball_direct = 1000;
        score_i = 0;
        time_i = 0;

        ballx = screenwidth/2 - ball_radius;
        bally = screenheigth/2 + ball_radius;


        timerThreadFlag = false;
        checkThreadFlag = false;
        isInGame = false;
        stayLongFlag = false;
    }

    private int getLevelNum()
    {
        if(gamelevel == 0)
        {
            return 5;
        }else if(gamelevel ==1)
        {
            return 7;
        }else
        {
            return 9;
        }
    }

    private ArrayList<HoleCoordinate> getRandomCoordinate()
    {
        ArrayList<HoleCoordinate> list = new ArrayList<>();
        int getwidth = (int)(screenwidth/2 - 2*hole_radius);
        int getheight = (int)(screenheigth/2 - 2*hole_radius);

        list.addAll(generatePartList((int)(holesize/4),hole_radius,hole_radius,getwidth,getheight));
        list.addAll(generatePartList((int)(holesize/4),screenwidth/2+hole_radius,hole_radius,getwidth,getheight));
        list.addAll(generatePartList((int)(holesize/4),hole_radius,screenheigth/2+hole_radius,getwidth,getheight));
        list.addAll(generatePartList((int)(holesize/4),screenwidth/2+hole_radius,screenheigth/2+hole_radius,getwidth,getheight));
        return list;
    }

    private ArrayList<HoleCoordinate> generatePartList(int num,float startx,float starty,int width,int height){

        ArrayList<HoleCoordinate> list = new ArrayList<>();
        Random random = new Random();
        for (int i=0;i<num;i++)
        {
            loop:
            while (true)
            {
                HoleCoordinate coordinate = new HoleCoordinate(random.nextInt((int)(width-hole_radius)),random.nextInt((int)(height-hole_radius)));
                float realx = coordinate.getX() + startx;
                float realy = coordinate.getY() + starty;
                if(isOverlapping(realx,realy,screenwidth/2,screenheigth/2,2*hole_radius) ||
                        isOverlapping(realx,realy,ballx+ball_radius,bally+ball_radius,hole_radius))
                    continue;

                for(int j=i-1;j>=0;j--)
                {
                    if(isOverlapping(realx,realy,list.get(j).getX(),list.get(j).getY(),2*hole_radius))
                        continue loop;
                }
                coordinate.setX(realx);
                coordinate.setY(realy);
                list.add(coordinate);
                break;
            }

        }
        return list;

    }

    private boolean isOverlapping(float x1,float y1,float x2,float y2,float radius)
    {
        if(Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)) < radius)
            return true;
        else
            return false;
    }

    private void gameover()
    {
        checkThreadFlag = false;
        gameTimer.cancel();
        holeTimer.cancel();

        unregisterSensor();

        if(score_i*10 > historyHigh)
        {
            handler.sendEmptyMessage(GAMEWIN);
        }else
        {
            handler.sendEmptyMessage(GAMELOSE);
        }


        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = format.format(new Date(System.currentTimeMillis()));
        SQLiteDatabase mSQLiteDatabase = this.openOrCreateDatabase("Game.db", MODE_PRIVATE, null);
        mSQLiteDatabase.execSQL("insert into score(gamedate, scorenum) " +
                "values("+date +","+score_i*10+")");
        mSQLiteDatabase.close();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!timerThreadFlag)
        {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {

                    handler.sendEmptyMessage(STARTGAME);

                }
            },0,1000);

        }
        if(isInGame)
        {
            startTimer();
        }
    }


    private void startTimer()
    {
        if(holeTimer != null)
            holeTimer.cancel();
        holeTimer = new Timer();
        holeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(ball_direct<200)
                {
                    stayLongFlag = true;
                    centerCoordinate.setX(ballx+ball_radius);
                    centerCoordinate.setY(bally+ball_radius);
                }
                ball_direct = 0;
                synchronized (this) {
                    holeCoordinates.clear();
                    holeCoordinates = getRandomCoordinate();
                    holeCoordinates.add(centerCoordinate);
                }
                handler.sendEmptyMessage(HOLECHANGE);
            }
        },0,3000);

        if(gameTimer != null)
            gameTimer.cancel();

        gameTimer = new Timer();
        gameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(GAMESCORE);
            }
        },0,1000);

        registerSensor();

        checkThreadFlag = true;
        new Thread(new CheckThread()).start();

        isInGame = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterSensor();
        checkThreadFlag = false;
        if(holeTimer != null)
            holeTimer.cancel();
        if(gameTimer != null)
            gameTimer.cancel();
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            exitDialog.show();
            exitDialog.getWindow().setLayout((int)(screenwidth/1.2), screenheigth/2);
            exitDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            checkThreadFlag = false;
            if(timer != null)
                timer.cancel();
            if(holeTimer != null)
                holeTimer.cancel();
            if(gameTimer != null)
                gameTimer.cancel();

            unregisterSensor();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void registerSensor()
    {
        mSensorManager.registerListener(listener, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    private void unregisterSensor()
    {
        mSensorManager.unregisterListener(listener);
    }

    class CheckThread implements Runnable
    {
        @Override
        public void run() {
            while (checkThreadFlag)
            {
                if(ballx<0-ball_radius || ballx>screenwidth-ball_radius || bally<0-ball_radius || bally> screenheigth-ball_radius)   //bound
                {
                    gameover();
                    break;
                }

                synchronized (this)
                {
                    for(int i=0;i<holesize;i++)
                    {
                        if((Math.sqrt(Math.pow(ballx+ball_radius-holeCoordinates.get(i).getX(),2) +
                                Math.pow(bally+ball_radius-holeCoordinates.get(i).getY(),2)) < hole_radius))
                        {
                            gameover();
                            break;
                        }


                    }
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
