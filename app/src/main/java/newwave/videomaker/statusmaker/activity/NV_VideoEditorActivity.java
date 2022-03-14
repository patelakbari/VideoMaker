package newwave.videomaker.statusmaker.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import newwave.videomaker.statusmaker.R;
import newwave.videomaker.statusmaker.adapter.NvImageListAdapter;
import newwave.videomaker.statusmaker.utils.Utils;
import newwave.videomaker.statusmaker.interfaces.onclick;
import newwave.videomaker.statusmaker.model.VideoviewModel;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.NativeAdLayout;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource.Factory;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoController.VideoLifecycleCallbacks;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.unity3d.ads.UnityAds;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import life.knowledge4.videotrimmer.utils.FileUtils;
import newwave.videomaker.statusmaker.utils.Utils_Admob;
import newwave.videomaker.statusmaker.utils.MyAppUtils;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class NV_VideoEditorActivity extends AppCompatActivity {
    public static final int REQUEST_PICK = 9162;
    public static boolean flagVideo = false;
    public static String resizedVideoPathFinal;
    JSONObject jsonObj;
    int i;
    private Dialog dialog;
    private RelativeLayout relativeLayout;
    private String url;
    private Handler handler;
    private Runnable runnable;
    private String mathc;
    private long K;
    private Utils_Admob.MyListener myListener;
    private String[] cmd;
    private CardView saveVideo;
    private int totalImage;
    private TextView btnTryAgain;
    private String imageList;
    private String ratio;
    private String downloadFileName;
    private String ffCmd;
    private PlayerView exo_playerview;
    private String ffCmdVideo;
    private String ffUser;
    private String videoResolution;
    private String duration;
    private String colorKayRandom;
    private String opVideo;
    private LinearLayout layoutTryAgain;
    private String picPath;
    private UnifiedNativeAd nativeAd;
    private int o = 0;
    private applyFiler applyFiler;
    private ProgressBar progressBarExoplayer;
    private RecyclerView rv_numberimg;
    private NvImageListAdapter nvImageListAdapter;
    private SimpleExoPlayer simpleExoPlayer;
    private ProgressBar progressBar;
    private String[] totalImages;
    private CardView cardView1;
    private CardView cardVideo;
    private TextView cardTextImage;
    private TextView cardTxtVideo;
    private ImageView thumb;
    private RelativeLayout rv_video_list;
    private String waterMarkPath;
    private ArrayList<String> finalCommand;
    private String filesPath;
    private Activity context;
    private Animation animation;
    private String[] listImages;
    private AdView adView;
    private LinearLayout llfbbanner;
    private InterstitialAd interstitialAd;
    private com.google.android.gms.ads.InterstitialAd googleAds;

    @Override
    @SuppressLint({"ClickableViewAccessibility", "IntentReset"})
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_video_editor);
        context = this;

        exo_playerview =  findViewById(R.id.exo_player);
        layoutTryAgain =  findViewById(R.id.layout_try_again);
        progressBarExoplayer =  findViewById(R.id.progressBar_exoplayer);
        btnTryAgain =  findViewById(R.id.btn_try_again);
        rv_video_list =  findViewById(R.id.rv_video_list);
        thumb =  findViewById(R.id.view_thumb);
        cardView1 =  findViewById(R.id.card_images);
        cardVideo =  findViewById(R.id.card_video);
        cardTextImage =  findViewById(R.id.card_text_img);
        cardTxtVideo =  findViewById(R.id.card_txet_video);
        saveVideo =  findViewById(R.id.save_video);
        adView =  findViewById(R.id.adView);
        llfbbanner =  findViewById(R.id.ll_fbbanner);
        rv_numberimg =  findViewById(R.id.rv_numberimg);


        initializeAds();

        MyAppUtils.showBannerAds(this, llfbbanner, adView);

        dialog = new Dialog(this);
        filesPath = getIntent().getStringExtra("filepath");

        // Copy  watermark to external storage
        try {
            copyRAWtoSDCard(R.raw.watermark, getDataDir() + "/.tempUV");
        } catch (IOException e) {
            e.printStackTrace();
        }

        waterMarkPath = getDataDir() + "/.tempUV/watermark.gif";
        finalCommand = new ArrayList<>();
        File yourFile = new File(filesPath + "/python.json");
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(yourFile);

            String jsonStr = null;
            try {

                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                jsonStr = Charset.defaultCharset().decode(bb).toString();
                jsonObj = new JSONObject(jsonStr);
                totalImage = jsonObj.getJSONArray("images").length();
                totalImages = new String[totalImage];
                for (int count = 0; count < jsonObj.getJSONArray("images").length(); count++) {
//                finalCommand.add
                    totalImages[count] = filesPath + "/" + jsonObj.getJSONArray("images").getJSONObject(count).getString("name");
                }
                int width;
                int height;

                height = Integer.parseInt(jsonObj.getJSONObject("video").getString("h"));
                width = Integer.parseInt(jsonObj.getJSONObject("video").getString("w"));
                duration = jsonObj.getJSONObject("video").getString("duration");
                if (height > width) {
                    ratio = "9,16";
                } else {
                    ratio = "16,9";
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                stream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        cardView1.setBackgroundResource(R.drawable.orangegradient);
        cardVideo.setBackgroundResource(R.drawable.round_white);
        cardTextImage.setTextColor(getColor(R.color.white));
        cardTxtVideo.setTextColor(getColor(R.color.colorPrimary));

        cardView1.setOnClickListener(view -> {
            flagVideo = false;
            cardView1.setBackgroundResource(R.drawable.orangegradient);
            cardVideo.setBackgroundResource(R.drawable.round_white);
            cardTextImage.setTextColor(getColor(R.color.white));
            cardTxtVideo.setTextColor(getColor(R.color.colorPrimary));
            rv_numberimg.setVisibility(View.VISIBLE);
            rv_video_list.setVisibility(View.GONE);
        });


        cardVideo.setOnClickListener(view -> {
            if (resizedVideoPathFinal != null) {
                flagVideo = true;
            }
            cardView1.setBackgroundResource(R.drawable.round_white);
            cardVideo.setBackgroundResource(R.drawable.orangegradient);
            cardTxtVideo.setTextColor(getColor(R.color.white));
            cardTextImage.setTextColor(getColor(R.color.colorPrimary));

            if (dialog == null || !dialog.isShowing()) {
                String str = Intent.ACTION_GET_CONTENT;
                String str2 = "video/*";
                Intent intent;
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 1001);
                    return;
                }
                intent = new Intent();
                intent.setType(str2);
                intent.setAction(str);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)), 1001);
                return;
            }
            try {
                if (animation != null) {
                    relativeLayout.startAnimation(animation);
                }
            } catch (Exception e) {
                e.printStackTrace();

                MyAppUtils.setToast(context, "Please Wait while Creating Video.");
            }
        });
        String[] listImages1 = listImages;
        if (listImages1 != null && listImages1.length > 0) {
            i = 0;
            while (true) {
                String[] strArr2 = listImages;
                if (i >= strArr2.length) {
                    break;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(getCacheDir());
                stringBuilder.append("/blankimage.jpg");
                strArr2[i] = stringBuilder.toString();
                i++;
            }
        }


        listImages1 = totalImages;
        if (listImages1 != null && listImages1.length > 0) {
            nvImageListAdapter = new NvImageListAdapter(context, totalImage, totalImages, new clickAdapter());
            rv_numberimg.setLayoutManager(new GridLayoutManager((Context) context, 1, RecyclerView.HORIZONTAL, false));
            rv_numberimg.setItemAnimator(new DefaultItemAnimator());
            rv_numberimg.setAdapter(nvImageListAdapter);
        }
        ( findViewById(R.id.back)).setOnClickListener(view -> onBackPressed());

        VideoviewModel videoviewModelData = Utils.static_video_model_data;
        if (!(videoviewModelData == null || videoviewModelData.getVideoThumb() == null || isFinishing())) {
            setDownloadDialog();
        }


        saveVideo.setOnClickListener(view -> {
            pausePlayer();
            if (dialog != null && dialog.isShowing()) {
                try {
                    if (animation != null) {
                        relativeLayout.startAnimation(animation);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            {
                if (!(dialog == null || dialog.isShowing())) {
                    dialog.show();
                }

                applyFiler = new applyFiler();
                applyFiler.execute(new Void[0]);
            }
        });
        btnTryAgain.setOnClickListener(view -> {
            layoutTryAgain.setVisibility(View.GONE);
            progressBarExoplayer.setVisibility(View.VISIBLE);
            initializePlayer();
        });
    }

    public void initializeAds() {
        AudienceNetworkAds.initialize(context);
        interstitialAd = new InterstitialAd(context, getString(R.string.fb_interstitial));
        interstitialAd.loadAd();
        MobileAds.initialize(context);
        googleAds = new com.google.android.gms.ads.InterstitialAd(context);
        googleAds.setAdUnitId(context.getString(R.string.admob_init));
        googleAds.loadAd(new Builder().addTestDevice(MyAppUtils.ADMOB_TEST_ID).build());
        UnityAds.initialize(context, MyAppUtils.UNITY_APP_ID, MyAppUtils.ENABLE_UNITY_TEST_ADS);

    }

    public NV_VideoEditorActivity() {
        String str = "";
        downloadFileName = str;
        url = str;
        handler = new Handler();

        runnable = new AdShow();
        mathc = "\\btime=\\b\\d\\d:\\d\\d:\\d\\d.\\d\\d";
        myListener = new Utils_Admob.MyListener() {
            @Override
            @SuppressLint({"WrongConstant"})
            public void callback(String str) {
                dialog.dismiss();
                Intent intent = new Intent(context, NV_SaveVideoFileActivity.class);
                intent.putExtra("videourl", url);
                intent.addFlags(335544320);
                startActivity(intent);
                finish();
            }

            @Override
            @SuppressLint({"WrongConstant"})
            public void callback2(String str) {
                dialog.dismiss();
                Intent intent = new Intent(context, NV_SaveVideoFileActivity.class);
                intent.putExtra("videourl", url);
                intent.addFlags(335544320);
                startActivity(intent);
                finish();
            }
        };
    }




    public final String replaceToOriginal(String str) {
        return str.replace("{pythoncomplex}", "filter_complex").replace("{pythonmerge}", "alphamerge").replace("{pythono}", "overlay").replace("{pythonz}", "zoom").replace("{pythonf}", "fade");
    }

    private void beginCrop(Uri uri) {
        if (uri != null) {
            try {
                String[] split = ratio.split(",");
                CropImage.activity(uri).setAspectRatio(Integer.parseInt(split[0]), Integer.parseInt(split[1])).start(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void loadNativeAds(final FrameLayout frameLayout, final NativeAdLayout nativeAdLayout) {
        AdLoader.Builder builder = new AdLoader.Builder((Context) context, getResources().getString(R.string.admob_native));
        builder.forUnifiedNativeAd(unifiedNativeAd -> {
            if (nativeAd != null) {
                nativeAd.destroy();
            }
            nativeAd = unifiedNativeAd;
            RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.ad_creation, null);
            populateUnifiedNativeAdView(unifiedNativeAd, (UnifiedNativeAdView) relativeLayout.findViewById(R.id.unified), (CardView) relativeLayout.findViewById(R.id.card_bg));
            frameLayout.removeAllViews();
            frameLayout.addView(relativeLayout);
        });
        builder.withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build());
        builder.withAdListener(new AdListener() {
            public void onAdFailedToLoad(int i) {
            }
        }).build().loadAd(new Builder().addTestDevice(MyAppUtils.ADMOB_TEST_ID).build());
    }

    private void populateUnifiedNativeAdView(UnifiedNativeAd unifiedNativeAd, UnifiedNativeAdView unifiedNativeAdView, CardView cardView) {
        unifiedNativeAdView.setMediaView((MediaView) unifiedNativeAdView.findViewById(R.id.ad_media));
        unifiedNativeAdView.setHeadlineView(unifiedNativeAdView.findViewById(R.id.ad_headline));
        unifiedNativeAdView.setBodyView(unifiedNativeAdView.findViewById(R.id.ad_body));
        unifiedNativeAdView.setCallToActionView(unifiedNativeAdView.findViewById(R.id.ad_call_to_action));
        unifiedNativeAdView.setIconView(unifiedNativeAdView.findViewById(R.id.ad_app_icon));
        unifiedNativeAdView.setPriceView(unifiedNativeAdView.findViewById(R.id.ad_price));
        unifiedNativeAdView.setStarRatingView(unifiedNativeAdView.findViewById(R.id.ad_stars));
        unifiedNativeAdView.setStoreView(unifiedNativeAdView.findViewById(R.id.ad_store));
        unifiedNativeAdView.setAdvertiserView(unifiedNativeAdView.findViewById(R.id.ad_advertiser));
        ((TextView) unifiedNativeAdView.getHeadlineView()).setText(unifiedNativeAd.getHeadline());
        if (unifiedNativeAd.getBody() == null) {
            unifiedNativeAdView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            unifiedNativeAdView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) unifiedNativeAdView.getBodyView()).setText(unifiedNativeAd.getBody());
        }
        if (unifiedNativeAd.getCallToAction() == null) {
            unifiedNativeAdView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            unifiedNativeAdView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) unifiedNativeAdView.getCallToActionView()).setText(unifiedNativeAd.getCallToAction());
        }
        if (unifiedNativeAd.getIcon() == null) {
            unifiedNativeAdView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) unifiedNativeAdView.getIconView()).setImageDrawable(unifiedNativeAd.getIcon().getDrawable());
            unifiedNativeAdView.getIconView().setVisibility(View.VISIBLE);
        }
        if (unifiedNativeAd.getPrice() == null) {
            unifiedNativeAdView.getPriceView().setVisibility(View.GONE);
        } else {
            unifiedNativeAdView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) unifiedNativeAdView.getPriceView()).setText(unifiedNativeAd.getPrice());
        }
        if (unifiedNativeAd.getStore() == null) {
            unifiedNativeAdView.getStoreView().setVisibility(View.GONE);
        } else {
            unifiedNativeAdView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) unifiedNativeAdView.getStoreView()).setText(unifiedNativeAd.getStore());
        }
        if (unifiedNativeAd.getStarRating() == null) {
            unifiedNativeAdView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) unifiedNativeAdView.getStarRatingView()).setRating(unifiedNativeAd.getStarRating().floatValue());
            unifiedNativeAdView.getStarRatingView().setVisibility(View.VISIBLE);
        }
        if (unifiedNativeAd.getAdvertiser() == null) {
            unifiedNativeAdView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) unifiedNativeAdView.getAdvertiserView()).setText(unifiedNativeAd.getAdvertiser());
            unifiedNativeAdView.getAdvertiserView().setVisibility(View.VISIBLE);
        }
        unifiedNativeAdView.setNativeAd(unifiedNativeAd);
        VideoController videoController = unifiedNativeAd.getVideoController();
        if (videoController.hasVideoContent()) {
            videoController.setVideoLifecycleCallbacks(new VideoLifecycleCallbacks() {
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });
        }
    }

    private void startTrimActivity(@NonNull Uri uri) {
        Intent intent = new Intent(context, NV_VideoTrimActivity.class);
        intent.putExtra("EXTRA_VIDEO_PATH", FileUtils.getPath(context, uri));
        intent.putExtra("duration", duration);
        intent.putExtra("video_resolution", videoResolution);
        startActivity(intent);
    }

    public void callBroadCast() {
        MediaScannerConnection.scanFile(context, new String[]{getDataDir().toString()}, null, new OnScanCompletedListener() {
            public void onScanCompleted(String str, Uri uri) {
                resizedVideoPathFinal = null;
                flagVideo = false;
            }
        });

    }

    public void deleteImage() {
        try {
            if (listImages != null) {
                for (int i = 0; i < listImages.length; i++) {
                    File file = new File("/storage/emulated/0/UV Video Status Maker/.Temp_Frame");
                    if (file.isDirectory()) {
                        String[] list = file.list();
                        int i2 = 0;
                        while (i2 < list.length) {
                            if (new File(file, list[i2]).exists() && new File(file, list[i2]).delete()) {
                                callBroadCast();
                            }
                            i2++;
                        }
                    }
                }
            }
            if (resizedVideoPathFinal != null) {
                File file2 = new File(resizedVideoPathFinal);
                if (file2.exists() && file2.delete()) {
                    callBroadCast();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getPercentages(int curntTime) {

        float progressF;
        int progress;
        int videoDuration = Integer.parseInt(duration);

        progressF = ((float) curntTime / (float) videoDuration) * 100;

        progress = (int) (progressF / 1000);

        if (progress >= 100) {
            return 100;
        } else {
            return progress;
        }


    }

    public void execureCommand(String[] strArr) {

        K = Long.parseLong(duration);
        FFmpeg.executeAsync(strArr, (executionId, returnCode) -> {

            if (returnCode == RETURN_CODE_SUCCESS) {

                new Handler(Looper.getMainLooper()).post(() -> {
                    dialog.dismiss();
                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{new File(url).getAbsolutePath()}, new String[]{"mp4"}, null);
                    handler.postDelayed(runnable, 500);
                });
            } else {

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    MyAppUtils.setToast(context, "Failed..");
                });

            }

        });

        Config.enableStatisticsCallback(statistics -> {

            if (progressBar != null) {
                progressBar.setProgress(getPercentages(statistics.getTime()));
            }

        });


    }

    public void initializePlayer() {
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance((Context) context, new DefaultRenderersFactory(getApplicationContext()), new DefaultTrackSelector());
        exo_playerview.setPlayer(simpleExoPlayer);
        CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(VideoCache.getInstance(this), new DefaultDataSourceFactory((Context) context, "MyVideoMakerApplication"));
        try {
            if (!(Utils.static_video_model_data == null || TextUtils.isEmpty(Utils.static_video_model_data.getVideo_link()))) {
                simpleExoPlayer.prepare(new Factory(cacheDataSourceFactory).createMediaSource(Uri.parse(filesPath + "/output.mp4")));
                simpleExoPlayer.setPlayWhenReady(true);
                simpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
                exo_playerview.hideController();


                exo_playerview.setOnTouchListener(new View.OnTouchListener() {
                    private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

                        @Override
                        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                            super.onFling(e1, e2, velocityX, velocityY);
                            float deltaY = e1.getX() - e2.getX();
                            float deltaYAbs = Math.abs(deltaY);
                            // Only when swipe distance between minimal and maximal distance value then we treat it as effective swipe
                            if ((deltaYAbs > 100) && (deltaYAbs < 1000)) {
                                if (deltaY > 0) {
                                    onBackPressed();
                                }
                            }


                            return true;
                        }

                        @Override
                        public boolean onSingleTapUp(MotionEvent e) {
                            super.onSingleTapUp(e);

                            if (!simpleExoPlayer.getPlayWhenReady()) {
                                simpleExoPlayer.setPlayWhenReady(true);
                            } else {

                                new Handler(getMainLooper()).postDelayed(() -> simpleExoPlayer.setPlayWhenReady(false), 200);
                            }


                            return true;
                        }


                        @Override
                        public boolean onDoubleTap(MotionEvent e) {

                            if (!simpleExoPlayer.getPlayWhenReady()) {
                                simpleExoPlayer.setPlayWhenReady(true);
                            }

                            return super.onDoubleTap(e);

                        }
                    });

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        gestureDetector.onTouchEvent(event);
                        return true;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        simpleExoPlayer.addListener(new EventListener() {
            @Override
            public void onLoadingChanged(boolean z) {
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            }

            @Override
            @SuppressLint({"WrongConstant"})
            public void onPlayerError(ExoPlaybackException exoPlaybackException) {
                if (exoPlaybackException != null && exoPlaybackException.getMessage() != null && exoPlaybackException.getMessage().contains("Unable to connect")) {
                    exo_playerview.hideController();
                    layoutTryAgain.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPlayerStateChanged(boolean z, int i) {
                progressBarExoplayer.setVisibility(i == 2 ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void onPositionDiscontinuity(int i) {
            }

            @Override
            public void onRepeatModeChanged(int i) {
            }

            @Override
            public void onSeekProcessed() {
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean z) {
            }

            @Override
            public void onTimelineChanged(Timeline timeline, Object obj, int i) {
            }

            public void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_PICK && resultCode == -1) {
            beginCrop(intent.getData());
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (intent != null) {
                if (resultCode == -1) {
                    Uri uri = CropImage.getActivityResult(intent).getUri();

                    totalImages[o] = uri.getPath();
                    nvImageListAdapter.notifyDataSetChanged();
                    rv_numberimg.setAdapter(nvImageListAdapter);
                }
            } else {
                return;
            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            CropImage.getActivityResult(intent).getError();
        }
        if (requestCode == 1001 && resultCode == -1) {
            try {
                Uri data = intent.getData();
                if (data == null || !new File(FileUtils.getPath(context, data)).exists()) {
                    Toast.makeText(context, R.string.toast_cannot_retrieve_selected_video, Toast.LENGTH_SHORT).show();
                } else {
                    startTrimActivity(data);
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private void copyRAWtoSDCard(int id, String path) throws IOException {
        InputStream in = getResources().openRawResource(id);
        File file = new File(path);
        file.mkdirs();
        FileOutputStream out = new FileOutputStream(path + "/watermark.gif");
        byte[] buff = new byte[1024];
        int read = 0;
        try {
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
        } finally {
            in.close();
            out.close();
        }
    }

    @Override
    public void onBackPressed() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (progressBar.isAnimating()) {

                Toast.makeText(context, "You can't go Back, Please wait to get back.", Toast.LENGTH_SHORT).show();

            } else {

                super.onBackPressed();
            }
        }


    }

    public void showDialogBack() {
        if (dialog == null || !dialog.isShowing()) {
            deleteImage();
            finish();
            return;
        }
        dialog.setContentView(R.layout.dialog_confirm_back);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(false);
        final Dialog finalDialog = dialog;
        ((ImageView) dialog.findViewById(R.id.cancel)).setOnClickListener(view -> {
            finalDialog.dismiss();
            dialog.show();
        });
        ((Button) dialog.findViewById(R.id.yes)).setOnClickListener(view -> {
            deleteImage();
            applyFiler.cancel(true);
            dialog.dismiss();
            finish();
        });
        final Dialog finalDialog1 = dialog;
        ((Button) dialog.findViewById(R.id.no)).setOnClickListener(view -> {
            finalDialog1.dismiss();
            dialog.show();
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        simpleExoPlayer.release();
    }

    @Override
    public void onPause() {
        super.onPause();
        pausePlayer();
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        imageList = bundle.getString("image_list");
        videoResolution = bundle.getString("video_resolution");
        colorKayRandom = bundle.getString("colorkey_rand");
        duration = bundle.getString("duration");
        ffCmd = bundle.getString("ff_cmd");
        ffCmdVideo = bundle.getString("ff_cmd_video");
        ffUser = bundle.getString("ff_cmd_user");
        picPath = bundle.getString("picturePath");
        opVideo = bundle.getString("opt_video");
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (resizedVideoPathFinal != null) {
                rv_numberimg.setVisibility(View.GONE);
                rv_video_list.setVisibility(View.VISIBLE);
                Picasso.get().load(resizedVideoPathFinal).into(thumb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putString("total_image", String.valueOf(totalImage));
        bundle.putString("image_list", String.valueOf(imageList));
        bundle.putString("video_resolution", String.valueOf(videoResolution));
        bundle.putString("image_ratio", String.valueOf(ratio));
        bundle.putString("colorkey_rand", String.valueOf(colorKayRandom));
        bundle.putString("duration", String.valueOf(duration));
        bundle.putString("ff_cmd", String.valueOf(ffCmd));
        bundle.putString("ff_cmd_video", String.valueOf(ffCmdVideo));
        bundle.putString("ff_cmd_user", String.valueOf(ffUser));
        bundle.putString("picturePath", String.valueOf(picPath));
        bundle.putString("opt_video", String.valueOf(opVideo));
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onStart() {
        super.onStart();
        initializePlayer();
        pausePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        simpleExoPlayer.release();
    }

    public void pausePlayer() {
        simpleExoPlayer.setPlayWhenReady(false);
        simpleExoPlayer.getPlaybackState();
    }

    public void setDownloadDialog() {
        if (Utils.static_video_model_data != null) {
            dialog = new Dialog(this);

            this.dialog.requestWindowFeature(1);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_download_file);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            progressBar = dialog.findViewById(R.id.progress_download_video);
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            loadNativeAds((FrameLayout) dialog.findViewById(R.id.fl_adplaceholder), (NativeAdLayout) dialog.findViewById(R.id.fb_native_ad_container));
            progressBar.setProgress(0);

            CardView cancel = dialog.findViewById(R.id.ll_cancel_download);
            cancel.setVisibility(View.GONE);

            TextView title = dialog.findViewById(R.id.tv_title);
            title.setText("Crafting");

            TextView downloading = dialog.findViewById(R.id.tvDownloading);
            downloading.setText("Please Wait, While We are crafting your Video ");

            cancel.setOnClickListener(view -> {
                try {
                    if (progressBar.getProgress() < 100.0d) {

                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_confirm_back);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                        dialog.setCancelable(false);
                        ((ImageView) dialog.findViewById(R.id.cancel)).setOnClickListener((View.OnClickListener) view12 -> {

                            dialog.dismiss();

                        });
                        ((Button) dialog.findViewById(R.id.yes)).setOnClickListener((View.OnClickListener) view1 -> {

                            applyFiler.cancel(true);
                            dialog.dismiss();
                            finish();


                        });

                        ((Button) dialog.findViewById(R.id.no)).setOnClickListener((View.OnClickListener) view13 -> {
                            dialog.dismiss();

                        });

                        if (!dialog.isShowing())
                            dialog.show();


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    static class VideoCache {
        static SimpleCache sDownloadCache;

        static SimpleCache getInstance(Context context) {
            if (sDownloadCache == null) {
                sDownloadCache = new SimpleCache(new File(context.getCacheDir(), "exoCache"), new LeastRecentlyUsedCacheEvictor(1073741824));
            }
            return sDownloadCache;
        }
    }

    public class applyFiler extends AsyncTask<Void, Integer, Void> {
        public Void doInBackground(Void... voidArr) {
            return doinback(voidArr);
        }

        @SuppressLint({"SdCardPath", "WrongConstant"})
        public Void doinback(Void... voidArr) {
            try {
                String format = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(Calendar.getInstance().getTime());
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(getDataDir().getAbsolutePath());
                stringBuilder.append("/");
                stringBuilder.append(getResources().getString(R.string.app_name));
                stringBuilder.append("/video_");
                stringBuilder.append(format);
                stringBuilder.append(".mp4");
                String str = "&";
                File file = new File(getDataDir() + "/" + getResources().getString(R.string.app_name));
                if (!file.exists()) {
                    file.mkdirs();

                }

                finalCommand = new ArrayList<>();
                File yourFile = new File(filesPath + "/python.json");
                FileInputStream stream = null;
                try {
                    stream = new FileInputStream(yourFile);

                    String jsonStr = null;
                    try {

                        for (int count = 0; count < jsonObj.getJSONArray("images").length(); count++) {

                            JSONArray tempArray = jsonObj.getJSONArray("images").getJSONObject(count).getJSONArray("prefix");
                            for (int count1 = 0; count1 < tempArray.length(); count1++) {
                                finalCommand.add(tempArray.getString(count1));
                            }
                            finalCommand.add(totalImages[count]);
                        }

                        for (int count = 0; count < jsonObj.getJSONArray("static_inputs").length(); count++) {

                            JSONArray tempArray = jsonObj.getJSONArray("static_inputs").getJSONObject(count).getJSONArray("prefix");
                            for (int count1 = 0; count1 < tempArray.length(); count1++) {
                                finalCommand.add(tempArray.getString(count1));
                            }
                            finalCommand.add(filesPath + "/" + jsonObj.getJSONArray("static_inputs").getJSONObject(count).getString("name"));
                        }

                        finalCommand.add("-ignore_loop");
                        finalCommand.add("0");
                        finalCommand.add("-i");
                        finalCommand.add(waterMarkPath);

                        JSONArray jSONArray = jsonObj.getJSONArray("m");
                        if (jSONArray.length() != 0) {
                            for (int i = 0; i < jSONArray.length(); i++) {
                                finalCommand.add(replaceToOriginal(jSONArray.getString(i)));
                            }
                        }

                        jSONArray = jsonObj.getJSONArray("r");
                        if (jSONArray.length() != 0) {
                            for (int i = 0; i < jSONArray.length(); i++) {
                                finalCommand.add(replaceToOriginal(jSONArray.getString(i)));
                            }
                        }

                        jSONArray = jsonObj.getJSONArray("d");
                        if (jSONArray.length() != 0) {
                            for (int i = 0; i < jSONArray.length(); i++) {
                                finalCommand.add(replaceToOriginal(jSONArray.getString(i)));
                            }
                        }

                        finalCommand.add("-preset");
                        finalCommand.add("ultrafast");
                        finalCommand.add(stringBuilder.toString());

                    } catch (Exception e) {
                        Log.d("FFMPEGException", "" + e);
                    }
                } catch (Exception e) {
                    Log.d("FFMPEGException", "" + e);
                }

                cmd = new String[finalCommand.size()];
                cmd = finalCommand.toArray(cmd);

                url = stringBuilder.toString();
                if (cmd.length != 0) {
                    execureCommand(cmd);
                } else {
                    Toast.makeText(getApplicationContext(), "Command Empty", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public void onPreExecute() {
        }
    }

    public class AdShow implements Runnable {
        @SuppressLint({"WrongConstant"})
        public void run() {
            try {

                Toast.makeText(context, "Video Saved in Gallery.", Toast.LENGTH_SHORT).show();
                handler.removeCallbacks(runnable);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                deleteImage();
                dialog.dismiss();
                Intent intent = new Intent(context, NV_SaveVideoFileActivity.class);
                intent.putExtra("videourl", url);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

                MyAppUtils.showInterstitialAds(interstitialAd, googleAds, context, getString(R.string.unity_inter));

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public class clickAdapter implements onclick {
        public void clickEvent(View view, int i) {
            if (dialog == null || dialog.isShowing()) {
                try {
                    if (animation != null) {
                        relativeLayout.startAnimation(animation);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(context, "Please Wait while Creating Video.", Toast.LENGTH_SHORT).show();
                return;
            }
            flagVideo = false;
            Intent intent = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
            try {
                o = i;
                startActivityForResult(intent, REQUEST_PICK);
            } catch (ActivityNotFoundException unused) {
                Toast.makeText(context, R.string.crop__pick_error, Toast.LENGTH_SHORT).show();
            }
        }
    }


}
