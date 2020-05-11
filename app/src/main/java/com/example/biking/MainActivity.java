package com.example.biking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.biking.ListView.questListAdaptor;
import com.example.biking.ListView.rankingListAdaptor;
import com.example.biking.Quest.CreateQuest;
import com.example.biking.Quest.QuestList;


public class MainActivity extends AppCompatActivity {
    Button homeButton;
    Button rankingButton;
    Button questButton;
    Button myPageButton;

    Button riddingButton;

    FrameLayout home;
    FrameLayout quest;
    FrameLayout myPage;
    FrameLayout ranking;

    FrameLayout riddingMode;

    Button challenge;
    Button challenge1;
    Button challenge2;
    Button challenge3;
    Button withMe;
    Button none;

    //권혜영 테스트용 추가
    TextView name;

    ImageView chaImage;

    private ListView rankingListView;
    private rankingListAdaptor rankingAdapter;

    private ListView questListView;
    private questListAdaptor questAdapter;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseDB.updateTempBase();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final int mainColor = getResources().getColor(R.color.main_color);
        final int subColor = getResources().getColor(R.color.sub_color);
        final int pointColor = getResources().getColor(R.color.point_color);

        homeButton = (Button)findViewById(R.id.main_button_home);
        rankingButton =(Button)findViewById(R.id.main_button_ranking);
        questButton = (Button)findViewById(R.id.main_button_quest);
        myPageButton = (Button)findViewById(R.id.main_button_my);

        riddingButton = (Button)findViewById(R.id.main_home_button_rriding);

        home= (FrameLayout)findViewById(R.id.main);
        quest= (FrameLayout)findViewById(R.id.quest);
        myPage= (FrameLayout)findViewById(R.id.myPage);
        ranking= (FrameLayout)findViewById(R.id.ranking);

        riddingMode =(FrameLayout) findViewById(R.id.riddingMode);

        challenge = (Button) findViewById(R.id.main_button_riddingMode_challenge);
        challenge1 = (Button) findViewById(R.id.main_button_riddingMode_challenge_1);
        challenge2 = (Button) findViewById(R.id.main_button_riddingMode_challenge_2);
        challenge3 = (Button) findViewById(R.id.main_button_riddingMode_challenge_3);
        withMe = (Button) findViewById(R.id.main_button_riddingMode_WithMe);
        none = (Button) findViewById(R.id.main_button_riddingMode_none);

        // 메인 캐릭터 이미지 설정
        chaImage = (ImageView) findViewById(R.id.cha_image);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(chaImage);
        Glide.with(this).load(R.drawable.cha_biking).into(gifImage);

        //메인화면 사용자 정보 업데이트
        name = (TextView) findViewById(R.id.textView);
        name.setText(firebaseDB.myUser.getUsername()+"님 환영합니다.");

        homeButton.setBackgroundColor(pointColor); // 디폴트 메인 화면



        // 하단바 버튼 시작

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home.setVisibility(View.VISIBLE);
                ranking.setVisibility(View.GONE);
                quest.setVisibility(View.GONE);
                myPage.setVisibility(View.GONE);
                riddingMode.setVisibility(View.GONE);


                homeButton.setBackgroundColor(pointColor);
                rankingButton.setBackgroundColor(mainColor);
                questButton.setBackgroundColor(mainColor);
                myPageButton.setBackgroundColor(mainColor);

            }
        });

        rankingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home.setVisibility(View.GONE);
                ranking.setVisibility(View.VISIBLE);
                quest.setVisibility(View.GONE);
                myPage.setVisibility(View.GONE);
                riddingMode.setVisibility(View.GONE);


                homeButton.setBackgroundColor(mainColor);
                rankingButton.setBackgroundColor(pointColor);
                questButton.setBackgroundColor(mainColor);
                myPageButton.setBackgroundColor(mainColor);

                rankingListView = (ListView) findViewById(R.id.ranking_listview);
                rankingAdapter = new rankingListAdaptor();

                rankingListView.setAdapter(rankingAdapter);
                for(int i = 1 ;i< 15; i+=3){
                    rankingAdapter.addItem(i,"홍길동","500000",R.drawable.pause);
                    rankingAdapter.addItem(i+1,"안길동","500000",R.drawable.pause);
                    rankingAdapter.addItem(i+2,"이길동","500000",R.drawable.pause);
                }
                rankingAdapter.notifyDataSetChanged();

            }
        });


        questButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home.setVisibility(View.GONE);
                ranking.setVisibility(View.GONE);
                quest.setVisibility(View.VISIBLE);
                myPage.setVisibility(View.GONE);
                riddingMode.setVisibility(View.GONE);


                homeButton.setBackgroundColor(mainColor);
                rankingButton.setBackgroundColor(mainColor);
                questButton.setBackgroundColor(pointColor);
                myPageButton.setBackgroundColor(mainColor);

                questListView = (ListView) findViewById(R.id.quest_listview);
                questAdapter = new questListAdaptor();

                CreateQuest.makeQuest();
                QuestList.checkQuest();
                QuestList.sortList();
                questListView.setAdapter(questAdapter);
                questAdapter.addItem();


                questAdapter.notifyDataSetChanged();





            }
        });


        myPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home.setVisibility(View.GONE);
                ranking.setVisibility(View.GONE);
                quest.setVisibility(View.GONE);
                myPage.setVisibility(View.VISIBLE);
                riddingMode.setVisibility(View.GONE);

                homeButton.setBackgroundColor(mainColor);
                rankingButton.setBackgroundColor(mainColor);
                questButton.setBackgroundColor(mainColor);
                myPageButton.setBackgroundColor(pointColor);

            }
        });
        //하단 바 버튼 종료

        //home -> 라이딩 기능 버튼
        riddingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home.setVisibility(View.GONE);
                ranking.setVisibility(View.GONE);
                quest.setVisibility(View.GONE);
                myPage.setVisibility(View.GONE);
                riddingMode.setVisibility(View.VISIBLE);

                challenge.setVisibility(View.VISIBLE);

                homeButton.setBackgroundColor(pointColor);
                rankingButton.setBackgroundColor(mainColor);
                questButton.setBackgroundColor(mainColor);
                myPageButton.setBackgroundColor(mainColor);

            }
        });

        // home->rriding -> 내부 버튼

        challenge.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                challenge.setVisibility(View.GONE);
            }

        });

        challenge1.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("mode", "challenge");
                intent.putExtra("challengeLevel", 1);
                intent.putExtra("time",10);
                intent.putExtra("km",1);
                startActivity(intent);

            }
        });

        challenge2.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("mode", "challenge");
                intent.putExtra("challengeLevel", 2);
                intent.putExtra("time",60);
                intent.putExtra("km",10);
                startActivity(intent);

            }
        });

        challenge3.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("mode", "challenge");
                intent.putExtra("challengeLevel", 3);
                intent.putExtra("time",60);
                intent.putExtra("km",15);
                startActivity(intent);

            }
        });

        withMe.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view){
                int time = firebaseDB.myUser.getBestRecord().getTime();
                int dis = firebaseDB.myUser.getBestRecord().getDistance();
                int goal = dis/time*60;
                if(goal == 0) goal = 1;
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("mode", "withMe");
                intent.putExtra("time",60);
                intent.putExtra("km",goal);
                startActivity(intent);}

        });

        none.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("mode", "none");
                startActivity(intent);
            }
        });
    }
}
