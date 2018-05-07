package net.karlitos.supersimplekeyboard;

// Copyright 2015 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import android.support.v4.content.FileProvider;

import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;

/**
 * A BroadcastReceiver that handles the Action Intent from the Custom Tab and shows the Url
 * in a Toast.
 */
public class ActionBroadcastReceiver extends BroadcastReceiver {

    public static final String KEY_ACTION_SOURCE = "swiftkey.customtab.action";
    public static final int ACTION_SCREENSHOT = 1;
    public static final int ACTION_BACK = 2;
    public static final int ACTION_SHARE = 3;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (intent == null) return;
                    int actionId = intent.getIntExtra(KEY_ACTION_SOURCE, -1);
                    if (actionId == ActionBroadcastReceiver.ACTION_SCREENSHOT) {
                        Uri uri = (Uri)(intent.getParcelableExtra("imageUri"));
                        //String path = intent.getStringExtra("imagePath");
                        Intent kintent = new Intent(context, KeyboardService.class);
                        kintent.putExtra("imageUri", uri);
                        context.startService(kintent);
                        return;
                    } else {
                        String url = intent.getParcelableExtra("url");
                        return;
                    }
                }catch (Exception e){

                }
            }
        }).start();

        //requestPermission(context);
        /*
        List<String> images = (List<String>)intent.getSerializableExtra("images");
        for (String path:images) {
            File file = new File(path);
            String filename = file.getName();
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(new File(path).getAbsolutePath());
                ((KeyboardService)context).showImage(bitmap,path);
                File sharePath = new File(context.getFilesDir(), "images");
                sharePath.mkdirs(); // don't forget to make the directory
                FileOutputStream stream = new FileOutputStream(sharePath + "/"+filename); // overwrites this image every time
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                stream.close();
            }catch (Exception e){
                Log.d("123",e.toString());
            }

            File imagePath = new File(context.getFilesDir(), "images");
            File newFile = new File(imagePath, filename);

            //final Uri contentUri = FileProvider.getUriForFile(context, "net.karlitos.supersimplekeyboard", newFile);
            //((KeyboardService)context).commitImage(contentUri,"test1234");
            Toast.makeText(context, "actionId:"+ actionId +" recieve image: "+path, Toast.LENGTH_SHORT).show();
        }
        */
    }
/*
    private boolean requestPermission(Context context) {
        //判断Android版本是否大于23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return true;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
    */
}

