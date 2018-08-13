package com.example.stepanenko.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.stepanenko.newsapp.News;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private static final String LOG_TAG = NewsLoader.class.getName();

    private String queryURL;
    public NewsLoader(Context context, String url) {
        super(context);
        queryURL = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        Log.v("VERIFY_ONSTART_LOADER", "Loader started");
    }

    @Override
    public List<News> loadInBackground() {
        if (queryURL == null) {
            Log.v("VERIFY_INBACK_LOADER", "Loader started in background");
            return null;
        }
        List<News> result = QueryUtils.fetchNewsData(queryURL);
        Log.v("VERIFY_QUERYUTILS", "Data fetched");
        return result;
    }
}
