package com.edgar.yurihouse.Presenters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edgar.yurihouse.Adapters.ReaderAdapter;
import com.edgar.yurihouse.Controllers.ReaderController;
import com.edgar.yurihouse.Items.ReaderImageItem;
import com.edgar.yurihouse.R;
import com.edgar.yurihouse.Utils.FullScreenUtil;
import com.edgar.yurihouse.Views.ZoomRecyclerView;
import com.google.gson.Gson;

public class ReaderScenePresenter {

    private Context context;
    private ReaderController readerController;

    private static final String TAG = "=================" + ReaderScenePresenter.class.getSimpleName();

    private String queryUrl, fullTitleString;
    private String[] authorsStrings;
    private String jsonString;

    private String viewpointUrl, viewpointJsonUrl;

    private ZoomRecyclerView recyclerView;
    private ReaderAdapter readerAdapter;

    private RecyclerView viewpointRecyclerview;

    private ReaderImageItem readerImageItem;
    private int width;

    private TextView tvFullTitle, tvAuthor, tvTranslator;
    private LinearLayout btnPrevious, btnNext;
    private int chapterId, comicId;
    private ConstraintLayout clReaderInfos;
    private String chapterName;
    private FullScreenUtil fullScreenUtil;
    private int curPosition;

    private int preVisibility, nextVisibility;

    public ReaderScenePresenter(Context context, ReaderController readerController) {
        this.context = context;
        this.readerController = readerController;
    }

    public void setImmerse(Window window) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            width = wm.getDefaultDisplay().getWidth();
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    public void setupArgs(Intent intent, int position) {

        curPosition = position;
        queryUrl = intent.getStringExtra(context.getString(R.string.info_url_string_extra));
        fullTitleString = intent.getStringExtra(context.getString(R.string.full_title_string_extra));
        authorsStrings = intent.getStringArrayExtra(context.getString(R.string.authors_string_extra));
        chapterId = intent.getIntExtra("chapterId", 68989);
        comicId = intent.getIntExtra("comicId", 39883);
        viewpointUrl = "https://m.dmzj.com/view/" + comicId + "/" + chapterId + ".html";
        viewpointJsonUrl = "https://interface.dmzj.com/api/viewpoint/getViewpoint?callback=success_jsonpCallback_201508281118&type=0&type_id=" +
                comicId + "&chapter_id=" + chapterId + "&more=1&_=" + readerController.getDateString();

        preVisibility = intent.getIntExtra("previousVisibility", View.VISIBLE);
        nextVisibility = intent.getIntExtra("nextVisibility", View.VISIBLE);

        chapterName = intent.getStringExtra("chapterName");
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initViews(View view) {

        tvFullTitle = view.findViewById(R.id.tv_drawer_chapter_title);
        tvAuthor = view.findViewById(R.id.tv_drawer_author);
        tvTranslator = view.findViewById(R.id.tv_drawer_translator);
        clReaderInfos = view.findViewById(R.id.cl_reader_info_layout);

        tvFullTitle.setText(fullTitleString);
        String authorsString = "";
        for (int i = 0; i < authorsStrings.length; i++) {
            authorsString += (i == 0) ? "" : ", ";
            authorsString = authorsString.concat(authorsStrings[i]);
        }
        tvAuthor.setText(context.getString(R.string.drawer_authors_name, authorsString));

        btnPrevious = view.findViewById(R.id.lv_button_previous);
        btnNext = view.findViewById(R.id.lv_button_next);

        viewpointRecyclerview = view.findViewById(R.id.rv_drawer_viewpoints);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewpointRecyclerview.setLayoutManager(layoutManager);

        btnPrevious.setVisibility(preVisibility);
        btnNext.setVisibility(nextVisibility);

        //block the touch event
        ConstraintLayout constraintLayout = view.findViewById(R.id.my_drawer_content_layout);
        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    public void initVerticalReader(View rootView, Window window) {

        recyclerView = rootView.findViewById(R.id.zlv_reader_layout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setEnableScale(true);
        readerAdapter = new ReaderAdapter(context, width);
        readerAdapter.setHasStableIds(true);
        recyclerView.setAdapter(readerAdapter);

        fullScreenUtil = new FullScreenUtil(context, window, recyclerView,
                clReaderInfos, chapterName, curPosition);
        fullScreenUtil.initInfoViews();
        fullScreenUtil.initListeners();

        @SuppressLint("HandlerLeak") final Handler getImageUrlsHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {

                    case R.integer.get_data_success:

                        jsonString = readerController.getJsonString();
                        if (jsonString.charAt(0) != '{' || jsonString.charAt(jsonString.length()-1) != '}') return;

                        Gson gson = new Gson();
                        readerImageItem = gson.fromJson(jsonString, ReaderImageItem.class);
                        readerAdapter.setItems(readerImageItem.getPage_url());
                        fullScreenUtil.setTotalPage(readerImageItem.getPage_url().length);

                        tvTranslator.setText(context.getString(R.string.drawer_translator_name,
                                readerImageItem.getTranslator()));
                        break;

                    case R.integer.get_data_failed:
                        Snackbar.make(recyclerView, "Network Error!", Snackbar.LENGTH_SHORT).show();
                        break;

                    default:
                        break;
                }
            }
        };

        readerController.setupImageUrl(queryUrl, getImageUrlsHandler);
    }

    public int getCurPosition() {
        return fullScreenUtil.getCurPosition();
    }


}
