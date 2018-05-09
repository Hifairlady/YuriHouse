package com.edgar.yurihouse.Activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;

import com.edgar.yurihouse.Controllers.ReaderController;
import com.edgar.yurihouse.Presenters.ReaderScenePresenter;
import com.edgar.yurihouse.R;

public class ReaderActivity extends AppCompatActivity {

    private ReaderScenePresenter scenePresenter;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        int curPosition = 0;
        if (savedInstanceState != null) {
            curPosition = savedInstanceState.getInt("curPosition", 1) - 1;
        }

        scenePresenter = new ReaderScenePresenter(ReaderActivity.this, new ReaderController());

        scenePresenter.setImmerse(getWindow());

        scenePresenter.setupArgs(getIntent(), curPosition);

        View rootView = findViewById(android.R.id.content);
        scenePresenter.initViews(rootView);

        scenePresenter.initVerticalReader(rootView, getWindow());

        drawerLayout = findViewById(R.id.my_drawer_layout);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && drawerLayout.isDrawerOpen(Gravity.END) ) {
            drawerLayout.closeDrawer(Gravity.END);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curPosition", scenePresenter.getCurPosition());
    }
}
