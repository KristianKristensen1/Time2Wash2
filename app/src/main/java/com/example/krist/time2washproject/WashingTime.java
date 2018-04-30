package com.example.krist.time2washproject;

import java.util.Date;

public class WashingTime {
    private String _time;
    private String _date;
    private String _intentAction;
    private int _resultCode;

    public WashingTime(String Time, String Date){
        this(Time, Date, null, 0);
    }

    public WashingTime(String Time, String Date, String WashingTimeAction, int WashingTimeResultCode){
        _time = Time;
        _date = Date;
        _intentAction = WashingTimeAction;
        _resultCode = WashingTimeResultCode;
    }

    public String getTime(){
        return _time;
    }
    public void setTime(String Time){
        _time = Time;
    }

    public String getDate(){
        return _date;
    }
    public void setDate(String Date){
        _date = Date;
    }

    public String getIntentAction(){return _intentAction;}

    public void setIntentAction(String intentAction){_intentAction = intentAction;}

    public int getDResultCode(){return _resultCode;}

    public void setResultCode(int resultCode){_resultCode = resultCode;}
}
