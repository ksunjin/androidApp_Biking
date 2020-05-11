package com.example.biking;

import com.google.firebase.database.IgnoreExtraProperties;
// 회원정보 저장할 User class
// email, custId, username, kakao, level, exp, totalRecord, bestRecord
// User(),User(email,username),setUser(~~),get~~()
@IgnoreExtraProperties
public class User {
    private String email; // 키
    static public int custId = 0; //총 사용자 인원수
    private String username;
    private String kakao;
    private int level;
    private int exp;
    private Record totalRecord;
    private Record bestRecord;
    public User() {
        email = null;
        username = null;
        kakao = null;
        level = 1;
        exp = 0;
        totalRecord = new Record(0,0);
        bestRecord = new Record(0,0);
    }
    public User(String email, String username) {
        this.email = email;
        this.username = username;
        kakao = "없음";
        level = 1;
        exp = 0;
        totalRecord = new Record(0,0);
        bestRecord = new Record(0,0);
        custId++;
    }
    //public User(String email, int custId, String username,String kakao, int level, int exp, Record totalRecord, Record bestRecord){  }
    public void setUser(String email, int custId, String username,String kakao, int level, int exp, Record totalRecord, Record bestRecord){
        this.email=email;
        this.custId=custId;
        this.username=username;
        this.kakao=kakao;
        this.level=level;
        this.exp=exp;
        this.totalRecord=totalRecord;
        this.bestRecord=bestRecord;
    }
    public String getEmail(){return email;}
    public void setEmail(String email){this.email = email;}
    public static int getCustId(){return custId;}
    public String getUsername(){return username;}
    public String getKakao(){return kakao;}
    public int getLevel(){return level;}
    public int getExp(){return exp;}
    public Record getTotalRecord(){return totalRecord;}
    public Record getBestRecord(){return bestRecord;}
}

