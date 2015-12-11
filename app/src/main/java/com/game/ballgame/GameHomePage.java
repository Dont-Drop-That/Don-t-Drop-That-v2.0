package com.game.ballgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.game.ballgame.view.MyPopup;


/**
 * Created by wangrh on 2015/12/2.
 */



public class GameHomePage extends Activity{


    private Spinner mSpinner;
    private ImageButton mStartGame;
    private ImageView mSetting;

    private int gamelevel = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.gamehome);

        mSpinner = (Spinner)findViewById(R.id.choose_level);
        SpinnerAdapter adapter = new SpinnerAdapter();
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gamelevel = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mStartGame = (ImageButton)findViewById(R.id.start_game);
        mStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GameHomePage.this,GamePage.class);
                intent.putExtra("level",gamelevel);
                GameHomePage.this.startActivity(intent);
            }
        });

        mSetting = (ImageView)findViewById(R.id.setting);
        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPopup popup = new MyPopup(GameHomePage.this);
                popup.showPopupWindow(mSetting);
            }
        });

    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "Press again to exit the game!", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    class SpinnerAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 3;
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

            ImageView imageView = new ImageView(GameHomePage.this);
            if(position == 0)
            {
                imageView.setImageResource(R.drawable.lv1);
            }else if(position == 1)
            {
                imageView.setImageResource(R.drawable.lv2);
            }else
            {
                imageView.setImageResource(R.drawable.lv3);
            }

            return imageView;
        }
    }
}
