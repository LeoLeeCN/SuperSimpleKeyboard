package net.karlitos.supersimplekeyboard;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.BundleCompat;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class CustomTabHelper {

    public static void openCustomTab(Context context,String url) {
        //String url = "http://www.baidu.com";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Bundle bundle = new Bundle();
        BundleCompat.putBinder(bundle, "android.support.customtabs.extra.SESSION", null);
        intent.putExtras(bundle);
        intent.setData(Uri.parse(url));
        intent.setPackage("com.microsoft.emmx.development");
        intent.putExtra("android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS", true);

        Bundle screenshotBundle = new Bundle();
        screenshotBundle.putString("description", "tap to get screenshot");
        PendingIntent screenshotIntent = createPendingIntent(context.getApplicationContext(),ActionBroadcastReceiver.ACTION_SCREENSHOT);
        screenshotBundle.putParcelable("pendingIntent", screenshotIntent);
        intent.putExtra("microsoft.edge.tools.screenshot", screenshotBundle);

        Bundle backBundle = new Bundle();
        backBundle.putString("description", "tap to back");
        PendingIntent backIntent = createPendingIntent(context.getApplicationContext(),ActionBroadcastReceiver.ACTION_BACK);
        backBundle.putParcelable("pendingIntent", backIntent);
        intent.putExtra("microsoft.edge.tools.back", backBundle);

        Bundle shareBundle = new Bundle();
        shareBundle.putString("description", "tap to share");
        PendingIntent shareIntent = createPendingIntent(context.getApplicationContext(),ActionBroadcastReceiver.ACTION_SHARE);
        shareBundle.putParcelable("pendingIntent", shareIntent);
        intent.putExtra("microsoft.edge.tools.share", shareBundle);

        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density = dm.density;
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int tabHeight = (int)(height * 0.618);
        //custom size
        intent.putExtra("com.microsoft.emmx.customtabs.EXTRA_DISPLAY_STYLE", true);
        intent.putExtra("com.microsoft.emmx.customtabs.EXTRA_SIZE_WIDTH", width);
        intent.putExtra("com.microsoft.emmx.customtabs.EXTRA_SIZE_HEIGHT", tabHeight);
        intent.putExtra("com.microsoft.emmx.customtabs.EXTRA_OFFSET_X", 0);
        intent.putExtra("com.microsoft.emmx.customtabs.EXTRA_OFFSET_Y", 0);

        //Bundle startAnimationBundle= ActivityOptionsCompat.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
        context.startActivity(intent);
    }

    private static PendingIntent createPendingIntent(Context context ,int actionSourceId) {
        Intent actionIntent = new Intent(
                context.getApplicationContext(), ActionBroadcastReceiver.class);
        actionIntent.putExtra(ActionBroadcastReceiver.KEY_ACTION_SOURCE, actionSourceId);
        return PendingIntent.getBroadcast(
                context, actionSourceId, actionIntent, 0);
    }
}
