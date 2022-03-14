package newwave.videomaker.statusmaker.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import newwave.videomaker.statusmaker.BuildConfig;
import newwave.videomaker.statusmaker.R;
import newwave.videomaker.statusmaker.activity.NV_VideoListActivity;
import newwave.videomaker.statusmaker.utils.Utils;
import newwave.videomaker.statusmaker.model.VideoviewModel;
import newwave.videomaker.statusmaker.utils.MyAppUtils;
import newwave.videomaker.statusmaker.utils.Utils_Admob;

import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.NativeAdLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader.Builder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoController.VideoLifecycleCallbacks;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.squareup.picasso.Picasso;
import com.unity3d.ads.UnityAds;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class NvVideoViewAdapter extends Adapter<NvVideoViewAdapter.MyViewHolder> {
    ArrayList<VideoviewModel> videoArr;
    private Activity context;
    private UnifiedNativeAd nativeAd;


    public NvVideoViewAdapter(Activity context, ArrayList<VideoviewModel> arrayList) {
        this.context = context;
        this.videoArr = arrayList;
        initializeAds();
    }


    private InterstitialAd interstitialAd;
    private com.google.android.gms.ads.InterstitialAd googleAds;

    public void initializeAds() {


        AudienceNetworkAds.initialize(context);
        interstitialAd = new InterstitialAd(context, context.getString(R.string.fb_interstitial));


        MobileAds.initialize(context);

        googleAds = new com.google.android.gms.ads.InterstitialAd(context);
        googleAds.setAdUnitId(context.getString(R.string.admob_init));

        googleAds.loadAd(new AdRequest.Builder().addTestDevice(MyAppUtils.ADMOB_TEST_ID).build());

        UnityAds.initialize(context, MyAppUtils.UNITY_APP_ID, MyAppUtils.ENABLE_UNITY_TEST_ADS);


    }


    public static String removeLastChars(String str, int chars) {

        String string = str.substring(0, str.length() - chars);
        String lastWord = str.substring(str.lastIndexOf(" ") + 1);
        if (lastWord.trim().toLowerCase().equals("v")) {

            return string;

        } else {
            return str;
        }
    }


    public void setDataList(ArrayList<VideoviewModel> dataListMe) {
        this.videoArr = dataListMe;
        notifyDataSetChanged();

    }

    private void loadFBAds(MyViewHolder myViewHolder, int i) {
        Utils_Admob.showCustomFBNativeAds(this.context, myViewHolder.fbNativeAsContainer, i);
    }

    private void nativeAd(final MyViewHolder myViewHolder, final int i) {
        Builder builder = new Builder(context, context.getResources().getString(R.string.admob_native));
        builder.forUnifiedNativeAd(unifiedNativeAd -> {
            if (NvVideoViewAdapter.this.nativeAd != null) {
                NvVideoViewAdapter.this.nativeAd.destroy();
            }
            NvVideoViewAdapter.this.nativeAd = unifiedNativeAd;
            RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(NvVideoViewAdapter.this.context).inflate(R.layout.ad, null);
            NvVideoViewAdapter.this.populateUnifiedNativeAdView(unifiedNativeAd, (UnifiedNativeAdView) relativeLayout.findViewById(R.id.unified));
            myViewHolder.fbPlaceHolder.removeAllViews();
            myViewHolder.fbPlaceHolder.addView(relativeLayout);
        });
        builder.withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build());
        builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                NvVideoViewAdapter.this.loadFBAds(myViewHolder, i);

                Toast.makeText(context, "admob fails", Toast.LENGTH_SHORT).show();

            }
        }).build().loadAd(new AdRequest.Builder().addTestDevice(MyAppUtils.ADMOB_TEST_ID).build());
    }

    private void populateUnifiedNativeAdView(UnifiedNativeAd u_nativead, UnifiedNativeAdView u_nativeAadview) {
        u_nativeAadview.setMediaView((MediaView) u_nativeAadview.findViewById(R.id.ad_media));
        u_nativeAadview.setHeadlineView(u_nativeAadview.findViewById(R.id.ad_headline));
        u_nativeAadview.setBodyView(u_nativeAadview.findViewById(R.id.ad_body));
        u_nativeAadview.setCallToActionView(u_nativeAadview.findViewById(R.id.ad_call_to_action));
        u_nativeAadview.setIconView(u_nativeAadview.findViewById(R.id.ad_app_icon));
        u_nativeAadview.setPriceView(u_nativeAadview.findViewById(R.id.ad_price));
        u_nativeAadview.setStarRatingView(u_nativeAadview.findViewById(R.id.ad_stars));
        u_nativeAadview.setStoreView(u_nativeAadview.findViewById(R.id.ad_store));
        u_nativeAadview.setAdvertiserView(u_nativeAadview.findViewById(R.id.ad_advertiser));
        ((TextView) u_nativeAadview.getHeadlineView()).setText(u_nativead.getHeadline());
        if (u_nativead.getBody() == null) {
            u_nativeAadview.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            u_nativeAadview.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) u_nativeAadview.getBodyView()).setText(u_nativead.getBody());
        }
        if (u_nativead.getCallToAction() == null) {
            u_nativeAadview.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            u_nativeAadview.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) u_nativeAadview.getCallToActionView()).setText(u_nativead.getCallToAction());
        }
        if (u_nativead.getIcon() == null) {
            u_nativeAadview.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) u_nativeAadview.getIconView()).setImageDrawable(u_nativead.getIcon().getDrawable());
            u_nativeAadview.getIconView().setVisibility(View.VISIBLE);
        }
        if (u_nativead.getPrice() == null) {
            u_nativeAadview.getPriceView().setVisibility(View.GONE);
        } else {
            u_nativeAadview.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) u_nativeAadview.getPriceView()).setText(u_nativead.getPrice());
        }
        if (u_nativead.getStore() == null) {
            u_nativeAadview.getStoreView().setVisibility(View.GONE);
        } else {
            u_nativeAadview.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) u_nativeAadview.getStoreView()).setText(u_nativead.getStore());
        }
        if (u_nativead.getStarRating() == null) {
            u_nativeAadview.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) u_nativeAadview.getStarRatingView()).setRating(u_nativead.getStarRating().floatValue());
            u_nativeAadview.getStarRatingView().setVisibility(View.VISIBLE);
        }
        if (u_nativead.getAdvertiser() == null) {
            u_nativeAadview.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) u_nativeAadview.getAdvertiserView()).setText(u_nativead.getAdvertiser());
            u_nativeAadview.getAdvertiserView().setVisibility(View.VISIBLE);
        }
        u_nativeAadview.setNativeAd(u_nativead);
        VideoController videoController = u_nativead.getVideoController();
        if (videoController.hasVideoContent()) {
            videoController.setVideoLifecycleCallbacks(new VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.videoArr.size();
    }

    @Override
    public int getItemViewType(int i) {
        return this.videoArr.get(i) != null ? 1 : 0;
    }

    @RequiresApi(api = 16)
    public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {

        int itemViewType = myViewHolder.getItemViewType();
        if (itemViewType == 0) {
            if (MyAppUtils.verifyInstallerId(context) || BuildConfig.DEBUG) {
                nativeAd(myViewHolder, i);
            }


        } else if (itemViewType == 1) {



            String videoTitle = (this.videoArr.get(i)).getTitle().trim().replaceAll("[0-9]", "").replace("_", " ").replace("boo", "");

            String titleCapital = removeLastChars(MyAppUtils.capitalize(videoTitle).trim(), 1);

            myViewHolder.videoName.setText(titleCapital);

            Picasso.get().load((this.videoArr.get(i)).getVideoThumb() != null ? (this.videoArr.get(i)).getVideoThumb() : "").placeholder(R.drawable.bg_card).into(myViewHolder.videoThumb);

            myViewHolder.videoThumb.setOnClickListener(view -> {
                if (!MyAppUtils.isConnectingToInternet(context)) {

                    Toast.makeText(NvVideoViewAdapter.this.context, "Please Connect to Internet.", Toast.LENGTH_SHORT).show();


                } else if (NvVideoViewAdapter.this.videoArr.get(i) != null) {

                    Utils.static_video_model_data = NvVideoViewAdapter.this.videoArr.get(i);

                    VideoviewModel videoviewModelData = Utils.static_video_model_data;

                    if (videoviewModelData == null || videoviewModelData.getVideo_link() == null) {
                        Toast.makeText(NvVideoViewAdapter.this.context, "Slow Network Connection. Try Again.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    MyAppUtils.showInterstitialAds(interstitialAd, googleAds, context, context.getString(R.string.unity_inter));

                    NvVideoViewAdapter.this.context.startActivity(new Intent(NvVideoViewAdapter.this.context, NV_VideoListActivity.class)
                            .putExtra("mdata", videoArr)
                            .putExtra("position", i));

                } else {
                    Toast.makeText(NvVideoViewAdapter.this.context, "Something went wrong! please check internet connection.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        View inflate;
        if (i == 0) {
            inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ad_view, viewGroup, false);
        } else {
            inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_video, viewGroup, false);
        }
        return new MyViewHolder(inflate);
    }

    public static class MyViewHolder extends ViewHolder {
        private ImageView videoThumb;
        private TextView videoName;
        private FrameLayout fbPlaceHolder;
        private NativeAdLayout fbNativeAsContainer;

        public MyViewHolder(View view) {
            super(view);
            videoThumb = (ImageView) view.findViewById(R.id.video_thumb);
            videoName = (TextView) view.findViewById(R.id.video_name);
            fbPlaceHolder = (FrameLayout) view.findViewById(R.id.fl_adplaceholder);
            fbNativeAsContainer = (NativeAdLayout) view.findViewById(R.id.fb_native_ad_container);
        }
    }

}
