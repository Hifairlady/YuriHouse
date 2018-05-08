package com.edgar.yurihouse.Presenters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;

import com.edgar.yurihouse.Adapters.MangaAdapter;
import com.edgar.yurihouse.Controllers.AuthorController;
import com.edgar.yurihouse.R;

public class AuthorScenePresenter {

    private Context context;
    private RecyclerView recyclerView;
    private AuthorController authorController;

    public AuthorScenePresenter(Context context, AuthorController authorController) {
        this.context = context;
        this.authorController = authorController;
    }

    public void setupRecyclerView(RecyclerView recyclerView1, String queryUrl) {
        this.recyclerView = recyclerView1;
        authorController.setupMangaList(queryUrl, getMangasHandler);
    }

    @SuppressLint("HandlerLeak")
    private Handler getMangasHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case R.integer.get_data_success:
                    MangaAdapter adapter = new MangaAdapter(context, true);
                    adapter.setHasStableIds(true);
                    adapter.addItems(authorController.getMangaItems());
                    recyclerView.setAdapter(adapter);
                    break;

                case R.integer.get_data_failed:
                    break;

                default:
                    break;
            }
        }
    };
}
