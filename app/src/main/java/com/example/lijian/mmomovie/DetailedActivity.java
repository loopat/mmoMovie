package com.example.lijian.mmomovie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.suitebuilder.TestMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lijian.mmomovie.R;
import com.squareup.picasso.Picasso;

public class DetailedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        Intent intent = getIntent();

        String []detailStr = intent.getStringArrayExtra(getString(R.string.intent_key));

        if(detailStr == null){
            return;
        }

        TextView tilteView = (TextView)findViewById(R.id.Detailed_Title);
        tilteView.setText(detailStr[0]);

        ImageView imageView = (ImageView)findViewById(R.id.Detailed_Image);
        Picasso.with(this)
                .load(detailStr[1])
                .into(imageView);

        TextView introView = (TextView)findViewById(R.id.Detailed_Intro);

        if(detailStr[2].length() == 0){
            detailStr[2] = getString(R.string.detailed_no_detail);
        }
        introView.setText(detailStr[2]);

        TextView scoreView = (TextView)findViewById(R.id.Detailed_Scroe);
        scoreView.setText(getString(R.string.detailed_score) + detailStr[3]);

        TextView dateView = (TextView)findViewById(R.id.Detailed_Date);
        dateView.setText(getString(R.string.detailed_date) + detailStr[4]);
    }
}


