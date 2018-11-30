package net.karlitos.supersimplekeyboard;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.app.BundleCompat;
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

    RemoteViews mRemoteViews;

    private static class CustomTabHelperHolder{
        static final CustomTabHelper INSTANCE = new CustomTabHelper();
    }

    public static CustomTabHelper getInstance() {
        return CustomTabHelperHolder.INSTANCE;
    }
    public void setContext(Context context){
        mContext = context.getApplicationContext();
        int t = edgeSupportsNewAPI(mContext);
    }

    int edgeSupportsNewAPI (Context context){
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo("com.microsoft.emmx.development", PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getInt("com.microsoft.emmx.customtab.version");
        } catch (Exception e) {

        }
        return 0;
    }

    public PendingIntent createPendingIntent() {
        Intent actionIntent = new Intent(
                mContext.getApplicationContext(), ActionBroadcastReceiver.class);
        return PendingIntent.getBroadcast(
                mContext, 0, actionIntent, 0);
    }

    public void openCustomTabWithRemoteViewSession(String url) {
        //if(getSession()==null)
        //    return;
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(null);
        //builder.setStartAnimations(mContext, R.anim.slide_in_right, R.anim.slide_out_left);
        //builder.setExitAnimations(mContext, R.anim.slide_in_left, R.anim.slide_out_right);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.intent.setPackage("com.microsoft.emmx.selfhost");
        customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        prepareBottombar(customTabsIntent.intent);

        Resources resources = mContext.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density = dm.density;
        int width = (int)(dm.widthPixels/density);
        int height = (int)(dm.heightPixels/density);
        int tabHeight = (int) (height * 0.6);
        //custom size
        customTabsIntent.intent.putExtra("com.microsoft.emmx.customtabs.DISPLAY_STYLE", "windowed");
        customTabsIntent.intent.putExtra("com.microsoft.emmx.customtabs.OFFSET_TOP", "300dp");
        //customTabsIntent.intent.putExtra("com.microsoft.emmx.customtabs.OFFSET_BOTTOM", "20%");
        //customTabsIntent.intent.putExtra("com.microsoft.emmx.customtabs.OFFSET_START", "10%");
        //customTabsIntent.intent.putExtra("com.microsoft.emmx.customtabs.OFFSET_END", "20%");

        Bundle landScape = new Bundle();
        landScape.putString("com.microsoft.emmx.customtabs.DISPLAY_STYLE", "windowed");
        landScape.putString("com.microsoft.emmx.customtabs.OFFSET_TOP", "100dp");
        landScape.putString("com.microsoft.emmx.customtabs.OFFSET_BOTTOM", "20%");
        landScape.putString("com.microsoft.emmx.customtabs.OFFSET_START", "100dp");
        landScape.putString("com.microsoft.emmx.customtabs.OFFSET_END", "20%");
        customTabsIntent.intent.putExtra("com.microsoft.emmx.customtabs.OFFSET_LANDSCAPE", landScape);

        customTabsIntent.intent.putExtra("com.microsoft.emmx.customtabs.DIM_AMOUNT", 0.5f);

        customTabsIntent.intent.putExtra("com.microsoft.emmx.customtabs.ADDRESS_EDITABLE", true);
        customTabsIntent.intent.putExtra("com.microsoft.emmx.customtabs.AUTO_HIDE_TOOLBAR", false);

        ArrayList<String> showItems = new ArrayList<>();
        showItems.add("add_to_readinglist");
        showItems.add("download_page");
        showItems.add("site_info");
        showItems.add("row_menu");
        showItems.add("request_desktop_site");
        showItems.add("add_to_home_screen");
        //showItems.add("share");
        showItems.add("reload");

        ArrayList<String> hideItems = new ArrayList<>();
        hideItems.add("go_forward");
        customTabsIntent.intent.putStringArrayListExtra("com.microsoft.emmx.customtabs.overflow_menu.MENU_ITEM_HIDE", showItems);

        customTabsIntent.intent.putExtra("com.microsoft.emmx.customtabs.CLOSE_BUTTON.ACTION", "hide");

        customTabsIntent.intent.putExtra("com.microsoft.emmx.customtabs.OPEN_INCOGNITO", false);

        customTabsIntent.intent.putExtra("com.microsoft.emmx.customtabs.PENDING_INTENT",getOnClickPendingIntent(mContext));

        customTabsIntent.intent.putExtra("com.microsoft.emmx.customtabs.HOST_APP_PACKAGE_NAME",mContext.getPackageName());

        customTabsIntent.intent.putExtra("com.microsoft.emmx.customtabs.RECENT_TASK_DESCRIPTION", "test task description");
        customTabsIntent.intent.putExtra("com.microsoft.emmx.customtabs.RECENT_TASK_ICON", BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_voice_search));

        customTabsIntent.intent.putExtra("android.support.customtabs.extra.TOOLBAR_COLOR", Color.parseColor("#787878"));
        customTabsIntent.launchUrl(mContext,Uri.parse(url));
    }

    public void sendExtraCommand(String command){
        String url = "http://";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Bundle bundle = new Bundle();
        BundleCompat.putBinder(bundle, "android.support.customtabs.extra.SESSION", null);
        intent.putExtras(bundle);
        intent.setData(Uri.parse(url));
        intent.setPackage("com.microsoft.emmx.development");
        //intent.putExtra("android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS", true);
        intent.putExtra("com.microsoft.emmx.customtabs.COMMAND",command);

        mContext.startActivity(intent);
    }

    private void  prepareBottombar(Intent intent) {
        intent.putExtra(EXTRA_REMOTEVIEWS, createRemoteViews(mContext, true));
        intent.putExtra(EXTRA_REMOTEVIEWS_VIEW_IDS, getClickableIDs());
        intent.putExtra(EXTRA_REMOTEVIEWS_PENDINGINTENT, getOnClickPendingIntent(mContext));
    }

    public void updateRemoteView(boolean leftenable, boolean rightenable){
        String url = "http://";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Bundle bundle = new Bundle();
        BundleCompat.putBinder(bundle, "android.support.customtabs.extra.SESSION", null);
        intent.putExtras(bundle);
        intent.setData(Uri.parse(url));
        intent.setPackage("com.microsoft.emmx.development");
        //intent.putExtra("android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS", true);
        intent.putExtra("com.microsoft.emmx.customtabs.COMMAND","update_remoteview");

        Bundle data = new Bundle();
        mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.remote_view);
        mRemoteViews.setImageViewResource(R.id.go_back,leftenable? R.drawable.left_enable:R.drawable.left_disable);
        mRemoteViews.setImageViewResource(R.id.go_forward,rightenable? R.drawable.right_enable:R.drawable.right_disable);
        data.putParcelable(EXTRA_REMOTEVIEWS, mRemoteViews);
        data.putIntArray(EXTRA_REMOTEVIEWS_VIEW_IDS, getClickableIDs());
        data.putParcelable(EXTRA_REMOTEVIEWS_PENDINGINTENT, getOnClickPendingIntent(mContext));
        intent.putExtra("com.microsoft.emmx.customtabs.COMMAND_DATA", data);
        intent.putExtras(bundle);

        mContext.startActivity(intent);
    }

    public RemoteViews createRemoteViews(Context context, boolean showPlayIcon) {
        mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.remote_view);
        return mRemoteViews;
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
            //bindCustomTabsService();
            return null;
        }
    }

    public void bindCustomTabsService() {
        if (mClient != null) return;

        mConnection = new ServiceConnection(this);
        boolean ok = CustomTabsClient.bindCustomTabsService(mContext, "com.microsoft.emmx.development", mConnection);
        if (ok) {

        } else {
            mConnection = null;
        }
    }

    private CustomTabsSession getSession() {
        if (mClient == null) {
            mCustomTabsSession = null;
            //bindCustomTabsService();
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
        mCustomTabsSession = null;
    }

    private class NavigationCallback extends CustomTabsCallback {
        @Override
        public void onNavigationEvent(int navigationEvent, Bundle extras) {
            boolean cangoback = extras.getBoolean("can_go_back");
            boolean cangoforward = extras.getBoolean("can_go_forward");
            Log.w("test", "onNavigationEvent: Code = " + navigationEvent);
        }

        @Override
        public void onMessageChannelReady(Bundle extras) {
        }

        @Override
        public void onPostMessage(String message, Bundle extras) {}
    }
}
