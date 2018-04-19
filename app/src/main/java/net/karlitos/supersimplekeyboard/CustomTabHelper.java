package net.karlitos.supersimplekeyboard;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.BundleCompat;

public class CustomTabHelper {

    public static void openCustomTab(Context context,String url){
        //String url = "http://www.baidu.com";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Bundle bundle = new Bundle();
        BundleCompat.putBinder(bundle, "android.support.customtabs.extra.SESSION", null);
        intent.putExtras(bundle);
        intent.setData(Uri.parse(url));
        intent.setPackage("com.microsoft.emmx.development");
        intent.putExtra("android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS", true);
        intent.putExtra("microsoft.edge.tools.screenshot",true);


                //custom size
                intent.putExtra("custom_tab_dialog_style",true);
                intent.putExtra("custom_tab_size_width",1080);
                intent.putExtra("custom_tab_size_height",1080);
                intent.putExtra("custom_tab_offset_x",0);
                intent.putExtra("custom_tab_offset_y",500);

        //Bundle startAnimationBundle= ActivityOptionsCompat.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
        context.startActivity(intent);
    }
}
