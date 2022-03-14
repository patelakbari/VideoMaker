package newwave.videomaker.statusmaker.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import newwave.videomaker.statusmaker.MyApplication;
import newwave.videomaker.statusmaker.R;
import newwave.videomaker.statusmaker.adapter.NvHomeAdapter;
import newwave.videomaker.statusmaker.model.VideoviewModel;

import com.coolerfall.download.DownloadCallback;
import com.coolerfall.download.DownloadManager;
import com.coolerfall.download.DownloadRequest;
import com.coolerfall.download.OkHttpDownloader;
import com.coolerfall.download.Priority;
import com.facebook.ads.NativeAdLayout;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import newwave.videomaker.statusmaker.utils.Utils_Admob;
import newwave.videomaker.statusmaker.utils.Utils_VideoDownload;
import newwave.videomaker.statusmaker.utils.MyAppUtils;
import okhttp3.OkHttpClient;

public class NV_VideoListActivity extends AppCompatActivity implements Player.EventListener {


    int page = 1;
    ArrayList<VideoviewModel> videoviewdata = new ArrayList<>();
    int currentPage = -1;
    LinearLayoutManager layoutManager;
    RelativeLayout pBar;
    SimpleExoPlayer exoPlayer;
    NvHomeAdapter adapter;
    Dialog dialog;
    private Activity context;
    private boolean isVisibleToUser = true;
    private SimpleCache simpleCache;
    private CacheDataSourceFactory cacheDataSourceFactory;
    private RecyclerView recyclerView;
    private ImageView llBack;
    private DownloadManager downloadManager;
    private int position = 0;
    private int fileDownloadingId = 0;
    private UnifiedNativeAd nativeAd;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyAppUtils.setStatusBarTransparentFlag(this);
        setContentView(R.layout.activity_video);
        context = this;
        initView();
        llBack.setOnClickListener(v -> onBackPressed());


        setupDialog();


        Intent intent = getIntent();

        if (intent != null) {
            videoviewdata = (ArrayList<VideoviewModel>) intent.getSerializableExtra("mdata");
            position = intent.getIntExtra("position", 0);
        }

        layoutManager = new LinearLayoutManager(context);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);


        recyclerView.scrollToPosition(position);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //here we find the current item number
                final int scrollOffset = recyclerView.computeVerticalScrollOffset();
                final int height = recyclerView.getHeight();
                int pageNo = scrollOffset / height;

                if (pageNo != currentPage) {
                    currentPage = pageNo;
                    releasePriviousPlayer();
                    setPlayer(currentPage);

                }
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastVisibleItemPosition();

                boolean endHasBeenReached = lastVisible >= totalItemCount;
                if (totalItemCount > 0 && endHasBeenReached) {
                }


            }
        });

        setAdapter();

    }

    private void initView() {
        llBack = findViewById(R.id.iv_close);
        recyclerView = findViewById(R.id.recyclerview);

        pBar = findViewById(R.id.progressbar_rl);

        recyclerView = findViewById(R.id.recyclerview);

    }

    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    public void setupDialog() {
        dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(1);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_download_file);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(false);
        progressBar = dialog.findViewById(R.id.progress_download_video);

        loadAdmobNativeAd((FrameLayout) dialog.findViewById(R.id.fl_adplaceholder), (NativeAdLayout) dialog.findViewById(R.id.fb_native_ad_container));

        this.progressBar.setProgress(0);

        CardView cardViewDownload = dialog.findViewById(R.id.ll_cancel_download);

        cardViewDownload.setOnClickListener(view -> {


            if (dialog.isShowing()) dialog.dismiss();


            if (downloadManager != null) {
                downloadManager.cancel(fileDownloadingId);

            }


            File filePath = new File(getDataDir(), videoviewdata.get(position).getTitle());

            String path = filePath.getAbsolutePath();

            File file = new File(path);
            deleteRecursive(file);


        });
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }

    public void setAdapter() {

        adapter = new NvHomeAdapter(context, videoviewdata, (postion, item, view) -> {

            switch (view.getId()) {

                case R.id.useNow:

                    if (MyAppUtils.isConnectingToInternet(context)) {
                        File filePath = new File(getDataDir(), videoviewdata.get(postion).getTitle());
                        String videoDownload = videoviewdata.get(postion).getVideo_zip();
                        if (filePath.exists()) {
                            Intent intent = new Intent(context, NV_VideoEditorActivity.class);
                            intent.putExtra("filepath", filePath.getAbsolutePath());
                            startActivity(intent);
                        } else {
                            filePath.mkdirs();
                            downloadFile(videoDownload, filePath.getAbsolutePath() + "/" + videoviewdata.get(postion).getTitle() + ".zip");
                        }


                    } else {

                        MyAppUtils.setToast(context, "Please Connect to Internet.");

                    }

                    break;


                case R.id.download:

                    String fileName = URLUtil.guessFileName(item.getVideo_link(), "", "video/*");

                    String location = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + MyAppUtils.FOLDER_NAME + "/" + fileName;

                    if (new File(location).exists()) {

                        Toast.makeText(context, "Already Downloaded", Toast.LENGTH_SHORT).show();

                    } else {


                        new Utils_VideoDownload(context, item.getVideo_link(), 0, 0, postion);

                    }

                    break;

                case R.id.share:


                    new Utils_VideoDownload(context, item.getVideo_link(), 1, 1, postion);

                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + view.getId());
            }

        });

        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

    }

    public void loadAdmobNativeAd(final FrameLayout frameLayout, final NativeAdLayout nativeAdLayout) {
        AdLoader.Builder builder = new AdLoader.Builder((Context) this, getResources().getString(R.string.admob_native));
        builder.forUnifiedNativeAd(unifiedNativeAd -> {
            if (nativeAd != null) {
                nativeAd.destroy();
            }
            nativeAd = unifiedNativeAd;
            RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.ad_creation, null);
            populateUnifiedNativeAdView(unifiedNativeAd, (UnifiedNativeAdView) relativeLayout.findViewById(R.id.unified));
            frameLayout.removeAllViews();
            frameLayout.addView(relativeLayout);
        });
        builder.withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build());
        builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                Utils_Admob.showFBNativeAds(context, nativeAdLayout);
            }
        }).build().loadAd(new AdRequest.Builder().addTestDevice(MyAppUtils.ADMOB_TEST_ID).build());
    }

    public void populateUnifiedNativeAdView(UnifiedNativeAd unifiedNativeAd, UnifiedNativeAdView unifiedNativeAdView) {
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
            videoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });
        }
    }

    public void downloadFile(String downloadUrl, String stringPath) {
        try {
            if (!this.dialog.isShowing())
                this.dialog.show();
            fileDownloadingId = downloadManager.add(new DownloadRequest.Builder().url(downloadUrl).retryTime(5).retryInterval(2, TimeUnit.SECONDS).progressInterval(1, TimeUnit.SECONDS).priority(Priority.HIGH).allowedNetworkTypes(1).destinationFilePath(stringPath).downloadCallback(new DownloadVideoCall()).build());


        } catch (Exception e) {
            e.getMessage();

        }
    }

    public void unzip(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory.getParentFile(), ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }

            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            zipFile.delete();
            Intent intent = new Intent(context, NV_VideoEditorActivity.class);
            intent.putExtra("filepath", targetDirectory.getAbsolutePath());
            startActivity(intent);

        } finally {
            zis.close();
        }
    }

    public void setPlayer(final int currentPage) {

        if (context != null) {

            if (videoviewdata.get(currentPage) != null) {
                final VideoviewModel item = videoviewdata.get(currentPage);
                final SimpleExoPlayer player = new SimpleExoPlayer.Builder(context).build();

                View layout = layoutManager.findViewByPosition(currentPage);
                final PlayerView playerView = layout.findViewById(R.id.player_view);
                simpleCache = MyApplication.simpleCache;
                cacheDataSourceFactory = new CacheDataSourceFactory(simpleCache, new DefaultHttpDataSourceFactory(Util.getUserAgent(context, "BubbleTok"))
                        , CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);

                ProgressiveMediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(Uri.parse(item.getVideo_link()));
                playerView.setPlayer(player);
                player.setPlayWhenReady(isVisibleToUser);
                try {

                    MediaPlayer mp = new MediaPlayer();
                    mp.setDataSource(context, Uri.parse(item.getVideo_link()));
                    int height = mp.getVideoHeight();

                    if (height >= 600) {
                        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);


                    } else {
                        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);

                }

                player.setRepeatMode(Player.REPEAT_MODE_ALL);
                player.seekTo(0, 0);
                player.addListener(this);

                exoPlayer = player;


                player.prepare(progressiveMediaSource, true, false);

                playerView.setOnTouchListener(new View.OnTouchListener() {
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

                            if (!player.getPlayWhenReady()) {
                                exoPlayer.setPlayWhenReady(true);
                            } else {

                                new Handler(getMainLooper()).postDelayed(() -> exoPlayer.setPlayWhenReady(false), 200);
                            }


                            return true;
                        }


                        @Override
                        public boolean onDoubleTap(MotionEvent e) {

                            if (!player.getPlayWhenReady()) {
                                exoPlayer.setPlayWhenReady(true);
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

        }
    }

    public void releasePriviousPlayer() {
        if (exoPlayer != null) {
            exoPlayer.removeListener(this);
            exoPlayer.release();
        }
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

        if (isLoading) {
            pBar.setVisibility(View.VISIBLE);

        } else {
            pBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_BUFFERING) {

            pBar.setVisibility(View.VISIBLE);
        } else if (playbackState == Player.STATE_READY) {
            pBar.setVisibility(View.GONE);
        }


    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    class DownloadVideoCall extends DownloadCallback {
        @Override
        public void onFailure(int i, int i2, String str) {

            Toast.makeText(context, "" + str, Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProgress(int i, long j, long j2) {
            j = (j * 100) / j2;
            if (j != 100) {
                progressBar.setProgress((int) j);

                Log.d("progress", "" + (int) j);
            }
        }

        @Override
        public void onRetry(int i) {
        }

        @Override
        public void onStart(int i, long j) {
        }

        @Override
        @SuppressLint({"WrongConstant"})
        public void onSuccess(int i, String str) {
            try {
                unzip(new File(str), new File(str).getParentFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((exoPlayer != null)) {
            exoPlayer.setPlayWhenReady(true);
        }

        try {
            if (this.dialog != null && !this.dialog.isShowing()) {
                downloadManager = new DownloadManager.Builder().context(this).downloader(OkHttpDownloader.create(new OkHttpClient.Builder().build())).threadPoolSize(3).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (exoPlayer != null)
            exoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        releasePriviousPlayer();
    }


}
