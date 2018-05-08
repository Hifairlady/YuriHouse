package com.edgar.yurihouse.Presenters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.edgar.yurihouse.Adapters.SearchAdapter;
import com.edgar.yurihouse.Controllers.SearchController;
import com.edgar.yurihouse.R;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SearchScenePresenter {

    private Context context;

    private static final String QUOTE_URL = "https://github.com/Hifairlady/LilyHouse/blob/master/quote_text-file.json";

    private static final String SEARCH_PREFIX = "https://m.dmzj.com/search/";

    private TextView tvQuote;
    private SearchView searchView;
    private TextView tvSearchCount;
    private boolean isSearching = false;

    private RecyclerView recyclerView;

    private String searchUrl;
    private SearchController searchController;

    public SearchScenePresenter(Context context, SearchController searchController) {
        this.context = context;
        this.searchController = searchController;
    }

    @SuppressLint("HandlerLeak")
    private Handler getQuoteHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case R.integer.get_data_success:
                    tvQuote.setText(searchController.getQuoteText(context));
                    break;

                case R.integer.get_data_failed:
                    Snackbar.make(recyclerView, R.string.search_failed_string, Snackbar.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }

        }
    };

    public void setupQuoteText(TextView tvQuote) {
        this.tvQuote = tvQuote;
        String dateString;
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateString = dateFormat.format(date);
        String quoteKeyString = "QUOTE " + dateString;
        String quoteTextString = searchController.getQuoteByKey(context, quoteKeyString);

        if (quoteTextString == null) {
            searchController.setupSearchUI(QUOTE_URL, getQuoteHandler);
            quoteTextString = searchController.getQuoteByKey(context, "QUOTE DEFAULT");
            if (quoteTextString == null) {
                quoteTextString = context.getString(R.string.search_quote_text_string);
            }
        }

        tvQuote.setText(quoteTextString);
    }

    public void customSearchView(SearchView mSearchView, TextView mSearchCount, RecyclerView recyclerView1) {
        this.recyclerView = recyclerView1;
        this.searchView = mSearchView;
        this.tvSearchCount = mSearchCount;
        AutoCompleteTextView searchTextView = searchView.findViewById(R.id.search_src_text);
        if (searchTextView == null) {
            return;
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        searchTextView.setTextAppearance(context, R.style.SearchTextStyle);
        searchTextView.setTextColor(context.getResources().getColor(R.color.primary_text));
        searchTextView.setHintTextColor(context.getResources().getColor(R.color.secondary_text));
        searchTextView.setHint(R.string.search_hint_string);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                if (!isSearching) {
                    isSearching = true;
                    searchUrl = URLEncoder.encode(query);
                    searchUrl = SEARCH_PREFIX + searchUrl + ".html";
                    searchController.setupSearchData(searchUrl, getSearchHandler);
                } else {
                    Snackbar.make(recyclerView, R.string.searching_string, Snackbar.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tvSearchCount.setVisibility(View.GONE);
                return false;
            }
        });

    }

    @SuppressLint("HandlerLeak")
    private Handler getSearchHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case R.integer.get_data_success:
                    tvSearchCount.setText(context.getString(R.string.search_result_count_string,
                            searchController.getSearchResultItems().size()));
                    tvSearchCount.setVisibility(View.VISIBLE);
                    SearchAdapter searchAdapter = new SearchAdapter(context, searchController.getSearchResultItems());
                    recyclerView.setAdapter(searchAdapter);
                    isSearching = false;
                    break;

                case R.integer.get_data_failed:
                    break;

                default:
                    break;
            }
        }
    };


}
