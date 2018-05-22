package net.karlitos.supersimplekeyboard;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.app.BundleCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RemoteViews;

public class CustomTabHelper {

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

    /*
    private CustomTabsSession mCustomTabsSession;
    private CustomTabsClient mClient;
    private CustomTabsServiceConnection mConnection;
*/

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

    public void openCustomTabWithRemoteView(String url, boolean reopen){
        if(reopen){
            sendCustomTabAction(ACTION_REOPEN);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Bundle bundle = new Bundle();
            BundleCompat.putBinder(bundle, "android.support.customtabs.extra.SESSION", null);
            intent.putExtras(bundle);
            intent.setPackage("com.microsoft.emmx.development");
            intent.putExtra("android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS", true);

            intent.setData(Uri.parse(url));

            Resources resources = mContext.getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            float density = dm.density;
            int width = dm.widthPixels;
            int height = dm.heightPixels;
            int tabHeight = (int)(height * 0.8);
            //custom size
            intent.putExtra("com.microsoft.emmx.customtabs.EXTRA_DISPLAY_STYLE", "windowed");
            //intent.putExtra("com.microsoft.emmx.customtabs.EXTRA_SIZE_WIDTH", width);
            intent.putExtra("com.microsoft.emmx.customtabs.EXTRA_SIZE_HEIGHT", tabHeight);
            //intent.putExtra("com.microsoft.emmx.customtabs.EXTRA_OFFSET_X", 0);
            intent.putExtra("com.microsoft.emmx.customtabs.EXTRA_OFFSET_Y", (int)(height * 0.2));

            prepareBottombar(intent);

            mContext.startActivity(intent);
        }
    }

    public PendingIntent createPendingIntent() {
        Intent actionIntent = new Intent(
                mContext.getApplicationContext(), ActionBroadcastReceiver.class);
        return PendingIntent.getBroadcast(
                mContext, 0, actionIntent, 0);
    }

/*
    public void openCustomTabWithRemoteViewSession(String url, boolean reopen){
        if(reopen){
            sendCustomTabActionSession(ACTION_REOPEN);
        } else {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(getSession());
            CustomTabsIntent customTabsIntent = builder.build();

            Intent intent = customTabsIntent.intent;

            intent.setData(Uri.parse(url));

            prepareBottombar( intent);

            mContext.startActivity(intent);
        }
    }
*/
    private void prepareBottombar(Intent intent) {
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

    public void sendCustomTabAction(String actionName){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Bundle bundle = new Bundle();
        BundleCompat.putBinder(bundle, "android.support.customtabs.extra.SESSION", null);
        intent.putExtras(bundle);
        intent.setPackage("com.microsoft.emmx.development");
        intent.putExtra("android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS", true);
        intent.setData(Uri.parse("http://"));

        Bundle actionBundle = new Bundle();
        actionBundle.putString(CustomTabHelper.ACTION_NAME, actionName);
        PendingIntent actionPendingIntent = createPendingIntent();
        actionBundle.putParcelable("pendingIntent",actionPendingIntent);
        intent.putExtra(CustomTabHelper.EXTRA_ACTION, actionBundle);

        mContext.startActivity(intent);
    }
/*
    public void sendCustomTabActionSession(String actionName){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(getSession());
        CustomTabsIntent customTabsIntent = builder.build();

        Intent intent = customTabsIntent.intent;

        intent.setData(Uri.parse("http://"));

        Bundle actionBundle = new Bundle();
        actionBundle.putString(CustomTabHelper.ACTION_NAME, actionName);
        PendingIntent actionPendingIntent = createPendingIntent( actionName);
        actionBundle.putParcelable("pendingIntent",actionPendingIntent);
        intent.putExtra(CustomTabHelper.EXTRA_ACTION, actionBundle);

        mContext.startActivity(intent);
    }

    public void sendAction(String action){
        mCustomTabsSession.postMessage(action,null);
    }
    */
/*
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
        } else if (mCustomTabsSession == null) {
            mCustomTabsSession = mClient.newSession(new NavigationCallback());
        }

        boolean getchannel = mCustomTabsSession.requestPostMessageChannel(Uri.parse("swiftkey"));
        if(getchannel){
            Log.d("test","get channel");
        }

        return mCustomTabsSession;
    }
*/
/*
    @Override
    public void onServiceConnected(CustomTabsClient client) {
        mClient = client;
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
            CustomTabHelper.getInstance().sendAction("test");
        }

        @Override
        public void onPostMessage(String message, Bundle extras) {}
    }
*/
}
