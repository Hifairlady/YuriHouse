package com.edgar.yurihouse.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.edgar.yurihouse.R;
import com.edgar.yurihouse.Utils.GlideUtil;

import java.util.Arrays;


public class ReaderAdapter extends RecyclerView.Adapter<ReaderAdapter.ZoomViewHolder> {

    private static final String TAG = "=====================" + ReaderAdapter.class.getSimpleName();

    private Context context;
    private String[] imageUrls;
    private int width;
    private int preloadPosition = 0;

    public ReaderAdapter(Context context, int width) {
        this.context = context;
        this.imageUrls = new String[0];
        this.width = width;
    }

    @NonNull
    @Override
    public ZoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_reader_image_view,
                parent, false);
        return new ZoomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ZoomViewHolder holder, int position) {
//        GlideUtil.setScaledImage(holder.imageView.getContext(), imageUrls[position], holder.imageView, width);
        GlideUtil.loadWithoutMemoryCache(holder.imageView, imageUrls[position]);
//        if (preloadPosition < position) {
//            preloadPosition += GlideUtil.preloadImage(context, imageUrls, position+1, 3);
//        }
//        Log.d(TAG, "getView: " + preloadPosition + "  " + position);
    }

    @Override
    public int getItemCount() {
        return (imageUrls == null ? 0 : imageUrls.length);
    }

    protected class ZoomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView imageView;

        public ZoomViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_reader_image);
        }
    }

    @Override
    public long getItemId(int position) {
        return imageUrls[position].hashCode();
    }

    public void setItems(String[] imageUrls1) {
        if (imageUrls1 == null || imageUrls1.length == 0) return;

        this.imageUrls = Arrays.copyOf(imageUrls1, imageUrls1.length);
        notifyDataSetChanged();
    }

}
