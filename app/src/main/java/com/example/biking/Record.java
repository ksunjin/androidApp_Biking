package com.example.biking;

// 기록 Record class
// distance, time
// Record(),Record(distance,time),setRecord(distance,time),setDistance(distance),setTime(time),getDistance(),getTime()
public class Record{
    private int distance;
    private int time;
    public Record(){
        distance = 0;
        time = 0;
    }
    public Record(int distance, int time){
        this.distance =distance;
        this.time = time;
    }
    public void setRecord(int distance, int time){
        this.distance =distance;
        this.time = time;
    }
    public void setDistance(int distance){
        this.distance =distance;
    }
    public void setTime(int time){
        this.time = time;
    }
    public int getDistance(){
        return distance;
    }
    public int getTime(){
        return time;
    }
}