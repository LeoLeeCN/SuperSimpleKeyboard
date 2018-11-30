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
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * A BroadcastReceiver that handles the Action Intent from the Custom Tab and shows the Url
 * in a Toast.
 */
public class ActionBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle tabstatus = intent.getBundleExtra("tab_status");
                if(tabstatus!=null) {
                    boolean cangoback = tabstatus.getBoolean("can_go_back");
                    boolean cangoforward = tabstatus.getBoolean("can_go_forward");
                    int navigationEvent = tabstatus.getInt("navigation_event");
                    boolean ableToUpdateRemoteview = tabstatus.getBoolean("able_to_update_remoteview");
                    if(ableToUpdateRemoteview)
                        CustomTabHelper.getInstance().updateRemoteView(cangoback,cangoforward);
                    return;
                }
                if (intent == null) return;
                try {
                    int clickedId = intent.getIntExtra(CustomTabHelper.EXTRA_REMOTEVIEWS_CLICKED_ID, -1);
                    if (clickedId!=-1) {
                        if (clickedId == R.id.go_back) {
                            //CustomTabHelper.getInstance().sendCustomTabActionSession(CustomTabHelper.ACTION_BACK, null);
                            CustomTabHelper.getInstance().sendExtraCommand("goback");
                        } else if (clickedId == R.id.go_forward) {
                            CustomTabHelper.getInstance().sendCustomTabActionSession(CustomTabHelper.ACTION_FORWARD, null);
                            CustomTabHelper.getInstance().sendExtraCommand("forward");
                        } else if (clickedId == R.id.share) {
                            //Bundle result = CustomTabHelper.getInstance().sendCustomTabActionSession(CustomTabHelper.ACTION_SHARE, null);
                            CustomTabHelper.getInstance().sendExtraCommand("get_siteinfo");
                        } else if (clickedId == R.id.screenshot) {
                            //Bundle bundle = new Bundle();
                            //bundle.putParcelable("pendingIntent",CustomTabHelper.getInstance().createPendingIntent());
                            //CustomTabHelper.getInstance().sendCustomTabActionSession(CustomTabHelper.ACTION_SCREENSHOT, bundle);
                            CustomTabHelper.getInstance().sendExtraCommand("screenshot");
                        }
                        return;
                    }

                    String proactiveActionName = intent.getStringExtra("command_name");
                    if(!TextUtils.isEmpty(proactiveActionName)) {
                        CustomTabHelper.getInstance().sendExtraCommand("close");
                        if (proactiveActionName.equals(CustomTabHelper.ACTION_SCREENSHOT)) {
                            Bundle bundle = intent.getBundleExtra("siteinfo");
                            String url = bundle.getString("url");
                            String title = bundle.getString("title");
                            //CustomTabHelper.getInstance().sendCustomTabActionSession(CustomTabHelper.ACTION_HIDE,null);
                            Uri uri = (Uri) (intent.getParcelableExtra("imageUri"));
                            Intent kintent = new Intent(context, KeyboardService.class);
                            kintent.putExtra("imageUri", uri);
                            context.startService(kintent);
                            return;
                        } else if (proactiveActionName.equals("get_siteinfo")) {
                            Bundle bundle = intent.getBundleExtra("siteinfo");
                            String url = bundle.getString("url");
                            String title = bundle.getString("title");

                            Intent kintent = new Intent(context, KeyboardService.class);
                            kintent.putExtra("title", title);
                            kintent.putExtra("url", url);
                            context.startService(kintent);
                        }
                    }
                } catch (Exception e) {

                }
            }
        }).start();
    }
}

