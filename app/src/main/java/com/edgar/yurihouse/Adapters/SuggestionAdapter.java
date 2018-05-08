package com.edgar.yurihouse.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edgar.yurihouse.R;

import java.util.ArrayList;

public class SuggestionAdapter extends ArrayAdapter<String> {

    private ArrayList<String> suggestions;
    private Context context;
    private OnDeleteListener listener;

    public SuggestionAdapter(@NonNull Context context, int resource,
                             @NonNull ArrayList<String> suggestions, OnDeleteListener listener) {
        super(context, resource, suggestions);
        this.context = context;
        this.listener = listener;
        this.suggestions = new ArrayList<>(suggestions);
    }

    @Override
    public int getCount() {
        return (suggestions == null ? 0 : suggestions.size());
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return suggestions.get(position);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_search_suggestion, parent, false);
        } else {
            view = convertView;
        }

        TextView textView = view.findViewById(R.id.tv_search_suggestion);
        ImageView imageView = view.findViewById(R.id.iv_search_suggestion_close);

        textView.setText(suggestions.get(position));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                suggestions.remove(position);
//                notifyDataSetChanged();
                listener.onDelete(position);
            }
        });
        return view;
    }

    public void setItems(ArrayList<String> items) {
        suggestions.clear();
        suggestions.addAll(items);
        notifyDataSetChanged();
    }

//    public ArrayList<String> getSuggestions() {
//        return suggestions;
//    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    public void setOnDeleteListener(OnDeleteListener listener) {
        this.listener = listener;
    }

}
