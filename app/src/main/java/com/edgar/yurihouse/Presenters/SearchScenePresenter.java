package com.edgar.yurihouse.Presenters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.edgar.yurihouse.Adapters.SearchAdapter;
import com.edgar.yurihouse.Adapters.SuggestionAdapter;
import com.edgar.yurihouse.Controllers.SearchController;
import com.edgar.yurihouse.R;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class SearchScenePresenter {

    private Context context;

    private static final String QUOTE_URL = "https://github.com/Hifairlady/LilyHouse/blob/master/quote_text-file.json";

    private static final String SEARCH_PREFIX = "https://m.dmzj.com/search/";

    private TextView tvQuote;
    private SearchView searchView;
    private AutoCompleteTextView searchTextView;
    private boolean isSearching = false;

    private RecyclerView recyclerView;

    private String searchUrl;
    private SearchController searchController;
    private ArrayList<String> suggestions;
    private SuggestionAdapter adapter;

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

    public void customSearchView(SearchView mSearchView, RecyclerView recyclerView1) {
        this.recyclerView = recyclerView1;
        this.searchView = mSearchView;
        this.searchTextView = searchView.findViewById(R.id.search_src_text);
        if (searchTextView == null) {
            return;
        }

        setupSuggestions();
        searchTextView.setThreshold(1);
        createNewAdapter(suggestions);
        searchTextView.setDropDownBackgroundResource(R.color.colorPrimary);

        searchTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchView.setQuery(adapter.getItem(position), true);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        searchTextView.setTextAppearance(context, R.style.SearchTextStyle);
        searchTextView.setTextColor(context.getResources().getColor(R.color.primary_text));
        searchTextView.setHintTextColor(context.getResources().getColor(R.color.secondary_text));
        searchTextView.setHint(R.string.search_hint_string);

        ImageView imageView = searchView.findViewById(R.id.search_go_btn);
        imageView.setImageResource(R.drawable.ic_arrow_forward);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                if (!isSearching) {
                    isSearching = true;
                    searchUrl = URLEncoder.encode(query);
                    searchUrl = SEARCH_PREFIX + searchUrl + ".html";
                    searchController.setupSearchData(searchUrl, getSearchHandler);
                    storeHistory(query);
                } else {
                    Snackbar.make(recyclerView, R.string.searching_string, Snackbar.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchTextView.dismissDropDown();
                filterItems(suggestions, newText);
                searchTextView.showDropDown();
                return true;
            }
        });

        searchView.setSubmitButtonEnabled(true);

    }

    @SuppressLint("HandlerLeak")
    private Handler getSearchHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case R.integer.get_data_success:
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

    private void filterItems(ArrayList<String> srcStrings, String newText) {
        if (newText.length() == 0) {
            createNewAdapter(srcStrings);
            return;
        }
        ArrayList<String> resultStrings = new ArrayList<>();
        for (int i = 0; i < srcStrings.size(); i++) {
            if (srcStrings.get(i).toLowerCase().contains(newText.toLowerCase())) {
                resultStrings.add(srcStrings.get(i));
            }
        }
        createNewAdapter(resultStrings);
    }

    private void storeHistory(String queryString) {
        for (int i = 0; i < suggestions.size(); i++) {
            if (suggestions.get(i).toLowerCase().equals(queryString.toLowerCase())) {
                return;
            }
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("SEARCH_HISTORY",
                MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int total = sharedPreferences.getInt("TOTAL", 0);

        String key = "SEARCH_HISTORY_" + total;
        editor.putString(key, queryString);
        suggestions.add(queryString);
        createNewAdapter(suggestions);

        total++;
        editor.putInt("TOTAL", total);
        editor.apply();
    }

    private void setupSuggestions() {
        suggestions = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("SEARCH_HISTORY",
                MODE_PRIVATE);
        int total = sharedPreferences.getInt("TOTAL", 0);
        for (int i = 0; i < total; i++) {
            String key = "SEARCH_HISTORY_" + i;
            String history = sharedPreferences.getString(key, "NONE");
            suggestions.add(history);
        }
    }

    public ArrayList<String> getSuggestions() {
        return suggestions;
    }

    private SuggestionAdapter.OnDeleteListener onDeleteListener = new SuggestionAdapter.OnDeleteListener() {
        @Override
        public void onDelete(int position) {
            suggestions.remove(position);
            searchTextView.dismissDropDown();
            createNewAdapter(suggestions);
            searchTextView.showDropDown();
        }
    };

    private void createNewAdapter(ArrayList<String> datas) {
        adapter = new SuggestionAdapter(context, R.layout.item_search_suggestion, datas, onDeleteListener);
        searchTextView.setAdapter(adapter);
    }



}
