package com.qihaocai.scrapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrackSelector extends AppCompatActivity {


    private static final String TAG = "TrackMaker";

    ListView menu;
    ArrayList<String> fileNames = new ArrayList<String>();


//    File file = new File(this.getFilesDir(), "/data/user/0/com.qihaocai.scrapp/files");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_selector);

        menu = findViewById(R.id.menu);


        //Est path
        File files = new File("/data/user/0/com.qihaocai.scrapp/files/Tracks");

        if (files.isDirectory() != true) {
            files.mkdirs();

            File[] DirList = files.listFiles();

            Log.d(TAG, "length of list " + DirList.length);

            //add them to the spinner array (this makes it crash)
            for (int i = 0; i < DirList.length; i++) {
                //if(DirList[i].getName().endsWith(".txt")){
                fileNames.add(DirList[i].getName());
                //}
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileNames);
            menu.setAdapter(adapter);

            menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(TrackSelector.this, "Clicked", Toast.LENGTH_SHORT).show();
                    String name = fileNames.get(i);
                    Intent TrackSelectorConfirm =new Intent(TrackSelector.this, TrackSelectorConfirm.class).putExtra("<StringName>", name);
                    startActivity(TrackSelectorConfirm);

                }
            });
        }
        else {
            //gets a list of the files
            File[] DirList = files.listFiles();

            Log.d(TAG, "length of list " + DirList.length);

            //add them to the spinner array (this makes it crash)
            for (int i = 0; i < DirList.length; i++) {
                //if(DirList[i].getName().endsWith(".txt")){
                fileNames.add(DirList[i].getName());
                //}
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileNames);
            menu.setAdapter(adapter);

            menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(TrackSelector.this, "Clicked", Toast.LENGTH_SHORT).show();
                    String name = fileNames.get(i);
                    Intent TrackSelectorConfirm =new Intent(TrackSelector.this, TrackSelectorConfirm.class).putExtra("<StringName>", name);
                    startActivity(TrackSelectorConfirm);

                }
            });
            //String[] TrackNames = getResources().getStringArray(R.array.)
        }
    }
}