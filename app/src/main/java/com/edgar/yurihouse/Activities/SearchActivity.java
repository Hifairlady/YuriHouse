package com.edgar.yurihouse.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.TextView;

import com.edgar.yurihouse.Controllers.SearchController;
import com.edgar.yurihouse.Presenters.SearchScenePresenter;
import com.edgar.yurihouse.R;

public class SearchActivity extends AppCompatActivity {

    private SearchScenePresenter scenePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        scenePresenter = new SearchScenePresenter(SearchActivity.this, new SearchController());

        initView();
    }

    private void initView() {

        TextView tvQuote = findViewById(R.id.tv_search_quote);
        TextView tvSearchCount = findViewById(R.id.tv_search_count);
        tvSearchCount.setVisibility(View.GONE);
        SearchView searchView = findViewById(R.id.my_search_view);

        RecyclerView recyclerView = findViewById(R.id.rv_search_result);

        scenePresenter.setupQuoteText(tvQuote);
        scenePresenter.customSearchView(searchView, tvSearchCount, recyclerView);

    }
}
