package com.edgar.yurihouse.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.edgar.yurihouse.R;
import com.edgar.yurihouse.Views.ZoomRecyclerView;

public class FullScreenUtil {

    private Window window;
    private int width, height;
    private boolean isFullScreen = true;
    private boolean isScrolling = false;
    private ConstraintLayout clReaderInfo;
    private ZoomRecyclerView recyclerView;
    private Context context;

    private TextView tvTime;
    private TextView tvNetwork;
    private TextView tvPage;
    private TextView tvChapter;
    private String chapterName;
    private int totalPage;
    private int curPosition;

    public FullScreenUtil(Context context, Window window, ZoomRecyclerView recyclerView,
                          ConstraintLayout clReaderInfo, String chapterName, int curPosition) {
        this.window = window;
        this.context = context;
        this.chapterName = chapterName;
        this.totalPage = 1;
        this.curPosition = curPosition + 1;
        this.clReaderInfo = clReaderInfo;
        this.recyclerView = recyclerView;
    }

    public void setTotalPage(int totalPage) {
        recyclerView.scrollToPosition(curPosition - 1);
        this.totalPage = totalPage;
        curPosition = ((LinearLayoutManager)recyclerView.getLayoutManager())
                .findFirstVisibleItemPosition() + 1;
        tvPage.setText(context.getString(R.string.reader_info_page, curPosition, totalPage));
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initListeners() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            width = wm.getDefaultDisplay().getWidth();
            height = wm.getDefaultDisplay().getHeight();
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                isScrolling = (newState != RecyclerView.SCROLL_STATE_IDLE
                        && newState != RecyclerView.SCROLL_STATE_SETTLING);
                if (!isScrolling) {
                    tvTime.setText(NetworkUtil.getDateString());
                    tvNetwork.setText(NetworkUtil.getNetworkType(context));
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                curPosition = ((LinearLayoutManager)recyclerView.getLayoutManager())
                        .findFirstVisibleItemPosition() + 1;
                tvPage.setText(context.getString(R.string.reader_info_page, curPosition, totalPage));
            }
        });

        //handle fullscreen and exit fullscreen
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int x = (int)event.getX();
                    int y = (int)event.getY();

                    if (x >= width/2-200 && x <= width/2+200
                            && y >= height/2-200 && y <= height/2+200) {
                        if (isScrolling) return false;

                        if (isFullScreen) {
                            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                            fullscreenHandler.sendEmptyMessageDelayed(R.integer.fullscreen_message, 3000);

                        } else {
                            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        }
                        isFullScreen = !isFullScreen;
                    }
                }

                return false;
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void initInfoViews() {

        tvTime = clReaderInfo.findViewById(R.id.tv_info_time);
        tvNetwork = clReaderInfo.findViewById(R.id.tv_info_network);
        tvPage = clReaderInfo.findViewById(R.id.tv_info_page);
        tvChapter = clReaderInfo.findViewById(R.id.tv_info_chapter);

        tvTime.setText(NetworkUtil.getDateString());
        tvNetwork.setText(NetworkUtil.getNetworkType(context));
        tvPage.setText(context.getString(R.string.reader_info_page, curPosition, totalPage));
        tvChapter.setText((chapterName == null ? "unknown" : chapterName));
    }

    //set fullscreen after 2000 milliseconds
    @SuppressLint("HandlerLeak")
    private Handler fullscreenHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == R.integer.fullscreen_message) {
                if (!isFullScreen) {
                    isFullScreen = true;
                    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
            }
        }
    };

    public int getCurPosition() {
        return curPosition;
    }
}
