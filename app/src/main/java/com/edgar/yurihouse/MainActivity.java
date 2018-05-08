package com.edgar.yurihouse;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.edgar.yurihouse.Activities.SearchActivity;
import com.edgar.yurihouse.Controllers.MainController;
import com.edgar.yurihouse.Presenters.MainScenePresenter;
import com.edgar.yurihouse.Utils.PermissionRequester;

public class MainActivity extends AppCompatActivity {

    private static boolean isExit = false;
    private RecyclerView recyclerView;
    private MainScenePresenter scenePresenter;

    private static final int REQUEST_PERMISSION_STORAGE_CODE = 105;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scenePresenter = new MainScenePresenter(MainActivity.this, new MainController());

        initView();
    }

    private void initView() {

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_app_logo);

        scenePresenter.initFilterDialog();
        scenePresenter.setupFilters();

        FloatingActionButton fab = findViewById(R.id.fab_main_scene);
        scenePresenter.initFab(fab);

        recyclerView = findViewById(R.id.rv_main_scene);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.srl_main_scene);
        scenePresenter.initRecyclerView(recyclerView, refreshLayout);

        PermissionRequester.request(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                getString(R.string.request_rationale_string), REQUEST_PERMISSION_STORAGE_CODE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.main_menu_action_filter:
                scenePresenter.showFilter();
                break;

            case R.id.main_menu_action_grid:
                scenePresenter.switchDisplayMode(item);
                break;

            case R.id.main_menu_action_search:
                Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(searchIntent);
                break;

            default:
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    //handle double click return key to exit
    @SuppressLint("HandlerLeak")
    private Handler exitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == R.integer.exit_program) {
                isExit = false;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Snackbar.make(recyclerView, R.string.exit_double_click_string, Snackbar.LENGTH_SHORT).show();
            // exit after 2 seconds
            exitHandler.sendEmptyMessageDelayed(R.integer.exit_program, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }
}
