package newwave.videomaker.statusmaker.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.StrictMode.VmPolicy;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;
import com.unity3d.ads.UnityAds;
import org.jetbrains.annotations.NotNull;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import newwave.videomaker.statusmaker.BuildConfig;
import newwave.videomaker.statusmaker.adapter.NvVideoViewAdapter1;
import newwave.videomaker.statusmaker.utils.SnappingRecyclerView;
import newwave.videomaker.statusmaker.MyApplication;
import newwave.videomaker.statusmaker.R;
import newwave.videomaker.statusmaker.Retrofit.APIClientData;
import newwave.videomaker.statusmaker.model.ModelVideoResponce;
import newwave.videomaker.statusmaker.model.VideoviewModel;
import newwave.videomaker.statusmaker.utils.MyAppUtils;
import newwave.videomaker.statusmaker.utils.Utils_Permission;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static newwave.videomaker.statusmaker.MyApplication.simpleCache;

public class MainActivity extends AppCompatActivity implements Player.EventListener{


    private Activity context;
    private ArrayList<VideoviewModel> videoviewModel = new ArrayList<>();
    private boolean isBackPressed = false;
    private RecyclerView rv_all_category = null;
    private SnappingRecyclerView rv_all_videos = null;
    private LinearLayout ll_no_data_available = null;
    private String selected_Category = "Latest";
    private int page = 1;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private boolean loading = false;
    private EditText et_search_bar;
    private AdView adView;
    ActionBarDrawerToggle toggle;
    private LinearLayout ll_progressbar;
    private LottieAnimationView lottieAnimationView;
    private NvVideoViewAdapter1 nvVideoViewAdapter;
    public static final String TAG_SEARCH_TERM = "tag_search";
    int currentPage = -1;
    private ImageView saveWp;
    private StaggeredGridLayoutManager layoutManager;
    private LinearLayout ll_fbbanner;
    private LottieAnimationView lottiAnimationNodata;
    private DrawerLayout drawer_layout;
    private ImageView home;
    private CacheDataSourceFactory cacheDataSourceFactory;
    private boolean isVisibleToUser = true;
    SimpleExoPlayer priviousPlayer;
    LinearLayout ll_library;
    private NavigationView navigationView;
    private InterstitialAd interstitialAd;
    private com.google.android.gms.ads.InterstitialAd googleAds;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        context = this;



        home =  findViewById(R.id.home);
        navigationView =  findViewById(R.id.navigationView);
        et_search_bar =  findViewById(R.id.et_search_bar);
        ll_progressbar =  findViewById(R.id.ll_progressbar);
        drawer_layout =  findViewById(R.id.drawer_layout);
        saveWp =  findViewById(R.id.save_wp);
        adView =  findViewById(R.id.adView);
        ll_fbbanner =  findViewById(R.id.ll_fbbanner);
        lottiAnimationNodata =  findViewById(R.id.lottiAnimationNodata);
        lottieAnimationView =  findViewById(R.id.animationView);
        rv_all_category =  findViewById(R.id.rv_all_category);
        ll_no_data_available =  findViewById(R.id.ll_no_data_available);
        rv_all_videos = findViewById(R.id.rv_all_videos);
        ll_library = findViewById(R.id.ll_library);
        rv_all_videos.enableViewScaling(true);

        initializeAds();


        lottiAnimationNodata.playAnimation();
//        MyAppUtils.showBannerAds(this, ll_fbbanner, adView);

        home.setOnClickListener(view -> drawer_layout.openDrawer(Gravity.START, true));

        toggle = new ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close);
        drawer_layout.addDrawerListener(toggle);

        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.album:
                    startActivity(new Intent(context, NV_AlbumdataActivity.class));
                    return true;
                case R.id.privacy:

                    startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(MyAppUtils.PRIVACY_URL), "text/plain"));

                    return true;

                case R.id.rateUs:
                    MyAppUtils.rateApp(context);
                    return true;
                case R.id.share:
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share) + getPackageName());
                    startActivity(Intent.createChooser(intent, "Share App..."));

                    return true;
                default:
                    return false;
            }
        });

        MyAppUtils.onlyInitializeAds(this);
//        MyAppUtils.showBannerAds(this, ll_fbbanner, adView);

        saveWp.setOnClickListener(v -> {
            MyAppUtils.showInterstitialAds(interstitialAd, googleAds, context, context.getString(R.string.unity_inter));

            startActivity(new Intent(getApplicationContext(), NV_WhatsappActivity.class));


        });

        new Utils_Permission(context).checkPermissionsGranted();


        StrictMode.setVmPolicy(new VmPolicy.Builder().build());
        copyFile("blankimage.jpg");

        ll_library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, NV_LibraryActivity.class);
                startActivity(i);
            }
        });




        RecyclerViewsetup();

    }


    private void copyFile(String str) {
        try {
            InputStream open = getAssets().open(str);
            StringBuilder sb = new StringBuilder();
            sb.append(getCacheDir());
            sb.append("/");
            sb.append(str);
            FileOutputStream fos = new FileOutputStream(sb.toString());
            byte[] bArr = new byte[1024];
            while (true) {
                int read = open.read(bArr);
                if (read != -1) {
                    fos.write(bArr, 0, read);
                } else {
                    open.close();
                    fos.flush();
                    fos.close();
                    return;
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }




    private void RecyclerViewsetup() {

        layoutManager = new StaggeredGridLayoutManager(2, 1);
//        rv_all_videos.setLayoutManager(layoutManager);
//        rv_all_videos.setHasFixedSize(true);
//        RecyclerView.LayoutManager recylerViewLayoutManager = new RecyclerView.LayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false) ;

        rv_all_videos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

//                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
//                    //Dragging
//                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//
//                }




            }

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                final int scrollOffset = recyclerView.computeVerticalScrollOffset();
                final int height = recyclerView.getHeight();
                int pageNo = scrollOffset / height;
                Log.e("xxxx", "onScrolled: "+pageNo +"---"+dx+"---"+dy);

//                if (pageNo != currentPage) {

                currentPage = pageNo;
                int pos = SnappingRecyclerView.getSelectedPosition();
                int ccc = pos +currentPage;
                Log.e("lllll", "onScrollStateChanged: "+pos);
                releasePriviousPlayer();
                setPlayer(ccc);
//                    setPlayer(currentPage);

//                }

//                int pos = SnappingRecyclerView.getSelectedPosition();
//
//                Log.e("kkkk", "onScrolled: "+pos );

//                setPlayer();
                if (!loading) {
                    Log.e("hhhhh", "onScrolled: " + "hhhh0");
//                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                    loading = true;
                    Log.e("hhhhh", "onScrolled: " + "hhhh");
                    page = page + 1;

                    ll_progressbar.setVisibility(View.VISIBLE);

                    new Handler(getMainLooper()).postDelayed(() -> {

                        loadMoreData(selected_Category);

                        ll_progressbar.setVisibility(View.GONE);

                    }, 100);

//                    }
                }

            }
        });

        if (MyAppUtils.isConnectingToInternet(context)) {
            lottieAnimationView.setVisibility(View.GONE);
            lottieAnimationView.pauseAnimation();
//            getCategory();
            getVideos("Latest");
        } else {
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.playAnimation();
        }


    }

    private void loadMoreData(String str) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("app", BuildConfig.APPLICATION_ID);
        jsonObject.addProperty("cat", str);
        jsonObject.addProperty("page", page);
        try {
            APIClientData.getInterface().getCatVideo(jsonObject).enqueue(new Callback<ModelVideoResponce>() {
                @Override
                public void onFailure(@NotNull Call<ModelVideoResponce> call, @NotNull Throwable th) {


                }

                @Override
                public void onResponse(@NotNull Call<ModelVideoResponce> call, @NotNull Response<ModelVideoResponce> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        ModelVideoResponce modelVideoResponce = response.body();
                        int i = 0;

                        ll_no_data_available.setVisibility(View.GONE);
                        ll_progressbar.setVisibility(View.GONE);
                        rv_all_videos.setVisibility(View.VISIBLE);

                        while (i < modelVideoResponce.getMsg().size()) {
                            videoviewModel.add(modelVideoResponce.getMsg().get(i));
                            i++;
                        }

                        nvVideoViewAdapter.setDataList(videoviewModel);
                        nvVideoViewAdapter.notifyDataSetChanged();
                        loading = false;


                    } else {
                        Toast.makeText(context, "No video found", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    public void getVideos(String str) {
        String f = "1";
        Log.e("sssss", "onResponse: " + str + page);
        ll_progressbar.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("app", BuildConfig.APPLICATION_ID);
        jsonObject.addProperty("cat", str);
        jsonObject.addProperty("page", page);
        try {
            APIClientData.getInterface().getCatVideo(jsonObject).enqueue(new Callback<ModelVideoResponce>() {
                public void onFailure(Call<ModelVideoResponce> call, Throwable th) {
                    ll_progressbar.setVisibility(View.GONE);
                    ll_no_data_available.setVisibility(View.VISIBLE);
                    rv_all_videos.setVisibility(View.GONE);

                }

                public void onResponse(@NotNull Call<ModelVideoResponce> call, @NotNull Response<ModelVideoResponce> response) {

                    try {
                        ModelVideoResponce modelVideoResponce = response.body();
                        int i = 0;
                        if (modelVideoResponce.getCode() == null || !modelVideoResponce.getCode().equals("200")) {
                            ll_no_data_available.setVisibility(View.VISIBLE);
                            rv_all_videos.setVisibility(View.GONE);
                            ll_progressbar.setVisibility(View.GONE);
                            return;
                        }
                        ll_no_data_available.setVisibility(View.GONE);
                        ll_progressbar.setVisibility(View.GONE);
                        rv_all_videos.setVisibility(View.VISIBLE);
                        videoviewModel = new ArrayList();

                        while (i < modelVideoResponce.getMsg().size()) {
                            videoviewModel.add(modelVideoResponce.getMsg().get(i));
                            i++;
                        }
//                        rv_all_videos.findViewHolderForAdapterPosition(pos);
                        nvVideoViewAdapter = new NvVideoViewAdapter1(context, videoviewModel);

//                        nvVideoViewAdapter.setEventListener(new NvVideoViewAdapter1.EventListener() {
//
//
//                            @Override
//                            public void onItemViewClick(int position) {
//
//
//                                setPlayer(position);
//
//                            }
//                        });
//                        layoutManager = new StaggeredGridLayoutManager(2, 1);
//                        rv_all_videos.setLayoutManager(layoutManager);
                        rv_all_videos.setAdapter(nvVideoViewAdapter);

                    } catch (Exception e) {
                        ll_progressbar.setVisibility(View.GONE);
                        ll_no_data_available.setVisibility(View.VISIBLE);
                        rv_all_videos.setVisibility(View.GONE);
                    }

                }
            });
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void initializeAds() {


        AudienceNetworkAds.initialize(context);
        interstitialAd = new InterstitialAd(context, getString(R.string.fb_interstitial));


        MobileAds.initialize(context);

        googleAds = new com.google.android.gms.ads.InterstitialAd(context);
        googleAds.setAdUnitId(context.getString(R.string.admob_init));

        googleAds.loadAd(new AdRequest.Builder().addTestDevice(MyAppUtils.ADMOB_TEST_ID).build());

        UnityAds.initialize(context, MyAppUtils.UNITY_APP_ID, MyAppUtils.ENABLE_UNITY_TEST_ADS);


    }


    public void setPlayer(final int currentPage) {

        if (context != null) {


            final SimpleExoPlayer player = new SimpleExoPlayer.Builder(context).build();
            final PlayerView playerView = findViewById(R.id.player_view);
//            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
//            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            final VideoviewModel item = videoviewModel.get(currentPage);
//            View layout = layoutManager.findViewByPosition(currentPage);

            simpleCache = simpleCache;
            cacheDataSourceFactory = new CacheDataSourceFactory(simpleCache, new DefaultHttpDataSourceFactory(Util.getUserAgent(context, "BubbleTok"))
                    , CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);

            ProgressiveMediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(Uri.parse(item.getVideo_link()));
            playerView.setPlayer(player);
            player.setPlayWhenReady(isVisibleToUser);
            try {

                MediaPlayer mp = new MediaPlayer();
                mp.setDataSource(context, Uri.parse(item.getVideo_link()));
                int height = mp.getVideoHeight();

//                if (height >= 600) {
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);


//                } else {
//                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
//
//                }


            } catch (Exception e) {
                e.printStackTrace();
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);

            }

            player.setRepeatMode(Player.REPEAT_MODE_ALL);
            player.seekTo(0, 0);
            player.addListener(this);

            priviousPlayer = player;


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
                            priviousPlayer.setPlayWhenReady(true);
                        } else {

                            new Handler(getMainLooper()).postDelayed(() -> priviousPlayer.setPlayWhenReady(false), 200);
                        }


                        return true;
                    }


                    @Override
                    public boolean onDoubleTap(MotionEvent e) {

                        if (!player.getPlayWhenReady()) {
                            priviousPlayer.setPlayWhenReady(true);
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

    public void releasePriviousPlayer() {
        if (priviousPlayer != null) {
            priviousPlayer.removeListener(this);
            priviousPlayer.release();
        }
    }

    @Override
    public void onBackPressed() {
        if (isBackPressed) {
            super.onBackPressed();
            return;
        }
        isBackPressed = true;
        Toast.makeText(context, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler(getMainLooper()).postDelayed(() -> isBackPressed = false, 2000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        releasePriviousPlayer();

    }


    @Override
    public void onStop() {
        super.onStop();
        if (priviousPlayer != null) {
            priviousPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (priviousPlayer != null) {
            priviousPlayer.setPlayWhenReady(true);
        }
    }


    @Override
    public void onPause() {
        super.onPause();

        if (priviousPlayer != null)
            priviousPlayer.setPlayWhenReady(false);
    }
    @Override
    public void onResume() {
        super.onResume();

        if ((priviousPlayer != null)) {
            priviousPlayer.setPlayWhenReady(true);
        }
        MyApplication.Click = false;
    }



}
