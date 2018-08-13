package com.example.stepanenko.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {
    private static final String GUARDIAN_WORLD_REQUEST_URL =
            "https://content.guardianapis.com/search?order-by=newest&q=world%20news&api-key=6fe977cd-ab92-4c04-add2-c38014fd464a";

    private static final int NEWS_LOADER_ID = 1;
    private NewsAdapter currentAdapter;
    private TextView emptyPlaceholder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView articleListView = findViewById(R.id.list);

        emptyPlaceholder = findViewById(R.id.empty_placeholder);
        articleListView.setEmptyView(emptyPlaceholder);

        currentAdapter = new NewsAdapter(this, new ArrayList<News>());
        articleListView.setAdapter(currentAdapter);
        Log.v("VERIFY_INIT_LOADER", "Loader initialized");

        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News currentArticle = currentAdapter.getItem(position);
                Uri webLink = Uri.parse(currentArticle.getUrl());
                Intent webBrowser = new Intent(Intent.ACTION_VIEW, webLink);
                startActivity(webBrowser);
            }
        });

        ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkStatus = connectionManager.getActiveNetworkInfo();

        if (networkStatus != null && networkStatus.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
           ProgressBar progress = findViewById(R.id.progressbar);
           progress.setVisibility(View.GONE);
            emptyPlaceholder.setText(R.string.no_connection);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        Log.v("VERIFY_ONCREATE_LOADER", "Loader created");
        return new NewsLoader(this, GUARDIAN_WORLD_REQUEST_URL);

    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        ProgressBar progress = findViewById(R.id.progressbar);
        progress.setVisibility(View.GONE);
        emptyPlaceholder.setText(R.string.empty_placeholder);
        currentAdapter.clear();

        if (news != null && !news.isEmpty()) {
            Log.v("VERIFY_ONFINISH_LOADER", "Loader finished");
            currentAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        Log.v("VERIFY_ONRESET_LOADER", "Loader reset");
        currentAdapter.clear();
    }
}
