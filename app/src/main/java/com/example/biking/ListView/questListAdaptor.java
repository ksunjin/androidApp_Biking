package com.example.biking.ListView;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biking.Quest.Quest;
import com.example.biking.Quest.QuestList;
import com.example.biking.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import static com.example.biking.firebaseDB.myUser;
import static com.example.biking.firebaseDB.tempBase;

public class questListAdaptor  extends BaseAdapter {
    public ArrayList<Quest> listViewItemList = new ArrayList<Quest>();

    @Override
    public int getCount(){
        return listViewItemList.size();
    }

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    @Override
    public Object getItem(int position)
    {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if( convertView == null ){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.quest_list_item, parent, false);
        }

        final TextView name = (TextView) convertView.findViewById(R.id.quest_name);
        TextView exp = (TextView) convertView.findViewById(R.id.quest_exp);
        Button condition = (Button) convertView.findViewById(R.id.quest_confirm);

        final Quest item = listViewItemList.get(position);

        name.setText(item.getName());
        exp.setText("보상: "+item.getExp()+"exp");
        if(item.getCondition()== 1){
            condition.setText("확인");
            condition.setBackgroundColor(Color.rgb(246,231,24));
        }else if(item.getCondition()==0){
            condition.setText("완료 가능");
            condition.setBackgroundColor(Color.rgb(100,63,63));
            condition.setTextColor(Color.RED);

        }else{
            condition.setText("완료");
            condition.setBackgroundColor(Color.rgb(63,63,63));
            condition.setTextColor(Color.WHITE);

        }


        condition.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
             //   Quest quest = QuestList.getQuest(item.getName.getText().toString());
                int condition = item.getCondition();

                switch( condition ){
                    case 0:
                        Toast.makeText(context, item.getExp()+"의 exp를 획득하였습니다.", Toast.LENGTH_SHORT).show();
                        item.setCondition(2);
                        mRootRef.child("회원").child(Integer.toString(myUser.getCustId())).child("exp").setValue(myUser.getExp()+item.getExp()); // 수정 필요
                        tempBase.put(myUser.getEmail(),myUser);
                        String questfile = "data/data/com.example.biking/files/"+myUser.getCustId()+".txt";


                        try{
                            BufferedWriter bw = new BufferedWriter(new FileWriter(questfile, true));
                            bw.write(item.getName()+"\n");
                            bw.close();
                            Log.d("TAG","createFile----------------------------------");

                        }catch (Exception e){
                            e.printStackTrace();
                            Log.d("TAG","errorFile----------------------------------\n ");

                        }
                        break;
                    case 1:
                        Toast.makeText(context, "아직 퀘스트 조건이 충족되지 않았습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(context, "이미 완료한 퀘스트 입니다.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

        });


        return convertView;
    }


    public void addItem(){
        Quest item;
        ArrayList<Quest> questList = QuestList.getList();
        for( int i = 0; i< questList.size(); i++) {
            item = questList.get(i);
            listViewItemList.add(item);

        }
    }

    public void clearItem(){
        listViewItemList.clear();

    }

}
