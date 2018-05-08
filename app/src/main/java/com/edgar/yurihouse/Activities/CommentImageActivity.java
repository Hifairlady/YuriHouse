package com.edgar.yurihouse.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.edgar.yurihouse.Adapters.ViewPagerAdapter;
import com.edgar.yurihouse.Fragments.CommentImageFragment;
import com.edgar.yurihouse.R;

import java.util.ArrayList;

public class CommentImageActivity extends AppCompatActivity {

    private ArrayList<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_image);

        int curPosition = getIntent().getIntExtra("curPosition", 0);
        String[] imageUrls = getIntent().getStringArrayExtra("imageUrls");

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        int width = 0;
        if (wm != null) {
            width = wm.getDefaultDisplay().getWidth();
        }

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle(R.string.comment_image_detail_title);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        PhotoView photoView = (PhotoView)findViewById(R.id.pv_comment_image);
//        GlideUtil.setScaledImage(this, urlString, photoView, width);

        ViewPager viewPager = findViewById(R.id.vp_comment_image);
        viewPager.setOffscreenPageLimit(2);

        for (int i = imageUrls.length - 1; i >= 0; i--) {
//            CommentImageFragment fragment = new CommentImageFragment(width, imageUrls[i]);
            CommentImageFragment fragment = CommentImageFragment.newInstance(width, imageUrls[i]);
            fragments.add(fragment);
        }

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(imageUrls.length - 1 - curPosition);

    }
}
