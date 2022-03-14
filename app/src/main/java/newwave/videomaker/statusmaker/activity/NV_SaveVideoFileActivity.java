package newwave.videomaker.statusmaker.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import newwave.videomaker.statusmaker.BuildConfig;
import newwave.videomaker.statusmaker.R;
import newwave.videomaker.statusmaker.utils.MyAppUtils;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.unity3d.ads.UnityAds;
import java.io.File;

public class NV_SaveVideoFileActivity extends AppCompatActivity {
    ImageView whatsapp;
    ImageView facebook;
    ImageView insta;
    ImageView more;
    ImageView playPuase;
    RelativeLayout rlPlaypause;
    ImageView back;
    NV_SessionManager NVSessionManager;
    VideoView videoView;
    public String videoUrl = "";
    private Activity context;
    private AdView adView;
    private LinearLayout ll_fbbanner;
    private InterstitialAd interstitialAd;
    private com.google.android.gms.ads.InterstitialAd googleAds;


    @SuppressLint({"ClickableViewAccessibility"})
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_saved);

        back =  findViewById(R.id.back);
        videoView =  findViewById(R.id.videoview);
        playPuase =  findViewById(R.id.play_pause);
        rlPlaypause =  findViewById(R.id.playing_status);
        whatsapp =  findViewById(R.id.whatsapp);
        facebook =  findViewById(R.id.facebook);
        insta =  findViewById(R.id.insta);
        adView =  findViewById(R.id.adView);
        ll_fbbanner =  findViewById(R.id.ll_fbbanner);
        context = this;
        initializeAds();

        MyAppUtils.showBannerAds(this, ll_fbbanner, adView);

        NVSessionManager = new NV_SessionManager(this);

        Intent intent1 = getIntent();
        if (intent1.getExtras() != null) {
            videoUrl = intent1.getStringExtra("videourl");
        }


        this.back.setOnClickListener(view -> {
            onBackPressed();

            MyAppUtils.showInterstitialAds(interstitialAd, googleAds, context, getString(R.string.unity_inter));


        });

        CardView backHome =  findViewById(R.id.home);


        if (!NVSessionManager.getBooleanData(NV_SessionManager.PREF_APP_RATED)) {
            showRateDialog();
        }
        backHome.setOnClickListener(view -> {
            Intent intent = new Intent(NV_SaveVideoFileActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            MyAppUtils.showInterstitialAds(interstitialAd, googleAds, context, getString(R.string.unity_inter));


        });

        initVideo();

        whatsapp.setOnClickListener(view -> {
            Log.d("AVUU",videoUrl);
//            MyAppUtils.shareImageVideoOnWhatsapp(context, videoUrl, true);

//            MyAppUtils.showInterstitialAds(interstitialAd, googleAds, context, getString(R.string.unity_inter));


        });


        facebook.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("video/*");
            Uri uriForFile = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", new File(videoUrl));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, uriForFile);
            String stringBuilder2 = getString(R.string.share) + getPackageName();
            intent.putExtra(Intent.EXTRA_TEXT, stringBuilder2);
            intent.setPackage("com.facebook.katana");
            try {

                startActivity(Intent.createChooser(intent, "Share Video..."));
                MyAppUtils.showInterstitialAds(interstitialAd, googleAds, context, getString(R.string.unity_inter));

            } catch (ActivityNotFoundException unused) {
                Toast.makeText(NV_SaveVideoFileActivity.this, R.string.install_fb, Toast.LENGTH_LONG).show();
            }
        });


        this.insta.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("video/*");
            Uri uriForFile = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", new File(videoUrl));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, uriForFile);
            String stringBuilder2 = getString(R.string.share) +
                    getPackageName();
            intent.putExtra(Intent.EXTRA_TEXT, stringBuilder2);
            intent.setPackage("com.instagram.android");
            try {
                startActivity(Intent.createChooser(intent, "Share Video..."));


                MyAppUtils.showInterstitialAds(interstitialAd, googleAds, context, getString(R.string.unity_inter));

            } catch (ActivityNotFoundException unused) {
                Toast.makeText(NV_SaveVideoFileActivity.this, "Please Install Instagram", Toast.LENGTH_LONG).show();
            }
        });
        this.more =  findViewById(R.id.more);
        this.more.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("video/*");

                Uri uriForFile = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", new File(videoUrl));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_STREAM, uriForFile);
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share) + getPackageName());
                startActivity(Intent.createChooser(intent, "Share Your Video!"));


                MyAppUtils.showInterstitialAds(interstitialAd, googleAds, context, getString(R.string.unity_inter));

            } catch (Exception e) {

                e.printStackTrace();
            }
        });
    }



    public void showRateDialog() {
        new NV_RatingDialog.Builder(this).title("Like New Wave 2021").positiveButtonTextColor(R.color.grey_500).negativeButtonTextColor(R.color.grey_500).playstoreUrl("https://play.google.com/store/apps/details?id=" + getPackageName()).onRatingBarFormSumbit(str -> {

        }).build().show();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initVideo() {

        if (getIntent() != null) {

            try {
                this.videoView.setVideoURI(Uri.parse(videoUrl));
                this.rlPlaypause.setVisibility(View.GONE);
                this.videoView.requestFocus();
                this.videoView.start();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        this.videoView.setOnCompletionListener(mediaPlayer -> videoView.start());
        this.videoView.setOnTouchListener((view, motionEvent) -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                rlPlaypause.setVisibility(View.VISIBLE);
                playPuase.setImageResource(R.drawable.ic_play_new);
                return false;
            }
            playPuase.setImageResource(R.drawable.ic_pause_new);
            videoView.start();
            new Handler(Looper.getMainLooper()).postDelayed(() -> rlPlaypause.setVisibility(View.GONE), 2000);
            return false;
        });
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
    public void onPause() {
        super.onPause();
        this.videoView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.rlPlaypause.setVisibility(View.GONE);
        this.videoView.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        MyAppUtils.showInterstitialAds(interstitialAd, googleAds, context, getString(R.string.unity_inter));


    }
}
