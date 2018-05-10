package com.edgar.yurihouse.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.edgar.yurihouse.Activities.MangaActivity;
import com.edgar.yurihouse.Items.MangaItem;
import com.edgar.yurihouse.R;
import com.edgar.yurihouse.Utils.GlideUtil;

import java.util.ArrayList;

public class MangaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<MangaItem> mangaItems;
    private Context context;
    private boolean isGridOn;

    public MangaAdapter(Context context, boolean isGridOn) {
        this.context = context;
        this.isGridOn = isGridOn;
        this.mangaItems = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == MangaItem.TYPE_FOOTER) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_recycler_footer, parent, false);
            return new MangaFooterHolder(itemView);
        }

        if (isGridOn) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_grid_manga, parent, false);
            return new MangaNormalHolder(itemView);
        }

        itemView = LayoutInflater.from(context).inflate(R.layout.item_vertical_manga, parent, false);
        return new MangaNormalHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof MangaFooterHolder) return;

        final MangaItem mangaItem = mangaItems.get(position);
        MangaNormalHolder mHolder = (MangaNormalHolder) holder;

        if (!isGridOn) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_from_bottom);
            mHolder.mView.startAnimation(animation);
            mHolder.tvTitle.setText(mangaItem.getName());
            mHolder.tvAuthor.setText(context.getString(R.string.cover_author_string, mangaItem.getAuthors()));
            mHolder.tvType.setText(context.getString(R.string.cover_types_string, mangaItem.getTypes()));
        } else {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_slide_right);
            mHolder.mView.startAnimation(animation);
            mHolder.tvTitle.setText(mangaItem.getName());
            mHolder.tvAuthor.setText(context.getString(R.string.cover_author_string, mangaItem.getAuthors()));
        }

        if (mangaItem.getStatus().equals(context.getString(R.string.chapter_is_serializing))) {
            mHolder.ivFinished.setVisibility(View.GONE);
        } else {
            mHolder.ivFinished.setVisibility(View.VISIBLE);
        }
        mHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent infoIntent = new Intent(context, MangaActivity.class);
                infoIntent.putExtra(context.getString(R.string.info_title_string_extra),
                        mangaItems.get(position).getName());

                String urlString, backupUrl;
                if (mangaItems.get(position).getQueryInfoUrl() != null) {
                    urlString = mangaItems.get(position).getQueryInfoUrl();
                    backupUrl = "NULL";

                } else {
                    urlString = "https://m.dmzj.com/info/" +
                            mangaItems.get(position).getId() + ".html";
                    backupUrl = "https://m.dmzj.com/info/" +
                            mangaItems.get(position).getComic_py() + ".html";
                }
                infoIntent.putExtra(context.getString(R.string.info_url_string_extra), urlString);
                infoIntent.putExtra(context.getString(R.string.back_up_url_string_extra), backupUrl);
                context.startActivity(infoIntent);
            }
        });

        String urlString = mangaItem.getCover();
        if (!urlString.startsWith("https")) {
            urlString = "https://images.dmzj.com/" + urlString;
        }
        GlideUtil.setImageView(mHolder.ivCover, urlString);
    }

    @Override
    public int getItemCount() {
        return (mangaItems == null ? 0 : mangaItems.size());
    }

    @Override
    public long getItemId(int position) {
        return mangaItems.get(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return mangaItems.get(position).getMangaItemType();
    }

    public void addItems(ArrayList<MangaItem> items) {
        int startPos = mangaItems.size();
        this.mangaItems.addAll(items);
//        notifyDataSetChanged();
        notifyItemRangeInserted(startPos, items.size());
    }

    public void setItems(ArrayList<MangaItem> items) {
        this.mangaItems = new ArrayList<>(items);
        notifyDataSetChanged();
    }

    protected class MangaNormalHolder extends RecyclerView.ViewHolder {

        protected ImageView ivCover, ivFinished;
        protected TextView tvTitle, tvAuthor, tvType;
        protected View mView;

        public MangaNormalHolder(View itemView) {
            super(itemView);

            ivCover = itemView.findViewById(R.id.cover_image);
            ivFinished = itemView.findViewById(R.id.cover_finished_logo);
            tvTitle = itemView.findViewById(R.id.cover_title);
            tvAuthor = itemView.findViewById(R.id.cover_author);
            mView = itemView;

            if (!isGridOn) {
                tvType = itemView.findViewById(R.id.cover_class_types);
            }

        }
    }

    protected class MangaFooterHolder extends RecyclerView.ViewHolder {

        public MangaFooterHolder(View itemView) {
            super(itemView);
        }
    }

    public void addFooter() {
        mangaItems.add(new MangaItem(MangaItem.TYPE_FOOTER));
        notifyItemInserted(mangaItems.size() - 1);
    }

    public void removeFooter() {
        if (getItemViewType(mangaItems.size() - 1) == MangaItem.TYPE_FOOTER) {
            mangaItems.remove(mangaItems.size() - 1);
            notifyItemRemoved(mangaItems.size());
        }
    }

}
