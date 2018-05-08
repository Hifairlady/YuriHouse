package com.edgar.yurihouse.Utils;

import android.content.Context;
import android.content.Intent;
import android.text.style.ClickableSpan;
import android.view.View;

import com.edgar.yurihouse.Activities.CommentImageActivity;

import java.util.Arrays;


public class ImageClickableSpan extends ClickableSpan {

    private Context context;
    private String[] urlStrings;
    private int curPosition;

    public ImageClickableSpan(Context context, String[] urlStrings, int curPosition) {
        this.context = context;
        this.urlStrings = Arrays.copyOf(urlStrings, urlStrings.length);
        this.curPosition = curPosition;
    }

    @Override
    public void onClick(View widget) {
        Intent imageIntent = new Intent(context, CommentImageActivity.class);
        imageIntent.putExtra("imageUrls", urlStrings);
        imageIntent.putExtra("curPosition", curPosition);
        context.startActivity(imageIntent);
    }

}
