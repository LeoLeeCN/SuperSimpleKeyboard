package net.karlitos.supersimplekeyboard;


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
        intent.putExtra("microsoft.edge.tools.screenshot", true);

        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density = dm.density;
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int tabHeight = (int)(height * 0.618);
        //custom size
        intent.putExtra("custom_tab_dialog_style", true);
        intent.putExtra("custom_tab_size_width", width);
        intent.putExtra("custom_tab_size_height", tabHeight);
        intent.putExtra("custom_tab_offset_x", 0);
        intent.putExtra("custom_tab_offset_y", height - tabHeight);

        //Bundle startAnimationBundle= ActivityOptionsCompat.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
        context.startActivity(intent);
    }
}
