package com.game.ballgame.model;

/**
 * Created by wangrh on 2015/12/6.
 */
public class UserData {

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    private String score;

    public UserData(String date,String score)
    {
        this.date = date;
        this.score = score;
    }
}
