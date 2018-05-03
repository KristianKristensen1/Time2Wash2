package com.example.krist.time2washproject;

import java.io.Serializable;
import java.util.Date;

public class WashingTime implements Serializable {
    private String Date;
    private String Machine;
    private String Time;
    private int _resultCode;

    public WashingTime() {
    }

    public WashingTime(String _time, String _date, String _machine) {
        this.Date = _time;
        this.Machine = _date;
        this.Time = _time;
        _resultCode = 0;
    }

    public WashingTime(String _time, String _date, String _machine, int WashingTimeResultCode) {
        this.Date = _date;
        this.Machine = _machine;
        this.Time = _time;
        _resultCode = WashingTimeResultCode;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String _time) {
        this.Time = _time;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String _date) {
        this.Date = _date;
    }

    public String getMachine() {
        return Machine;
    }

    public void setMachine(String _machine) {
        this.Machine = _machine;
    }
}

