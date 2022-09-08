package com.qihaocai.scrapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TrackMakerMenu extends AppCompatActivity {

    Button MapMaker, BreadCrumbs,rec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_maker_menu);

        MapMaker = (Button) findViewById(R.id.MapMaker);
        BreadCrumbs = (Button) findViewById(R.id.BreadCrumbs);
        rec = (Button) findViewById(R.id.rec);
        rec.setVisibility(View.INVISIBLE);

        MapMaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent TrackSelector = new Intent(TrackMakerMenu.this, TrackMaker.class);
                startActivity(TrackSelector);
            }
        });

        BreadCrumbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapMaker.setVisibility(view.INVISIBLE);
                BreadCrumbs.setVisibility(view.INVISIBLE);
                rec.setVisibility(View.VISIBLE);
            }
        });

        rec.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });





    }
}