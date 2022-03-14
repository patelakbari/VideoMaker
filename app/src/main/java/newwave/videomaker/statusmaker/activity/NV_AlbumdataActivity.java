package newwave.videomaker.statusmaker.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import newwave.videomaker.statusmaker.R;
import newwave.videomaker.statusmaker.adapter.NvAlbumAdapter;
import newwave.videomaker.statusmaker.utils.MyAppUtils;

import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.unity3d.ads.UnityAds;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

public class NV_AlbumdataActivity extends AppCompatActivity {

    private RecyclerView rv_video_list;
    private ImageView iv_back;
    ArrayList<String> arrayList = new ArrayList();
    private AdView adView;
    private LinearLayout ll_fbbanner;
    private InterstitialAd interstitialAd;
    private com.google.android.gms.ads.InterstitialAd googleAds;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    Activity context;
    ArrayList<String> newList = new ArrayList();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_album);

        iv_back =  findViewById(R.id.iv_back);
        rv_video_list = findViewById(R.id.rv_video_list);
        adView =  findViewById(R.id.adView);
        ll_fbbanner =  findViewById(R.id.ll_fbbanner);
        context = this;

        initializeAds();

        MyAppUtils.showBannerAds(this, ll_fbbanner, adView);

        iv_back.setOnClickListener(view -> {

            MyAppUtils.showInterstitialAds(interstitialAd, googleAds, context, getString(R.string.unity_inter));

            NV_AlbumdataActivity.this.onBackPressed();


        });
        initViews();
    }


    public ArrayList<String> getAlbum() {
        this.arrayList.clear();
        File file = new File(getDataDir().toString(), getResources().getString(R.string.app_name));
        if (!file.exists()) {
            file.mkdirs();
        }
        String path = file.getPath();
        if (!new File(path).exists()) {
            new File(path).mkdirs();
        }
        File[] listFiles = new File(path).listFiles();
        if (listFiles != null) {
            int i = 0;
            while (i < listFiles.length) {
                PrintStream ps;
                StringBuilder sb;
                if (listFiles[i].isFile()) {
                    ps = System.out;
                    sb = new StringBuilder();
                    sb.append("File ");
                    sb.append(listFiles[i].getName());
                    ps.println(sb.toString());
                    if (listFiles[i].getName().contains(".mp4") && new File(listFiles[i].getPath().toString()).length() > 1024) {
                        this.arrayList.add(listFiles[i].getPath());
                    }
                } else if (listFiles[i].isDirectory()) {
                    ps = System.out;
                    sb = new StringBuilder();
                    sb.append("Directory ");
                    sb.append(listFiles[i].getName());
                    ps.println(sb.toString());
                }
                i++;
            }
        }
        return arrayList;
    }


    public void initViews() {
        arrayList = getAlbum();
        int i = 0;


        if (arrayList.size() > 0) {
            while (i < arrayList.size()) {
                if (i % 3 == 0 && i != 0) {
                    this.newList.add(null);
                }
                this.newList.add(this.arrayList.get(i));
                i++;
            }
            ArrayList arrayList = newList;
            if (arrayList != null && arrayList.size() > 0) {
                NvAlbumAdapter nvAlbumAdapter = new NvAlbumAdapter(this, newList);
                staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
                rv_video_list.setLayoutManager(this.staggeredGridLayoutManager);
                rv_video_list.setAdapter(nvAlbumAdapter);
                return;
            }
            return;
        }
        Toast.makeText(this, "No Any Creation Found.", Toast.LENGTH_SHORT).show();
    }

    public void initializeAds() {

        AudienceNetworkAds.initialize(context);
        interstitialAd = new InterstitialAd(context, getString(R.string.fb_interstitial));
        interstitialAd.loadAd();

        MobileAds.initialize(context);


        googleAds = new com.google.android.gms.ads.InterstitialAd(context);
        googleAds.setAdUnitId(context.getString(R.string.admob_init));

        googleAds.loadAd(new AdRequest.Builder().addTestDevice(MyAppUtils.ADMOB_TEST_ID).build());

        UnityAds.initialize(context, MyAppUtils.UNITY_APP_ID, MyAppUtils.ENABLE_UNITY_TEST_ADS);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
