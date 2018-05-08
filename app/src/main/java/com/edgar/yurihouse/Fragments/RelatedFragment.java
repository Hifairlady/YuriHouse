package com.edgar.yurihouse.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edgar.yurihouse.Adapters.MangaAdapter;
import com.edgar.yurihouse.Controllers.MangaController;
import com.edgar.yurihouse.R;

public class RelatedFragment extends Fragment {

    private static final String ARG_QUERY_URL = "queryUrl";

    private String queryUrl;

    private boolean isFragDestroyed = false;

    private RecyclerView recyclerView1, recyclerView2;
    private TextView tvAuthor;

    private LinearLayout lvHasData;
    private CardView cvNoData;
    private Button btnRetry;
    private MangaController mangaController;

    public RelatedFragment() { }

    public static RelatedFragment newInstance(String relatedUrl, MangaController mangaController) {
        RelatedFragment fragment = new RelatedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY_URL, relatedUrl);
        fragment.setArguments(args);
        fragment.setMangaController(mangaController);
        return fragment;
    }

    public void setMangaController(MangaController mangaController) {
        this.mangaController = mangaController;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            queryUrl = getArguments().getString(ARG_QUERY_URL);
        }
        queryUrl = mangaController.getRelatedUrl();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_related, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView1 = view.findViewById(R.id.related_recycler_view1);
        recyclerView2 = view.findViewById(R.id.related_recycler_view2);
        tvAuthor = view.findViewById(R.id.tv_author_related_title);

        lvHasData = view.findViewById(R.id.lv_related_has_data);
        cvNoData = view.findViewById(R.id.cv_no_data_page);
        btnRetry = view.findViewById(R.id.btn_error_retry);
        btnRetry.setOnClickListener(mOnClickListener);

        mangaController.setupRelated(queryUrl, getRelatedHandler);

    }

    @SuppressLint("HandlerLeak")
    private Handler getRelatedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case R.integer.get_data_success:

                    if (isFragDestroyed) return;

                    cvNoData.setVisibility(View.GONE);
                    lvHasData.setVisibility(View.VISIBLE);
                    if (mangaController.getRelatedAuthorName() == null) {
                        cvNoData.setVisibility(View.VISIBLE);
                        return;
                    }
                    tvAuthor.setText(mangaController.getRelatedAuthorName());
//                        btnRetry.setClickable(true);

                    MangaAdapter adapter1 = new MangaAdapter(getContext(), true);
                    MangaAdapter adapter2 = new MangaAdapter(getContext(), true);

                    adapter1.setHasStableIds(true);
                    adapter2.setHasStableIds(true);
                    adapter1.setItems(mangaController.getMangaItems1());
                    adapter2.setItems(mangaController.getMangaItems2());
                    GridLayoutManager layoutManager1 = new GridLayoutManager(getContext(), 3);
                    recyclerView1.setLayoutManager(layoutManager1);
                    recyclerView1.setAdapter(adapter1);

                    GridLayoutManager layoutManager2 = new GridLayoutManager(getContext(), 3);
                    recyclerView2.setLayoutManager(layoutManager2);
                    recyclerView2.setAdapter(adapter2);

                    break;

                case R.integer.get_data_failed:
                    lvHasData.setVisibility(View.GONE);
                    cvNoData.setVisibility(View.VISIBLE);
                    btnRetry.setClickable(true);
                    break;

                default:
                    break;
            }
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.btn_error_retry:
                    cvNoData.setVisibility(View.GONE);
                    lvHasData.setVisibility(View.VISIBLE);
                    mangaController.setupRelated(queryUrl, getRelatedHandler);
                    btnRetry.setClickable(false);

                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFragDestroyed = true;
    }

}
