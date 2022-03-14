package newwave.videomaker.statusmaker.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
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
import newwave.videomaker.statusmaker.MyApplication;
import newwave.videomaker.statusmaker.R;
import newwave.videomaker.statusmaker.Retrofit.APIClientData;
import newwave.videomaker.statusmaker.adapter.NvCategoryAdapter;
import newwave.videomaker.statusmaker.adapter.NvVideoViewAdapter;
import newwave.videomaker.statusmaker.model.ModelCategoryResponse;
import newwave.videomaker.statusmaker.model.ModelVideoResponce;
import newwave.videomaker.statusmaker.model.VideoviewModel;
import newwave.videomaker.statusmaker.utils.MyAppUtils;
import newwave.videomaker.statusmaker.utils.Utils_Permission;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NV_LibraryActivity extends AppCompatActivity {


    private Activity context;
    private ArrayList<VideoviewModel> videoviewModel = new ArrayList<>();
    private boolean isBackPressed = false;
    private RecyclerView rv_all_category = null;
    private RecyclerView rv_all_videos = null;
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
    private NvVideoViewAdapter nvVideoViewAdapter;
    public static final String TAG_SEARCH_TERM = "tag_search";
    private SwipeRefreshLayout swiperefreshLayout;
    //    private ImageView saveWp;
    private StaggeredGridLayoutManager layoutManager;
    private LinearLayout ll_fbbanner;
    private LottieAnimationView lottiAnimationNodata;
    private InterstitialAd interstitialAd;
    private com.google.android.gms.ads.InterstitialAd googleAds;
    LinearLayout ll_back;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.contain_main);
        context = this;

        initializeAds();

        et_search_bar =  findViewById(R.id.et_search_bar);
        ll_progressbar =  findViewById(R.id.ll_progressbar);
        swiperefreshLayout = findViewById(R.id.swiperefreshLayout);
        adView =  findViewById(R.id.adView);
        ll_fbbanner =  findViewById(R.id.ll_fbbanner);
        lottiAnimationNodata =  findViewById(R.id.lottiAnimationNodata);
        lottieAnimationView =  findViewById(R.id.animationView);
        rv_all_category =  findViewById(R.id.rv_all_category);
        ll_no_data_available =  findViewById(R.id.ll_no_data_available);
        rv_all_videos =  findViewById(R.id.rv_all_videos);
        ll_back = findViewById(R.id.ll_back);




        MyAppUtils.showBannerAds(this, ll_fbbanner, adView);

        lottiAnimationNodata.playAnimation();

        MyAppUtils.onlyInitializeAds(this);
//        MyAppUtils.showBannerAds(this, ll_fbbanner, adView);


        new Utils_Permission(context).checkPermissionsGranted();
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
        copyFile("blankimage.jpg");

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        swiperefreshLayout.setOnRefreshListener(() -> {

            page = 1;
            videoviewModel.clear();
            nvVideoViewAdapter.notifyDataSetChanged();

            getVideos(selected_Category);

            swiperefreshLayout.setRefreshing(false);

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
        rv_all_videos.setLayoutManager(layoutManager);
        rv_all_videos.setHasFixedSize(true);


        rv_all_videos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.e("hhhhh", "onScrolled: " + "hhhh2");
                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                int[] firstVisibleItems = null;
                firstVisibleItems = layoutManager.findFirstVisibleItemPositions(firstVisibleItems);
                if (firstVisibleItems != null && firstVisibleItems.length > 0) {
                    pastVisibleItems = firstVisibleItems[0];
                    Log.e("hhhhh", "onScrolled: " + "hhhh1");
                }

                if (!loading) {
                    Log.e("hhhhh", "onScrolled: " + "hhhh0");
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loading = true;
                        Log.e("hhhhh", "onScrolled: " + "hhhh");
                        page = page + 1;

                        ll_progressbar.setVisibility(View.VISIBLE);

                        new Handler(getMainLooper()).postDelayed(() -> {

                            loadMoreData(selected_Category);

                            ll_progressbar.setVisibility(View.GONE);

                        }, 100);

                    }
                }

            }
        });

        if (MyAppUtils.isConnectingToInternet(context)) {
            lottieAnimationView.setVisibility(View.GONE);
            lottieAnimationView.pauseAnimation();
            getCategory();
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
                            if (i % 5 == 2) {
                                videoviewModel.add(null);
                            }
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

    private void getCategory() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("app", BuildConfig.APPLICATION_ID);
        jsonObject.addProperty("cat", "Latest");

        APIClientData.getInterface().getAllCategory(jsonObject).enqueue(new Callback<ModelCategoryResponse>() {
            public void onFailure(@NotNull Call<ModelCategoryResponse> call, @NotNull Throwable th) {

            }

            public void onResponse(@NotNull Call<ModelCategoryResponse> call, @NotNull Response<ModelCategoryResponse> response) {
                ModelCategoryResponse modelCategoryResponse = response.body();

                if (modelCategoryResponse == null || modelCategoryResponse.getMsg() == null || modelCategoryResponse.getMsg().isEmpty()) {
                    ll_no_data_available.setVisibility(View.VISIBLE);
                    return;
                }


                ll_no_data_available.setVisibility(View.GONE);
                NvCategoryAdapter nvCategoryAdapter = new NvCategoryAdapter(context, modelCategoryResponse.getMsg());
                rv_all_category.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, false));
                rv_all_category.setAdapter(nvCategoryAdapter);

                nvCategoryAdapter.setOnItemClickListener((position, catData) -> {

                    page = 1;
                    selected_Category = catData.get(position).getCategory();
                    Log.e("sssss", "onResponse: " + selected_Category);
                    getVideos(selected_Category);
                    nvCategoryAdapter.notifyDataSetChanged();

                });

            }
        });
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
                            if (i % 5 == 0 && i != 0) {
                                videoviewModel.add(null);
                            }
                            videoviewModel.add(modelVideoResponce.getMsg().get(i));
                            i++;
                        }
                        nvVideoViewAdapter = new NvVideoViewAdapter(context, videoviewModel);
                        layoutManager = new StaggeredGridLayoutManager(2, 1);
                        rv_all_videos.setLayoutManager(layoutManager);
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


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.Click = false;
    }


}

