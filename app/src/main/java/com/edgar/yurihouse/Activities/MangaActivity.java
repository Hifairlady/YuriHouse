package com.edgar.yurihouse.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edgar.yurihouse.Controllers.MangaController;
import com.edgar.yurihouse.Presenters.MangaScenePresenter;
import com.edgar.yurihouse.R;

public class MangaActivity extends AppCompatActivity {

    private MangaScenePresenter scenePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga);

        scenePresenter = new MangaScenePresenter(MangaActivity.this, new MangaController());

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String titleString = getIntent().getStringExtra(getString(R.string.info_title_string_extra));
        String urlString = getIntent().getStringExtra(getString(R.string.info_url_string_extra));
        String backupUrl = getIntent().getStringExtra(getString(R.string.back_up_url_string_extra));

        scenePresenter.setupStrings(titleString, urlString, backupUrl);

        toolbar.setTitle(titleString);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayout authorContainer = findViewById(R.id.basic_info_authors_container);

        TextView tvLabelStatus = findViewById(R.id.basic_info_status);
        TextView tvLabelTypes = findViewById(R.id.basic_info_types);
        TextView tvLabelTime = findViewById(R.id.basic_info_time);
        ImageView ivCover = findViewById(R.id.info_cover_image);

        scenePresenter.setupBasicInfos(authorContainer, tvLabelStatus, tvLabelTypes, tvLabelTime, ivCover);
        ViewPager viewPager = findViewById(R.id.info_view_pager);
        TabLayout tabLayout = findViewById(R.id.info_tab_layout);

        scenePresenter.setupViewpager(viewPager, tabLayout, getSupportFragmentManager());
    }

    @Override
    protected void onDestroy() {
        scenePresenter.setDestroyed(true);
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        scenePresenter.setupNewIntent();
        super.recreate();
    }
}
