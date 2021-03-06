package com.example.stepanenko.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String GUARDIAN_WORLD_REQUEST_URL =
            ("https://content.guardianapis.com/search");

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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News currentArticle = currentAdapter.getItem(position);
                Uri webLink = Uri.parse(currentArticle.getUrl());
                Intent webBrowser = new Intent(Intent.ACTION_VIEW, webLink);

                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(webBrowser, 0);
                boolean isIntentSafe = activities.size() > 0;

                if (isIntentSafe) {
                    startActivity(webBrowser);
                }
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
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(getString(R.string.settings_articles_per_page_key)) ||
                key.equals(getString(R.string.settings_order_by_key))) {
            currentAdapter.clear();
            emptyPlaceholder.setVisibility(View.GONE);
            View loadingIndicator = findViewById(R.id.progressbar);
            loadingIndicator.setVisibility(View.VISIBLE);
            getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String articlesPerPage = sharedPrefs.getString(
                getString(R.string.settings_articles_per_page_key),
                getString(R.string.settings_articles_per_page_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        Uri baseUri = Uri.parse(GUARDIAN_WORLD_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("q", "world news");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("api-key", BuildConfig.GUARDIAN_API_TOKEN);

        if (!articlesPerPage.isEmpty()) {
            uriBuilder.appendQueryParameter("page-size", articlesPerPage);
        }
        if (!orderBy.isEmpty()) {
            uriBuilder.appendQueryParameter("order-by", orderBy);
        }

        Log.v("VERIFY_URI", "url: " + uriBuilder.toString());
        return new NewsLoader(MainActivity.this, uriBuilder.toString());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
