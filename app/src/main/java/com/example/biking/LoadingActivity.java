package com.example.biking;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

public class LoadingActivity extends AppCompatActivity {
    ImageView chaImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        chaImage = (ImageView) findViewById(R.id.cha_image);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(chaImage);
        Glide.with(this).load(R.drawable.cha_biking).into(gifImage);
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //손가락으로 화면을 누르기 시작했을 때 할 일
                break;
            case MotionEvent.ACTION_MOVE:
                //터치 후 손가락을 움직일 때 할 일
                break;
            case MotionEvent.ACTION_UP:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case MotionEvent.ACTION_CANCEL:
                // 터치가 취소될 때 할 일
                break;
            default:
                break;
        }
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();
        firebaseDB.updateTempBase();
    }
    /*firebaseDB.updateTempBase();
        editTextEmail.setText(firebaseDB.tempBase.toString());*/
}