package newwave.videomaker.statusmaker.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.NativeAdLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;
import com.unity3d.ads.UnityAds;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import newwave.videomaker.statusmaker.R;
import newwave.videomaker.statusmaker.activity.NV_VideoListActivity;
import newwave.videomaker.statusmaker.model.VideoviewModel;
import newwave.videomaker.statusmaker.utils.MyAppUtils;
import newwave.videomaker.statusmaker.utils.Utils;

public class NvVideoViewAdapter1 extends Adapter<NvVideoViewAdapter1.MyViewHolder> {
    ArrayList<VideoviewModel> videoArr;
    private Activity context;
    EventListener mEventListener;

    public NvVideoViewAdapter1(Activity context, ArrayList<VideoviewModel> arrayList) {
        this.context = context;
        this.videoArr = arrayList;
        initializeAds();
    }

    public interface EventListener {

        void onItemViewClick(int position);

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


    @Override
    public int getItemCount() {
        return this.videoArr.size();
    }


//    @Override
//    public int getItemViewType(int i) {
//        return this.videoArr.get(i) != null ? 1 : 0;
//    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @RequiresApi(api = 16)
    public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {

        int itemViewType = myViewHolder.getItemViewType();

//        mEventListener.onItemViewClick(position);

        String videoTitle = (this.videoArr.get(i)).getTitle().trim().replaceAll("[0-9]", "").replace("_", " ").replace("boo", "");

        String titleCapital = removeLastChars(MyAppUtils.capitalize(videoTitle).trim(), 1);

        if (i == 0) {
            myViewHolder.rv_back.setBackgroundResource(R.drawable.gradient1);
        }else if(i == 1){
            myViewHolder.rv_back.setBackgroundResource(R.drawable.gradient2);
        }else if(i == 2){
            myViewHolder.rv_back.setBackgroundResource(R.drawable.gradient3);
        }else if(i == 3){
            myViewHolder.rv_back.setBackgroundResource(R.drawable.gradient4);
        }else if(i == 4){
            myViewHolder.rv_back.setBackgroundResource(R.drawable.gradient5);
        }else if(i == 5){
            myViewHolder.rv_back.setBackgroundResource(R.drawable.gradient6);
        }else if(i == 6){
            myViewHolder.rv_back.setBackgroundResource(R.drawable.gradient1);
        }else if(i == 7){
            myViewHolder.rv_back.setBackgroundResource(R.drawable.gradient2);
        }else if(i == 8){
            myViewHolder.rv_back.setBackgroundResource(R.drawable.gradient3);
        }else if(i == 9){
            myViewHolder.rv_back.setBackgroundResource(R.drawable.gradient4);
        }else if(i == 10){
            myViewHolder.rv_back.setBackgroundResource(R.drawable.gradient5);
        }
        Picasso.get().load((this.videoArr.get(i)).getVideoThumb() != null ? (this.videoArr.get(i)).getVideoThumb() : "").into(myViewHolder.videoThumb);

        myViewHolder.videoThumb.setOnClickListener(view -> {
            if (!MyAppUtils.isConnectingToInternet(context)) {

                Toast.makeText(NvVideoViewAdapter1.this.context, "Please Connect to Internet.", Toast.LENGTH_SHORT).show();


            } else if (NvVideoViewAdapter1.this.videoArr.get(i) != null) {

                Utils.static_video_model_data = NvVideoViewAdapter1.this.videoArr.get(i);

                VideoviewModel videoviewModelData = Utils.static_video_model_data;

                if (videoviewModelData == null || videoviewModelData.getVideo_link() == null) {
                    Toast.makeText(NvVideoViewAdapter1.this.context, "Slow Network Connection. Try Again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                MyAppUtils.showInterstitialAds(interstitialAd, googleAds, context, context.getString(R.string.unity_inter));

                NvVideoViewAdapter1.this.context.startActivity(new Intent(NvVideoViewAdapter1.this.context, NV_VideoListActivity.class)
                        .putExtra("mdata", videoArr)
                        .putExtra("position", i));

            } else {
                Toast.makeText(NvVideoViewAdapter1.this.context, "Something went wrong! please check internet connection.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        View inflate;

        inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_video1, viewGroup, false);

        return new MyViewHolder(inflate);
    }

    public static class MyViewHolder extends ViewHolder {
        private ImageView videoThumb;
        private FrameLayout fbPlaceHolder;
        private NativeAdLayout fbNativeAsContainer;
        RelativeLayout rv_back;

        public MyViewHolder(View view) {
            super(view);
            videoThumb = (ImageView) view.findViewById(R.id.video_thumb);
            fbPlaceHolder = (FrameLayout) view.findViewById(R.id.fl_adplaceholder);
            fbNativeAsContainer = (NativeAdLayout) view.findViewById(R.id.fb_native_ad_container);
            rv_back = view.findViewById(R.id.rv_back);
        }
    }

    public void setEventListener(EventListener mEventListener) {
        this.mEventListener = mEventListener;
    }

}
