package newwave.videomaker.statusmaker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import newwave.videomaker.statusmaker.R;
import newwave.videomaker.statusmaker.activity.NV_VideoEditorActivity;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class NvImageListAdapter extends Adapter<NvImageListAdapter.MyViewHolder> {
    Context context;
    NV_VideoEditorActivity.clickAdapter clickAdapter;
    String[] tempImgUrl;
    int videoArr = 0;

    public NvImageListAdapter(Context context, int i, String[] strArr, NV_VideoEditorActivity.clickAdapter clickAdapter) {
        this.context = context;
        this.videoArr = i;
        this.tempImgUrl = strArr;
        this.clickAdapter = clickAdapter;
    }

    public int getItemCount() {
        return this.videoArr;
    }

    @RequiresApi(api = 16)
    public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {


        Glide.with(context).load(new File(this.tempImgUrl[i])).into(myViewHolder.imageView);

        myViewHolder.imageView.setOnClickListener(view -> NvImageListAdapter.this.clickAdapter.clickEvent(view, i));
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_img_list_, viewGroup, false));
    }

    public static class MyViewHolder extends ViewHolder {
        ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.gallery);
        }
    }
}
