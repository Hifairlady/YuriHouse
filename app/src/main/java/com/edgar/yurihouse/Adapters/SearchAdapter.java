package com.edgar.yurihouse.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.edgar.yurihouse.Activities.MangaActivity;
import com.edgar.yurihouse.Items.SearchResultItem;
import com.edgar.yurihouse.R;
import com.edgar.yurihouse.Utils.GlideUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private static final String TAG = "====================" + SearchAdapter.class.getSimpleName();

    private Context context;
    private ArrayList<SearchResultItem> resultItems;

    public SearchAdapter(Context context, ArrayList<SearchResultItem> resultItems) {
        this.context = context;
        this.resultItems = resultItems;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_search_result,
                parent, false);
        return new SearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, final int position) {

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_from_bottom);
        holder.mView.startAnimation(animation);

        final SearchResultItem resultItem = resultItems.get(position);
        String urlString = resultItem.getCover();
        if (!urlString.startsWith("https")) {
            urlString = "https://images.dmzj.com/" + urlString;
        }
        GlideUtil.setImageView(context, holder.ivCover, urlString);

        holder.tvTitle.setText(resultItem.getName());
        holder.tvAuthors.setText(resultItem.getAuthors());
        holder.tvTypes.setText(resultItem.getTypes());
        holder.tvChapter.setText(resultItem.getLast_update_chapter_name());
        String dateString = getDateString(resultItem.getLast_updatetime());
        holder.tvTime.setText(dateString);

        if (resultItem.getStatus().equals("连载中")) {
            holder.ivFinishedLogo.setVisibility(View.GONE);
        } else {
            holder.ivFinishedLogo.setVisibility(View.VISIBLE);
        }

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SearchResultItem resultItem = resultItems.get(position);
                Intent infoIntent = new Intent(context, MangaActivity.class);
                infoIntent.putExtra(context.getString(R.string.info_title_string_extra), resultItem.getName());
                String urlString = "https://m.dmzj.com/info/" + resultItem.getId() + ".html";
                String backupUrl = "https://m.dmzj.com/info/" + resultItem.getComic_py() + ".html";
                infoIntent.putExtra(context.getString(R.string.info_url_string_extra), urlString);
                infoIntent.putExtra(context.getString(R.string.back_up_url_string_extra), backupUrl);
                Log.d(TAG, "onClick: " + urlString);
                context.startActivity(infoIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (resultItems == null ? 0 : resultItems.size());
    }

    protected class SearchViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivCover, ivFinishedLogo;
        private TextView tvTitle, tvAuthors, tvTypes, tvTime, tvChapter;
        private View mView;
        private ConstraintLayout rootLayout;

        public SearchViewHolder(View itemView) {
            super(itemView);

            ivCover = itemView.findViewById(R.id.cover_image);
            ivFinishedLogo = itemView.findViewById(R.id.cover_finished_logo);
            tvTitle = itemView.findViewById(R.id.tv_search_item_title);
            tvAuthors = itemView.findViewById(R.id.tv_search_item_authors);
            tvTypes = itemView.findViewById(R.id.tv_search_item_types);
            tvTime = itemView.findViewById(R.id.tv_search_item_time);
            tvChapter = itemView.findViewById(R.id.tv_search_item_chapter);
            rootLayout = itemView.findViewById(R.id.cl_search_item_root);
            mView = itemView;

        }
    }

    private String getDateString(long lastUpdateTime) {
        long times = lastUpdateTime * 1000;
        Date date = new Date(times);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formatter.format(date);
    }

}
