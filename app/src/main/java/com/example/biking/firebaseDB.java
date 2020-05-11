package com.example.biking;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class firebaseDB{
    public static HashMap<String,User> tempBase = new HashMap<String, User>();;
    public static User newUser = null;
    public static User myUser = null;
    static DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    //회원가입 시 firebase database로 새 회원 data 전송
    public static  void signUpFirebase(String email, String username) {
        newUser = new User(email, username);
        mRootRef.child("회원").child(Integer.toString(newUser.getCustId())).setValue(newUser.getCustId());
        mRootRef.child("회원").child(Integer.toString(newUser.getCustId())).child("email").setValue(email);
        mRootRef.child("회원").child(Integer.toString(newUser.getCustId())).child("userName").setValue(username);
        mRootRef.child("회원").child(Integer.toString(newUser.getCustId())).child("kakao").setValue("없음");
        // 혹은 user.get~~로 setVlaue
        mRootRef.child("회원").child(Integer.toString(newUser.getCustId())).child("level").setValue(1);
        mRootRef.child("회원").child(Integer.toString(newUser.getCustId())).child("exp").setValue(0);
        mRootRef.child("회원").child(Integer.toString(newUser.getCustId())).child("totalDistance").setValue(0);
        mRootRef.child("회원").child(Integer.toString(newUser.getCustId())).child("totalTime").setValue(0);
        mRootRef.child("회원").child(Integer.toString(newUser.getCustId())).child("bestDistance").setValue(0);
        mRootRef.child("회원").child(Integer.toString(newUser.getCustId())).child("bestTime").setValue(0);
        mRootRef.child("회원").child(Integer.toString(newUser.getCustId())).child("custId").setValue(Integer.toString(newUser.getCustId()));
        mRootRef.child("사용자수").setValue(newUser.getCustId());
    }
    //firebae database에서 tempBase로 data 가져오기
    public static void updateTempBase(){
        mRootRef.child("회원").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String number = Long.toString(dataSnapshot.getChildrenCount());
                User.custId=Integer.parseInt(number);
                Log.d("MainActivity", "Single ValueEventListener2 : " + number);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = new User();
                    Record totalRecord = new Record(Integer.parseInt(""+snapshot.child("totalDistance").getValue()),Integer.parseInt(""+snapshot.child("totalTime").getValue()));
                    Record bestRecord = new Record(Integer.parseInt(""+snapshot.child("bestDistance").getValue()),Integer.parseInt(""+snapshot.child("bestTime").getValue()));
                    user.setUser(""+snapshot.child("email").getValue(),Integer.parseInt(""+snapshot.child("custId").getValue()),""+snapshot.child("userName").getValue(),
                            ""+snapshot.child("kakao").getValue(), Integer.parseInt(""+snapshot.child("level").getValue()), Integer.parseInt(""+snapshot.child("exp").getValue()),
                            totalRecord, bestRecord);
                    tempBase.put(""+snapshot.child("email").getValue(),user);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        mRootRef.child("사용자수").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int number;
                if(dataSnapshot.getValue()==null){
                    number = 0;
                }
                else{
                    number = Integer.parseInt(""+dataSnapshot.getValue());
                }
                Log.d("MainActivity", "스냅샷 : " + dataSnapshot.getValue());
                if(newUser!=null)
                    tempBase.put(newUser.getEmail(),newUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //email 입력하면 해당하는 User Data 반환 (tempBase에서 가져옴), 없는 사용자면 null 반환
    //내부 myUser에도 저장
    public static  User myData(String email){
        try{
            myUser = tempBase.get(email);
        }
        catch(Exception e){
            return null;
        }
        return myUser;
    }
    //firebase database의 내용 수정+tempBase의 내용 수정
    public static void updateDatabase(String email, String username, String kakao, int level, int exp, Record totalRecord, Record bestRecord){
        // tempBase에서 email(키)로 custId를 찾고, 그 custId를 key로 firebase database 수정
        User user = tempBase.get(email);
        mRootRef.child("회원").child(Integer.toString(user.getCustId())).child("userName").setValue(username);
        mRootRef.child("회원").child(Integer.toString(user.getCustId())).child("kakao").setValue(kakao);
        mRootRef.child("회원").child(Integer.toString(user.getCustId())).child("level").setValue(level);
        mRootRef.child("회원").child(Integer.toString(user.getCustId())).child("exp").setValue(exp);
        mRootRef.child("회원").child(Integer.toString(user.getCustId())).child("totalDistance").setValue(totalRecord.getDistance());
        mRootRef.child("회원").child(Integer.toString(user.getCustId())).child("totalTime").setValue(totalRecord.getTime());
        mRootRef.child("회원").child(Integer.toString(user.getCustId())).child("bestDistance").setValue(bestRecord.getDistance());
        mRootRef.child("회원").child(Integer.toString(user.getCustId())).child("bestTime").setValue(bestRecord.getTime());
        //mRootRef.child("회원").child(Integer.toString(user.getCustId())).child("custId").setValue(Integer.toString(user.getCustId()));
        user.setUser(email,user.getCustId(),username,kakao,level,exp,totalRecord,bestRecord);
        tempBase.put(email,user);
    }
    public static String printUserInfo(User user){
        String print = "총사용자수:"+ Integer.toString(user.getCustId())+" 이메일:"+user.getEmail()+" 카카오:"+user.getKakao()+" 닉네임:"+user.getUsername()+" 총거리:"
                +Integer.toString(user.getTotalRecord().getDistance()) +" 총시간:"+Integer.toString(user.getTotalRecord().getTime())+" 최고거리:"
                +Integer.toString(user.getBestRecord().getDistance())+" 최고시간:"+Integer.toString(user.getBestRecord().getTime())+
                " 레벨:"+Integer.toString(user.getLevel()) +" 경험치:"+Integer.toString(user.getExp());
        return print;
    }

}