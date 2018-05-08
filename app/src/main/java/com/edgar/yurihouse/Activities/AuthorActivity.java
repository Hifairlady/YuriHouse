package com.edgar.yurihouse.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.edgar.yurihouse.Controllers.AuthorController;
import com.edgar.yurihouse.Presenters.AuthorScenePresenter;
import com.edgar.yurihouse.R;

public class AuthorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);

        AuthorScenePresenter scenePresenter = new AuthorScenePresenter(AuthorActivity.this, new AuthorController());

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        String titleString = getIntent().getStringExtra(getString(R.string.info_title_string_extra));
        String queryUrl = getIntent().getStringExtra(getString(R.string.info_url_string_extra));

        toolbar.setTitle(titleString);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.rv_author_chapters_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);

        scenePresenter.setupRecyclerView(recyclerView, queryUrl);
    }
}
