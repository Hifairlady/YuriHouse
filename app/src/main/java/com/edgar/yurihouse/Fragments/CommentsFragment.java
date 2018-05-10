package com.edgar.yurihouse.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edgar.yurihouse.Activities.CommentsActivity;
import com.edgar.yurihouse.Controllers.CommentController;
import com.edgar.yurihouse.Items.CommentItem;
import com.edgar.yurihouse.R;
import com.edgar.yurihouse.Utils.GlideUtil;
import com.edgar.yurihouse.Utils.MyHtmlImageGetter;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommentsFragment extends Fragment {

    private static final String ARG_ALL_URL = "allUrlString";
    private static final String ARG_HOT_URL = "hotUrlString";

    private boolean isFragDestroyed = false;

    private static final int MAX_COMMENT_LOAD = 10;

    private String allUrl, hotUrl;

    private Button btnLoadAllComments, btnLoadHotComments;

    private ArrayList<CommentItem> hotComments = new ArrayList<>();
    private ArrayList<CommentItem> allComments = new ArrayList<>();
    private LinearLayout lvHotCommentsContainer, lvAllCommentsContainer;

    private CommentController commentController;

    public CommentsFragment() { }

    public static CommentsFragment newInstance(String allUrl, String hotUrl,
                                               CommentController commentController) {
        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ALL_URL, allUrl);
        args.putString(ARG_HOT_URL, hotUrl);
        fragment.setArguments(args);
        fragment.setCommentController(commentController);
        return fragment;
    }

    public void setCommentController(CommentController commentController) {
        this.commentController = commentController;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            allUrl = getArguments().getString(ARG_ALL_URL);
            hotUrl = getArguments().getString(ARG_HOT_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lvHotCommentsContainer = view.findViewById(R.id.lv_hot_comment_container);
        lvAllCommentsContainer = view.findViewById(R.id.lv_all_comment_container);

        btnLoadAllComments = view.findViewById(R.id.btn_load_all_comments);
        btnLoadHotComments = view.findViewById(R.id.btn_load_all_hot_comments);

        btnLoadAllComments.setOnClickListener(mOnClickListener);
        btnLoadHotComments.setOnClickListener(mOnClickListener);

        btnLoadHotComments.setClickable(false);
        btnLoadAllComments.setClickable(false);

        commentController.setupComments(hotUrl, hotJsonHandler);

    }

    @SuppressLint("HandlerLeak")
    private Handler hotJsonHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case R.integer.get_data_success:

                    if (isFragDestroyed) return;
                    String jsonString = (String)msg.obj;
                    int startPos = jsonString.indexOf("[");
                    int endPos = jsonString.lastIndexOf("]") + 1;
                    jsonString = jsonString.substring(startPos, endPos);
                    if (jsonString.charAt(0) != '[' && jsonString.charAt(jsonString.length()-1) != ']') {
                        return;
                    }
                    Type listType = new TypeToken<ArrayList<CommentItem>>() {}.getType();
                    hotComments = new GsonBuilder().create().fromJson(jsonString, listType);

                    if (hotComments == null || hotComments.size() == 0) {
                        btnLoadHotComments.setText(R.string.no_any_hot_coments);
                        commentController.setupComments(allUrl, allJsonHandler);
                        return;
                    }

                    loadComments(lvHotCommentsContainer, hotComments);
                    btnLoadHotComments.setClickable(true);
                    btnLoadHotComments.setText(R.string.check_hot_comments_string);
                    commentController.setupComments(allUrl, allJsonHandler);
                    break;

                case R.integer.get_data_failed:

                    if (isFragDestroyed) return;
                    Snackbar.make(lvHotCommentsContainer, R.string.loading_comments_failed_string,
                            Snackbar.LENGTH_SHORT).show();
                    btnLoadHotComments.setText(R.string.loading_comments_failed_string);
                    btnLoadHotComments.setClickable(false);
                    commentController.setupComments(hotUrl, hotJsonHandler);
                    break;

                default:
                    break;
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler allJsonHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case R.integer.get_data_success:

                    if (isFragDestroyed) return;
                    String jsonString = (String)msg.obj;
                    int startPos = jsonString.indexOf("[");
                    int endPos = jsonString.lastIndexOf("]") + 1;
                    jsonString = jsonString.substring(startPos, endPos);
                    if (jsonString.charAt(0) != '[' && jsonString.charAt(jsonString.length()-1) != ']') {
                        return;
                    }
                    Type listType = new TypeToken<ArrayList<CommentItem>>() {}.getType();
                    allComments = new GsonBuilder().create().fromJson(jsonString, listType);

                    if (allComments == null || allComments.size() == 0) {
                        btnLoadAllComments.setText(R.string.no_any_comments);
                        return;
                    }

                    loadComments(lvAllCommentsContainer, allComments);
                    btnLoadAllComments.setClickable(true);
                    btnLoadAllComments.setText(R.string.check_all_comments_string);
                    break;

                case R.integer.get_data_failed:

                    if (isFragDestroyed) return;
                    Snackbar.make(lvAllCommentsContainer, R.string.loading_comments_failed_string,
                            Snackbar.LENGTH_SHORT).show();
                    btnLoadAllComments.setText(R.string.loading_comments_failed_string);
                    btnLoadAllComments.setClickable(false);
                    break;

                default:
                    break;
            }
        }
    };

    private void loadComments(LinearLayout lvHotContainer, ArrayList<CommentItem> commentItems) {

        int maxLoading = Math.min(MAX_COMMENT_LOAD, commentItems.size());

//        ArrayList<CommentItem> newCommentItems = new ArrayList<>(commentItems.subList(0, maxLoading));

        for (int i = 0; i < maxLoading; i++) {

            if (isFragDestroyed) return;

            View outSideView = LayoutInflater.from(getContext()).inflate(R.layout.item_outside_comment, null);
            TextView tvTime = outSideView.findViewById(R.id.tv_comment_time);
            TextView tvLikeCount = outSideView.findViewById(R.id.tv_like_count);
            TextView tvCommentCount = outSideView.findViewById(R.id.tv_comment_count);
            HtmlTextView tvCommentContent = outSideView.findViewById(R.id.tv_outside_comment_content);
            TextView tvUsername = outSideView.findViewById(R.id.tv_user_nickname);
            ImageView ivGender = outSideView.findViewById(R.id.iv_user_gender);
            ImageView ivAvatar = outSideView.findViewById(R.id.iv_user_avatar);

            tvTime.setText(getDateString(commentItems.get(i).getCreate_time()));
            tvLikeCount.setText(String.valueOf(commentItems.get(i).getLike_amount()));
            tvCommentCount.setText(String.valueOf(commentItems.get(i).getReply_amount()));
            tvUsername.setText(commentItems.get(i).getNickname());
            GlideUtil.setCircularImage(ivAvatar, commentItems.get(i).getAvatar_url());

            int genderResId = (commentItems.get(i).getSex() == 2) ? R.drawable.ic_female : R.drawable.ic_male;
            ivGender.setImageResource(genderResId);

            String htmlContent = "<span>" + commentItems.get(i).getContent() + "</span><p></p>";
            String[] outImageUrls = commentItems.get(i).getUpload_images().split(",");

            for (int outIndex = 0; outIndex < outImageUrls.length; outIndex++) {
                if (outImageUrls[outIndex].length() == 0) continue;
                if (!outImageUrls[outIndex].startsWith("http")) {
                    outImageUrls[outIndex] = "https://images.dmzj.com/commentImg/" +
                            commentItems.get(i).getObj_id() % 500 + "/" + outImageUrls[outIndex];
                }
                htmlContent = htmlContent + "<img src=\"" + outImageUrls[outIndex] + "\">";
            }
            tvCommentContent.setHtml(htmlContent, new MyHtmlImageGetter(tvCommentContent));

            if (commentItems.get(i).getMasterCommentNum() != 0) {
                LinearLayout lvInnerContainer = outSideView.findViewById(R.id.lv_inner_comment_container);
                lvHotContainer.setVisibility(View.VISIBLE);
                ArrayList<CommentItem.MasterCommentItem> masterCommentItems = commentItems.get(i).getMasterComment();
//                loadInnerComments(lvInnerContainer, masterCommentItems);
                commentController.loadInnerComments(getContext(), lvInnerContainer, masterCommentItems);
            }

            commentController.setupImageSpan(getContext(), tvCommentContent, outImageUrls);
            lvHotContainer.addView(outSideView);

        }
    }

    private String getDateString(long lastUpdateTime) {
        long times = lastUpdateTime * 1000;
        Date date = new Date(times);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formatter.format(date);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFragDestroyed = true;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.btn_load_all_comments:
                    Intent allCommentIntent = new Intent(getContext(), CommentsActivity.class);
                    allCommentIntent.putExtra("queryUrl", allUrl);
                    allCommentIntent.putExtra("titleString", getString(R.string.all_comments_string_extra));
                    startActivity(allCommentIntent);
                    break;

                case R.id.btn_load_all_hot_comments:
                    Intent hotCommentIntent = new Intent(getContext(), CommentsActivity.class);
                    hotCommentIntent.putExtra("queryUrl", hotUrl);
                    hotCommentIntent.putExtra("titleString", getString(R.string.all_hot_comments_string_extra));
                    startActivity(hotCommentIntent);
                    break;

                default:
                    break;
            }
        }
    };




}
