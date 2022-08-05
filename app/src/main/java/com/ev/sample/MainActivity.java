package com.ev.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.ev.easyratingbar.EasyRatingBar;

public class MainActivity extends AppCompatActivity{
    private EasyRatingBar mStarRating, mHeartRating, mPlaneRating;
    private TextView mTvStar, mTvHeart, mTvPlane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStarRating = findViewById(R.id.erb_star);
        mHeartRating = findViewById(R.id.erb_heart);
        mPlaneRating = findViewById(R.id.erb_airplane);
        mTvStar = findViewById(R.id.tv_star);
        mTvHeart = findViewById(R.id.tv_heart);
        mTvPlane = findViewById(R.id.tv_airplane);

        mStarRating.setOnRatingSeekListener(newRate -> mTvStar.setText("current rate: " + newRate));
        mHeartRating.setOnRatingSeekListener(newRate -> mTvHeart.setText("current rate: " + newRate));
        mPlaneRating.setOnRatingSeekListener(newRate -> mTvPlane.setText("current rate: " + newRate));
    }
}