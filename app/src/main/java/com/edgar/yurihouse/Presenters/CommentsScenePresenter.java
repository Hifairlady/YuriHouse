package com.edgar.yurihouse.Presenters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.edgar.yurihouse.Adapters.CommentAdapter;
import com.edgar.yurihouse.Controllers.CommentController;
import com.edgar.yurihouse.Items.CommentItem;
import com.edgar.yurihouse.R;

import java.util.ArrayList;

public class CommentsScenePresenter {

    private Context context;
    private CommentController commentController;

    private boolean isActivityDestroyed = false;

    private String queryUrl;
    private int curPage = 1;
    private int scrollDistance = 0;

    private boolean isLoadingNextPage = false;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private CommentAdapter adapter;
    private boolean fromRefresh = false;

    private FloatingActionButton fabTop;


    public CommentsScenePresenter(Context context, CommentController commentController) {
        this.context = context;
        this.commentController = commentController;
    }

    public void initFab(FloatingActionButton fabTop1) {

        this.fabTop = fabTop1;
        fabTop.bringToFront();
        fabTop.setOnClickListener(mOnClickListener);
        fabTop.hide();
    }

    public void initRecyclerView(RecyclerView recyclerView1, SwipeRefreshLayout refreshLayout1, String queryUrl1) {
        this.queryUrl =queryUrl1;
        this.recyclerView = recyclerView1;
        this.refreshLayout = refreshLayout1;

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        adapter = new CommentAdapter(context, commentController);
        recyclerView.setAdapter(adapter);
        refreshLayout.setColorSchemeColors(0x000000);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                adapter.addFooter();
                refreshLayout.setRefreshing(true);
                fromRefresh = true;
                curPage = 1;
                scrollDistance = 0;
                isLoadingNextPage = true;
                String urlString = getUrlString(queryUrl, curPage);
                commentController.setupCommentsList(urlString, getCommentsHandler);
//                Snackbar.make(recyclerView, context.getString(R.string.loading_next_page_string),
//                        Snackbar.LENGTH_SHORT).show();

            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollDistance = scrollDistance + dy;

                if (scrollDistance > 10000 && !fabTop.isShown()) {
                    fabTop.show();
                } else if (scrollDistance <= 10000 && fabTop.isShown()) {
                    fabTop.hide();
                }

                if (isSlideToBottom(recyclerView) && !isLoadingNextPage) {
                    curPage++;
                    isLoadingNextPage = true;
                    String urlString = getUrlString(queryUrl, curPage);
                    commentController.setupCommentsList(urlString, getCommentsHandler);
//                    Snackbar.make(recyclerView, R.string.loading_next_page_string, Snackbar.LENGTH_SHORT).show();

                }
            }
        });

        adapter.addFooter();
        commentController.setupCommentsList(queryUrl, getCommentsHandler);
    }

    private boolean isSlideToBottom(RecyclerView recyclerView) {
        if (isLoadingNextPage) return false;
        if (recyclerView == null) return false;
        return recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset()
                >= recyclerView.computeVerticalScrollRange();
    }

    private String getUrlString(String srcUrl, int pageNum) {
        int startPos = srcUrl.indexOf("page_index=") + 11;
        int endPos = srcUrl.indexOf("&_=");
        String prefix = srcUrl.substring(0, startPos);
        String suffix = srcUrl.substring(endPos);
        return prefix + String.valueOf(pageNum) + suffix;

    }

    @SuppressLint("HandlerLeak")
    private Handler getCommentsHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isLoadingNextPage = false;
            refreshLayout.setRefreshing(false);
            adapter.removeFooter();
            switch (msg.what) {

                case R.integer.get_data_success:

                    if (isActivityDestroyed) return;

                    String jsonString = (String)msg.obj;
                    int startPos = jsonString.indexOf("[");
                    int endPos = jsonString.lastIndexOf("]") + 1;
                    jsonString = jsonString.substring(startPos, endPos);
                    if (jsonString.charAt(0) != '[' && jsonString.charAt(jsonString.length()-1) != ']') {
                        return;
                    }
                    ArrayList<CommentItem> items = commentController.getCommentItems(jsonString);
                    if (items == null || items.size() == 0) {
                        isLoadingNextPage = true;
                        Snackbar.make(recyclerView, context.getString(R.string.has_loaded_all_string),
                                Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    if (fromRefresh) {
                        adapter.setItems(items);
                    } else {
                        adapter.addItems(items);
                    }
                    adapter.addFooter();
                    fromRefresh = false;
                    break;

                case R.integer.get_data_failed:

                    if (isActivityDestroyed) return;
                    isLoadingNextPage = false;
                    Snackbar.make(recyclerView, R.string.network_error_string, Snackbar.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };


    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.fab_comments_top:
                    fabTop.hide();
                    scrollDistance = 0;
                    recyclerView.stopScroll();
                    recyclerView.scrollToPosition(0);
                    break;

                default:
                    break;
            }
        }
    };

    public void setActivityDestroyed(boolean activityDestroyed) {
        isActivityDestroyed = activityDestroyed;
    }
}
