package net.karlitos.supersimplekeyboard;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;

public class CustomTabHelper implements ServiceConnectionCallback{

    public static final String EXTRA_REMOTEVIEWS =
            "android.support.customtabs.extra.EXTRA_REMOTEVIEWS";
    public static final String EXTRA_REMOTEVIEWS_VIEW_IDS =
            "android.support.customtabs.extra.EXTRA_REMOTEVIEWS_VIEW_IDS";
    public static final String EXTRA_REMOTEVIEWS_PENDINGINTENT =
            "android.support.customtabs.extra.EXTRA_REMOTEVIEWS_PENDINGINTENT";

    public static final String EXTRA_REMOTEVIEWS_CLICKED_ID =
            "android.support.customtabs.extra.EXTRA_REMOTEVIEWS_CLICKED_ID";

    public static final String EXTRA_ACTION =
            "com.microsoft.emmx.customtabs.extra.EXTRA_ACTION";

    //action list
    public static final String ACTION_SCREENSHOT = "screenshot";
    public static final String ACTION_BACK = "goback";
    public static final String ACTION_FORWARD = "forward";
    public static final String ACTION_SHARE = "share";
    public static final String ACTION_REOPEN = "reopen";
    public static final String ACTION_HIDE = "hide";
    public static final String ACTION_CLOSE = "close";

    public static final String ACTION_NAME = "com.microsoft.emmx.customtabs.extra.ACTION_NAME";

    private CustomTabsSession mCustomTabsSession;
    private CustomTabsClient mClient;
    private CustomTabsServiceConnection mConnection;

    private Context mContext;

    private static class CustomTabHelperHolder{
        static final CustomTabHelper INSTANCE = new CustomTabHelper();
    }

    public static CustomTabHelper getInstance() {
        return CustomTabHelperHolder.INSTANCE;
    }
    public void setContext(Context context){
        mContext = context.getApplicationContext();
    }

    public PendingIntent createPendingIntent() {
        Intent actionIntent = new Intent(
                mContext.getApplicationContext(), ActionBroadcastReceiver.class);
        return PendingIntent.getBroadcast(
                mContext, 0, actionIntent, 0);
    }


    public void openCustomTabWithRemoteViewSession(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(getSession());
        builder.setStartAnimations(mContext, R.anim.slide_in_right, R.anim.slide_out_left);
        builder.setExitAnimations(mContext, R.anim.slide_in_left, R.anim.slide_out_right);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        prepareBottombar(customTabsIntent.intent);

        Resources resources = mContext.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density = dm.density;
        int width = (int)(dm.widthPixels/density);
        int height = (int)(dm.heightPixels/density);
        int tabHeight = (int) (height * 0.6);
        //custom size
        customTabsIntent.intent.putExtra("com.microsoft.emmx.customtabs.EXTRA_DISPLAY_STYLE", "windowed");
        //intent.putExtra("com.microsoft.emmx.customtabs.EXTRA_SIZE_WIDTH", width);
        customTabsIntent.intent.putExtra("com.microsoft.emmx.customtabs.EXTRA_SIZE_HEIGHT", tabHeight);
        //intent.putExtra("com.microsoft.emmx.customtabs.EXTRA_OFFSET_X", 0);
        customTabsIntent.intent.putExtra("com.microsoft.emmx.customtabs.EXTRA_OFFSET_Y", (int) (height * 0.4));

        customTabsIntent.intent.putExtra("com.microsoft.emmx.customtabs.EXTRA_ADDRESS_EDITABLE", true);

        ArrayList<String> showItems = new ArrayList<>();
        showItems.add("add_to_readinglist");
        showItems.add("download_page");
        showItems.add("site_info");
        showItems.add("row_menu");
        showItems.add("request_desktop_site");
        showItems.add("add_to_home_screen");
        showItems.add("share");
        customTabsIntent.intent.putStringArrayListExtra("com.microsoft.emmx.customtabs.overflow_menu.MENU_ITEM_SHOW", showItems);

        customTabsIntent.intent.putExtra("com.microsoft.emmx.customtabs.CLOSE_BUTTON.ACTION", "hide");

        customTabsIntent.launchUrl(mContext,Uri.parse(url));
    }

    private void  prepareBottombar(Intent intent) {
        intent.putExtra(EXTRA_REMOTEVIEWS, createRemoteViews(mContext, true));
        intent.putExtra(EXTRA_REMOTEVIEWS_VIEW_IDS, getClickableIDs());
        intent.putExtra(EXTRA_REMOTEVIEWS_PENDINGINTENT, getOnClickPendingIntent(mContext));
    }

    public RemoteViews createRemoteViews(Context context, boolean showPlayIcon) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.remote_view);
        return remoteViews;
    }

    public int[] getClickableIDs() {
        return new int[]{R.id.go_back, R.id.go_forward, R.id.share, R.id.screenshot};
    }

    public  PendingIntent getOnClickPendingIntent(Context context) {
        Intent broadcastIntent = new Intent(context, ActionBroadcastReceiver.class);
        return PendingIntent.getBroadcast(context, 0, broadcastIntent, 0);
    }

    public Bundle sendCustomTabActionSession(String actionName, Bundle bundle){
        if(getSession()!=null) {
            return getSession().edgeExtraCommand(actionName, bundle);
        } else {
            bindCustomTabsService();
            return null;
        }
    }

    public void bindCustomTabsService() {
        if (mClient != null) return;

        mConnection = new ServiceConnection(this);
        boolean ok = CustomTabsClient.bindCustomTabsService(mContext, "com.microsoft.emmx.development", mConnection);
        if (ok) {

        } else {

        }
    }

    private CustomTabsSession getSession() {
        if (mClient == null) {
            mCustomTabsSession = null;
            bindCustomTabsService();
        } else if (mCustomTabsSession == null) {
            mCustomTabsSession = mClient.newSession(new NavigationCallback());
        }
        return mCustomTabsSession;
    }


    @Override
    public void onServiceConnected(CustomTabsClient client) {
        mClient = client;
        mCustomTabsSession = mClient.newSession(new NavigationCallback());
    }

    @Override
    public void onServiceDisconnected() {
        mClient = null;
    }

    private static class NavigationCallback extends CustomTabsCallback {
        @Override
        public void onNavigationEvent(int navigationEvent, Bundle extras) {
            Log.w("test", "onNavigationEvent: Code = " + navigationEvent);
        }

        @Override
        public void onMessageChannelReady(Bundle extras) {
        }

        @Override
        public void onPostMessage(String message, Bundle extras) {}
    }
}
