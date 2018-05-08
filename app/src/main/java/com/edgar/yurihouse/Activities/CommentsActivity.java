package com.edgar.yurihouse.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.edgar.yurihouse.Controllers.CommentController;
import com.edgar.yurihouse.Presenters.CommentsScenePresenter;
import com.edgar.yurihouse.R;

public class CommentsActivity extends AppCompatActivity {

    private CommentsScenePresenter scenePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        scenePresenter = new CommentsScenePresenter(CommentsActivity.this, new CommentController());

        String queryUrl = getIntent().getStringExtra("queryUrl");
        String titleString = getIntent().getStringExtra("titleString");

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(titleString);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FloatingActionButton fabTop = findViewById(R.id.fab_comments_top);
        scenePresenter.initFab(fabTop);

        RecyclerView recyclerView = findViewById(R.id.rv_comments_list);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.srl_comments_scene);
        scenePresenter.initRecyclerView(recyclerView, refreshLayout, queryUrl);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scenePresenter.setActivityDestroyed(true);
    }
}
