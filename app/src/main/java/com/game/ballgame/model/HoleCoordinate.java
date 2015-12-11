package com.game.ballgame.model;

/**
 * Created by wangrh on 2015/12/4.
 */
public class HoleCoordinate {

    public float x;
    public float y;

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public HoleCoordinate(float x,float y)
    {
        this.x = x;
        this.y = y;
    }

    public HoleCoordinate()
    {

    }


}
