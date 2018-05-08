package com.edgar.yurihouse.Controllers;

import android.content.Context;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edgar.yurihouse.Items.CommentItem;
import com.edgar.yurihouse.R;
import com.edgar.yurihouse.Utils.ImageClickableSpan;
import com.edgar.yurihouse.Utils.JsonGetter;
import com.edgar.yurihouse.Utils.MyHtmlImageGetter;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class CommentController {

    public CommentController() {}

    public void setupComments(String urlString, Handler handler) {

        JsonGetter jsonGetter = new JsonGetter(urlString, handler);
        jsonGetter.start();

    }

    public void setupCommentsList(String urlString, Handler handler) {

        JsonGetter jsonGetter = new JsonGetter(urlString, handler);
        jsonGetter.start();

    }

    public ArrayList<CommentItem> getCommentItems(String jsonString) {
        Type listType = new TypeToken<ArrayList<CommentItem>>() {}.getType();
        ArrayList<CommentItem> items = new GsonBuilder().create().fromJson(jsonString, listType);
        return items;
    }

    public void setupImageSpan(Context context, HtmlTextView htmlTextView, String[] urls) {

        try {
            SpannableStringBuilder spannableString = new SpannableStringBuilder();
            spannableString.append(htmlTextView.getText());
            Collections.reverse(Arrays.asList(urls));
//            String[] original
            for (int mIndex = 0; mIndex < urls.length; mIndex++) {
                if (urls[mIndex].length() == 0) continue;
                ImageClickableSpan clickableSpan = new ImageClickableSpan(context, urls, mIndex);
                int start = htmlTextView.getText().length() - mIndex - 1;
                int end = htmlTextView.getText().length() - mIndex;
                spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
//            htmlTextView.setMovementMethod(LinkMovementMethod.getInstance());
            htmlTextView.setText(spannableString);

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    public void loadInnerComments(Context context, LinearLayout lvInnerContainer,
                                  ArrayList<CommentItem.MasterCommentItem> masterCommentItems) {

        lvInnerContainer.removeAllViews();

        for (int index = 0; index < masterCommentItems.size(); index++) {

            View view = LayoutInflater.from(context).inflate(R.layout.item_inner_comment, null);

            TextView tvFloor = view.findViewById(R.id.tv_comment_floor_number);
            HtmlTextView htvInnerContent = view.findViewById(R.id.tv_inner_comment_content);

            tvFloor.setText(context.getString(R.string.inner_comment_floor_num_string,
                    index + 1));
            String nameHtml = "<b>" + masterCommentItems.get(index).getNickname() + "</b>";
            String textContent = nameHtml + ": " + masterCommentItems.get(index).getContent();
            String htmlContent = "<span>" + textContent + "</span><p></p>";

            String[] innerUrls = masterCommentItems.get(index).getUpload_images().split(",");
            for (int iii = 0; iii < innerUrls.length; iii++) {
                if (innerUrls[iii].length() == 0) continue;
                if (!innerUrls[iii].startsWith("http")) {
                    innerUrls[iii] = "https://images.dmzj.com/commentImg/" +
                            masterCommentItems.get(index).getObj_id() % 500 + "/" + innerUrls[iii];
                }
                htmlContent = htmlContent + "<img src=\"" + innerUrls[iii] + "\" />";
            }
            htvInnerContent.setHtml(htmlContent, new MyHtmlImageGetter(htvInnerContent));
            setupImageSpan(context, htvInnerContent, innerUrls);
            lvInnerContainer.addView(view);

        }

    }


}
