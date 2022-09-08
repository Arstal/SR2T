package com.qihaocai.scrapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.Application;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.io.File;

public class YesnoDialog extends AppCompatDialogFragment {

    private static final String TAG = "YesnoDialog";
    String name;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete")
                .setMessage("are you sure you want to delete this file")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "\n-------------------------------: " + name);


                        File dir = new File("/data/user/0/com.qihaocai.scrapp/files/Tracks");
                        File file = new File(dir, name);
                        boolean deleted = file.delete();

                        Intent intent = new Intent(getContext(),TrackSelector.class);
                        getContext().startActivity(intent);



                        Log.d(TAG, "did i delete it?: " + deleted);


                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        return builder.create();
    }

    public void setValue(String name){
        this.name = name;
    }


}
