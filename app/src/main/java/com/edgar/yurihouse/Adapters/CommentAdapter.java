package com.edgar.yurihouse.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edgar.yurihouse.Controllers.CommentController;
import com.edgar.yurihouse.Items.CommentItem;
import com.edgar.yurihouse.Items.MangaItem;
import com.edgar.yurihouse.R;
import com.edgar.yurihouse.Utils.GlideUtil;
import com.edgar.yurihouse.Utils.MyHtmlImageGetter;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<CommentItem> commentItems;
    private CommentController commentController;

    public CommentAdapter(Context context, CommentController commentController) {
        this.context = context;
        this.commentController = commentController;
        this.commentItems = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == CommentItem.TYPE_FOOTER) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_recycler_footer, parent,
                    false);
            return new CommentsFooterHolder(itemView);
        }
        itemView = LayoutInflater.from(context).inflate(R.layout.item_outside_comment, parent, false);
        return new CommentsNormalHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof CommentsFooterHolder) return;
        CommentsNormalHolder mHolder = (CommentsNormalHolder) holder;

        CommentItem commentItem = commentItems.get(position);

        if (commentItem.getMasterCommentNum() != 0) {
            mHolder.lvInnerContainer.setVisibility(View.VISIBLE);
            commentController.loadInnerComments(context, mHolder.lvInnerContainer,
                    commentItem.getMasterComment());
//            loadInnerComments(mHolder.lvInnerContainer, commentItem.getMasterComment());
        } else {
            mHolder.lvInnerContainer.setVisibility(View.GONE);
        }

        String avatarUrl = commentItem.getAvatar_url();
//        imageUtil.setCircularImage(mHolder.ivAvatar, avatarUrl);
        GlideUtil.setCircularImage(mHolder.ivAvatar, avatarUrl);
        int genderResId = (commentItem.getSex() == 1) ? R.drawable.ic_male : R.drawable.ic_female;
        mHolder.ivGender.setImageResource(genderResId);
        mHolder.tvNickname.setText(commentItem.getNickname());

        mHolder.tvTime.setText(getDateString(commentItem.getCreate_time()));
        mHolder.tvLikeCount.setText(String.valueOf(commentItem.getLike_amount()));
        mHolder.tvCommentCount.setText(String.valueOf(commentItem.getReply_amount()));

        String htmlContentString = "<span>" + commentItem.getContent() + "</span><p></p>";
        String[] outsideUrls = commentItem.getUpload_images().split(",");

        for (int imageIndex = 0; imageIndex < outsideUrls.length; imageIndex++) {
            if (outsideUrls[imageIndex].length() != 0) {
                if (!outsideUrls[imageIndex].startsWith("http")) {
                    outsideUrls[imageIndex] = "https://images.dmzj.com/commentImg/" +
                            commentItem.getObj_id() % 500 + "/" + outsideUrls[imageIndex];
                }
                htmlContentString = htmlContentString + "<img src=\"" + outsideUrls[imageIndex] + "\" />";
            }
        }
        mHolder.htvContent.setHtml(htmlContentString, new MyHtmlImageGetter(mHolder.htvContent));
        commentController.setupImageSpan(context, mHolder.htvContent, outsideUrls);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_from_bottom);
        mHolder.mView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return (commentItems == null ? 0 : commentItems.size());
    }

    @Override
    public long getItemId(int position) {
        return commentItems.get(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return commentItems.get(position).getItemType();
    }

    public void addItems(ArrayList<CommentItem> items) {
        int startPos = commentItems.size();
        this.commentItems.addAll(items);
        notifyItemRangeInserted(startPos, items.size());
    }

    public void setItems(ArrayList<CommentItem> items) {
        this.commentItems = new ArrayList<>(items);
        notifyDataSetChanged();
    }


    protected class CommentsNormalHolder extends RecyclerView.ViewHolder {

        protected ImageView ivAvatar, ivGender;
        protected TextView tvNickname, tvTime, tvLikeCount, tvCommentCount;
        protected HtmlTextView htvContent;
        protected LinearLayout lvInnerContainer;
        protected View mView;

        public CommentsNormalHolder(View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_user_avatar);
            ivGender = itemView.findViewById(R.id.iv_user_gender);
            tvNickname = itemView.findViewById(R.id.tv_user_nickname);
            tvTime = itemView.findViewById(R.id.tv_comment_time);
            tvLikeCount = itemView.findViewById(R.id.tv_like_count);
            tvCommentCount = itemView.findViewById(R.id.tv_comment_count);
            htvContent = itemView.findViewById(R.id.tv_outside_comment_content);
            lvInnerContainer = itemView.findViewById(R.id.lv_inner_comment_container);
            mView = itemView;

        }
    }

    protected class CommentsFooterHolder extends RecyclerView.ViewHolder {
        public CommentsFooterHolder(View itemView) {
            super(itemView);
        }
    }

    private String getDateString(long lastUpdateTime) {
        long times = lastUpdateTime * 1000;
        Date date = new Date(times);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formatter.format(date);
    }

    public void addFooter() {
        commentItems.add(new CommentItem(MangaItem.TYPE_FOOTER));
        notifyItemInserted(commentItems.size() - 1);
    }

    public void removeFooter() {
        if (commentItems.size() == 0)return;
        if (getItemViewType(commentItems.size() - 1) == MangaItem.TYPE_FOOTER) {
            commentItems.remove(commentItems.size() - 1);
            notifyItemRemoved(commentItems.size());
        }
    }

}
