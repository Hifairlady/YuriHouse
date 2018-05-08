package com.edgar.yurihouse.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.edgar.yurihouse.Adapters.GridExAdapter;
import com.edgar.yurihouse.Controllers.MangaController;
import com.edgar.yurihouse.Items.ChapterItem;
import com.edgar.yurihouse.R;
import com.edgar.yurihouse.Views.GridViewEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class ChaptersFragment extends Fragment {

    private static final String ARG_QUERY_URL = "queryUrl";
    private static final String ARG_QUERY_TITLE = "titleString";
    private static final String ARG_QUERY_AUTHORS = "authorsStrings";

    private boolean isFragDestroyed = false;

    private String queryUrl = null;
    private String titleString;
    private String[] authorsStrings;

    private LinearLayout lvContainer;
    private TextView tvIntro;
    private ImageView ivIconMore;
    private MangaController mangaController;

    private RadioButton rbAscending, rbDescending;

    private boolean isIntroCollapsed = true;
    private boolean isDataLoading = true;

    private ArrayList<ChapterItem> chapterItems = new ArrayList<>();
    private GridExAdapter[] adapters = new GridExAdapter[0];

    public ChaptersFragment() { }

    public static ChaptersFragment newInstance(String urlString, String titleString,
                                               String[] authorsStrings, MangaController mangaController) {
        ChaptersFragment fragment = new ChaptersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY_URL, urlString);
        args.putString(ARG_QUERY_TITLE, titleString);
        args.putStringArray(ARG_QUERY_AUTHORS, authorsStrings);
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
            titleString = getArguments().getString(ARG_QUERY_TITLE);
            authorsStrings = getArguments().getStringArray(ARG_QUERY_AUTHORS);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chapters, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivIconMore = view.findViewById(R.id.iv_intro_icon_more);
        tvIntro = view.findViewById(R.id.tv_brief_intro);
        LinearLayout lvIntroRoot = view.findViewById(R.id.lv_brief_intro_root);
        lvContainer = view.findViewById(R.id.lv_chapter_cards_container);

        rbDescending = view.findViewById(R.id.rb_sort_descending);
        rbAscending = view.findViewById(R.id.rb_sort_ascending);
        rbDescending.setOnCheckedChangeListener(mOnCheckListener);

        lvIntroRoot.setOnClickListener(mOnClickListener);

    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mangaController.setupChaptersList(queryUrl, getChaptersHandler);
    }

    @SuppressLint("HandlerLeak")
    private Handler getChaptersHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case R.integer.get_data_success:
                    if (isFragDestroyed) return;
                    chapterItems = mangaController.getChapterItems();
                    for (int i = 0; i < chapterItems.size(); i++) {
                        addCardsToContainer(lvContainer, chapterItems.get(i));
                    }
                    tvIntro.setText(mangaController.getIntroString());
                    isDataLoading = false;
                    break;

                case R.integer.get_data_failed:
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
                case R.id.lv_brief_intro_root:
                    isIntroCollapsed = !isIntroCollapsed;
                    if (isIntroCollapsed) {
                        tvIntro.setMaxLines(2);
                        ivIconMore.setImageResource(R.drawable.ic_expand_more);
                    } else {
                        tvIntro.setMaxLines(Integer.MAX_VALUE);
                        ivIconMore.setImageResource(R.drawable.ic_expand_less);
                    }
                    break;

                default:
                    break;
            }
        }
    };

    CompoundButton.OnCheckedChangeListener mOnCheckListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isDataLoading) {
                rbDescending.setChecked(!rbDescending.isChecked());
                rbAscending.setChecked(!rbAscending.isChecked());
                return;
            }
            for (int i = 0; i < chapterItems.size(); i++) {
                isDataLoading = true;
                Collections.reverse(chapterItems.get(i).getData());
                adapters[i].setAscending(rbDescending.isChecked());
                adapters[i].notifyDataSetChanged();
            }
            isDataLoading = false;
        }
    };

    private void addCardsToContainer(final LinearLayout lvContainer, ChapterItem chapterItem) {

        if (isFragDestroyed)return;

        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_chapter_list_cards, null);

        TextView tvTitleView = view.findViewById(R.id.tv_cards_title);
        TextView tvTotal = view.findViewById(R.id.tv_total_chapters);

        final GridViewEx gridView = view.findViewById(R.id.chapter_gridview);
        if (getContext() == null) return;

        GridExAdapter adapter = new GridExAdapter(getContext(), chapterItem, titleString,
                authorsStrings);
        adapters = Arrays.copyOf(adapters, adapters.length + 1);
        adapters[adapters.length - 1] = adapter;

        tvTitleView.setText(chapterItem.getTitle());
        tvTotal.setText(getContext().getString(R.string.total_chapters_num, chapterItem.getData().size()));
        gridView.setAdapter(adapter);

        lvContainer.addView(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDataLoading = true;
        isFragDestroyed = true;
    }

}
