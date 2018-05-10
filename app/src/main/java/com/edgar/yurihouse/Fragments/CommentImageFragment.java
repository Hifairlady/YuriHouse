package com.edgar.yurihouse.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edgar.yurihouse.R;
import com.edgar.yurihouse.Utils.GlideUtil;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentImageFragment extends Fragment {

    private static final String ARG_WIDTH = "width";
    private static final String ARG_IMAGE_URL = "imageUrl";

    private String imageUrl;
    private int width;

    public CommentImageFragment() {}

    public static CommentImageFragment newInstance(int width, String imageUrl) {
        CommentImageFragment fragment = new CommentImageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_WIDTH, width);
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            width = getArguments().getInt(ARG_WIDTH, 0);
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PhotoView photoView = view.findViewById(R.id.pv_comment_image);
        GlideUtil.setScaledImage(photoView, imageUrl);
    }
}
