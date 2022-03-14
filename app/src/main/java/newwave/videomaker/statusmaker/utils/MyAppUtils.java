package newwave.videomaker.statusmaker.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import newwave.videomaker.statusmaker.BuildConfig;
import newwave.videomaker.statusmaker.R;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class MyAppUtils {

    public static final String FOLDER_NAME = "MVMaker";
    public static final File RootDirectoryWhatsappShow = new File(Environment.getExternalStorageDirectory() + "/" + FOLDER_NAME + "/WhatsappStatus");


    // Add Your mobile test id here
    public static final String ADMOB_TEST_ID = "5186C6A964A218A7D3A51F4E3D7C6251";

    // Want to turn on Unity Ads? Make it false here
    public static final boolean ENABLE_UNITY_TEST_ADS = true;

    // Enter your PrivacyUrl
    public static final String PRIVACY_URL = "https://epsilonitservice.com/Privacy_policy/indiapp/newwave/newwave.html";

    // Create your Unity App Id, from Unity Dashboard(google it) and Add here.
    public static final String UNITY_APP_ID = "54542";

    // Turn on this if you want to serve ads.
    public static final Boolean IS_AD_ENABLED = true;


    private static boolean isAdShown = false;

    public static void createFileFolder() {

        if (!RootDirectoryWhatsappShow.exists()) {
            RootDirectoryWhatsappShow.mkdirs();
        }
    }

    public static boolean isConnectingToInternet(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean verifyInstallerId(Context context) {
        // A list with valid installers package name
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));

        // The package name of the app that has installed your app
        final String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());

        // true if your app has been downloaded from Play Store
        return installer != null && validInstallers.contains(installer);
    }


    public static void onlyInitializeAds(Activity context) {

        AudienceNetworkAds.initialize(context);
        MobileAds.initialize(context);
        UnityAds.initialize(context, MyAppUtils.UNITY_APP_ID, MyAppUtils.ENABLE_UNITY_TEST_ADS);

    }

    public static void showBannerAds(Activity context, LinearLayout bannerContainer, com.google.android.gms.ads.AdView googleAdView) {

        if (IS_AD_ENABLED) {
            AdRequest adRequest = new AdRequest.Builder().addTestDevice(ADMOB_TEST_ID).build();

            googleAdView.loadAd(adRequest);

            googleAdView.setAdListener(new com.google.android.gms.ads.AdListener() {

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);

                    // show Fb Banner ads when Admob fails to fill them

                    AdView fbAds = new AdView(context, context.getString(R.string.fb_banner_ad), AdSize.BANNER_HEIGHT_50);
                    bannerContainer.addView(fbAds);
                    fbAds.loadAd();

                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }
            });

        } else {

            Log.i("ADS", "Not Enabled, Turn on from 'MyUtils' on Production Mode");
        }


    }

    public static void setStatusBarTransparentFlag(Activity context) {

        View decorView = context.getWindow().getDecorView();
        decorView.setOnApplyWindowInsetsListener((v, insets) -> {

            WindowInsets defaultInsets = v.onApplyWindowInsets(insets);
            return defaultInsets.replaceSystemWindowInsets(
                    defaultInsets.getSystemWindowInsetLeft(),
                    0,
                    defaultInsets.getSystemWindowInsetRight(),
                    defaultInsets.getSystemWindowInsetBottom());

        });
        ViewCompat.requestApplyInsets(decorView);
        context.getWindow().setStatusBarColor(ContextCompat.getColor(context, android.R.color.transparent));

    }



    public static String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString() + " ";

    }

    public static void shareImage(Context context, String filePath) {


        String appLink = "\nhttps://play.google.com/store/apps/details?id=" + context.getPackageName();


        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.share_txt) + appLink);
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), filePath, "", null);
            Uri screenshotUri = Uri.parse(path);
            intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            intent.setType("image/*");
            context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.share_image_via)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void setUpRecycler(RecyclerView recyclerView) {


        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    public static void shareImageVideoOnWhatsapp(Context context, String filePath, boolean isVideo) {
        Uri imageUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", new File(filePath));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setPackage("com.whatsapp");
        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share) + context.getPackageName());
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        if (isVideo) {
            shareIntent.setType("video/*");
        } else {
            shareIntent.setType("image/*");
        }
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(shareIntent);
        } catch (Exception e) {
            setToast(context, "Whtasapp not installed.");
        }
    }

    public static void shareVideo(Context context, String filePath) {
        Uri mainUri = Uri.parse(filePath);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("video/mp4");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(Intent.createChooser(sharingIntent, "Share Video using"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No application found to open this file.", Toast.LENGTH_LONG).show();
        }
    }

    public static void setToast(Context context, String str) {
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void rateApp(Context context) {
        final String appName = context.getPackageName();
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
        }
    }




    public static boolean showInterstitialAds(InterstitialAd interstitialAd, com.google.android.gms.ads.InterstitialAd googleAds, Activity activity, String unityID) {


        if (IS_AD_ENABLED) {
            if (interstitialAd.isAdLoaded()) {
                interstitialAd.show();
                isAdShown = true;
            } else {
                interstitialAd.loadAd();

                if (interstitialAd.isAdLoaded()) {
                    interstitialAd.show();
                } else {

                    isAdShown = false;

                    // If facebook fails to show ads, Admob Ads will show there.

                    googleAds.loadAd(new AdRequest.Builder().addTestDevice(ADMOB_TEST_ID).build());

                    if (googleAds.isLoaded()) {
                        googleAds.show();
                        isAdShown = true;

                    } else {

                        // If all above fails, Unity will serve the ads.

                        if (UnityAds.isReady(unityID)) {
                            UnityAds.show(activity, unityID);
                            isAdShown = true;
                        } else {
                            UnityAds.load(unityID);
                            if (UnityAds.isReady(unityID)) {
                                UnityAds.show(activity, unityID);
                                isAdShown = true;
                                UnityAds.addListener(new IUnityAdsListener() {
                                    @Override
                                    public void onUnityAdsReady(String s) {
                                        isAdShown = true;

                                    }

                                    @Override
                                    public void onUnityAdsStart(String s) {

                                    }

                                    @Override
                                    public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {

                                    }

                                    @Override
                                    public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {
                                        isAdShown = false;

                                    }
                                });
                            }
                        }


                    }
                }

            }
        } else {
            Log.i("ADS", "Ads not Enable. To Turn on Ads, Go To UTILS and make IS_ADS_ENABLE= true ");
        }

        return isAdShown;

    }

    public static void removeStatusBarTransparentFlag(Activity context) {
        View decorView = context.getWindow().getDecorView();
        decorView.setOnApplyWindowInsetsListener((v, insets) -> {
            WindowInsets defaultInsets = v.onApplyWindowInsets(insets);
            return defaultInsets.replaceSystemWindowInsets(
                    defaultInsets.getSystemWindowInsetLeft(),
                    defaultInsets.getSystemWindowInsetTop(),
                    defaultInsets.getSystemWindowInsetRight(),
                    defaultInsets.getSystemWindowInsetBottom());


        });
        ViewCompat.requestApplyInsets(decorView);
        context.getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
    }

}
