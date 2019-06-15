package com.example.android.newsappstage1;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.app.LoaderManager.LoaderCallbacks;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Story>>{

    private NewsAdapter mAdapter;

    private static final int NEWS_LOADER_ID = 1;

    private static final String GUARDIAN_REQUEST_URL =
            "http://content.guardianapis.com/search?q=%22Elon%20Musk%22&show-tags=contributor&api-key=a750e32c-017e-4114-958c-e873405857c1";

    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        final ListView newsListView = (ListView) findViewById(R.id.list);

        mAdapter = new NewsAdapter(this, new ArrayList<Story>());

        //Hook up the adapter to the ListView
        newsListView.setAdapter(mAdapter);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Story story = (Story) mAdapter.getItem(position);
                Uri webPage = Uri.parse(story.getUrl());
                Intent goToWeb = new Intent(Intent.ACTION_VIEW, webPage);
                startActivity(goToWeb);
            }
        });

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        }else{
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String topic = sharedPrefs.getString(getString(R.string.settings_topic_key),getString(R.string.settings_topics_none_value));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("section", topic);
        uriBuilder.appendQueryParameter("orderby", orderBy);



        //TODO: HERE I WANT WHEN THE APP FIRST LAUNCHES OR WHEN "NONE" IS CLICKED TO QUERY THE ORIGINAL URL
        //SO WHEN I FIRST OPEN THE APP THIS DOESNT WORK, BUT WHEN I PRESS NONE FROM PREFERENCES AND GO BACK , IT LOADS JUST FINE
        if(topic.equals(getString(R.string.settings_topics_none_value))) {
            Log.e("PROBLEM WITH URL?","topic = "+topic);
            return new NewsLoader(this, GUARDIAN_REQUEST_URL);
        }

            Log.e("PROBLEM WITH URL?",uriBuilder.toString());
            return new NewsLoader(this, uriBuilder.toString());


    }

    @Override
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> stories) {

        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mAdapter.clear();

        mEmptyStateTextView.setText(R.string.no_news);

        if(stories != null && !stories.isEmpty()) mAdapter.addAll(stories);
    }

    @Override
    public void onLoaderReset(Loader<List<Story>> loader) {
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
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
