package com.edgar.yurihouse.Presenters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.edgar.yurihouse.Adapters.MangaAdapter;
import com.edgar.yurihouse.Controllers.MainController;
import com.edgar.yurihouse.Items.MangaItem;
import com.edgar.yurihouse.R;

import java.util.ArrayList;

public class MainScenePresenter {

    private Context context;

    private int curStatusCode = 0, curRegionCode = 0, curOrderCode = 0, curPageNum = 0;

    private String curStatusString = "全部";
    private String curRegionString = "全部";
    private String curOrderString = "默认";

    private TextView tvFilterStatus;
    private TextView tvFilterRegion;
    private TextView tvFilterOrder;

    private LinearLayout btnStatus;
    private LinearLayout btnOrder;
    private LinearLayout btnRegion;

    private FloatingActionButton fabTop;

    private Dialog filterDialog;
    private String queryUrl;
    private boolean isLoadingNextPage = false;

    private int scrollDistance = 0;

    private RecyclerView recyclerView;
    private MangaAdapter listAdapter;
    private SwipeRefreshLayout refreshLayout;

    private boolean isGridOn = true;
    private MainController mainController;

    public MainScenePresenter(Context context, MainController mainController) {
        this.context = context;
        this.mainController = mainController;
    }

    public void switchDisplayMode(MenuItem item) {
        isGridOn = !isGridOn;
        scrollDistance = 0;
        fabTop.hide();
        if (!isGridOn) {
            item.setIcon(R.drawable.ic_grid_on);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            listAdapter = new MangaAdapter(context, isGridOn);
        } else {
            item.setIcon(R.drawable.ic_grid_off);
            GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
            recyclerView.setLayoutManager(layoutManager);
            listAdapter = new MangaAdapter(context, isGridOn);
        }
        setupRecyclerView();
    }

    public void initFilterDialog() {

        filterDialog = new Dialog(context);
        filterDialog.setContentView(R.layout.layout_popup_filter);
        filterDialog.setCanceledOnTouchOutside(true);

        tvFilterStatus = filterDialog.findViewById(R.id.filter_status_textview);
        tvFilterRegion = filterDialog.findViewById(R.id.filter_region_textview);
        tvFilterOrder = filterDialog.findViewById(R.id.filter_order_textview);

        btnStatus = filterDialog.findViewById(R.id.btn_filter_by_status);
        btnOrder = filterDialog.findViewById(R.id.btn_filter_by_order);
        btnRegion = filterDialog.findViewById(R.id.btn_filter_by_region);

        btnStatus.setOnClickListener(mOnClickListener);
        btnOrder.setOnClickListener(mOnClickListener);
        btnRegion.setOnClickListener(mOnClickListener);

        Button btnFilterDismiss = filterDialog.findViewById(R.id.btn_filter_dismiss);
        Button btnFilterApply = filterDialog.findViewById(R.id.btn_filter_apply);

        btnFilterDismiss.setOnClickListener(mOnClickListener);
        btnFilterApply.setOnClickListener(mOnClickListener);

    }

    public void setupFilters() {

        curStatusCode = mainController.getStoredIndices(context, "curStatusCode");
        curRegionCode = mainController.getStoredIndices(context, "curRegionCode");
        curOrderCode = mainController.getStoredIndices(context, "curOrderCode");

        curStatusString = mainController.getStoredNames(context, "curStatusString");
        curRegionString = mainController.getStoredNames(context, "curRegionString");
        curOrderString = mainController.getStoredNames(context, "curOrderString");

        curStatusString = (curStatusString == null ? "全部" : curStatusString);
        curRegionString = (curRegionString == null ? "全部" : curRegionString);
        curOrderString = (curOrderString == null ? "默认" : curOrderString);

        tvFilterStatus.setText(curStatusString);
        tvFilterRegion.setText(curRegionString);
        tvFilterOrder.setText(curOrderString);
    }

    private void applyFilters() {
        curPageNum = 0;
        queryUrl = mainController.getSortUrl(curStatusCode, curRegionCode,
                curOrderCode, curPageNum);

        String[] filterNames = {curStatusString, curRegionString, curOrderString};
        int[] filterIndices = {curStatusCode, curRegionCode, curOrderCode};

        mainController.storeFilterNames(context, filterNames,
                MainController.storeStringKeys);
        mainController.storeFilterIndices(context, filterIndices,
                MainController.storeIntegerKeys);

        queryUrl = mainController.getSortUrl(curStatusCode, curRegionCode, curOrderCode, curPageNum);
        mainController.refreshData(queryUrl, refreshHandler);

    }

    public void showFilter() {
        filterDialog.show();
    }

    public void initFab(FloatingActionButton fab) {
        this.fabTop = fab;
        this.fabTop.bringToFront();
        this.fabTop.hide();
        this.fabTop.setOnClickListener(mOnClickListener);
    }

    public void initRecyclerView(RecyclerView recyclerView, SwipeRefreshLayout refreshLayout) {
        this.recyclerView = recyclerView;
        this.refreshLayout = refreshLayout;
        refreshLayout.setColorSchemeColors(0x000000);
        refreshLayout.setOnRefreshListener(mOnRefreshListener);

        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(layoutManager);
        listAdapter = new MangaAdapter(context, true);
        setupRecyclerView();
    }

    private void setupRecyclerView() {

        listAdapter.setHasStableIds(true);
        recyclerView.setAdapter(listAdapter);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        curPageNum = 0;
        queryUrl = mainController.getSortUrl(curStatusCode, curRegionCode, curOrderCode, curPageNum);
        mainController.refreshData(queryUrl, refreshHandler);

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
                    curPageNum++;
                    isLoadingNextPage = true;
//                    Snackbar.make(recyclerView, "Loading next page...", Snackbar.LENGTH_SHORT).show();
                    queryUrl = mainController.getSortUrl(curStatusCode, curRegionCode,
                            curOrderCode, curPageNum);
                    mainController.loadMoreData(queryUrl, loadMoreHandler);
                }
            }
        });

    }

    private boolean isSlideToBottom(RecyclerView recyclerView) {
        return !isLoadingNextPage && recyclerView != null &&
                recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >=
                        recyclerView.computeVerticalScrollRange();
    }

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            curPageNum = 0;
            queryUrl = mainController.getSortUrl(curStatusCode, curRegionCode,
                    curOrderCode, curPageNum);
            mainController.refreshData(queryUrl, refreshHandler);
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.fab_main_scene:
                    recyclerView.stopScroll();
                    recyclerView.getLayoutManager().scrollToPosition(0);
                    scrollDistance = 0;
                    fabTop.hide();
                    break;

                case R.id.btn_filter_by_status:
                    PopupMenu popupStatusMenu = new PopupMenu(context, btnStatus);
                    popupStatusMenu.getMenuInflater().inflate(R.menu.filter_status_menu, popupStatusMenu.getMenu());
                    popupStatusMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            tvFilterStatus.setText(item.getTitle());
                            curStatusString = item.getTitle().toString();
                            curStatusCode = item.getOrder();
                            return true;
                        }
                    });
                    popupStatusMenu.show();
                    break;

                case R.id.btn_filter_by_region:
                    PopupMenu popupRegionMenu = new PopupMenu(context, btnRegion);
                    popupRegionMenu.getMenuInflater().inflate(R.menu.filter_region_menu, popupRegionMenu.getMenu());
                    popupRegionMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            tvFilterRegion.setText(item.getTitle());
                            curRegionString = item.getTitle().toString();
                            curRegionCode = item.getOrder();
                            return true;
                        }
                    });
                    popupRegionMenu.show();
                    break;

                case R.id.btn_filter_by_order:
                    PopupMenu popupOrderMenu = new PopupMenu(context, btnOrder);
                    popupOrderMenu.getMenuInflater().inflate(R.menu.filter_order_menu, popupOrderMenu.getMenu());
                    popupOrderMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            tvFilterOrder.setText(item.getTitle());
                            curOrderString = item.getTitle().toString();
                            curOrderCode = item.getOrder();
                            return true;
                        }
                    });
                    popupOrderMenu.show();
                    break;

                case R.id.btn_filter_dismiss:
                    setupFilters();
                    filterDialog.dismiss();
                    break;

                case R.id.btn_filter_apply:
                    applyFilters();
                    filterDialog.dismiss();
                    break;

                default:
                    break;
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler refreshHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isLoadingNextPage = false;
            refreshLayout.setRefreshing(false);
            switch (msg.what) {
                case R.integer.get_data_success:
                    String jsonString = (String)msg.obj;
                    ArrayList<MangaItem> items = mainController.getDataFromJson(jsonString);
                    if (items != null) {
                        listAdapter = new MangaAdapter(context, isGridOn);
                        listAdapter.setItems(items);
                        listAdapter.addFooter();
                        listAdapter.setHasStableIds(true);
                        recyclerView.setAdapter(listAdapter);
                    } else {
                        Snackbar.make(recyclerView, R.string.no_data_string, Snackbar.LENGTH_SHORT).show();
                    }
                    break;

                case R.integer.get_data_failed:
                    String errorMsg = (String)msg.obj;
                    Snackbar.make(recyclerView, errorMsg, Snackbar.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };

    @SuppressLint("HandlerLeak") private Handler loadMoreHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            listAdapter.removeFooter();
            switch (msg.what) {
                case R.integer.get_data_success:
                    String jsonString = (String)msg.obj;
                    isLoadingNextPage = false;
                    ArrayList<MangaItem> items = mainController.getDataFromJson(jsonString);
                    if (items != null) {
                        listAdapter.addItems(items);
                        listAdapter.addFooter();
                    } else {
                        isLoadingNextPage = true;
                        Snackbar.make(recyclerView, R.string.has_loaded_all_string, Snackbar.LENGTH_SHORT).show();
                    }
                    break;

                case R.integer.get_data_failed:
                    String errorMsg = (String)msg.obj;
                    isLoadingNextPage = false;
                    Snackbar.make(recyclerView, errorMsg, Snackbar.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };
}
