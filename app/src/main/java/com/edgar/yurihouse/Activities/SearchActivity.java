package com.edgar.yurihouse.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import com.edgar.yurihouse.Controllers.SearchController;
import com.edgar.yurihouse.Presenters.SearchScenePresenter;
import com.edgar.yurihouse.R;

import java.util.ArrayList;

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
        SearchView searchView = findViewById(R.id.my_search_view);

        RecyclerView recyclerView = findViewById(R.id.rv_search_result);

        scenePresenter.setupQuoteText(tvQuote);
        scenePresenter.customSearchView(searchView, recyclerView);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sharedPreferences = getSharedPreferences("SEARCH_HISTORY", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                ArrayList<String> dataList = scenePresenter.getSuggestions();
                if (dataList != null && dataList.size() != 0) {

                    editor.putInt("TOTAL", dataList.size());
                    for (int i = 0; i < dataList.size(); i++) {
                        String key = "SEARCH_HISTORY_" + i;
                        editor.putString(key, dataList.get(i));
                    }
                }
                editor.apply();

            }
        }).start();
    }
}
