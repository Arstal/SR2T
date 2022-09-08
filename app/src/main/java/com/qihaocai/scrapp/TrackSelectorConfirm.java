package com.qihaocai.scrapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;

public class TrackSelectorConfirm extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_selector_confirm);

        TextView Title = (TextView) findViewById(R.id.name);
        Button Confirm = findViewById(R.id.confirm);
        Button Delete = findViewById(R.id.delete);

        Intent i = getIntent();
        String name = i.getStringExtra("<StringName>");


        if (i!=null) {
            Title.setText(name);
        }
        else{
            Title.setText("for testing: if this is here u fuked up");
        }


        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent TrackSelectorConfirm = new Intent(TrackSelectorConfirm.this, TougeRun.class).putExtra("<StringName>", name);;
                startActivity(TrackSelectorConfirm);
                //delete file
            }
        });

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YesnoDialog dialog = new YesnoDialog();
                dialog.setValue(name);
                dialog.show(getSupportFragmentManager(),"");



                //maybe add file name to the dialog
                // and delete file

            }
        });



    }


}