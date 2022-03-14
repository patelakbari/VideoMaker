package newwave.videomaker.statusmaker.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import newwave.videomaker.statusmaker.R;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdSettings;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;

import java.util.ArrayList;
import java.util.List;

public class Utils_Admob {

    public static void addTestDevide() {
        AdSettings.addTestDevice("855abad1-056a-463c-a3c1-36bb0643a036");
    }

    public static void inflateAd(Context context, NativeAd nativeAd, NativeAdLayout nativeAdLayout) {
        nativeAd.unregisterView();
        try {
            int i = 0;
            View view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.native_ad_layout_1, nativeAdLayout, false);
            nativeAdLayout.addView(view);
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ad_choices_container);
            AdOptionsView adOptionsView = new AdOptionsView(context, nativeAd, nativeAdLayout);
            linearLayout.removeAllViews();
            linearLayout.addView(adOptionsView, 0);
            TextView textView = (TextView) view.findViewById(R.id.native_ad_title);
            MediaView mediaView2 = (MediaView) view.findViewById(R.id.native_ad_media);
            TextView textView2 = (TextView) view.findViewById(R.id.native_ad_social_context);
            TextView textView3 = (TextView) view.findViewById(R.id.native_ad_body);
            TextView textView4 = (TextView) view.findViewById(R.id.native_ad_sponsored_label);
            Button button = (Button) view.findViewById(R.id.native_ad_call_to_action);
            textView.setText(nativeAd.getAdvertiserName());
            textView3.setText(nativeAd.getAdBodyText());
            textView2.setText(nativeAd.getAdSocialContext());
            if (!nativeAd.hasCallToAction()) {
                i = 4;
            }
            button.setVisibility(i);
            button.setText(nativeAd.getAdCallToAction());
            textView4.setText(nativeAd.getSponsoredTranslation());
            List arrayList = new ArrayList();
            arrayList.add(textView);
            arrayList.add(button);
            nativeAd.registerViewForInteraction(view, mediaView2, arrayList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void inflateCustomAd(Context context, NativeAd nativeAd, NativeAdLayout nativeAdLayout) {
        nativeAd.unregisterView();
        try {
            int i = 0;
            View view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.fb_custom_ad, nativeAdLayout, false);
            nativeAdLayout.addView(view);
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ad_choices_container);
            AdOptionsView adOptionsView = new AdOptionsView(context, nativeAd, nativeAdLayout);
            linearLayout.removeAllViews();
            linearLayout.addView(adOptionsView, 0);
            TextView textView = (TextView) view.findViewById(R.id.native_ad_title);
            MediaView mediaView2 = (MediaView) view.findViewById(R.id.native_ad_media);
            TextView textView2 = (TextView) view.findViewById(R.id.native_ad_social_context);
            TextView textView3 = (TextView) view.findViewById(R.id.native_ad_body);
            TextView textView4 = (TextView) view.findViewById(R.id.native_ad_sponsored_label);
            Button button = (Button) view.findViewById(R.id.native_ad_call_to_action);
            textView.setText(nativeAd.getAdvertiserName());
            textView3.setText(nativeAd.getAdBodyText());
            textView2.setText(nativeAd.getAdSocialContext());
            if (!nativeAd.hasCallToAction()) {
                i = 4;
            }
            button.setVisibility(i);
            button.setText(nativeAd.getAdCallToAction());
            textView4.setText(nativeAd.getSponsoredTranslation());
            List arrayList = new ArrayList();
            arrayList.add(textView);
            arrayList.add(button);
            nativeAd.registerViewForInteraction(view, mediaView2, arrayList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showCustomFBNativeAds(final Context context, final NativeAdLayout nativeAdLayout, int i) {
        final NativeAd nativeAd = new NativeAd(context, context.getString(R.string.fb_native));
        addTestDevide();
        nativeAd.loadAd();
        if (nativeAd.isAdLoaded()) {
            Utils_Admob.inflateCustomAd(context, nativeAd, nativeAdLayout);
        }
    }

    public static void showFBNativeAds(final Context context, final NativeAdLayout nativeAdLayout) {
        final NativeAd nativeAd = new NativeAd(context, context.getString(R.string.fb_native));
        addTestDevide();
        nativeAd.loadAd();

        if (nativeAd.isAdLoaded()) {
            Utils_Admob.inflateAd(context, nativeAd, nativeAdLayout);
        }
    }


    public interface MyListener {
        void callback(String str);

        void callback2(String str);
    }
}
