package com.example.krist.time2washproject;

public class WashingTime {
    private String _time;
    private boolean _booked;
    private String _intentAction;
    private int _resultCode;

    public WashingTime(String Time, boolean Booked){
        this(Time, Booked, null, 0);
    }

    public WashingTime(String Time, boolean Booked, String WashingTimeAction, int WashingTimeResultCode){
        _time = Time;
        _booked = Booked;
        _intentAction = WashingTimeAction;
        _resultCode = WashingTimeResultCode;
    }

    public String getTime(){
        return _time;
    }
    public void setTime(String Time){
        _time = Time;
    }

    public boolean getBooked(){
        return _booked;
    }
    public void setBooked(boolean Booked){
        _booked = Booked;
    }

    public String getIntentAction(){return _intentAction;}

    public void setIntentAction(String intentAction){_intentAction = intentAction;}

    public int getDResultCode(){return _resultCode;}

    public void setResultCode(int resultCode){_resultCode = resultCode;}
}
