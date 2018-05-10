package com.edgar.yurihouse.Presenters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edgar.yurihouse.Activities.AuthorActivity;
import com.edgar.yurihouse.Adapters.ViewPagerAdapter;
import com.edgar.yurihouse.Controllers.CommentController;
import com.edgar.yurihouse.Controllers.MangaController;
import com.edgar.yurihouse.Fragments.ChaptersFragment;
import com.edgar.yurihouse.Fragments.CommentsFragment;
import com.edgar.yurihouse.Fragments.RelatedFragment;
import com.edgar.yurihouse.Items.CommentQueryArg;
import com.edgar.yurihouse.R;
import com.edgar.yurihouse.Utils.GlideUtil;

import java.util.ArrayList;
import java.util.Date;

public class MangaScenePresenter {

    private static final String TAG = "====================" + MangaScenePresenter.class.getSimpleName();

    private Context context;

    private MangaController mangaController;

    private String urlString, backupUrl, titleString;

    private boolean firstTry = true, isDestroyed = false;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private LinearLayout authorContainer;

    private ArrayList<Fragment> fragments = new ArrayList<>();

    private TextView tvLabelTypes, tvLabelStatus, tvLabelTime;
    private ImageView ivCover;
    private FragmentManager fragmentManager;

    private String[] authorsStrings = new String[0];

    public MangaScenePresenter(Context context, MangaController mangaController) {
        this.context = context;
        this.mangaController = mangaController;
    }

    @SuppressLint("HandlerLeak")
    private Handler getInfosHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case R.integer.get_data_success:

                    if (isDestroyed) return;

                    tvLabelTime.setText(mangaController.getTimeString());
                    tvLabelStatus.setText(mangaController.getStatusString());
                    tvLabelTypes.setText(mangaController.getTypeString());
                    GlideUtil.setImageView(ivCover, mangaController.getCoverUrl());
                    authorContainer.removeAllViews();
                    authorsStrings = new String[mangaController.getAuthorItems().size()];
                    for (int i = 0; i < mangaController.getAuthorItems().size(); i++) {
                        addAuthorsView(authorContainer, mangaController.getAuthorItems().get(i).getAuthorName(),
                                mangaController.getAuthorItems().get(i).getAuthorUrl());
                        authorsStrings[i] = mangaController.getAuthorItems().get(i).getAuthorName();
                    }
                    setupFragments();
                    break;

                case R.integer.get_data_failed:
                    if (firstTry) {
                        firstTry = false;
                        if (backupUrl == null || backupUrl.length() == 0) break;
                        mangaController.setupMangaInfos(backupUrl, getInfosHandler);
                    }

                    break;

                default:
                    break;
            }
        }
    };

    public void setupStrings(String titleString, String urlString, String backupUrl) {
        this.titleString = titleString;
        this.urlString = urlString;
        this.backupUrl = backupUrl;
    }

    public void setupBasicInfos(LinearLayout authorContainer, TextView tvLabelStatus, TextView tvLabelTypes,
                                TextView tvLabelTime, ImageView ivCover) {
        this.authorContainer = authorContainer;
        this.tvLabelStatus = tvLabelStatus;
        this.tvLabelTypes = tvLabelTypes;
        this.tvLabelTime = tvLabelTime;
        this.ivCover = ivCover;

        mangaController.setupMangaInfos(urlString, getInfosHandler);
    }

    private void addAuthorsView(@NonNull LinearLayout container, @NonNull final String authorName,
                                @NonNull final String authorUrl) {

        View view = LayoutInflater.from(context).inflate(R.layout.button_author_name, null);
        TextView tvAuthor = view.findViewById(R.id.tv_author_button);
        tvAuthor.setText(authorName);
        tvAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authorIntent = new Intent(context, AuthorActivity.class);
                authorIntent.putExtra(context.getString(R.string.info_url_string_extra), authorUrl);
                authorIntent.putExtra(context.getString(R.string.info_title_string_extra), authorName);
                context.startActivity(authorIntent);
            }
        });
        container.addView(view);
    }

    public void setDestroyed(boolean destroyed) {
        isDestroyed = destroyed;
    }

    private void setupFragments() {
        fragments.clear();
        fragments.add(RelatedFragment.newInstance(mangaController.getRelatedUrl(), mangaController));
        fragments.add(ChaptersFragment.newInstance(urlString, titleString, authorsStrings, mangaController));

        Date date = new Date();
        String dateString = String.valueOf(date.getTime());

        CommentQueryArg commentQueryArg = mangaController.getCommentQueryArg();
        String allCommentUrl = "https://interface.dmzj.com/api/NewComment2/list?callback=comment_list_s&type=";
        allCommentUrl = allCommentUrl + commentQueryArg.getComment_type() + "&obj_id=" +
                commentQueryArg.getObj_id() + "&hot=0&page_index=1";
        allCommentUrl = allCommentUrl + "&_=" + dateString;

        String hotCommentUrl = "https://interface.dmzj.com/api/NewComment2/list?callback=hotComment_s&type=";
        hotCommentUrl = hotCommentUrl + commentQueryArg.getComment_type() + "&obj_id=" + commentQueryArg.getObj_id() + "&hot=1&page_index=1";
        hotCommentUrl = hotCommentUrl + "&_=" + dateString;

        fragments.add(CommentsFragment.newInstance(allCommentUrl, hotCommentUrl, new CommentController()));

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(fragmentManager, fragments);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.removeAllTabs();

        tabLayout.addTab(tabLayout.newTab(), 0);
        tabLayout.addTab(tabLayout.newTab(), 1);
        tabLayout.addTab(tabLayout.newTab(), 2);
        tabLayout.setupWithViewPager(viewPager);

        if (tabLayout.getTabAt(0) == null) {
            Log.d(TAG, "setupViewpager: null");
            return;
        }

        tabLayout.getTabAt(0).setText(context.getString(R.string.info_related_chapter_title));
        tabLayout.getTabAt(1).setText(context.getString(R.string.info_chapter_list_title));
        tabLayout.getTabAt(2).setText(context.getString(R.string.info_comment_title));

        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(2);

    }

    public void setupViewpager(ViewPager viewPager1, TabLayout tabLayout1, FragmentManager fragmentManager) {

        this.viewPager = viewPager1;
        this.tabLayout = tabLayout1;
        this.fragmentManager = fragmentManager;

    }

    public void setupNewIntent() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (int i = 0; i < fragments.size(); i++) {
            fragmentTransaction.remove(fragments.get(i));
        }
        fragmentTransaction.commit();
    }
}
