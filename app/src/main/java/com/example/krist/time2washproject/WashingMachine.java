package com.example.krist.time2washproject;

public class WashingMachine {

    private String Name;
    private boolean InUse;
    public WashingMachine()
    {}
    public WashingMachine(Boolean _inUSe,String _name){
        this.Name = _name;
        this.InUse = _inUSe;
    }

    public String getName() {
        return Name;
    }

    public void setName(String _name) {
        this.Name = _name;
    }

    public Boolean getInUse() {
        return InUse;
    }

    public void setState(Boolean _inUse) {
        this.InUse = _inUse;
    }

}

