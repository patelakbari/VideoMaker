package newwave.videomaker.statusmaker.adapter;

import android.app.AlertDialog;
import android.content.Context;
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

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import newwave.videomaker.statusmaker.R;
import com.bumptech.glide.Glide;
import com.facebook.ads.BuildConfig;
import com.facebook.ads.NativeAdLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader.Builder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoController.VideoLifecycleCallbacks;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import newwave.videomaker.statusmaker.activity.NV_SaveVideoFileActivity;
import newwave.videomaker.statusmaker.utils.Utils_Admob;
import newwave.videomaker.statusmaker.utils.MyAppUtils;

public class NvAlbumAdapter extends Adapter<NvAlbumAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<String> arrayList;
    private UnifiedNativeAd nativeAd;

    public NvAlbumAdapter(Context context, ArrayList<String> arrayList) {
        this.mContext = context;
        this.arrayList = arrayList;
    }

    private void loadFBAds(MyViewHolder myViewHolder, int i) {
        Utils_Admob.showCustomFBNativeAds(this.mContext, myViewHolder.nativeAdLayout, i);
    }

    private void nativeAd(final MyViewHolder myViewHolder, final int i) {
        Context context = this.mContext;
        Builder builder = new Builder(context, context.getResources().getString(R.string.admob_native));
        builder.forUnifiedNativeAd(unifiedNativeAd -> {
            if (NvAlbumAdapter.this.nativeAd != null) {
                NvAlbumAdapter.this.nativeAd.destroy();
            }
            NvAlbumAdapter.this.nativeAd = unifiedNativeAd;

            RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(NvAlbumAdapter.this.mContext).inflate(R.layout.ad_creation, null);

            NvAlbumAdapter.this.populateUnifiedNativeAdView(unifiedNativeAd, (UnifiedNativeAdView) relativeLayout.findViewById(R.id.unified), (CardView) relativeLayout.findViewById(R.id.card_bg));

            try {
                myViewHolder.adplace.removeAllViews();
                myViewHolder.adplace.addView(relativeLayout);
            } catch (Exception unused) {
                unused.getStackTrace();
            }
        });
        builder.withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build());
        builder.withAdListener(new AdListener() {
            public void onAdFailedToLoad(int i) {
                NvAlbumAdapter.this.loadFBAds(myViewHolder, i);
            }
        }).build().loadAd(new AdRequest.Builder().addTestDevice("510B65D53E3C09244A45D97D02BCCA3B").addTestDevice("B3EEABB8EE11C2BE770B684D95219ECB").addTestDevice("BA53E751687A8A485F5A01F729C9C012").addTestDevice("9B4ACA2709B714E1AB63C6D14F694935").build());
    }

    private void populateUnifiedNativeAdView(UnifiedNativeAd u_nativead, UnifiedNativeAdView u_nativeAadview, CardView cardView) {
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
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });
        }
    }

    public int getItemCount() {
        return this.arrayList.size();
    }

    public int getItemViewType(int i) {
        return this.arrayList.get(i) != null ? 1 : 0;
    }


    public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {
        int itemViewType = myViewHolder.getItemViewType();
        if (itemViewType == 0) {


            if (MyAppUtils.verifyInstallerId(mContext) || BuildConfig.DEBUG) {
                nativeAd(myViewHolder, i);
            }

        } else if (itemViewType == 1) {


            Glide.with(this.mContext).load(this.arrayList.get(i)).placeholder(R.drawable.bg_card).into(myViewHolder.myimage);


            myViewHolder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(NvAlbumAdapter.this.mContext, NV_SaveVideoFileActivity.class);
                intent.putExtra("videourl", NvAlbumAdapter.this.arrayList.get(i));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NvAlbumAdapter.this.mContext.startActivity(intent);
            });


            myViewHolder.delete.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(NvAlbumAdapter.this.mContext);
                builder.setTitle("Sure to Delete?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", (dialogInterface, i12) -> {
                    try {
                        if (NvAlbumAdapter.this.arrayList.size() > i12) {
                            NvAlbumAdapter.this.arrayList.remove(i12);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    NvAlbumAdapter.this.notifyDataSetChanged();
                }).setPositiveButton("NO", (dialogInterface, i1) -> dialogInterface.dismiss());
                builder.create();
                builder.show();
            });
        }
    }

    @NotNull
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        View inflate;
        if (i == 0) {
            inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ad_view, viewGroup, false);
        } else if (i != 1) {
            inflate = null;
        } else {
            inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album, viewGroup, false);
        }
        return new MyViewHolder(inflate);
    }

    public static class MyViewHolder extends ViewHolder {
        private ImageView myimage;
        private ImageView delete;
        private FrameLayout adplace;
        private NativeAdLayout nativeAdLayout;

        public MyViewHolder(View view) {
            super(view);
            this.myimage = view.findViewById(R.id.c_myImage);
            this.delete = view.findViewById(R.id.delete);
            this.adplace = view.findViewById(R.id.fl_adplaceholder);
            this.nativeAdLayout = view.findViewById(R.id.fb_native_ad_container);
        }
    }
}
