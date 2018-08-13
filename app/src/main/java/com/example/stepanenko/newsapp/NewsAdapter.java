package com.example.stepanenko.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {
    public NewsAdapter(Context context, List<News> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final News currentArticle = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        TextView authorView = convertView.findViewById(R.id.author);
        TextView dateView = convertView.findViewById(R.id.date);
        TextView titleView = convertView.findViewById(R.id.webTitle);

        String authorName = currentArticle.getAuthor();
        if (currentArticle.getAuthor() != null) {
            authorView.setText(authorName);
        } else {
            authorView.setVisibility(View.GONE);
        }

        dateView.setText(currentArticle.getDate());
        String titleString = currentArticle.getTitle();
        titleView.setText(titleString);
        String webLink = currentArticle.getUrl();
        return convertView;
    }
}
