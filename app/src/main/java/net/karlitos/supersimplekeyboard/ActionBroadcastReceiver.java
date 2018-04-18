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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import android.support.v4.content.FileProvider;

import static android.provider.UserDictionary.AUTHORITY;

/**
 * A BroadcastReceiver that handles the Action Intent from the Custom Tab and shows the Url
 * in a Toast.
 */
public class ActionBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        List<String> images = (List<String>)intent.getSerializableExtra("images");
        for (String path:images) {
            File file = new File(path);
            String filename = file.getName();
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                File cachePath = new File(context.getCacheDir(), "images");
                cachePath.mkdirs(); // don't forget to make the directory
                FileOutputStream stream = new FileOutputStream(cachePath + "/"+filename+"image.png"); // overwrites this image every time
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.close();
            }catch (Exception e){

            }

            File imagePath = new File(context.getCacheDir(), "images");
            File newFile = new File(imagePath, filename+".png");

            final Uri contentUri = FileProvider.getUriForFile(context, "net.karlitos.supersimplekeyboard", newFile);
            ((KeyboardService)context).commitImage(contentUri,"test1234");
            //Toast.makeText(context, "recieve image: "+path, Toast.LENGTH_SHORT).show();
        }
    }
}

