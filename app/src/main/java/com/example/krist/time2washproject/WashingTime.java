package com.example.krist.time2washproject;

import java.io.Serializable;
import java.util.Date;

public class WashingTime implements Serializable {
    private String _time;
    private String _date;
    private String _machine;
    private String _intentAction;
    private int _resultCode;

    public WashingTime(String Time, String Date, String Machine){
        this(Time, Date, Machine, 0);
    }

    public WashingTime(String Time, String Date, String Machine, int WashingTimeResultCode){
        _time = Time;
        _date = Date;
        _machine = Machine;
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

    public String getMachine(){
        return _machine;
    }
    public void setMachine(String Machine){
        _machine = Machine;
    }


    public String getIntentAction(){return _intentAction;}

    public void setIntentAction(String intentAction){_intentAction = intentAction;}

    public int getDResultCode(){return _resultCode;}

    public void setResultCode(int resultCode){_resultCode = resultCode;}
}
